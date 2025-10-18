package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    void createGame(GameData gameData);
    int findNextID();
    Collection<GameData> listGames();
    void clearGames();
}
