package dataaccess;

import datamodel.User;

public interface DataAccess {
    public void createUser(User user);

    public void getUser(String username);
}
