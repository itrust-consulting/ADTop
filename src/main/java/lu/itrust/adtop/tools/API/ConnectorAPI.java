package lu.itrust.adtop.tools.API;

import java.util.List;

/**
 * Created by philippe on 30.03.15.
 */
public interface ConnectorAPI {

    void setSessionId(String sessionId);

    String getSessionId();

    boolean connect(String username, String password);

    boolean isConnected();
    
    boolean isAuthorized();
    
    boolean logout();

	List<ApiNamable> getCustomer();

	List<String> getCustomerIdString();

	List<String> getCustomerNameString();
}