package dataaccess;

import model.GameData;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public int findNextID() {
        int id = 1;

        while (games.containsKey(id)) {
            id++;
        }
        return id;
    }

    @Override
    public void clearGames() {
        games.clear();
    }
}
