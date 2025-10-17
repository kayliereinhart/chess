package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    void createUser(UserData userData);
    UserData getUser(String username);
    void clearUsers();

    void createAuth(AuthData authData);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void clearAuths();
}
