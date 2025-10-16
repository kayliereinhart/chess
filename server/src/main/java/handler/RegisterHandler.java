package handler;

import com.google.gson.Gson;
import datamodel.RegistrationResult;
import datamodel.UserData;
import service.UserService;

public class RegisterHandler {
    //create RegisterRequest(same as UserData) from json
    //call UserService register(RegisterRequest)
    //service returns RegistrationResult, turn into json and return to server
    private UserService userService;

    public String handleRegister(String reqJson) {
        Gson serializer = new Gson();
        UserData registerRequest = serializer.fromJson(reqJson, UserData.class);
        RegistrationResult res = userService.register(registerRequest);

        return serializer.toJson(res);
    }
}
