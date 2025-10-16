package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess = new MemoryDataAccess();

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData userData) throws DataAccessException {
        UserData existingUser = dataAccess.getUser(userData.username());

        if (existingUser != null) {
            throw new AlreadyTakenException("already taken");
        }
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, userData.username());

        dataAccess.createUser(userData);
        dataAccess.createAuth(authData);

        return authData;
    }
}
