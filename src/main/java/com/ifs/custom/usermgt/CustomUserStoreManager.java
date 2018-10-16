package com.ifs.custom.usermgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.jasypt.util.password.StrongPasswordEncryptor;
import org.wso2.carbon.user.api.Properties;
import org.wso2.carbon.user.api.Property;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.jdbc.JDBCUserStoreManager;
import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;
import org.wso2.carbon.utils.Secret;

import java.sql.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class CustomUserStoreManager extends JDBCUserStoreManager {

    private static Log log = LogFactory.getLog(CustomUserStoreManager.class);
    // This instance is used to generate the hash values
//    private static StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

    public CustomUserStoreManager(){}
    // You must implement at least one constructor
    public CustomUserStoreManager(RealmConfiguration realmConfig, Map<String, Object> properties, ClaimManager
            claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId)
            throws UserStoreException {
        super(realmConfig, properties, claimManager, profileManager, realm, tenantId);
        log.info("IFSCustomUserStoreManager initialized...");
    }

    @Override
    public boolean doAuthenticate(String userName, Object credential) throws UserStoreException {
        boolean isAuthenticated = false;
        if (userName != null && credential != null) {
//        if (userName != null) {
            try {
                String candidatePassword = String.copyValueOf(((Secret) credential).getChars());

                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection dbConnection = null;

                dbConnection = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=cmbpde2293)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=s2293)))",userName,candidatePassword);

                if(dbConnection != null){
                    isAuthenticated = true;
                }
                else {
                    isAuthenticated = false;
                }

                dbConnection.close();
//                ResultSet rs = null;
//                PreparedStatement prepStmt = null;
//                String sql = null;
//                dbConnection = this.getDBConnection();
//                dbConnection.setAutoCommit(false);
                // get the SQL statement used to select user details
//                sql = this.realmConfig.getUserStoreProperty("SelectUserSQL");
//                if (log.isDebugEnabled()) {
//                    log.debug(sql);
//                }
//
//                prepStmt = dbConnection.prepareStatement(sql);
//                prepStmt.setString(1, userName);
                // check whether tenant id is used
//                if (sql.contains("UM_TENANT_ID")) {
//                    prepStmt.setInt(2, this.tenantId);
//                }

//                rs = prepStmt.executeQuery();
//                if (rs.next()) {
//                    String storedUsername = rs.getString("USERNAME");
//                    log.info("Username: " + storedUsername);

                    // check whether password is expired or not
//                    boolean requireChange = rs.getBoolean(5);
//                    Timestamp changedTime = rs.getTimestamp(6);
//                    GregorianCalendar gc = new GregorianCalendar();
//                    gc.add(GregorianCalendar.HOUR, -24);
//                    Date date = gc.getTime();
//                    if (!(requireChange && changedTime.before(date))) {
//                        // compare the given password with stored password using jasypt
//                        isAuthenticated = candidatePassword.equals(storedPassword);
//                    }
//                    isAuthenticated = candidatePassword.equals(storedPassword);
//                    isAuthenticated = userName.equals(storedUsername);
//                }
                log.info(userName + " is authenticated using connection? " + isAuthenticated);
            }
            catch (ClassNotFoundException e){
                log.error("JDBC Driver not found!");
            }
            catch (SQLException exp) {
                log.error("Error occurred while retrieving user authentication info.", exp);
                throw new UserStoreException("Authentication Failure");
            }
        }
        return isAuthenticated;
    }

//    @Override
//    protected String preparePassword(Object password, String saltValue) throws UserStoreException {
//        if (password != null) {
//            String candidatePassword = String.copyValueOf(((Secret) password).getChars());
//            // ignore saltValue for the time being
//            log.info("Generating hash value using jasypt...");
//            return passwordEncryptor.encryptPassword(candidatePassword);
//        } else {
//            log.error("Password cannot be null");
//            throw new UserStoreException("Authentication Failure");
//        }
//    }

    @Override
    public Date getPasswordExpirationTime(String userName) throws UserStoreException {
        return null;
    }

    public String[] getUserListFromProperties(String property, String value, String profileName)
            throws UserStoreException {
        return new String[0];
    }

    @Override
    public boolean isReadOnly() throws UserStoreException {
        return true;
    }

    @Override
    public Map<String, String> getUserPropertyValues(String userName, String[] propertyNames, String profileName) throws UserStoreException {
        return new HashMap<String, String>();
    }

    @Override
    public String[] doGetRoleNames(String filter, int maxItemLimit) throws UserStoreException {
        return new String[0];
    }

    @Override
    public boolean doCheckExistingRole(String roleName) throws UserStoreException {

        return false;
    }

    @Override
    public boolean doCheckExistingUser(String userName) throws UserStoreException {

        return true;
    }

    @Override
    public boolean isBulkImportSupported() {
        return false;
    }

    public String[] getExternalRoleListOfUser(String userName) throws UserStoreException {
        /*informix user store manager is supposed to be read only and users in the custom user store
          users in the custom user store are only assigned to internal roles. Therefore this method
          returns an empty string.
         */

        return new String[0];
    }

    @Override
    public org.wso2.carbon.user.api.Properties getDefaultUserStoreProperties(){
        Properties properties = new Properties();
        properties.setMandatoryProperties(CustomUserStoreConstants.CUSTOM_UM_MANDATORY_PROPERTIES.toArray
                (new Property[CustomUserStoreConstants.CUSTOM_UM_MANDATORY_PROPERTIES.size()]));
        properties.setOptionalProperties(CustomUserStoreConstants.CUSTOM_UM_OPTIONAL_PROPERTIES.toArray
                (new Property[CustomUserStoreConstants.CUSTOM_UM_OPTIONAL_PROPERTIES.size()]));
        properties.setAdvancedProperties(CustomUserStoreConstants.CUSTOM_UM_ADVANCED_PROPERTIES.toArray
                (new Property[CustomUserStoreConstants.CUSTOM_UM_ADVANCED_PROPERTIES.size()]));
        return properties;
    }

}
