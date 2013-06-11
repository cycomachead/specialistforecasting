package test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis.encoding.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import test.runner.Globals;

import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.fault.InvalidFieldFault;
import com.sforce.soap.partner.fault.InvalidIdFault;
import com.sforce.soap.partner.fault.InvalidSObjectFault;
import com.sforce.soap.partner.fault.MalformedQueryFault;
import com.sforce.soap.partner.fault.UnexpectedErrorFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class ComergentTestingUtil {

    private static LoginResult lr = null;

    private static Properties props = null;

    private static PartnerConnection stub = null;

    private boolean ref = false;

    private String xmlPreTag = null;

    private String xmlPostTag = null;

    private String xmlPreSeperator = "<![CDATA[";

    private String xmlPostSeperator = "]]>";

    private final String propertiesFilePath = "..\\..\\build\\comergentTesting.properties";

    File tempBomFile = new File(Globals.ROOT_DIR+"\\BOM\\bom.xml");

    private Object lock = new Object();

    /**
     * saves the tags before the configurator xml
     *
     * @param s
     */
    public void setXmlPreTag(String s) {
        xmlPreTag = s;
    }

    /**
     * saves the tags after the configurator xml
     *
     * @param s
     */
    public void setXmlPostTag(String s) {
        xmlPostTag = s;
    }

    /**
     * returns the tags before the configurator xml
     *
     * @return
     */
    public String getXmlPreTag() {
        return xmlPreTag;
    }

    /**
     * returns the tags after the configurator xml
     *
     * @return
     */
    public String getXmlPostTag() {
        return xmlPostTag;
    }


    /**
     * Saves the configurator xml part of the whole attachment as bom.xml
     *
     * @param message--
     *            the attachment from sforce
     */
    public void getBomFile(String message) {
        try {
            String decodedMessage = new String(Base64.decode(message));
            setXmlPreTag(decodedMessage
                    .substring(0, decodedMessage.indexOf(xmlPreSeperator) + xmlPreSeperator.length()));
            decodedMessage = decodedMessage.substring(decodedMessage.indexOf(xmlPreSeperator)
                    + xmlPreSeperator.length());
            setXmlPostTag(decodedMessage.substring(decodedMessage.indexOf(xmlPostSeperator)));
            decodedMessage = decodedMessage.substring(0, decodedMessage.indexOf(xmlPostSeperator));

            synchronized (lock) {
                FileOutputStream fstream = new FileOutputStream(tempBomFile);
                OutputStreamWriter out = new OutputStreamWriter(fstream);
                out.write(decodedMessage); // Close the output stream
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Error in getBomFile(): " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Converts the updated bom file to a string which can be now stored as an
     * attachment in sforce
     *
     * @return the entire attachment to be stored in sforce
     */
    public String setBomFile() {
        String uncodedMessage = "";
        try {
            synchronized (lock) {

                FileReader myFile = new FileReader(tempBomFile);
                BufferedReader in = new BufferedReader(myFile);
                String line = "";
                while ((line = in.readLine()) != null) {
                    uncodedMessage += line + "\n";
                }
                // Close the input stream
                in.close();
            }
            uncodedMessage = getXmlPreTag() + uncodedMessage + getXmlPostTag();

        } catch (IOException e) {
            System.out.println("Error in setBomFile(): " + e.getMessage());
            e.printStackTrace();
        }
        return uncodedMessage;

    }

    public void changeAttribute(String name, String attributeName, String value) {
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc;
            synchronized (lock) {
                doc = docBuilder.parse(tempBomFile);

            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfLineItems = doc.getElementsByTagName("LineItem");
            int found = 0;
            for (int s = 0; s < listOfLineItems.getLength(); s++) {
                Node lineItemNode = listOfLineItems.item(s);
                if (lineItemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element lineItemElement = (Element)lineItemNode;
                    if (lineItemElement.getAttribute("Name").startsWith(name)) {
                        lineItemElement.setAttribute(attributeName, value);
                        found++;
                        // break;
                    }
                }// end of if clause
            }// end of for loop with s var

            if (found > 1) {
                System.out.println("Attribute found more than once. Fail Test");
            }

          //  synchronized (lock) {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                // initialize StreamResult with File object to save to file
                StreamResult result = new StreamResult(new FileWriter(tempBomFile));
                DOMSource source = new DOMSource(doc);
                transformer.transform(source, result);
            }

        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        } catch (Throwable t) {
            t.printStackTrace();
        }
        // System.exit (0);
    }

    /**
     *
     * @param quoteName:
     *            the quote who's id is required
     * @return The 15-length id for the quote specified
     */
    public String getQuoteId(String quoteName) {
        SObject quote = new SObject();
        try {
            QueryResult qr = stub.query("select id from sfquote__Quote__c where sfquote__Quote_Name__c = '" + quoteName + "'");
            quote = qr.getRecords()[0];
            return quote.getId().substring(0, 15);
        } catch (InvalidFieldFault e) {
            System.out.println("getQuoteId failed :" + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (MalformedQueryFault e) {
            System.out.println("getQuoteId failed :" + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (InvalidSObjectFault e) {
            System.out.println("getQuoteId failed :" + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (UnexpectedErrorFault e) {
            System.out.println("getQuoteId failed :" + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (ConnectionException e) {
            System.out.println("getQuoteId failed :" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gets the BOM from the attachment and stores the configurator xml from it
     * in bom.xml
     *
     * @param quoteId-
     *            The id of the quote with which the BOM file is attached
     */
    public void getBOM(String quoteId) {
        try {
            SObject attachment = new SObject();
            // get attachment
            QueryResult qr = stub.query("Select Id,Body from Attachment where ParentId ='" + quoteId
                    + "' and Name='CONFIG_BOM'");
            attachment = qr.getRecords()[0];
            // turn the attachment into bom.xml -- the xml of the configurator
            // part
            getBomFile(attachment.getField("Body").toString());
        } catch (InvalidFieldFault e) {
            System.out.println("getBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (MalformedQueryFault e) {
            System.out.println("getBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (InvalidSObjectFault e) {
            System.out.println("getBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (UnexpectedErrorFault e) {
            System.out.println("getBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (ConnectionException e) {
            System.out.println("getBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        }

    }

    /**
     * update the attachment for the quote with the new BOM
     *
     * @param quoteID-
     *            The id of the quote with which the BOM file is to be attached
     */
    public void setBOM(String quoteID) {
        try {
            SObject attachment = new SObject();
            attachment.setType("Attachment");
            // get Id of the existing attachment that needs to be replaced
            QueryResult qr = stub.query("Select Id,Body from Attachment where ParentId ='" + quoteID
                    + "' and Name='CONFIG_BOM'");
            // set the update elements Id to the queried id
            attachment.setId(qr.getRecords()[0].getId());

            // add to the new attachment
            attachment.setField("Body", setBomFile().getBytes());
            // update old attachment
            stub.update(new SObject[] { attachment });
        } catch (InvalidIdFault e) {
            System.out.println("setBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (InvalidFieldFault e) {
            System.out.println("setBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (MalformedQueryFault e) {
            System.out.println("setBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (InvalidSObjectFault e) {
            System.out.println("setBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (UnexpectedErrorFault e) {
            System.out.println("setBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (ConnectionException e) {
            System.out.println("setBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            System.out.println("setBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        }

    }

    /**
     * Initializes the Quote with the original right super bom
     *
     * @param quoteID
     */
    public void setSuperBOM(String quoteID, String filename) {
        try {
            SObject attachment = new SObject();
            attachment.setType("Attachment");
            // get Id of the existing attachment that needs to be replaced
            QueryResult qr = stub.query("Select Id,Body from Attachment where ParentId ='" + quoteID
                    + "' and Name='CONFIG_BOM'");
            if (qr.getSize() == 0) {
                String fileMessage = "";
                FileReader myFile = new FileReader(filename);
                BufferedReader in = new BufferedReader(myFile);
                String line = "";
                while ((line = in.readLine()) != null) {
                    fileMessage += line + "\n";
                }
                // add to the new attachment
                attachment.setField("Body", new String(Base64.decode(fileMessage)).getBytes());
                attachment.setField("ParentId", quoteID);
                attachment.setField("Name", "CONFIG_BOM");
                // update old attachment
                stub.create(new SObject[] { attachment });
            } else {
                // set the update elements Id to the queried id
                attachment.setId(qr.getRecords()[0].getId());

                String fileMessage = "";
                FileReader myFile = new FileReader(filename);
                BufferedReader in = new BufferedReader(myFile);
                String line = "";
                while ((line = in.readLine()) != null) {
                    fileMessage += line + "\n";
                }
                // create new message of the bom
                attachment.setField("Body", new String(Base64.decode(fileMessage)).getBytes());
                // update old attachment
                stub.update(new SObject[] { attachment });
            }
        } catch (InvalidIdFault e) {
            System.out.println("setSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (InvalidFieldFault e) {
            System.out.println("setSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (MalformedQueryFault e) {
            System.out.println("setSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (InvalidSObjectFault e) {
            System.out.println("setSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (UnexpectedErrorFault e) {
            System.out.println("setSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (ConnectionException e) {
            System.out.println("setSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (FileNotFoundException e) {
            System.out.println("setSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.out.println("setSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            System.out.println("setSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    /**
     * Method to store the superbom in the folder structure
     *
     * @param quoteID
     */
    public synchronized void getSuperBOM(String quoteID, String filename) {
        try {
            SObject attachment = new SObject();
            // get attachment
            QueryResult qr = stub.query("Select Id,Body from Attachment where ParentId ='" + quoteID
                    + "' and Name='CONFIG_BOM'");
            attachment = qr.getRecords()[0];
            FileOutputStream fstream = new FileOutputStream(filename);
            OutputStreamWriter out = new OutputStreamWriter(fstream);
            out.write(attachment.getField("Body").toString());
            // Close the output stream
            out.close();

        } catch (InvalidFieldFault e) {
            System.out.println("getSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (MalformedQueryFault e) {
            System.out.println("getSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (InvalidSObjectFault e) {
            System.out.println("getSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (UnexpectedErrorFault e) {
            System.out.println("getSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (RemoteException e) {
            System.out.println("getSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            System.out.println("getSuperBOM failed :" + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    /**
     * loads the properties from user.properties file
     *
     * @return the properties that have been read
     * @throws IOException
     *             when not able to read the file
     */
    public Properties loadProperties() throws IOException {
        props = new Properties();
        InputStream is = ComergentTestingUtil.class.getClassLoader().getResourceAsStream(propertiesFilePath);
        props.load(is);
        is.close();
        return props;
    }

    public synchronized void doLogin() throws Exception {
        if (!ref) {
            props = loadProperties();
            stub = getStub(props.getProperty("AppServer"));
            lr = stub.login(props.getProperty("Username"), props.getProperty("Password"));
            stub.getConfig().setServiceEndpoint(lr.getServerUrl());
            stub.setSessionHeader(lr.getSessionId());
            ref=true;
        }
    }

    public String getSessionId() {
        return lr.getSessionId();
    }

    private PartnerConnection getStub(String authEndpoint) throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(authEndpoint);
        config.setServiceEndpoint(authEndpoint);
        config.setUsername(props.getProperty("Username"));
        config.setPassword(props.getProperty("Password"));
        config.setManualLogin(true);
        return Connector.newConnection(config);
    }

    public PartnerConnection getConnection() throws Exception {
        doLogin();
        return stub;
    }

}
