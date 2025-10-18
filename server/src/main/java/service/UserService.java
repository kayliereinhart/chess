package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpResponseException;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData userData) throws HttpResponseException {
        UserData existingUser = userDAO.getUser(userData.username());

        if (existingUser != null) {
            throw new ForbiddenResponse("already taken");
        } else if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadRequestResponse("bad request");
        }
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, userData.username());

        userDAO.createUser(userData);
        authDAO.createAuth(authData);

        return authData;
    }

    public AuthData login(UserData userData) throws HttpResponseException {
        UserData existingUser = userDAO.getUser(userData.username());

        if (userData.username() == null || userData.password() == null) {
            throw new BadRequestResponse("bad request");
        } else if (existingUser == null || !existingUser.password().equals(userData.password())) {
            throw new UnauthorizedResponse("unauthorized");
        }

        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, userData.username());

        authDAO.createAuth(authData);

        return authData;
    }

    public void logout(String authToken) throws HttpResponseException {
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new UnauthorizedResponse("unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }

    public void clear() {
        userDAO.clearUsers();
        authDAO.clearAuths();
    }

    public String verifyAuth(String authToken) {
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new UnauthorizedResponse("unauthorized");
        }
        return authData.username();
    }
}
