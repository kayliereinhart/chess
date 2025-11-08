package dataaccess;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import model.AuthData;

public class SQLAuthDAOTests {

    private static SQLAuthDAO dao;
    private static AuthData auth;

    @BeforeAll
    public static void init() {
        dao = assertDoesNotThrow(SQLAuthDAO::new);
        auth = new AuthData("abc", "user");
    }

    @AfterEach
    public void positiveClear() {
        assertDoesNotThrow(() -> dao.clearAuths());
    }

    @Test
    public void positiveCreateAuth() {
        assertDoesNotThrow(() -> dao.createAuth(auth));
    }

    @Test
    public void createAuthNoToken() {
        assertThrows(Exception.class, () -> dao.createAuth(new AuthData(null, "usernotoken")));
    }

    @Test
    public void createAuthNoUsername() {
        assertThrows(Exception.class, () -> dao.createAuth(new AuthData("abcd", null)));
    }

    @Test
    public void positiveGetAuth() {
        assertDoesNotThrow(() -> dao.createAuth(auth));
        AuthData data = assertDoesNotThrow(() -> dao.getAuth(auth.authToken()));
        assertNotNull(data);
    }

    @Test
    public void getAuthNoToken() {
        assertThrows(Exception.class, () -> dao.getAuth(null));
    }
}
