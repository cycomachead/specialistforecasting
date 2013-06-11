/*
 * Copyright, 1999-2007, SALESFORCE.com All Rights Reserved Company Confidential
 */

package tools.apex;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import shared.xml.soap.SoapXmlWriter;
import tools.util.Config;

import com.sforce.soap.apex.*;
import com.sforce.soap.partner.wsc.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * A tool to run an apex webmethod over and over again until completion. The webmethod must be of the form<br>
 * <tt>webservice String <i>methodName</i>(String arg)</tt> The first argument <strong>must</strong> be named arg.
 * This function should use the argument as a way of tracking forward progress. For example, if you are processing
 * contracts, your method should look something like this
 * 
 * <pre>
 * package myPkg {
 *   webservice String processContracts(String arg) {
 *      Contract[] contracts = [Select Id,ContractNumber from Contract where ContractNumber &gt; :arg LIMIT 100];
 *      if (contracts.length() == 0) return null; // This will tell ApexRunner to stop.
 *      String lastProcessed = arg;
 *      for (Contract c : contracts) {
 *          // Do processing
 *          lastProcessed = c.ContractNumber;
 *      }
 *      commit;
 *      return lastProcessed
 *   }
 * }
 * </pre>
 * 
 * Then, ApexRunner will repeatedly call this method returning the value returned from the webmethod as the argument to
 * the next method. Any exception will be printed out to System.err NOTE: You should call this with an API-only user
 * with a rather large session timeout. We should probably handle password timeout and reconnect, but that's a pain.
 * 
 * @author stamm
 * @since 146.prov
 */
public class ApexRunner {
    // The name of the argument we're supposed to call
    private static final String ARG_NAME = "arg";
    // The base API endpoint for wsdl packages
    private static final String PACKAGE_API_ENDPOINT = "/services/Soap/class/";
    // The base anames
    private static final String BASE_PACKAGE_NS_URI = "http://soap.sforce.com/schemas/class/";

    // How often we will try to "relogin" to the server
    private static final long LOGIN_INTERVAL = 15 * 60 * 1000; // 25 minutes

    private static void printHelp() {
        System.out.println("\nApexRunner -u <user> -p <pw> -l <org url> -m <webmethod> [-s <start>] [-d <level]");
        System.out.println("       -d level = Set the debugging level to level");
        System.out.println("                  Allowed levels are None, Debugonly, Db, and Profiling\n");

    }

    public static void main(String args[]) throws Exception {
        try {

            OptionParser parser = new OptionParser("u:p:l:m:d:h:");
            parser.accepts("s").withOptionalArg();
            parser.accepts("nc");
            OptionSet options = parser.parse(args);

            if (options.wasDetected("h")
                    || !(options.wasDetected("u") && options.wasDetected("p") && options.wasDetected("l") && options
                            .wasDetected("m"))) {
                // Print help
                printHelp();
                System.exit(-1);
            }

            String username = options.argumentOf("u");
            String password = options.argumentOf("p");
            String loginEndpointUrl = options.argumentOf("l") + Config.LOCATION_URL;
            String method = options.argumentOf("m");
            String start = options.wasDetected("s") ? options.argumentOf("s") : null;
            String debug = options.wasDetected("d") ? options.argumentOf("d") : null;

            ApexRunner l = new ApexRunner();
            l.runWebMethod(username, password, method, loginEndpointUrl, start, debug, System.out);

        } catch (Exception x) {
            printHelp();
            throw x;
        }
    }

    private PrintStream output;

    private void out(String str) {
        if (this.output != null) output.println(str);
    }

    public void runWebMethod(String username, String password, String method, String loginEndpointUrl,
            String startArgument, String debug, PrintStream output) throws ConnectionException, IOException {
        this.output = output;
        out("Apex Webmethod Runner");

        LogType logtype = debug == null ? null : LogType.valueOf(debug);

        // Convert to slash notation and separate method from namespace and package
        String realMethod = method.replace('.', '/');
        int slashLoc = realMethod.lastIndexOf('/');
        String pkg = realMethod.substring(0, slashLoc);
        realMethod = realMethod.substring(slashLoc + 1);

        // Login to the partner api to get a session id
        out("Connecting as " + username + " to " + loginEndpointUrl + "\n");
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setAuthEndpoint(loginEndpointUrl);
        new PartnerConnection(config);
        String sessionId = config.getSessionId();

        URL apexEndPoint = new URL(new URL(config.getAuthEndpoint()), PACKAGE_API_ENDPOINT + pkg);
        long startTime = System.currentTimeMillis();
        long nextLogin = startTime + LOGIN_INTERVAL;
        boolean keepGoing = true;
        String lastSuccessful = startArgument;
        int passes = 0;
        while (keepGoing) {
            // Need to keep track of timeouts.
            if (System.currentTimeMillis() > nextLogin) {
                config.setSessionId(null);
                new PartnerConnection(config);
                sessionId = config.getSessionId();
                nextLogin = System.currentTimeMillis() + LOGIN_INTERVAL;
            }
            out("Calling method " + method + " with " + (lastSuccessful == null ? "nothing" : lastSuccessful));
            passes++;
            try {
                lastSuccessful = callMethod(apexEndPoint, sessionId, pkg, realMethod, logtype, lastSuccessful);
            } catch (ParserConfigurationException ex) {
                throw new RuntimeException(ex);
            } catch (SAXException ex) {
                System.err.println("Couldn't parse response from " + apexEndPoint);
                throw new RuntimeException(ex);
            }
            if (lastSuccessful == null) {
                // We're done
                keepGoing = false;
                break;
            }
        }

        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        out("Finished " + passes + " passes in " + totalTime + " seconds");
    }

