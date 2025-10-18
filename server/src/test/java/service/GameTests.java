package service;

import chess.ChessGame;
import gamerequest.CreateGameRequest;
import gameresult.ListGamesResult;
import io.javalin.http.BadRequestResponse;
import model.GameData;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    private static GameService gameService;

    @BeforeAll
    public static void init() {
        gameService = new GameService();
    }

    @BeforeEach
    public void setup() {
        gameService.clear();
    }

    @Test
    public void positiveClear() {
        CreateGameRequest request = new CreateGameRequest("abc", "newGame");
        int gameID = gameService.createGame(request);

        GameData gameData = new GameData(gameID, null, null, "newGame", new ChessGame());
        ArrayList<GameData> games = new ArrayList<GameData>();
        games.add(gameData);

        assertEquals(new ListGamesResult(games), gameService.listGames());

        gameService.clear();

        assertEquals(0, gameService.listGames().games().size());

    }

    @Test
    public void positiveCreateGame() {
        CreateGameRequest request = new CreateGameRequest("xyz", "newGame");
        int gameID = gameService.createGame(request);

        assertEquals(1, gameID);
    }

    @Test
    public void createTwoGames() {
        CreateGameRequest request = new CreateGameRequest("abc", "newGame1");
        int gameID1 = gameService.createGame(request);

        request = new CreateGameRequest("xyz", "newGame2");
        int gameID2 = gameService.createGame(request);

        assertEquals(1, gameID1);
        assertEquals(2, gameID2);
    }

    @Test
    public void createGameWithoutName() {
        CreateGameRequest request = new CreateGameRequest("abc", null);
        assertThrows(BadRequestResponse.class, () -> gameService.createGame(request));
    }

    @Test
    public void positiveListGames() {
        CreateGameRequest request = new CreateGameRequest("abc", "newGame");
        int gameID = gameService.createGame(request);

        GameData gameData = new GameData(gameID, null, null, "newGame", new ChessGame());
        ArrayList<GameData> games = new ArrayList<GameData>();
        games.add(gameData);

        assertEquals(new ListGamesResult(games), gameService.listGames());
    }
}
