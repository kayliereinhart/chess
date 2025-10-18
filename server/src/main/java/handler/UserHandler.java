package handler;

import com.google.gson.Gson;

import model.UserData;
import model.AuthData;
import service.UserService;

public class UserHandler {

    private final UserService userService = new UserService();
    private final Gson serializer = new Gson();

    public String handleRegister(String requestJson) {
        UserData request = serializer.fromJson(requestJson, UserData.class);
        AuthData result = userService.register(request);

        return serializer.toJson(result);
    }

    public String handleLogin(String requestJson) {
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

    public void handleClear() {
        userService.clear();
    }
}