    /**
     * Create the SOAP envelope and body header for making an apex webmethod call Use the SoapXmlWriter because it's so
     * much easier. This should switch to something far more hard-coded if we want to use this anywhere else
     * 
     * @param sessionId
     *            the session ID for the user
     * @param pkg
     *            the name of the package we're calling
     */
    private SoapXmlWriter getEnvelopeWithSessionHeader(String sessionId, String pkg, LogType logtype)
            throws IOException {
        SoapXmlWriter w = new SoapXmlWriter(null);
        shared.xml.Namespace sfdc = this.declarePackageNamespace(w, pkg, null);
        w.writeStartDocument();
        w.writeStartElement(w.getEnvelopeNamespace(), "Envelope");
        w.writeStartElement(w.getEnvelopeNamespace(), "Header");
        w.writeStartElement(sfdc, "SessionHeader");
        w.writeElementString(sfdc, "sessionId", sessionId);
        w.writeEndElement();
        if (logtype != null) {
            w.writeStartElement(sfdc, "DebuggingHeader");
            w.writeStartElement(sfdc, "debugLevel");
            w.writeXsiType(sfdc, "LogType");
            w.writeText(logtype.name());
            w.writeEndElement();
            w.writeEndElement();
        }
        w.writeEndElement();
        w.writeStartElement(w.getEnvelopeNamespace(), "Body");
        return w;
    }

    // Declare the base namespace with the given prefix. Called
    private shared.xml.Namespace declarePackageNamespace(SoapXmlWriter w, String pkg, String prefix) throws IOException {
        return w.declareNamespace(BASE_PACKAGE_NS_URI + pkg, prefix);
    }

    // we need to do this by handle because WSC incorrectly maps nillable types to their lower case java type
    private String callMethod(URL server, String sessionId, String pkg, String method, LogType logtype, String start)
            throws IOException, SAXException, ParserConfigurationException {
        // Create the soap request
        SoapXmlWriter w = getEnvelopeWithSessionHeader(sessionId, pkg, logtype);
        shared.xml.Namespace sfdc = declarePackageNamespace(w, pkg, null);
        w.writeStartElement(sfdc, method);
        writeString(w, sfdc, ARG_NAME, start);
        w.writeEndDocument();
        String xsiNamespace = w.getXsiNamesspace().namespaceURI;

        // Connect to the endpoint
        HttpURLConnection conn = (HttpURLConnection)server.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("SOAPAction", "\"\"");
        conn.setRequestProperty("Content-Type", "text/xml");
        conn.connect();

        try {
            // Send the soap request
            w.copyToStream(conn.getOutputStream());
            conn.getOutputStream().flush();

            // Establish the DOM reader
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();

            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                // Parse the standard response for the "result" tag.
                Document d = db.parse(conn.getInputStream());
                // See if there's any debugging info
                if (logtype != null) {
                    NodeList nl = d.getElementsByTagName("debugLog");
                    Element e = (Element)nl.item(0);
                    if (e != null) {
                        if (!e.hasAttributeNS(xsiNamespace, "nil")) {
                            System.out.println(e.getTextContent());
                        }
                    }
                }
                NodeList nl = d.getElementsByTagName("result");
                Element e = (Element)nl.item(0);
                if (e.hasAttributeNS(xsiNamespace, "nil")) {
                    return null;
                } else {
                    return e.getTextContent();
                }
            } else {
                // Parse the error stream to retrieve the fault and detail streams.
                Document d = db.parse(conn.getErrorStream());
                NodeList nl = d.getElementsByTagName("faultstring");
                if (nl.getLength() > 0) {
                    Element e = (Element)nl.item(0);
                    System.err.println("Error found:");
                    System.err.println(e.getTextContent());
                }
                nl = d.getElementsByTagName("detail");
                if (nl.getLength() > 0) {
                    Element e = (Element)nl.item(0);
                    System.err.println("Error details:");
                    System.err.println(e.getTextContent());
                }
                System.exit(-1);
                throw new AssertionError("System.exit lies."); // Unreachable.
            }
        } finally {
            // Be a nice citizen for now and not hold a thread indefinitely.
            conn.disconnect();
        }
    }

    // A version of writeElementString() that does the xsi:nil thing.
    private void writeString(SoapXmlWriter w, shared.xml.Namespace ns, String name, String v1) throws IOException {
        w.writeStartElement(ns, name);
        if (v1 == null)
            w.writeAttribute(w.getXsiNamesspace(), "nil", "true");
        else {
            w.writeText(v1);
        }
        w.writeEndElement();
    }

}
