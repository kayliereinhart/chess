package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;

public interface GameDAO {
    void createGame(GameData gameData);
    int findNextID();
    Collection<GameData> listGames();
    GameData getGame(int gameID);
    void addPlayer(String username, ChessGame.TeamColor playerColor, int gameID);
    void clearGames();
}
