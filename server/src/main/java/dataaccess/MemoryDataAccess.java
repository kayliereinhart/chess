package dataaccess;

import model.AuthData;
import model.UserData;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void createUser(UserData userData) {
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void clearUsers() {
        users.clear();
    }

    @Override
    public void createAuth(AuthData authData) {
        auths.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void clearAuths() {
        auths.clear();
    }
}
