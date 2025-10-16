package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.RegistrationResult;
import datamodel.UserData;

public class UserService {
    private DataAccess dataAccess = new MemoryDataAccess();

    public RegistrationResult register(UserData registerRequest) {
        dataAccess.createUser(registerRequest);
        return new RegistrationResult(registerRequest.username(), "xyz");
    }
}
