package tools.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

import com.sforce.soap.crossinstance.CrossInstanceConnection;
import com.sforce.soap.crossinstance.LoginResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

/**
 * Java command line utility to mass link orgs to accounts
 * @author stamm
 * @since 146
 */
public class MassAccountLink {
    
    public static void main(String args[]) throws Exception {
        try {
            MassAccountLink l = new MassAccountLink();
            StringBuilder target = new StringBuilder(128);
            if (args.length > 3) {
                target.append(args[3]); 
            } else {
                target.append("http://localhost");
            }
			target.append(Config.CROSS_INSTANCE_LOCATION_URL);
            l.linkAccounts(args[0], args[1], args[2], target.toString(), System.out);
        }
        catch (Exception x) {
			System.err.println("MassAccountLink <user> <pw> <csvFileName> [urlOfTarget]\n");
            throw x;
        }
    }
    
    private PrintStream output;
    
    private void out(String str) {
        if (this.output != null) output.println(str);
    }
    
    public void linkAccounts(String username, String password, String csvFile,
            String loginEndpointUrl, PrintStream output) throws ConnectionException, IOException {
        this.output = output;
        out("Mass Account Org Linker");
        long start = System.currentTimeMillis();
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
                
        BufferedReader manifestFileRdr = new BufferedReader(new FileReader(csvFile));

        String nextline;
        int lineCount = 0;
        int numOrgs = 0;
        int failures = 0;
        while ((nextline = manifestFileRdr.readLine()) != null) {
            lineCount++;
            if (nextline.startsWith("#")) continue; // ignore
            if (nextline.startsWith("//")) continue; // ignore
            
            
            StringTokenizer tokenizer = new StringTokenizer(nextline, " ,;\t");
            int numTokens = tokenizer.countTokens();
            if (numTokens == 0) continue;
            if (tokenizer.countTokens() > 2 ) {
                System.err.println("Each line must be of the form <orgId> <accountId> at line " + lineCount);
                System.exit(-1);
            }
            
            String orgId = tokenizer.nextToken().trim();
            if (!orgId.startsWith("00D")) {
                System.err.println("Invalid org id at line " + lineCount);
                System.exit(-1);
            }
            String accId = null;
            if (tokenizer.hasMoreTokens())  {
                accId = tokenizer.nextToken();
                if (!accId.startsWith("001")) {
                    System.err.println("Invalid account id at line " + lineCount);
                    System.exit(-1);
                }
            }
            // Now make the call
            try {
                xiConn.linkAccount(orgId, accId);
            } catch (ConnectionException ex) {
                System.err.println("Could not link organization " + orgId + " at line " + lineCount + "\n" + ex.getMessage());
                failures++;
            }
            numOrgs++;
        }
        
        out("\nLinked " + (numOrgs-failures) + " orgs in " + (System.currentTimeMillis() - start) / 1000
            + " seconds");
        if (failures > 0) {
            System.exit(-1);
        }
    }
    
}
