
package main.helpers;

//CLASS ΓΙΑ ΝΑ ΔΙΑΒΑΖΕΙ ΠΟΙΟΣ USER ΚΑΝΕΙ LOGIN ΣΤΟ APP.
public class UserStoreHelper {
    private static UserStoreHelper instance;
    private String username;
    private int id;

    public UserStoreHelper() {
    }

    public UserStoreHelper getInstance() {
        if (instance == null) {
            instance = new UserStoreHelper();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
