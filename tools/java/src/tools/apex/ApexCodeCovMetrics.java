package tools.apex;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author vkrishnamurthy
 * @since 170
 */
public class ApexCodeCovMetrics {
	public static void main(String args[]) {
		final String FINAL_RESULTS_XML_FILE="finalresultsFile.xml";

		// d - folder containing all data files
		// r - folder to write final code coverage metrics. html and xsl file should be in this folder.

		OptionParser parser = new OptionParser("d:r:");
		OptionSet options = parser.parse(args);

		String dataFileFolderName = options.argumentOf("d");
		//String finalResultsFileName = options.argumentOf("r")+"/"+FINAL_RESULTS_XML_FILE;
		String finalResultsFileName = options.argumentOf("r");
				

		new ApexCodeCovMetrics().execute(finalResultsFileName , dataFileFolderName );
	}

	private void execute(String finalResultsFileName , String dataFileFolderName ) {
		XMLWriter writer = null;
		Document doc = null;
		Set<Object> lncSet = null;		
		String className = null;
		final String COVERAGE_RESULTS_XML_FILE="codeCovData.xml";

		String codeCoverageResultsFileName = dataFileFolderName+"/"+COVERAGE_RESULTS_XML_FILE;

		try {
			Document resultsdoc =    DocumentHelper.createDocument();

			// create the root element and add it to the document
			Element resultsRoot = resultsdoc.addElement("CoverageResults");

			Element timestamp = resultsRoot.addElement("timestamp");
			timestamp.setText(getCurrentTime());

			if (dataFileFolderName == null) {
				throw new Exception("Folder name for Data files is null.");
			}
			File file = new File(codeCoverageResultsFileName);
			boolean fileExists = file.exists();
			if (!fileExists) {
				throw new Exception("Code coverage summary results File "
						+ codeCoverageResultsFileName + " does not exist.");
			}
			SAXReader xmlReader = new SAXReader();
			doc = xmlReader.read(file);
			
			// Get list of data files
			File f1 = new File(dataFileFolderName);
			FilenameFilter filter = new DataFileFilter();
			String[] dataFilesArray = f1.list(filter);
			
			// Iterate through all *.data files
			for (int cnt = 0; cnt < dataFilesArray.length; cnt++) {
				StringBuilder linesNotCovered = new StringBuilder();
				int linesNotCoveredCnt = 0;
				double percCov = 0.0;
				
				// Read the data file
				String classDataFileName = dataFileFolderName + "/"
						+ dataFilesArray[cnt];
				FileInputStream fis = new FileInputStream(classDataFileName);
				ObjectInputStream in = new ObjectInputStream(fis);
				lncSet = (TreeSet) in.readObject();
				in.close();
				fis.close();
				
				// Concatenate data file entries to form lines not covered 
				String linesNotCoveredString = "";
				linesNotCoveredCnt = lncSet.size();
				if(linesNotCoveredCnt != 0){
					Iterator<Object> it1 = lncSet.iterator();
					while (it1.hasNext()) {
						Object obj = it1.next();
						linesNotCovered.append(obj + " , ");
					}
					// Delete the last comma char
					linesNotCoveredString = linesNotCovered.toString();
					linesNotCoveredString = linesNotCoveredString.substring(0,
							linesNotCoveredString.lastIndexOf(",") - 1);					
				}
				
				// From the codecovresults aggregate xml file look for xml node matching this specific class
				String nodeSelector = null;
				if(dataFilesArray[cnt].startsWith("PACK_")) {
					String filename = dataFilesArray[cnt].substring(5);
					int sep = filename.lastIndexOf("_FILE_");
					String packageName = filename.substring(0,sep);
					String fileNameSplit[] = filename.substring(sep+6).split("\\.");
					className = fileNameSplit[0];
					nodeSelector = "/Results/classItem[@namespace=\""+packageName+"\" and @classname=\""
						+ className + "\"]";
				}
				else {
					String fileNameSplit[] = dataFilesArray[cnt].split("\\.");
					className = fileNameSplit[0];
					nodeSelector = "/Results/classItem[@classname=\""
							+ className + "\"]";					
				}
				List<Element> elementList = doc.selectNodes(nodeSelector);
				
				// Get the first element for this class name
				Element firstElement = null;
				if(elementList.size() > 0)
					firstElement = (Element) elementList.get(0);
				else {
					throw new Exception("ERROR: classname "+className+" is not in the codecovResults.xml file.");
				}

				// Get details for this class - name, namespace, numlocations
				String nameSpace = firstElement.attributeValue("namespace");
				className = firstElement.attributeValue("classname");
				String totalLocn = firstElement.attributeValue("numlocations");
				
				// Calculate lines covered and percentage coverage
				int linesCovered = new Long(totalLocn).intValue()
						- linesNotCoveredCnt;
				percCov = getPercCov(linesCovered, totalLocn);
				
				// Add this class item to the xml created by this code
				Element classItem = resultsRoot.addElement("classItem");

				// create package element, and add to ClassItem
				Element packageElement = classItem.addElement("package");
				if (nameSpace != null)
					packageElement.setText(nameSpace);

				// create classname element, and add to ClassItem
				Element classNameElement = classItem.addElement("classname");
				classNameElement.setText(className);

				// create totalLocations element, and add to ClassItem
				Element totalLocnsElement = classItem
						.addElement("totallocations");
				totalLocnsElement.setText(totalLocn);

				// create linesCovered element, and add to ClassItem
				Element linesCovElement = classItem.addElement("linescovered");
				linesCovElement.setText(new Long(linesCovered).toString());

				// create percCovered element, and add to ClassItem
				Element percCovElement = classItem.addElement("perccovered");
				percCovElement.setText(new Double(percCov).toString());

				// create linesNotCovered element, and add to ClassItem
				Element linesNotCovElement = classItem
						.addElement("linesnotcovered");
				linesNotCovElement.setText(linesNotCoveredString);
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			writer = new XMLWriter(new FileWriter(finalResultsFileName), format);
			writer.write(resultsdoc);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception ex) {
				System.out.println("exception: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}
	// Utility method to calculate code coverage percentage
	private double getPercCov(int linesCovered, String totalLocn) {
		if(  (totalLocn == null ) || ("null".equals(totalLocn) || ("0".equals(totalLocn)))) {return 0.0;}
		try {
			double percCov = ((double) linesCovered)
				/ ((double) new Long(totalLocn).intValue()) * 100;
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			return Double.valueOf(twoDForm.format(percCov));
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0.0;
		}
	}
	// get current time
	private String getCurrentTime() {
		TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
		DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa zzz");
		dateFormat.setTimeZone(tz);
		return dateFormat.format(new Date());
	}
}

class DataFileFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		return (name.endsWith(".data"));
	}
}