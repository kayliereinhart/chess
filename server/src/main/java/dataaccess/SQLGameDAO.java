package dataaccess;

import chess.ChessGame;
import gsonbuilder.GameGsonBuilder;
import model.GameData;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO extends SQLDao implements GameDAO {

    private final Gson serializer;

    public SQLGameDAO() throws DataAccessException {
        String[] createStatements = {
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
        configureDatabase(createStatements);
        GameGsonBuilder builder = new GameGsonBuilder();
        serializer = builder.createSerializer();
    }

    private GameData readGameDataGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");

        String gameStr = rs.getString("game");
        ChessGame game = serializer.fromJson(gameStr, ChessGame.class);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private GameData readGameDataNoGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");

        return new GameData(gameID, whiteUsername, blackUsername, gameName, null);
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), serializer.toJson(gameData.game()));
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException{
        var result = new ArrayList<GameData>();

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName FROM games";

            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGameDataNoGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    private GameData getFromDB(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameDataGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException{
        if (gameID == null) {
            throw new DataAccessException("Unable to read game data: gameID == null");
        } else {
            return getFromDB(gameID);
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

    public void updateGame(Integer gameID, ChessGame game) throws DataAccessException {
        try {
            GameData currentGame = getGame(gameID);
            String statement;

            if (currentGame == null) {
                throw new DataAccessException("game does not exist");
            } else {
                statement = "UPDATE games SET game=? WHERE gameID=?";
            }
            executeUpdate(statement, serializer.toJson(game), gameID);
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to update game: %s", e.getMessage()));
        }
    }

    @Override
    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }
}
