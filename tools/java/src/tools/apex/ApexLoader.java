/*
 * Copyright, 1999-2006, SALESFORCE.com All Rights Reserved Company Confidential
 */

package tools.apex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import shared.util.TextUtil;
import tools.util.Config;

import com.sforce.soap.apex.*;
import com.sforce.soap.partner.sobject.wsc.SObject;
import com.sforce.soap.partner.wsc.Error;
import com.sforce.soap.partner.wsc.PartnerConnection;
import com.sforce.soap.partner.wsc.QueryResult;
import com.sforce.soap.partner.wsc.UpsertResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

/**
 * Apex incremental compilation
 *
 * @author cweissman
 * @since 144
 */
public class ApexLoader {

    private static final String TRIGGER = "trigger";
    private static final String PACKAGE = "package";
    private static final String CLASS = "class";
    private static final String PAGE = "dummypage";
    private static final String SUFFIX_CLASS = ".cls";
	private static final String SUFFIX_TRIGGER = ".trigger";
    public static final String DUMMY_MARKUP = "<apex:page>dummy</apex:page>";

    public static void main(String args[]) throws Exception {
        try {
            ApexLoader l = new ApexLoader();
            StringBuilder target = new StringBuilder(128);
            LogType logType = LogType.None;
            boolean forceload = false;
            if (args.length > 5) {
                target.append(args[5]); //
                if (args.length > 6) {
                    try {
                        logType = LogType.valueOf(args[6]);
                        if (args.length > 7) {
                            forceload = args[7].equalsIgnoreCase("forceload");
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid log type : " + args[5] + " valid values: "
                                + LogType.values().toString());
                    }
                }
            } else {
                target.append("http://localhost");
            }
            target.append(Config.LOCATION_URL);

            l.loadProject(args[0], args[1], true, args[2], args[3], args[4], target.toString(), logType, System.out, forceload);
        } catch (Exception x) {
            System.err
					.println("\nApexLoader <user> <pw> <apex source dir> <manifest dir> <apex manifest file> [server url, defaults to http://localhost] [debug level ("
                            + TextUtil.arrayToString(LogType.values()) + "), defaults to '" + LogType.None.toString() + "'] [forceload, defaults to noforce]\n");
            throw x;
        }
    }

    private PrintStream output;

    private void out(String str) {
        if (this.output != null) output.println(str);
    }

