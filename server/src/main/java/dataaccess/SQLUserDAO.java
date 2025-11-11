package dataaccess;

import model.UserData;
import java.sql.*;

public class SQLUserDAO extends SQLDao implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            username varchar(256) NOT NULL,
            password varchar(256) NOT NULL,
            email varchar(256) NOT NULL,
            PRIMARY KEY (username)
            )
            """
        };
        configureDatabase(createStatements);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");

        return new UserData(username, password, email);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, userData.username(), userData.password(), userData.email());
    }

    private UserData getFromDB(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("Unable to read user data: username == null");
        } else {
            return getFromDB(username);
        }
    }

    @Override
    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }
}
