/** Collects the data from AllObjects.xml config file and creates the Sobjects
 *
 */
//TODO: can also be done using lexiloder

package test.util;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import test.runner.Globals;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class ObjectsSetUp {

    private String CREATE_ALL_FILENAME = Globals.ROOT_DIR+"/xml/AllObjects.xml";

    private String CREATE_ONE_FILENAME = Globals.ROOT_DIR+"/xml/AllObjects.xml";

    private static PartnerConnection stub = null;

    public static ArrayList<String> createdIds = new ArrayList<String>();

    private static boolean flag1=false;
    private static boolean flag2=false;
    private static String id1="";
    private static String id2="";

    public ObjectsSetUp() throws Exception {
        stub = new ComergentTestingUtil().getConnection();
    }

    /**
     * creates all Sobjects that are mentioned in the xml file
     *
     */

    public void createAllFromXML() {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(CREATE_ALL_FILENAME));

            // normalize text representation
            doc.getDocumentElement().normalize();

            // get all SObjects
            NodeList listOfLineItems = doc.getDocumentElement().getChildNodes();
            for (int s = 0; s < listOfLineItems.getLength(); s++) {
                // set values for each Sobject-- type followed by attributes
                Node lineItemNode = listOfLineItems.item(s);
                if (lineItemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element lineItemElement = (Element)lineItemNode;

                    SObject[] objects = new SObject[1];

                    SObject obj = new SObject();

                    //Sets SObject type
                    obj.setType(lineItemElement.getAttribute("name"));

                    //Gets the fields of the SObject from the XML
                    NodeList fields = lineItemElement.getElementsByTagName("field");


                    for (int i = 0; i < fields.getLength(); i++) {
                        Node fieldNode = fields.item(i);
                        Element fieldElement = (Element)fieldNode;
                        String attr = fieldElement.getAttribute("name");
                        String type = fieldElement.getAttribute("type");
                        String value = fieldElement.getFirstChild().getNodeValue();
                        obj.setField(attr, toObject(type, value));
                    }// end of i loop

                    //Gets the relationship object to which the SObject is related
                    NodeList relations = lineItemElement.getElementsByTagName("relationship");
                    for (int i = 0; i < relations.getLength(); i++) {
                        Node relationNode = relations.item(i);
                        Element relationElement = (Element)relationNode;
                        String attr = relationElement.getAttribute("name");

                        //Get the type of the object to which the SObject is related
                        NodeList parent = relationElement.getElementsByTagName("entity");
                        Node parentNode = parent.item(0);
                        Element parentElement = (Element)parentNode;
                        String parentRecordType = parentElement.getFirstChild().getNodeValue();

                        //Get the name of the object to which the SObject is related
                        parent = relationElement.getElementsByTagName("param");
                        parentNode = parent.item(0);
                        parentElement = (Element)parentNode;
                        String parentfield = parentElement.getAttribute("name");
                        String parentRecord = parentElement.getFirstChild().getNodeValue();

                        // To make the query execute only for the first time and if the SObject is Quote
                        if(!flag1 || obj.getType().equals("sfquote__Quote__c")){

                        //Get the id of this related object
                        String query = "Select Id from " + parentRecordType + " where " + parentfield + " = '"
                                + parentRecord + "'";
                        SObject queryRecord = stub.query(query).getRecords()[0];
                        id1=queryRecord.getId();
                        flag1=true;

                        }


                        //Sets the relationship field of the SObject
                        obj.setField(attr, id1);

                    }
                    objects[0] = obj;
                    SaveResult[] result = stub.create(objects);
                    createdIds.add(result[0].getId());

                }// end of if clause
            }// end of for loop with s var

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
    }

    /**
     * Creates all SObjects of a particular type from the xml file
     *
     * @param sobjectType
     */
    public void createAllSObjectFromXML(String sobjectType){
        try{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(CREATE_ONE_FILENAME));

            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfLineItems = doc.getDocumentElement().getChildNodes();

            SObject[] objects = new SObject[1];

            //Loop to read all SObjects
            for (int s = 0; s < listOfLineItems.getLength(); s++) {

                Node lineItemNode = listOfLineItems.item(s);
                if (lineItemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element lineItemElement = (Element)lineItemNode;

                    //Gets the SObject from the XML which is to be created
                    if (lineItemElement.getAttribute("name").equals(sobjectType)) {
                        SObject obj = new SObject();


                        //Sets SObject type
                        obj.setType(lineItemElement.getAttribute("name"));

                        //Gets the fields of the SObject from the XML
                        NodeList fields = lineItemElement.getElementsByTagName("field");
                        for (int i = 0; i < fields.getLength(); i++) {
                            Node fieldNode = fields.item(i);
                            Element fieldElement = (Element)fieldNode;
                            String attr = fieldElement.getAttribute("name");
                            String type = fieldElement.getAttribute("type");
                            String value = fieldElement.getFirstChild().getNodeValue();
                            obj.setField(attr, toObject(type, value));
                        }

                        //Gets the relationship object to which the SObject is related
                        NodeList relations = lineItemElement.getElementsByTagName("relationship");
                        for (int i = 0; i < relations.getLength(); i++) {
                            Node relationNode = relations.item(i);
                            Element relationElement = (Element)relationNode;
                            String attr = relationElement.getAttribute("name");

                            //Get the type of the object to which the SObject is related
                            NodeList parent = relationElement.getElementsByTagName("entity");
                            Node parentNode = parent.item(0);
                            Element parentElement = (Element)parentNode;
                            String parentRecordType = parentElement.getFirstChild().getNodeValue();

                            //Get the name of the object to which the SObject is related
                            parent = relationElement.getElementsByTagName("param");
                            parentNode = parent.item(0);
                            parentElement = (Element)parentNode;
                            String parentfield = parentElement.getAttribute("name");
                            String parentRecord = parentElement.getFirstChild().getNodeValue();

                            //To make the query execute only for the first time and if the SObject is Quote
                            if(!flag2 || obj.getType().equals("sfquote__Quote__c")){

                                //Get the id of this related object
                                String query = "Select Id from " + parentRecordType + " where " + parentfield + " = '"
                                        + parentRecord + "'";
                                SObject queryRecord = stub.query(query).getRecords()[0];
                                id2=queryRecord.getId();
                                flag2=true;

                            }

                            //Sets the relationship field of the SObject
                            obj.setField(attr, id2);

                        }
                        objects[0] = obj;

                        //Create the SObject
                        SaveResult[] result = stub.create(objects);
                        createdIds.add(result[0].getId());

                    }// end of if clause
                }
            }// end of for loop with s var


        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
    }


    public ArrayList<String> getCreatedIds() {
        return createdIds;
    }

    private Object toObject(String type, String value) {
        if (type.equals("String"))
            return value;

        else if (type.equals("Integer"))
            return Integer.parseInt(value);
        else if (type.equals("Boolean"))
            return Boolean.valueOf(value);
        else if (type.equals("Double"))
            return Double.valueOf(value).doubleValue();

        else if (type.equals("Date")) {
            try {
                DateFormat dateformat = new SimpleDateFormat("yyyy-mm-dd");
                return Date.class.cast(dateformat.parseObject(value));
            } catch (ParseException e) {
                System.out.println("Date not in required format");
            }
        }
        return value;
    }


    //For deleting the created objects
    public void deleteAllObjects(){
        try{
            System.out.println("Created IDs-"+createdIds);
            stub.delete(createdIds.toArray (new String [createdIds.size()]));
        }catch(ConnectionException e){
            System.out.println("error while deleting "+e.getMessage());
            e.printStackTrace();
        }
    }

}
