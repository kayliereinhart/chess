package dataaccess;

import model.GameData;
import chess.ChessGame;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public int findNextID() {
        int id = 1;

        while (games.containsKey(id)) {
            id++;
        }
        return id;
    }

    @Override
    public void createGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void addPlayer(String username, ChessGame.TeamColor playerColor, int gameID) {
        GameData currentGame = games.get(gameID);
        GameData newGame;

        if (playerColor == ChessGame.TeamColor.WHITE) {
            newGame = new GameData(gameID, username, currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
        } else {
            newGame = new GameData(gameID, currentGame.whiteUsername(), username, currentGame.gameName(), currentGame.game());
        }
        games.put(gameID, newGame);
    }

    @Override
    public void clearGames() {
        games.clear();
    }
}
