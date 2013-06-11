package tools.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

import com.sforce.soap.crossinstance.CrossInstanceConnection;
import com.sforce.soap.partner.sobject.wsc.SObject;
import com.sforce.soap.partner.wsc.LoginResult;
import com.sforce.soap.partner.wsc.PartnerConnection;
import com.sforce.soap.partner.wsc.QueryResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

/**
 * Java command line utility to mass enqueue orgs
 * @author stamm
 * @since 146
 */
public class MassEnqueue {
    
    public static void main(String args[]) throws Exception {
        try {
            MassEnqueue l = new MassEnqueue();
            StringBuilder target = new StringBuilder(128);
            if (args.length > 3) {
                target.append(args[3]); 
            } else {
                target.append("http://localhost");
            }
			target.append(Config.LOCATION_URL);
            l.enqueueAccounts(args[0], args[1], args[2], target.toString(), System.out);
        }
        catch (Exception x) {
            System.err.println("MassEnqueue <user> <pw> <csvFileName> [urlOfTarget]\n");
            throw x;
        }
    }
    
    private PrintStream output;
    
    private void out(String str) {
        if (this.output != null) output.println(str);
    }
    
    public void enqueueAccounts(String username, String password, String csvFile,
            String loginEndpointUrl, PrintStream output) throws ConnectionException, IOException {
        this.output = output;
        out("Mass Account Enqueuing Linker");
        long start = System.currentTimeMillis();
        // Login to the Api
        out("Connecting as " + username + "\n");
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setAuthEndpoint(loginEndpointUrl);
        config.setServiceEndpoint(loginEndpointUrl);
        PartnerConnection con = new PartnerConnection(config);
                
        LoginResult res = con.login(username, password);

        ConnectorConfig xiConfig = new ConnectorConfig();
        xiConfig.setServiceEndpoint(loginEndpointUrl.replace("/services", "/sfdc").replace("/u/","/x/"));
        xiConfig.setSessionId(res.getSessionId());
        
        CrossInstanceConnection xiConn = new CrossInstanceConnection(xiConfig);
                
        BufferedReader manifestFileRdr = new BufferedReader(new FileReader(csvFile));

        String nextline;
        int lineCount = 0;
        int numOrgs = 0;
        int failures = 0;
        while ((nextline = manifestFileRdr.readLine()) != null) {
            lineCount++;
            if (nextline.startsWith("#")) continue; // ignore
            if (nextline.startsWith("//")) continue; // ignore
            
            // it's a query.  so enqueue the query
            if (nextline.startsWith("SELECT")) {

                System.out.println("Executing query " + nextline);
                int startOrgs = numOrgs;
                String locator = null;
                do {
                    QueryResult r = locator == null ? con.query(nextline) : con.queryMore(locator);
                    for (SObject s : r.getRecords()) {
                        // See which field we should get, see if it's RelatedAccountId
                        String accId = (String) s.getField("RelatedAccountId");
                        if (accId == null) accId = (String) s.getField("AccountId");
                        if (accId == null) accId = (String) s.getField("Id");
                        if (!accId.startsWith("001")) {
                            System.err.println("Invalid account id at line " + lineCount);
                            System.exit(-1);
                        }
                        // Now make the call
                        try {
                            xiConn.enqueueAccount(accId);
                        } catch (ConnectionException ex) {
                            System.err.println("Could not enqueue account " + accId + " at line " + lineCount + "\n" + ex.getMessage());
                            failures++;
                        }
                        numOrgs++;
                    }

                    locator = r.isDone() ? null : r.getQueryLocator();
                    System.out.println("Query enqueued " + (numOrgs - startOrgs) + " orgs");
                } while (locator != null);
                
            } else {
                StringTokenizer tokenizer = new StringTokenizer(nextline, " ,;\t");
                int numTokens = tokenizer.countTokens();
                if (numTokens == 0) continue;
                if (tokenizer.countTokens() > 1 ) {
                    System.err.println("Each line must be of the form <accountId> at line " + lineCount);
                    System.exit(-1);
                }
                String accId = tokenizer.nextToken();
                if (!accId.startsWith("001")) {
                    System.err.println("Invalid account id at line " + lineCount);
                    System.exit(-1);
                }
                // Now make the call
                try {
                    xiConn.enqueueAccount(accId);
                } catch (ConnectionException ex) {
                    System.err.println("Could not enqueue account " + accId + " at line " + lineCount + "\n" + ex.getMessage());
                    failures++;
                }
                numOrgs++;
            }
        }
        
        out("\nEnqueued " + (numOrgs-failures) + " orgs in " + (System.currentTimeMillis() - start) / 1000
            + " seconds");
        if (failures > 0) {
            System.exit(-1);
        }
    }
    
}
