package testcase.appstore.comergent.api;

/** Clears all the data created initially */


import test.util.BaseTest;
import test.util.UnlinkOrg;
import test.util.ObjectsSetUp;

public class TestFinalCleanUp extends BaseTest {

    public TestFinalCleanUp(String name){
        super(name);
    }


    public void testDoCleanUp() {
        try{
            new UnlinkOrg().unlinkAllOrgAccount();
        } catch(Exception e){
            System.out.println("error in unlinking org - accounts");
        }

        ObjectsSetUp objSetUp=null;

        try{
        objSetUp = new ObjectsSetUp();
        }catch (Exception e){
            System.out.println("error in final tear down");
        }
        objSetUp.deleteAllObjects();


    }

}
