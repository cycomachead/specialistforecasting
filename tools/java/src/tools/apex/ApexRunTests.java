/*
 * Copyright, 1999-2005, SALESFORCE.com All Rights Reserved Company Confidential
 */

package tools.apex;

import com.sforce.soap.apex.*;
import com.sforce.soap.partner.wsc.PartnerConnection;
import com.sforce.ws.ConnectorConfig;
import org.dom4j.*;
import org.dom4j.io.*;

import tools.util.Config;

import java.util.Properties;
import java.io.*;
import java.text.NumberFormat;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;


/**
 * @author cweissman
 * @since 144
 */
public class ApexRunTests {
    private static Document createXmlResult(RunTestsResult res) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("testsuite").addAttribute("errors", "0").addAttribute("failures",
                Integer.toString(countTestFailures(res))).addAttribute("tests", Integer.toString(res.getNumTestsRun()))
                .addAttribute("name", "apex");

        // Store the properties
        Properties properties = System.getProperties();
        Element propertiesElement = root.addElement("properties");
        for (Object key : properties.keySet()) {
            propertiesElement.addElement("property").addAttribute("name", (String)key).addAttribute("value",
                    properties.getProperty((String)key));
        }

        // Store the test failures
        for (RunTestFailure rtf : res.getFailures()) {
            String classname = (rtf.getNamespace() == null ? "" : rtf.getNamespace() + ".") + rtf.getName();
            String methodname = rtf.getMethodName();

            Element testcase = root.addElement("testcase")
                    .addAttribute("classname", classname != null ? classname : "").addAttribute("name",
                            methodname != null ? methodname : "");

            Element failure = testcase.addElement("failure").addAttribute("message", rtf.getMessage());

            // Construct the failure text.
            StringBuffer failureText = new StringBuffer();
            if (rtf.getMessage() != null) {
                failureText.append(rtf.getMessage());
            }
            if (rtf.getStackTrace() != null) {
                failureText.append("\n");
                failureText.append(rtf.getStackTrace());
            }
            if (failureText.length() > 0) {
                failure.addText(failureText.toString());
            }
        }
	
      	// Store the test successes              
        for (RunTestSuccess rts : res.getSuccesses()) {
            String classname = (rts.getNamespace() == null ? "" : rts.getNamespace() + ".") + rts.getName();
            String methodname = rts.getMethodName();

            Element testcase = root.addElement("testcase")
                    .addAttribute("classname", classname != null ? classname : "").addAttribute("name",
                            methodname != null ? methodname : "");
            Element success = testcase.addElement("Success").addAttribute("message", "Passed");    
                                
         }
        
