package test.util;

/**Picks the Org information from the file Org.xml
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


public class PickOrg {

    //File where Org data is stored
    String fileName = Globals.ROOT_DIR+"/xml/Org.xml";

    public HashMap[] createAllOrgs() throws Exception {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(fileName));

        // normalize text representation
        doc.getDocumentElement().normalize();

        // get all Org Objects
        NodeList listOfLineItems = doc.getElementsByTagName("Org");

        /* Create an object array of type HashMap
        with maximum number of elements equals to the number of Orgs */

        HashMap[] allOrg = new HashMap[listOfLineItems.getLength()];
        for (int i = 0; i < listOfLineItems.getLength(); i++) {

            HashMap<String,String> newhash =new HashMap<String, String>();
            //HashMap newhash = new HashMap();

            Node lineItemNode = listOfLineItems.item(i);

            //Get the fields of the Org

            NodeList attributeList = lineItemNode.getChildNodes();
            for (int j = 0; j < attributeList.getLength(); j++) {
                Node attributeNode = attributeList.item(j);

                if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element attributeElement = (Element) attributeNode;

                    newhash.put(attributeElement.getNodeName(),attributeElement.getFirstChild().getNodeValue());

                }
            }
            allOrg[i] = newhash;

        }
        //Return the Hash of all the Orgs specified in the Org.xml file
        return allOrg;
    }

}