    public void loadProject(String username, String password, boolean withExceptions, String projectDirectory, String manifestDirectory,
            String manifestFilename, String loginEndpointUrl, LogType logType, PrintStream output, boolean forceload)
            throws ConnectionException, IOException {
        this.output = output;
        out("Differential Apex Class Loader");
        long start = System.currentTimeMillis();
        // Login to the Api
        out("Connecting as " + username  + " to " + loginEndpointUrl + "\n");
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setAuthEndpoint(loginEndpointUrl);
        PartnerConnection con = new PartnerConnection(config);

        // get an Apex stub
        ConnectorConfig apexCfg = new ConnectorConfig();
        apexCfg.setSessionId(con.getSessionHeader().getSessionId());
        apexCfg.setServiceEndpoint(config.getAuthEndpoint().replace("/u/", "/s/"));
        com.sforce.soap.apex.SoapConnection apex = Connector.newConnection(apexCfg);
        
		LogInfo[] logInfoArray = new LogInfo[1];

		LogInfo logInfo = new LogInfo();

		logInfo.setCategory(LogCategory.All);

		logInfoArray[0] = logInfo;
		
		// Set debug log level
        apex.setDebuggingHeader(logInfoArray, logType);

        // Read the existing metadata info for classes and triggers
        Map<String, Double> classes = new HashMap<String, Double>();
        QueryResult r = con.query("select bodycrc, name from ApexClass where isValid = true");
        if (r.getSize() > 0) {
            for (SObject s : r.getRecords()) {
                classes.put((String)s.getField("Name"), Double.parseDouble((String)s.getField("BodyCrc")));
            }
        }
        Map<String, Double> triggers = new HashMap<String, Double>();
        r = con.query("select bodycrc, name from ApexTrigger where isValid = true");
        if (r.getSize() > 0) {
            for (SObject s : r.getRecords()) {
                triggers.put((String)s.getField("Name"), Double.parseDouble((String)s.getField("BodyCrc")));
            }
        }
		
        File dir = new File(projectDirectory);
		File manifestDir = new File(manifestDirectory);
        File manifestFile = new File(manifestDir, manifestFilename);
        BufferedReader manifestFileRdr = new BufferedReader(new FileReader(manifestFile));

        List<String> classNamesToCompile = new ArrayList<String>();
        List<String> classesToCompile = new ArrayList<String>();
        List<String> triggerNamesToCompile = new ArrayList<String>();
        List<String> triggersToCompile = new ArrayList<String>();
        List<String> pagesToCreate = new ArrayList<String>();
        String nextline;
        while ((nextline = manifestFileRdr.readLine()) != null) {
            if (nextline.startsWith(PACKAGE) || nextline.startsWith(CLASS)) {
                String className = nextline
                        .substring((nextline.startsWith(PACKAGE) ? PACKAGE.length() : CLASS.length()) + 1);
                File sourceFile = new File(dir, className + SUFFIX_CLASS);
                String script = getFileContents(sourceFile);
                Double crc = new Double(getCrc(script));
                Double existingCrc = classes.get(className);
                if (forceload || !crc.equals(existingCrc)) {
                    classNamesToCompile.add(className);
                    classesToCompile.add(script);
                }
            } else if (nextline.startsWith(TRIGGER)) {
                String triggerName = nextline.substring(TRIGGER.length() + 1);
                File sourceFile = new File(dir, triggerName + SUFFIX_TRIGGER);
                String script = getFileContents(sourceFile);
                Double crc = new Double(getCrc(script));
                Double existingCrc = triggers.get(triggerName);
                if (forceload || !crc.equals(existingCrc)) {
                    triggerNamesToCompile.add(triggerName);
                    triggersToCompile.add(script);
                }
            } else if (nextline.startsWith(PAGE)) {
                String pageName = nextline.substring(PAGE.length() + 1);
                pagesToCreate.add(pageName);
            }
        }

        // find existing pages if there're any to be loaded
        if (pagesToCreate.size() > 0) {
            upsertDummyPages(pagesToCreate, withExceptions, con);
        }

        // Compile all classes at once
        CompileAndTestRequest request = new CompileAndTestRequest();
        boolean evenBother = false;
        if (classesToCompile.size() > 0) {
            out("Compiling class(s): " + collectionToString(classNamesToCompile));
            request.setClasses(classesToCompile.toArray(new String[classesToCompile.size()]));
            evenBother = true;
        }
        if (triggersToCompile.size() > 0) {
            out("Compiling trigger(s): " + collectionToString(triggerNamesToCompile));
            request.setTriggers(triggersToCompile.toArray(new String[triggersToCompile.size()]));
            evenBother = true;
        }

        if (evenBother) {
            CompileAndTestResult catr = apex.compileAndTest(request);

            if (!catr.isSuccess()) {
                if (classesToCompile.size() > 0) {
                    for (int i = 0; i < catr.getClasses().length; i++) {
                        CompileClassResult result = catr.getClasses()[i];
                        if (!result.isSuccess()) {
                            String msg = "Compilation failure for " + classNamesToCompile.get(i) + "\n\nline "
                                    + result.getLine() + ", column " + result.getColumn() + ": " + result.getProblem();
                            if (withExceptions) throw new RuntimeException(msg);
                            out(msg);
                        }
                    }
                }
                if (triggersToCompile.size() > 0) {
                    for (int i = 0; i < catr.getTriggers().length; i++) {
                        CompileTriggerResult result = catr.getTriggers()[i];
                        if (!result.isSuccess()) {
                            String msg = "Compilation failure for " + triggerNamesToCompile.get(i) + "\n\nline "
                                    + result.getLine() + ", column " + result.getColumn() + ": " + result.getProblem();
                            if (withExceptions) throw new RuntimeException(msg);
                            out(msg);
                        }
                    }
                }
                // OK, next try runtests
                if (catr.getRunTestsResult() != null) {
                    RunTestsResult res = catr.getRunTestsResult();
                    System.out.println("Number of tests: " + res.getNumTestsRun());
                    System.out.println("Number of failures: " + res.getNumFailures());
                    if (res.getNumFailures() > 0) {
                        for (RunTestFailure rtf : res.getFailures()) {
                            String msg = "Failure: "
                                    + (rtf.getNamespace() == null ? "" : rtf.getNamespace() + ".") + rtf.getName()
                                    + "." + rtf.getMethodName() + ": " + rtf.getMessage() + "\n" + rtf.getStackTrace();
                            if (withExceptions) throw new RuntimeException(msg);
                            out(msg);
                        }
                    }
                    if (res.getCodeCoverageWarnings() != null) {
                        for (CodeCoverageWarning ccw : res.getCodeCoverageWarnings()) {
                            String msg = "Failure: "
                                + (ccw.getNamespace() == null ? "" : ccw.getNamespace() + ".") + ccw.getName()
                                + ": " + ccw.getMessage();
                            if (withExceptions) throw new RuntimeException(msg);
                            out(msg);
                        }
                    }
                }
            }
        }

        int filesLoaded = classesToCompile.size() + triggersToCompile.size();
        out("\nLoaded " + filesLoaded + " files in " + (System.currentTimeMillis() - start) / 1000 + " seconds");
    }

