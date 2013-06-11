package tools.util;

import java.io.IOException;
import java.io.PrintStream;

import com.sforce.soap.crossinstance.*;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

/**
 * Java command line utility to mass link orgs to accounts
 * @author stamm
 * @since 146
 */
public class GetOrgUsage {
    
    public static void main(String args[]) throws Exception {
        try {
            GetOrgUsage l = new GetOrgUsage();
            StringBuilder target = new StringBuilder(128);
            if (args.length > 3) {
                target.append(args[3]); 
            } else {
                target.append("http://localhost");
            }
			target.append(Config.CROSS_INSTANCE_LOCATION_URL);
            l.getOrgUsage(args[0], args[1], args[2], target.toString(), System.out);
        }
        catch (Exception x) {
            System.err.println("GetOrgUsage <user> <pw> <orgId> [urlOfTarget]\n");
            throw x;
        }
    }
    
    private PrintStream output;
    
    private void out(String str) {
        if (this.output != null) output.println(str);
    }
    
    public void getOrgUsage(String username, String password, String orgId,
            String loginEndpointUrl, PrintStream output) throws ConnectionException, IOException {
        this.output = output;
        out("Get Org Usage Tester");
        // Login to the Api
        out("Connecting as " + username + "\n");
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setAuthEndpoint(loginEndpointUrl);
        config.setServiceEndpoint(loginEndpointUrl);
        CrossInstanceConnection xiConn = new CrossInstanceConnection(config);
        
        LoginResult res = xiConn.login(username, password);
        config.setServiceEndpoint(loginEndpointUrl);
        config.setSessionId(res.getSessionId());
        
        xiConn = new CrossInstanceConnection(config);
        GetOrgUsageResult result = xiConn.getOrgUsage(orgId);
        
        System.out.println("getActivityFieldsLicensed: " + result.getActivityFieldsLicensed());
        System.out.println("getActivityFieldsUsed:     " + result.getActivityFieldsUsed());
        System.out.println("getCustomAppsAllowed:      " + result.getCustomAppsAllowed());
        System.out.println("getCustomAppsLicensed:     " + result.getCustomAppsLicensed());
        System.out.println("getCustomAppsUsed:         " + result.getCustomAppsUsed());
        System.out.println("getCustomObjectsAllowed:   " + result.getCustomObjectsAllowed());
        System.out.println("getCustomObjectsLicensed:  " + result.getCustomObjectsLicensed());
        System.out.println("getCustomObjectsUsed:      " + result.getCustomObjectsUsed());
        System.out.println("getCustomTabsAllowed:      " + result.getCustomTabsAllowed());
        System.out.println("getCustomTabsLicensed:     " + result.getCustomTabsLicensed());
        System.out.println("getCustomTabsUsed:         " + result.getCustomObjectsUsed());

        for (OrganizationValueUsage ovu : result.getOrganizationValueUsage()) {
            System.out.println(ovu.getValueType().name() + " = " + ovu.getNumberInUse() + "/" + ovu.getNumberAllowed());
        }
    }
    
}
