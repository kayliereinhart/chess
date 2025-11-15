package client;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static UserData user;

    @BeforeAll
    public static void init() {
        user = new UserData("player1", "password", "p1@email.com");
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    @Test
    public void positiveClear() {
        assertDoesNotThrow(() -> facade.clear());
    }


    @Test
    public void positiveRegister() throws Exception {
        var authData = facade.register(user);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerSameUserTwice() throws Exception {
        assertDoesNotThrow(() -> facade.register(user));
        assertThrows(Exception.class, () -> facade.register(user));
    }

    @Test
    public void positiveLogin() throws Exception {
        facade.register(user);
        AuthData authData = assertDoesNotThrow(() -> facade.login(user));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginWrongPassword() throws Exception {
        facade.register(user);
        assertThrows(Exception.class, () -> facade.login(new UserData("player1", "wrong",
                "p1@email.com")));
    }

}
