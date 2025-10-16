package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    void createUser(UserData userData);
    void getUser(String username);

    void createAuth(AuthData authData);
    void getAuth(String authToken);
}
