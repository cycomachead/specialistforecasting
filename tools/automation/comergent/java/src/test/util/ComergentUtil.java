package test.util;

import java.net.URL;
import java.util.Properties;

import com.comergent.www.webservices.UserLoginType;
import com.comergent.www.webservices.UserAuthenticatorType;
import com.comergent.www.webservices.MessageHeaderType;
import com.comergent.www.webservices.MessageTypeType;
import com.comergent.www.webservices.MessageVersionType;
import com.comergent.www.webservices.RemoteUserType;
import com.comergent.www.webservices.SfdcMessageResults;
import com.comergent.www.webservices.SfdcQuoteValidation;
import com.comergent.www.webservices.SfdcQuoteValidationPortType;
import com.comergent.www.webservices.SfdcQuoteValidationServiceLocator;

/**
 * provides for methods related to comergent and its API's
 * 
 * @author dgupta
 * 
 */
public class ComergentUtil {

    private Properties props = null;

    private static SfdcQuoteValidationPortType comergentStub = null;

    private MessageHeaderType messageHeader = null;

    private RemoteUserType remoteUser = null;

    private ComergentTestingUtil utils = new ComergentTestingUtil();

    public ComergentUtil() throws Exception {
        initStubToComergent();
    }

    /**
     * initializes the comergent port-- create connection, sets user and message
     * header
     * 
     * @return the comergent port to which the connection has been established
     * @throws Exception
     */
    public SfdcQuoteValidationPortType initStubToComergent() throws Exception {
        props = utils.loadProperties();
        if (comergentStub == null) {
            SfdcQuoteValidationServiceLocator locator = new SfdcQuoteValidationServiceLocator();
            comergentStub = locator.getSfdcQuoteValidationPort(new URL(props.getProperty("ComergentServer")));
        }
        return comergentStub;
    }

    public void setHeaders() {
        setComergentMessageHeader();
        setComergentRemoteUser();
    }

    /**
     * sets the message header type for comergent
     * 
     */
    public void setComergentMessageHeader() {
        messageHeader = new MessageHeaderType();

        MessageTypeType messageType = MessageTypeType.fromString("dXML");

        messageHeader.setMessageType(messageType);

        MessageVersionType temp_MessageVersionType = MessageVersionType.fromString("5.0");

        messageHeader.setMessageVersion(temp_MessageVersionType);

        /*
         * messageHeader.setMessageID("");
         */}

    /**
     * 
     * @return the message header with values that have been set for API calls
     */
    public MessageHeaderType getComergentMessageHeader() {
        return messageHeader;
    }

    /**
     * sets the remote user Type login and authenticator
     * 
     */
    public void setComergentRemoteUser() {
        remoteUser = new RemoteUserType();

        remoteUser.setUserLogin(new UserLoginType("admin"));

        remoteUser.setUserAuthenticator(new UserAuthenticatorType("admin"));

    }

    /**
     * 
     * @return the remote user type with values that have been set for API calls
     */
    public RemoteUserType getComergentRemoteUser() {
        return remoteUser;
    }

    public SfdcMessageResults validate(SfdcQuoteValidation qval) throws Exception {
        return comergentStub.sfdcQuoteValidationRequest(getComergentMessageHeader(), getComergentRemoteUser(), qval);
    }
}
