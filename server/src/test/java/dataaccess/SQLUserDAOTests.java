package dataaccess;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import model.UserData;

public class SQLUserDAOTests {

    private static SQLUserDAO dao;
    private static UserData user;

    @BeforeAll
    public static void init() {
        dao = assertDoesNotThrow(SQLUserDAO::new);
        user = new UserData("user", "xyz", "user@email");
    }

    @AfterEach
    public void positiveClear() {
        assertDoesNotThrow(() -> dao.clearUsers());
    }

    @Test
    public void positiveCreateUser() {
        assertDoesNotThrow(() -> dao.createUser(user));
    }

    @Test
    public void createUserNoUsername() {
        assertThrows(Exception.class, () -> dao.createUser(new UserData(null, "qrs", "email")));
    }

    @Test
    public void createUserNoPassword() {
        assertThrows(Exception.class, () -> dao.createUser(new UserData("usernopassword", null, "email")));
    }

    @Test
    public void createUserNoEmail() {
        assertThrows(Exception.class, () -> dao.createUser(new UserData("usernoemail", "qrs", null)));
    }

    @Test
    public void createUserTakenUsername() {
        assertDoesNotThrow(() -> dao.createUser(user));
        assertThrows(Exception.class, () -> dao.createUser(user));
    }

    @Test
    public void positiveGetUser() {
        assertDoesNotThrow(() -> dao.createUser(user));
        UserData data = assertDoesNotThrow(() -> dao.getUser(user.username()));
        assertNotNull(data);
    }

    @Test
    public void getUserNoUsername() {
        assertThrows(Exception.class, () -> dao.getUser(null));
    }
}
