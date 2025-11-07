package handler;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import service.UserService;

public class UserHandler {

    private final Gson serializer = new Gson();
    private final UserService userService;

    public UserHandler() throws DataAccessException {
        userService = new UserService();
    }

    public String handleRegister(String requestJson) throws DataAccessException {
        UserData request = serializer.fromJson(requestJson, UserData.class);
        AuthData result = userService.register(request);

        return serializer.toJson(result);
    }

    public String handleLogin(String requestJson) throws DataAccessException {
        UserData request = serializer.fromJson(requestJson, UserData.class);
        AuthData result = userService.login(request);

        return serializer.toJson(result);
    }

    public void handleLogout(String authToken) {
        userService.logout(authToken);
    }

    public String handleVerifyAuth(String authToken) {
        return userService.verifyAuth(authToken);
    }

    public void handleClear() throws DataAccessException{
        userService.clear();
    }
}
