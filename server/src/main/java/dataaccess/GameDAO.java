package dataaccess;

import java.util.Collection;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    int findNextID();
    void createGame(GameData gameData);
    Collection<GameData> listGames();
    GameData getGame(int gameID);
    void addPlayer(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException;
    void clearGames();
}
