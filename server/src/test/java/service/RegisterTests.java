package service;

import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.TestCreateRequest;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;

public class RegisterTests {
    @Test
    public void positiveRegister() {
        Server server = new Server();
        var port = server.run(0);

        UserData newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
    }

    @Test
    public void negativeRegister() {

    }
}