        return document;
    }

    /**
     * Counts the number of test failures.
     */
    private static int countTestFailures(RunTestsResult res) {
        int failureCount = 0;

        for (RunTestFailure failure : res.getFailures()) {
            if (failure.getMethodName() != null) ++failureCount;
        }

        return failureCount;
    }

    private static void printHelp() {
        System.out.println("\nApexRunTests -u <user> -p <pw> -l <org url> [-nc] [-k <pkg> -n <ns> -o <xml file>]");
        System.out.println("       -nc = Don't display coverage results");
        System.out.println("       -nt = Don't display coverage results for triggers");
        System.out.println("       -ps = Proxy String consisting of proxyHost:proxyPort");
        System.out.println("       -v = Verbose (Use profile debugging)");
        System.out.println("       -d level = Set the debugging level to level");
        System.out.println("                  Allowed levels are None, Debugonly, Db, and Profiling\n");

    }

    public static void main(String args[]) throws Exception {
		final String COVERAGE_RESULTS_XML_FILE="codeCovData.xml";
        OptionParser parser = new OptionParser("u:p:l:k:n:o:h:d:x:v");
        parser.accepts("nc");
        parser.accepts("nt");
        parser.accepts("ps").withRequiredArg().ofType(String.class);        
        OptionSet options = parser.parse(args);
        
        if (options.wasDetected("h")
                || !(options.wasDetected("u") && options.wasDetected("p") && options.wasDetected("l"))) {
            // Print help
            printHelp();
            System.exit(-1);
        }

        String username = options.argumentOf("u");
        String password = options.argumentOf("p");
        String proxyString = options.wasDetected("ps") ? options.argumentOf("ps"):null;
        String loginEndpointUrl = options.argumentOf("l") + Config.LOCATION_URL;       
        String pkg = options.wasDetected("k") ? options.argumentOf("k") : null;
        String ns = options.wasDetected("n") ? options.argumentOf("n") : null;
        String xmlOutfile = options.wasDetected("o") ? options.argumentOf("o") : null;
        boolean nocov = options.wasDetected("nc");
        boolean notriggercov = options.wasDetected("nt");
        String debug = options.wasDetected("d") ? options.argumentOf("d") : null;
        boolean profile = options.wasDetected("v");
		String folderForDataFiles = options.wasDetected("x") ?options.argumentOf("x"):null;
		XMLWriter xmlWriter = null;
		XMLWriter reswriter=null;
        try {
            System.out.println("Apex RunTests");
            if(folderForDataFiles.equals("NULL"))folderForDataFiles=null;

            // Login to the Api
            System.out.println("Connecting as " + username + " to " + loginEndpointUrl + "\n");
            ConnectorConfig config = new ConnectorConfig();
            config.setUsername(username);
            config.setPassword(password);
            // set proxy in connectorConfig.  Will only set if the proxyString is not null
            setProxy(proxyString, config);
            config.setAuthEndpoint(loginEndpointUrl);
            PartnerConnection con = new PartnerConnection(config);
            // get an Apex stub
            ConnectorConfig apexCfg = new ConnectorConfig();
            apexCfg.setSessionId(con.getSessionHeader().getSessionId());
            apexCfg.setServiceEndpoint(config.getAuthEndpoint().replace("/u/", "/s/"));
            com.sforce.soap.apex.SoapConnection apex = Connector.newConnection(apexCfg);

			LogInfo[] logInfoArray = new LogInfo[1];

			LogInfo logInfo = new LogInfo();

			logInfo.setCategory(LogCategory.Apex_profiling);

			logInfoArray[0] = logInfo;

            if (profile)
                apex.setDebuggingHeader(logInfoArray, LogType.Profiling);
            else if (debug != null) {
                try {
                    apex.setDebuggingHeader(logInfoArray, LogType.valueOf(debug));
                } catch (IllegalArgumentException ex) {
                    printHelp();
                    System.exit(-1);
                }
            }

            long start = System.currentTimeMillis();
            RunTestsRequest rtr = new RunTestsRequest();
            if (pkg == null) {
                rtr.setAllTests(true);
            } else {
                rtr.setNamespace(ns);
                rtr.setClasses(new String[] { pkg });
            }

            RunTestsResult res = apex.runTests(rtr);
            if (apex.getDebuggingInfo() != null) {
                System.out.println(apex.getDebuggingInfo().getDebugLog());
            }

            System.out.println("Number of tests: " + res.getNumTestsRun());
            System.out.println("Number of failures: " + res.getNumFailures());
            if (res.getNumFailures() > 0) {
                for (RunTestFailure rtf : res.getFailures()) {
                    System.out.println("Failure: " + (rtf.getNamespace() == null ? "" : rtf.getNamespace() + ".")
                            + rtf.getName() + "." + rtf.getMethodName() + ": " + rtf.getMessage() + "\n"
                            + rtf.getStackTrace());
                }
            }

            NumberFormat percentInstance = NumberFormat.getPercentInstance();

            if (res.getCodeCoverage() != null) {
                int totalLocs = 0;
                int locsNotCovered = 0;
				Document doc = null;
				Element root = null;
				OutputFormat format = OutputFormat.createPrettyPrint();
				String resultsFile=folderForDataFiles+"/"+COVERAGE_RESULTS_XML_FILE;
				
				File file = new File(resultsFile);
				boolean fileExists = file.exists();
				SAXReader xmlReader = null;
				// If the results file exists, open and get the root node
				//if (fileExists && (file.length() > 0)) {
				if (!nocov) {
					if (fileExists ) {
						xmlReader = new SAXReader();
						doc = xmlReader.read(file);

						root = doc.getRootElement();
					} else {
						// We need a Document
						doc = DocumentHelper.createDocument();
	
						// create the root element and add it to the document
						root = doc.addElement("Results");
					}
				}
				                
                for (CodeCoverageResult ccr : res.getCodeCoverage()) {
                    if (notriggercov && "Trigger".equalsIgnoreCase(ccr.getType())) continue;
                    if (!nocov) {
                    	if(folderForDataFiles !=null) {
                    		xmlWriter = new XMLWriter(new FileWriter(resultsFile), format);
                    	}
/*
                        System.out.println("Code coverage for " + ccr.getType() + " "
                                + (ccr.getNamespace() == null ? "" : ccr.getNamespace() + ".") + ccr.getName() + ": "
                                + ccr.getNumLocationsNotCovered() + " locations not covered out of "
                                + ccr.getNumLocations());
*/
						int numLocationsNotCovered = new Integer(ccr
								.getNumLocationsNotCovered()).intValue();
						int totalLocations = new Integer(ccr.getNumLocations())
								.intValue();
						Set<Object> linesNotCoveredSet = new TreeSet<Object>();
						StringBuilder linesNotCovered = new StringBuilder();
                        
                        if (ccr.getNumLocationsNotCovered() > 0) {
							for (CodeLocation cl : ccr.getLocationsNotCovered()) {
								linesNotCovered.append(cl.getLine() + "  ,  ");
								linesNotCoveredSet.add(cl.getLine());
							}
							String linesNotCoveredString = linesNotCovered
									.toString();
							linesNotCoveredString = linesNotCoveredString
									.substring(0, linesNotCoveredString
											.lastIndexOf(",") - 1);
							if(folderForDataFiles!= null)
								ApexRunTests.writeXMLFile(ccr.getNamespace(),
									ccr.getName(), ccr.getNumLocations(), ccr
											.getNumLocationsNotCovered(),
									linesNotCoveredString, linesNotCoveredSet,
									folderForDataFiles, xmlWriter, resultsFile, doc, root);                            
                     
                       
                    }
						else {

								if(folderForDataFiles!= null)							
									ApexRunTests.writeXMLFile(ccr.getNamespace(),
									ccr.getName(), ccr.getNumLocations(), ccr
											.getNumLocationsNotCovered(),
									"", linesNotCoveredSet,
									folderForDataFiles, xmlWriter, resultsFile, doc, root);
						}
					}                        
                    totalLocs += ccr.getNumLocations();
                    locsNotCovered += ccr.getNumLocationsNotCovered();
                }
                if ( ( !nocov)&&(folderForDataFiles !=null) ) {
                	xmlWriter.write(doc);
                	xmlWriter.flush();
                }
                int locsCovered = totalLocs - locsNotCovered;
                double percent = totalLocs == 0 ? 0 : (((double)totalLocs - locsNotCovered) / totalLocs);
                System.out.println("Total code coverage: " + locsCovered + "/" + totalLocs + "  "
                        + percentInstance.format(percent) + "\n");
            }
            System.out.println("Finished in " + (System.currentTimeMillis() - start) + "ms");

            // Create XML output
            if (xmlOutfile != null) {
                Document document = createXmlResult(res);
                XMLWriter writer = new XMLWriter(new FileWriter(xmlOutfile));
                writer.write(document);
                writer.close();
            }

            // Set correct exit code
            if (res.getNumFailures() > 0) {
                System.exit(-1);
            }
        } catch (Exception x) {
            printHelp();
            throw x;
        }
		finally {
			try {
				if (xmlWriter != null) {
					xmlWriter.close();
				}
				if (reswriter != null)
					reswriter.close();
			} catch (Exception e) {
				System.out.println("Exception in closing resources :" + e);
			}
		}        
    }
	private static String getNameSpaceValue(String nameSpaceVal){
		return nameSpaceVal==null?"":nameSpaceVal;
	}
	public static void writeXMLFile(String namespace, String className,
			int numLocations, int numLocationsNotCovered,
			String linesNotCoveredList, Set<Object> linesNotCoveredNewSet, String folderForDataFiles, XMLWriter writer, String resultsFile, Document doc, Element root) {

		ObjectOutputStream out = null;
		FileOutputStream fos = null;
		
		try {
					
			// Form filename to use
			String classDataFileName = (namespace != null) ? folderForDataFiles+"/"+"PACK_"+namespace + "_"+"FILE_"
					+ className + ".data" : folderForDataFiles+"/"+className + ".data";

			File classDataFile = new File(classDataFileName);
			boolean classDataFileExists = classDataFile.exists();
			TreeSet<Object> lncOldSet = null;

			// Check if the data file exists already. If yes, read the data
			if (classDataFileExists) {
				FileInputStream fis = new FileInputStream(classDataFileName);
				ObjectInputStream in = new ObjectInputStream(fis);
				lncOldSet = (TreeSet) in.readObject();
				in.close();
				fis.close();
			}

			// create a class element and put it in the root element
			Element classItem = root.addElement("classItem");
			String namespaceVal = (namespace != null) ? namespace : "";
			classItem.addAttribute("namespace", namespaceVal);
			classItem.addAttribute("classname", className);
			classItem.addAttribute("numlocations", new Long(numLocations)
					.toString());
			classItem.addAttribute("locationsNotCovered", new Long(
					numLocationsNotCovered).toString());
			classItem.setText(linesNotCoveredList);
			// Identify common lines of code not covered between the two sets
			if (lncOldSet != null) {
				linesNotCoveredNewSet.retainAll(lncOldSet);
			}
			// Save lines not covered to the class data file
			fos = new FileOutputStream(classDataFileName);
			out = new ObjectOutputStream(fos);
			out.writeObject(linesNotCoveredNewSet);

			// save class specific info to the results file			

		} catch (Exception e) {
			System.out.println("Exception in processing coverage data :" + e);
		} finally {
			try {
				if (out != null)
					out.close();
				if (fos != null)
					fos.close();
			} catch (Exception e) {
				System.out.println("Exception in closing resources :" + e);
			}
		}

	} 
	
	/**
	 * This method will look for proxyString and set the proxy in connectorConfig.  
	 * If there are any validation failure, proxy will not be set for the connection.
	 * @author kvyas
	 * @param proxyString - string with proxy of format proxyHostName:proxyPort 
	 * @param config - Configuration that will be used to make the SOAP connection using proxy
	 */
	private static void setProxy(String proxyString, ConnectorConfig config){
		//proxyString validation
		if(proxyString!=null && proxyString.length()>0 && proxyString.contains(":")){			
			config.setProxy(proxyString.split(":")[0], Integer.valueOf(proxyString.split(":")[1]));
			System.out.println("Connection to server being established using Proxy- "+proxyString);
		}
		
	}
}