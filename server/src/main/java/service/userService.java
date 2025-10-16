package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.RegistrationResult;
import datamodel.UserData;

public class userService {
    private DataAccess dataAccess = new MemoryDataAccess();

    public RegistrationResult register(UserData userData) {
        dataAccess.createUser(userData);
        return new RegistrationResult("NewUser", "xyz");
    }
}
