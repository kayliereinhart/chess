package dataaccess;

import datamodel.UserData;

public interface DataAccess {
    public void createUser(UserData userData);

    public void getUser(String username);
}
