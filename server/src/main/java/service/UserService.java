package service;

import java.util.UUID;

import dataaccess.*;
import encrypter.Encrypter;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

import model.AuthData;
import model.UserData;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final Encrypter encrypter = new Encrypter();

    public UserService() throws DataAccessException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData request) throws DataAccessException{
        try {
            UserData existingUser = userDAO.getUser(request.username());

            if (existingUser != null) {
                throw new ForbiddenResponse("already taken");
            } else if (request.username() == null || request.password() == null || request.email() == null) {
                throw new BadRequestResponse("bad request");
            }
            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, request.username());

            String hashedPassword = encrypter.encryptPassword(request.password());

            userDAO.createUser(request.replacePassword(hashedPassword));
            authDAO.createAuth(authData);

            return authData;
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public AuthData login(UserData request) throws DataAccessException {
        try {
            if (request.username() == null || request.password() == null) {
                throw new BadRequestResponse("bad request");
            }
            UserData userData = userDAO.getUser(request.username());

            if (userData == null || !encrypter.checkPassword(request.password(), userData.password())) {
                throw new UnauthorizedResponse("unauthorized");
            }
            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, request.username());

            authDAO.createAuth(authData);

            return authData;
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void logout(String authToken) throws DataAccessException {
        try {
            if (authDAO.getAuth(authToken) != null) {
                authDAO.deleteAuth(authToken);
            } else {
                throw new UnauthorizedResponse("unauthorized");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public String verifyAuth(String authToken) throws DataAccessException{
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new UnauthorizedResponse("unauthorized");
        }
        return authData.username();
    }

    public void clear() throws DataAccessException {
        userDAO.clearUsers();
        authDAO.clearAuths();
    }
}
