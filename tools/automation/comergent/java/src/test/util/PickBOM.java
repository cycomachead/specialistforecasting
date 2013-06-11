package test.util;

/**Picks the BOM information from the file Bom.xml
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


public class PickBOM {

    //File where Bom data is stored
    String fileName = Globals.ROOT_DIR+"/xml/Bom.xml";

    public HashMap[] createAllBoms() throws Exception {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(fileName));

        // normalize text representation
        doc.getDocumentElement().normalize();

        // get all Bom Objects
        NodeList listOfLineItems = doc.getElementsByTagName("Bom");

        /* Create an object array of type HashMap
        with maximum number of elements equals to the number of Boms */

        HashMap[] allBom = new HashMap[listOfLineItems.getLength()];
        for (int i = 0; i < listOfLineItems.getLength(); i++) {

            HashMap<String,String> newhash =new HashMap<String, String>();

            Node lineItemNode = listOfLineItems.item(i);

            //Get the fields of the BOM

            NodeList attributeList = lineItemNode.getChildNodes();
            for (int j = 0; j < attributeList.getLength(); j++) {
                Node attributeNode = attributeList.item(j);

                if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element attributeElement = (Element) attributeNode;

                    newhash.put(attributeElement.getNodeName(),attributeElement.getFirstChild().getNodeValue());

                }
            }
            allBom[i] = newhash;

        }
        //Return the Hash of all the Orgs specified in the Org.xml file
        return allBom;
    }

}