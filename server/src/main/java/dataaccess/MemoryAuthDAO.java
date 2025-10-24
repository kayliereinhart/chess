package dataaccess;

import java.util.HashMap;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {

    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        auths.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (auths.containsKey(authToken)) {
            auths.remove(authToken);
        } else {
            throw new DataAccessException("authToken does not exist");
        }
    }

    @Override
    public void clearAuths() {
        auths.clear();
    }
}
