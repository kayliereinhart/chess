package service;

import dataaccess.DataAccess;
import datamodel.RegistrationResult;
import datamodel.UserData;

public class UserService {
    private DataAccess dataAccess;

    public RegistrationResult register(UserData registerRequest) {
        return new RegistrationResult(registerRequest.username(), "xyz");
    }
}
