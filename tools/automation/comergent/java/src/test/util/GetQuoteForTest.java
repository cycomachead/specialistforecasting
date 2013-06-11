package test.util;

/**Picks the Quote and User information from the file QuoteLogin.xml
 * @author hsodha
 */

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import test.runner.Globals;


public class GetQuoteForTest {

    //File where Bom data is stored
    File fileName = new File(Globals.ROOT_DIR, "xml/QuoteLogin.xml");

    public HashMap[] getAllQuotes() throws Exception {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(fileName);

        // normalize text representation
        doc.getDocumentElement().normalize();

        // get all Quote and Login information objects
        NodeList listOfLineItems = doc.getElementsByTagName("QuoteLogin");

        /* Create an object array of type HashMap
        with maximum number of elements equals to the number of QuoteLogin objects */

        HashMap[] allQuote = new HashMap[listOfLineItems.getLength()];
        for (int i = 0; i < listOfLineItems.getLength(); i++) {
            HashMap<String,String> newhash =new HashMap<String, String>();

            Node lineItemNode = listOfLineItems.item(i);

            //Get the fields of QuoteLogin

            NodeList attributeList = lineItemNode.getChildNodes();
            for (int j = 0; j < attributeList.getLength(); j++) {
                Node attributeNode = attributeList.item(j);

                if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element attributeElement = (Element) attributeNode;

                    newhash.put(attributeElement.getNodeName(),attributeElement.getFirstChild().getNodeValue());

                }
            }
            allQuote[i] = newhash;

        }
        //Return the Hash of all the Quote and Login info specified in the QuoteLogin.xml file
        return allQuote;
    }

}