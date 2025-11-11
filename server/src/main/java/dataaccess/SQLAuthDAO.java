package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO extends SQLDao implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auths (
            authToken varchar(256) NOT NULL,
            username varchar(256) NOT NULL,
            PRIMARY KEY (authToken)
            )
            """
        };
        configureDatabase(createStatements);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");

        return new AuthData(authToken, username);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authData.authToken(), authData.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Unable to read auth data: authToken == null");
        } else {
            try (Connection conn = DatabaseManager.getConnection()) {
                var statement = "SELECT authToken, username FROM auths WHERE authToken=?";
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.setString(1, authToken);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return readAuth(rs);
                        }
                    }
                }
            } catch (Exception e) {
                throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
            }
            return null;
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (getAuth(authToken) != null) {
            var statement = "DELETE FROM auths WHERE authToken=?";
            executeUpdate(statement, authToken);
        } else {
            throw new DataAccessException("authToken does not exist");
        }
    }

    @Override
    public void clearAuths() throws DataAccessException {
        var statement = "TRUNCATE auths";
        executeUpdate(statement);
    }
}
