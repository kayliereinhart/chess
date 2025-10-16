package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess = new MemoryDataAccess();

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData userData) {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, userData.username());

        dataAccess.createUser(userData);
        dataAccess.createAuth(authData);

        return authData;
    }
}
