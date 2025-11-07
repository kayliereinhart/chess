package service;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import model.AuthData;
import model.UserData;

public class UserTests {

    private static UserService userService;
    private static UserData existingUser;
    private static String existingAuth;
    private static UserData newUser;

    @BeforeAll
    public static void init() {
        userService = assertDoesNotThrow(UserService::new);
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
    }

    @BeforeEach
    public void setup() {
        assertDoesNotThrow(() -> userService.clear());
        AuthData registerResponse = assertDoesNotThrow(() -> userService.register(existingUser));
        existingAuth = registerResponse.authToken();
    }

    @Test
    public void positiveClear() {
        assertDoesNotThrow(() -> userService.register(newUser));

        assertThrows(ForbiddenResponse.class, () -> userService.register(existingUser));
        assertThrows(ForbiddenResponse.class, () -> userService.register(newUser));

        assertDoesNotThrow(() -> userService.clear());

        AuthData registerResponse1 = assertDoesNotThrow(() -> userService.register(existingUser));
        AuthData registerResponse2 = assertDoesNotThrow(() -> userService.register(newUser));
        assertNotNull(registerResponse1);
        assertNotNull(registerResponse2);
    }

    @Test
    public void positiveRegister() {
        AuthData authData = assertDoesNotThrow(() -> userService.register(newUser));

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

        assertThrows(Exception.class, () -> userService.register(userData));
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
        assertNotNull(registerResponse.authToken());
    }

    @Test
    public void loginInvalidUsername() {
        assertThrows(UnauthorizedResponse.class, () -> userService.login(newUser));
    }

    @Test
    public void loginInvalidPassword() {
        UserData loginRequest = new UserData("ExistingUser", "existingUserWrongPassword", null);
        assertThrows(UnauthorizedResponse.class, () -> userService.login(loginRequest));
    }

    @Test
    public void loginWithoutUsername() {
        UserData loginRequest = new UserData(null, "existingUserPassword", null);
        assertThrows(Exception.class, () -> userService.login(loginRequest));
    }

    @Test
    public void loginWithoutPassword() {
        UserData loginRequest = new UserData("ExistingUser", null, null);
        assertThrows(BadRequestResponse.class, () -> userService.login(loginRequest));
    }

    @Test
    public void positiveLogout() {
        assertDoesNotThrow(() -> userService.logout(existingAuth));
    }

    @Test
    public void logoutAlreadyLoggedOut() {
        assertDoesNotThrow(() -> userService.logout(existingAuth));
        assertThrows(UnauthorizedResponse.class, () -> userService.logout(existingAuth));
    }
}
