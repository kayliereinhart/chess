package dataaccess;

import chess.ChessGame;
import model.GameData;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

    private final Gson serializer = new Gson();

    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
            gameID int NOT NULL AUTO_INCREMENT,
            whiteUsername varchar(256),
            blackUsername varchar(256),
            gameName varchar(256) NOT NULL,
            game TEXT DEFAULT NULL,
            PRIMARY KEY (gameID)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to update database: %s", e.getMessage()));
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var game = serializer.fromJson(rs.getString("game"), ChessGame.class);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return executeUpdate(statement, gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), gameData.game());
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException{
        var result = new ArrayList<GameData>();

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";

            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException{
        if (gameID == null) {
            throw new DataAccessException("Unable to read game data: gameID == null");
        } else {
            try (Connection conn = DatabaseManager.getConnection()) {
                var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.setInt(1, gameID);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return readGame(rs);
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
    public void addPlayer(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException{
        try {
            GameData currentGame = getGame(gameID);
            String statement;

            if (currentGame == null) {
                throw new DataAccessException("game does not exist");
            } else if (playerColor == ChessGame.TeamColor.WHITE) {
                statement = "UPDATE games SET whiteUsername=? WHERE gameID=?";
            } else {
                statement = "UPDATE games SET blackUsername=? WHERE gameID=?";
            }
            executeUpdate(statement, username, gameID);
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to add player: %s", e.getMessage()));
        }
    }

    @Override
    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }
}
