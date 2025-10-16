package handler;

import com.google.gson.Gson;
import datamodel.RegistrationResult;
import datamodel.UserData;
import service.UserService;

public class RegisterHandler {
    //create RegisterRequest(same as UserData) from json
    //call UserService register(RegisterRequest)
    //service returns RegistrationResult, turn into json and return to server
    private final UserService userService = new UserService();
    private final Gson serializer = new Gson();

    public String handleRegister(String requestJson) {
        UserData registerRequest = serializer.fromJson(requestJson, UserData.class);
        RegistrationResult registrationResult = userService.register(registerRequest);

        return serializer.toJson(registrationResult);
    }
}
