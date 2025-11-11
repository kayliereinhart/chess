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
        serializer = new GameGsonBuilder().createSerializer();
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
        return executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), serializer.toJson(gameData.game()));
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
