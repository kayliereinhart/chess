package service;

import gamerequest.CreateGameRequest;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameTests {

    private static GameService gameService;

    @BeforeAll
    public static void init() {
        gameService = new GameService();
    }

//    @Test
//    public void positiveClear() {
//        CreateGameRequest request = new CreateGameRequest("xyz", "newGame");
//        int gameID = gameService.createGame(request);
//
//    }

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
    public void createGameWithoutAuth() {
        CreateGameRequest request = new CreateGameRequest(null, "newGame");
        assertThrows(UnauthorizedResponse.class, () -> gameService.createGame(request));
    }
}
