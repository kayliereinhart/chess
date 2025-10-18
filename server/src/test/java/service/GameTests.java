package service;

import gamerequest.CreateGameRequest;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTests {

    private static GameService gameService;

    @BeforeAll
    public static void init() {
        gameService = new GameService();
    }

    @Test
    public void positiveCreateGame() {
        CreateGameRequest request = new CreateGameRequest("xyz", "newGame");
        int gameID = gameService.createGame(request);

        assertEquals(1, gameID);
    }
}
