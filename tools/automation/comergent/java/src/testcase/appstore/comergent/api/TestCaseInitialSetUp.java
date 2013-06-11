package testcase.appstore.comergent.api;

import test.util.ObjectsSetUp;

public class TestCaseInitialSetUp {


    private static ObjectsSetUp objSetUp=null;


    private static boolean flag=true;

    public synchronized void doInitialize() throws Exception{
        if (flag){
        objSetUp = new ObjectsSetUp();

        //create all Accounts
        objSetUp.createAllSObjectFromXML("Account");

        //create contacts
        objSetUp.createAllSObjectFromXML("Contact");

        //create contracts
        objSetUp.createAllSObjectFromXML("Contract");

        //create order
        objSetUp.createAllSObjectFromXML("Order");

        //create opportunity
        objSetUp.createAllSObjectFromXML("Opportunity");

        //create Quotes
        objSetUp.createAllSObjectFromXML("sfquote__Quote__c");
        flag = false;

        }
    }

}
