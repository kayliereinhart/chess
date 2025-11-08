package dataaccess;

import java.util.Collection;
import java.util.HashMap;

import chess.ChessGame;
import model.GameData;

public class MemoryGameDAO implements GameDAO {

    private final HashMap<Integer, GameData> games = new HashMap<>();

    private int findNextID() {
        int id = 1;

        while (games.containsKey(id)) {
            id++;
        }
        return id;
    }

    @Override
    public int createGame(GameData gameData) {
        int id = findNextID();
        games.put(id, gameData.addID(id));

        return id;
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public GameData getGame(Integer gameID) {
        return games.get(gameID);
    }

    @Override
    public void addPlayer(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException{
        GameData currentGame = games.get(gameID);
        GameData newGame;

        if (currentGame == null) {
            throw new DataAccessException("game does not exist");
        } else if (playerColor == ChessGame.TeamColor.WHITE) {
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
