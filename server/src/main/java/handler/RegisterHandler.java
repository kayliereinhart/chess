package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;

public class RegisterHandler {

    private final UserService userService = new UserService();
    private final Gson serializer = new Gson();

    public String handleRegister(String requestJson) throws Exception {
        UserData registerRequest = serializer.fromJson(requestJson, UserData.class);
        AuthData registrationResult = userService.register(registerRequest);

        return serializer.toJson(registrationResult);
    }
}
