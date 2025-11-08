package dataaccess;

import java.util.Collection;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    int createGame(GameData gameData) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    void addPlayer(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException;
    void clearGames() throws DataAccessException;
}
