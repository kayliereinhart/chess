package dataaccess;

import datamodel.UserData;

public interface DataAccess {
    void createUser(UserData userData);

    void getUser(String username);
}
