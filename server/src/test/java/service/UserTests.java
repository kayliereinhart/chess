package service;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpResponseException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    private static UserService userService;
    private static UserData existingUser;
    private static String existingAuth;
    private static UserData newUser;

    @BeforeAll
    public static void init() {
        userService = new UserService();
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
    }

    @BeforeEach
    public void setup() {
        userService.clear();
        AuthData registerResponse = userService.register(existingUser);
        existingAuth = registerResponse.authToken();
    }

    @Test
    public void positiveClear() {
        userService.register(newUser);

        assertThrows(ForbiddenResponse.class, () -> userService.register(existingUser));
        assertThrows(ForbiddenResponse.class, () -> userService.register(newUser));

        userService.clear();

        assertNotNull(userService.register(existingUser));
        assertNotNull(userService.register(newUser));
    }

    @Test
    public void positiveRegister() {
        AuthData authData = userService.register(newUser);

        assertEquals(newUser.username(), authData.username());
        assertEquals(String.class, authData.authToken().getClass());
    }

    @Test
    public void registerWithTakenUsername() {
        assertThrows(ForbiddenResponse.class, () -> userService.register(existingUser));
    }

    @Test
    public void registerWithoutUsername() {
        UserData userData = new UserData(null, "password", "u@email.com");

        assertThrows(BadRequestResponse.class, () -> userService.register(userData));
    }

    @Test
    public void registerWithoutPassword() {
        UserData userData = new UserData("user", null, "u@email.com");

        assertThrows(BadRequestResponse.class, () -> userService.register(userData));
    }

    @Test
    public void registerWithoutEmail() {
        UserData userData = new UserData("user", "password", null);

        assertThrows(BadRequestResponse.class, () -> userService.register(userData));
    }

    @Test
    public void positiveLogin() {
        AuthData registerResponse = assertDoesNotThrow(() -> userService.login(existingUser));
        assertEquals(existingUser.username(), registerResponse.username());
        assertEquals(existingAuth, registerResponse.authToken());
    }
}
