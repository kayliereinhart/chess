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

    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final UserDAO userDAO;
    private final Encrypter encrypter = new Encrypter();

    public UserService() throws DataAccessException {
        userDAO = new SQLUserDAO();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData request) throws DataAccessException{
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
    }

    public AuthData login(UserData request) {
        try {
            UserData userData = userDAO.getUser(request.username());

            if (request.password() == null) {
                throw new BadRequestResponse("bad request");
            } else if (userData == null || !encrypter.checkPassword(request.password(), userData.password())) {
                throw new UnauthorizedResponse("unauthorized");
            }
            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, request.username());

            authDAO.createAuth(authData);

            return authData;
        } catch (DataAccessException e) {
            throw new BadRequestResponse("bad request");
        }
    }

    public void logout(String authToken) {
        try {
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedResponse("unauthorized");
        }
    }

    public String verifyAuth(String authToken) {
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
