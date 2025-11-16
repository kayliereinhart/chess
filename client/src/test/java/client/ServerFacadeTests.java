package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static UserData user;

    @BeforeAll
    public static void init() {
        user = new UserData("player1", "password", "p1@email.com");
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    @Test
    public void positiveClear() {
        assertDoesNotThrow(() -> facade.clear());
    }


    @Test
    public void positiveRegister() throws Exception {
        var authData = facade.register(user);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerSameUserTwice() {
        assertDoesNotThrow(() -> facade.register(user));
        assertThrows(Exception.class, () -> facade.register(user));
    }

    @Test
    public void positiveLogin() throws Exception {
        facade.register(user);
        AuthData authData = assertDoesNotThrow(() -> facade.login(user));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginWrongPassword() throws Exception {
        facade.register(user);
        assertThrows(Exception.class, () -> facade.login(new UserData("player1", "wrong",
                "p1@email.com")));
    }

    @Test
    public void positiveLogout() throws Exception {
        var authData = facade.register(user);
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    public void logoutNotLoggedIn() throws Exception {
        var authData = facade.register(user);
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
        assertThrows(Exception.class, () -> facade.logout(authData.authToken()));
    }

    @Test
    public void positiveCreateGame() throws Exception {
        var authData = facade.register(user);
        CreateGameRequest request = new CreateGameRequest("GameName");
        CreateGameResult result = assertDoesNotThrow(() -> facade.createGame(request, authData.authToken()));
        assertTrue(result.gameID() >= 0);
    }

    @Test
    public void createGameNoName() throws Exception {
        var authData = facade.register(user);
        CreateGameRequest request = new CreateGameRequest(null);
        assertThrows(Exception.class, () -> facade.createGame(request, authData.authToken()));
    }

    @Test
    public void createGameNoAuth() {
        CreateGameRequest request = new CreateGameRequest("GameName");
        assertThrows(Exception.class, () -> facade.createGame(request, null));
    }

    @Test
    public void positiveListGames() throws Exception {
        var authData = facade.register(user);
        CreateGameRequest request = new CreateGameRequest("GameName");
        CreateGameResult createResult = assertDoesNotThrow(() -> facade.createGame(request, authData.authToken()));

        GameData gameData = new GameData(createResult.gameID(), null, null,
                "GameName", null);
        ArrayList<GameData> games = new ArrayList<>();
        games.add(gameData);

        ListGamesResult listResult = assertDoesNotThrow(() -> facade.listGames(authData.authToken()));
        assertEquals(new ListGamesResult(games), listResult);
    }

    @Test
    public void listGamesNotAuthorized() {
        assertThrows(Exception.class, () -> facade.listGames("adkf"));
    }

    @Test
    public void positiveJoinGame() throws Exception {
        var authData = facade.register(user);
        CreateGameRequest createRequest = new CreateGameRequest("GameName");
        CreateGameResult createResult = assertDoesNotThrow(() -> facade.createGame(createRequest, authData.authToken()));

        JoinGameRequest joinRequest = new JoinGameRequest(user.username(), ChessGame.TeamColor.WHITE,
                createResult.gameID());
        assertDoesNotThrow(() -> facade.joinGame(joinRequest, authData.authToken()));

        ArrayList<GameData> games = new ArrayList<>(facade.listGames(authData.authToken()).games());
        assertEquals(user.username(), games.getFirst().whiteUsername());
    }

    @Test
    public void joinGameColorTaken() throws Exception {
        var authData = facade.register(user);
        CreateGameRequest createRequest = new CreateGameRequest("GameName");
        CreateGameResult createResult = assertDoesNotThrow(() -> facade.createGame(createRequest, authData.authToken()));

        JoinGameRequest joinRequest = new JoinGameRequest(user.username(), ChessGame.TeamColor.WHITE,
                createResult.gameID());
        assertDoesNotThrow(() -> facade.joinGame(joinRequest, authData.authToken()));
        assertThrows(Exception.class, () -> facade.joinGame(joinRequest, authData.authToken()));
    }

}
