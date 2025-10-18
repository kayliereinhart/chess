package dataaccess;

import model.GameData;
import chess.ChessGame;
import java.util.Collection;

public interface GameDAO {
    int findNextID();
    void createGame(GameData gameData);
    Collection<GameData> listGames();
    GameData getGame(int gameID);
    void addPlayer(String username, ChessGame.TeamColor playerColor, int gameID);
    void clearGames();
}
