package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpResponseException;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess = new MemoryDataAccess();

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData userData) throws HttpResponseException {
        UserData existingUser = dataAccess.getUser(userData.username());

        if (existingUser != null) {
            throw new ForbiddenResponse("already taken");
        } else if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadRequestResponse("bad request");
        }
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, userData.username());

        dataAccess.createUser(userData);
        dataAccess.createAuth(authData);

        return authData;
    }

    public void clear() {
        dataAccess.clearUsers();
        dataAccess.clearAuths();
    }
}
