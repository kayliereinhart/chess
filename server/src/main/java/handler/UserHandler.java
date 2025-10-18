package handler;

import com.google.gson.Gson;
import io.javalin.http.HttpResponseException;
import model.AuthData;
import model.UserData;
import service.UserService;

public class UserHandler {

    private final UserService userService = new UserService();
    private final Gson serializer = new Gson();

    public String handleRegister(String requestJson) throws HttpResponseException {
        UserData registerRequest = serializer.fromJson(requestJson, UserData.class);
        AuthData registrationResult = userService.register(registerRequest);

        return serializer.toJson(registrationResult);
    }

    public String handleLogin(String requestJson) throws HttpResponseException {
        UserData loginRequest = serializer.fromJson(requestJson, UserData.class);
        AuthData loginResult = userService.login(loginRequest);

        return serializer.toJson(loginResult);
    }

    public void handleLogout(String authToken) throws HttpResponseException {
        userService.logout(authToken);
    }

    public void handleClear() {
        userService.clear();
    }

    public String handleVerifyAuth(String authToken) {
        return userService.verifyAuth(authToken);
    }
}
