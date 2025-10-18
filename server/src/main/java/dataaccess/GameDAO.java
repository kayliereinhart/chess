package dataaccess;

import model.GameData;

public interface GameDAO {
    void createGame(GameData gameData);
    int findNextID();
    void clearGames();
}
