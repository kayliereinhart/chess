package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.RegistrationResult;
import datamodel.User;

public class userService {
    private DataAccess dataAccess = new MemoryDataAccess();

    public RegistrationResult register(User user) {
        dataAccess.createUser(user);
        return new RegistrationResult("NewUser", "xyz");
    }
}
