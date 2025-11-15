package service;

import java.util.ArrayList;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import chess.ChessGame;
import model.CreateGameRequest;
import model.JoinGameRequest;
import model.ListGamesResult;
import model.GameData;

public class GameTests {

    private static GameService gameService;

    @BeforeAll
    public static void init() {
        gameService = assertDoesNotThrow(GameService::new);
    }

    @BeforeEach
    public void setup() {
        assertDoesNotThrow(() -> gameService.clear());
    }

    @Test
    public void positiveClear() {
        CreateGameRequest request = new CreateGameRequest("newGame");
        int gameID = assertDoesNotThrow(() -> gameService.createGame(request));

        GameData gameData = new GameData(gameID, null, null, "newGame", new ChessGame());
        ArrayList<GameData> games = new ArrayList<>();
        games.add(gameData);

        ListGamesResult result = assertDoesNotThrow(() -> gameService.listGames());
        assertEquals(new ListGamesResult(games), result);

        assertDoesNotThrow(() -> gameService.clear());

        result = assertDoesNotThrow(() -> gameService.listGames());
        assertEquals(0, result.games().size());

    }

    @Test
    public void positiveCreateGame() {
        CreateGameRequest request = new CreateGameRequest("newGame");
        int gameID = assertDoesNotThrow(() -> gameService.createGame(request));

        assertEquals(1, gameID);
    }

    @Test
    public void createTwoGames() {
        CreateGameRequest request1 = new CreateGameRequest("newGame1");
        int gameID1 = assertDoesNotThrow(() -> gameService.createGame(request1));

        CreateGameRequest request2 = new CreateGameRequest("newGame2");
        int gameID2 = assertDoesNotThrow(() -> gameService.createGame(request2));

        assertEquals(1, gameID1);
        assertEquals(2, gameID2);
    }

    @Test
    public void createGameWithoutName() {
        CreateGameRequest request = new CreateGameRequest(null);
        assertThrows(BadRequestResponse.class, () -> gameService.createGame(request));
    }

    @Test
    public void positiveListGames() {
        CreateGameRequest request = new CreateGameRequest("newGame");
        int gameID = assertDoesNotThrow(() -> gameService.createGame(request));

        GameData gameData = new GameData(gameID, null, null, "newGame", new ChessGame());
        ArrayList<GameData> games = new ArrayList<>();
        games.add(gameData);

        ListGamesResult result = assertDoesNotThrow(() -> gameService.listGames());
        assertEquals(new ListGamesResult(games), result);
    }

    @Test
    public void positiveJoinGame() {
        CreateGameRequest createRequest = new CreateGameRequest("newGame");
        int gameID = assertDoesNotThrow(() -> gameService.createGame(createRequest));

        JoinGameRequest joinRequest = new JoinGameRequest("user", ChessGame.TeamColor.BLACK, gameID);
        assertDoesNotThrow(() -> gameService.joinGame(joinRequest));
    }

    @Test
    public void joinGameInvalidGameID() {
        CreateGameRequest createRequest = new CreateGameRequest("newGame");
        assertDoesNotThrow(() -> gameService.createGame(createRequest));

        JoinGameRequest joinRequest = new JoinGameRequest("user", ChessGame.TeamColor.BLACK, 5);
        assertThrows(BadRequestResponse.class, () -> gameService.joinGame(joinRequest));
    }

    @Test
    public void joinGameNoColor() {
        CreateGameRequest createRequest = new CreateGameRequest("newGame");
        assertDoesNotThrow(() -> gameService.createGame(createRequest));

        JoinGameRequest joinRequest = new JoinGameRequest("user", null, 5);
        assertThrows(BadRequestResponse.class, () -> gameService.joinGame(joinRequest));
    }

    @Test
    public void joinGameColorTaken() {
        CreateGameRequest createRequest = new CreateGameRequest("newGame");
        int gameID = assertDoesNotThrow(() -> gameService.createGame(createRequest));

        JoinGameRequest joinRequest1 = new JoinGameRequest("user1", ChessGame.TeamColor.BLACK, gameID);
        assertDoesNotThrow(() -> gameService.joinGame(joinRequest1));

        JoinGameRequest joinRequest2 = new JoinGameRequest("user2", ChessGame.TeamColor.BLACK, gameID);
        assertThrows(ForbiddenResponse.class, () -> gameService.joinGame(joinRequest2));
    }
}