    /**
     * @param pagesToCreate
     * @param withExceptions
     * @param con
     * @throws ConnectionException
     */
    private void upsertDummyPages(List<String> pagesToCreate, boolean withExceptions, PartnerConnection con)
            throws ConnectionException {
        QueryResult r;
        Map<String, SObject> pages = new HashMap<String, SObject>();
        r = con.query("select  Id, Name, Markup from ApexPage");
        if (r.getSize() > 0) {
            for (SObject s : r.getRecords()) {
                s.setId(s.getId());
                pages.put((String)s.getField("Name"), s);
            }
        }

        // populate SObjects needed to create pages
        List<SObject> pageSobjs = new ArrayList<SObject>();
        for (String pageName : pagesToCreate) {
            SObject s = new SObject();
            if (! pages.containsKey(pageName)) {
                // create page so that apex code can find it
                s.setType("ApexPage");
                s.setField("Name", pageName);
                s.setField("MasterLabel", pageName);
                s.setField("Markup", DUMMY_MARKUP);

                pageSobjs.add(s);
            }
        }

        // Preload pages on which apex code has dependency
        out("Upserting dummy pages: " + sobjCollectionToString(pageSobjs));
        UpsertResult[] results = con.upsert("Id", pageSobjs.toArray(new SObject[pageSobjs.size()]));
        for (int i = 0; i < results.length; i++) {
            if (!results[i].isSuccess()) {
                Error err = results[i].getErrors()[0];
                String msg = ("Couldn't upsert " + pagesToCreate.get(i) + "\nField(s): "
                        + TextUtil.arrayToString(err.getFields()) + "\nError: " + err.getMessage());
                if (withExceptions) throw new RuntimeException(msg);
                out(msg);
            }
        }
    }

    private static Long getCrc(String body) {
        CRC32 crc = new CRC32();
        try {
            crc.update(body.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // /CLOVER:OFF
            throw new RuntimeException(e);
            // /CLOVER:ON
        }
        return crc.getValue();
    }

    private String collectionToString(Collection<String> c) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Object o : c) {
            if (i++ > 0) sb.append(", ");
            sb.append(o);
        }
        return sb.toString();
    }

    private String sobjCollectionToString(Collection<SObject> c) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (SObject o : c) {
            if (i++ > 0) sb.append(", ");
            sb.append(o.getField("Name"));
        }
        return sb.toString();
    }

    /**
     * @return the contents of the given text file, as a string
     */
    private static String getFileContents(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
        return trim(sb.toString());
    }

    private static String trim(String str) {
        if (str == null) return null;

        int start = 0;
        int end = str.length();
        char c;
        while ((start < end) && ((c = str.charAt(start)) <= ' ' || Character.isWhitespace(c))) {
            start++;
        }
        while ((start < end) && ((c = str.charAt(end - 1)) <= ' ' || Character.isWhitespace(c))) {
            end--;
        }

        return ((start > 0) || (end < str.length())) ? str.substring(start, end) : str;
    }

}
