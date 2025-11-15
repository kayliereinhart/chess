package dataaccess;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAOTests {

    private static SQLGameDAO dao;
    private static GameData gameData;

    @BeforeAll
    public static void init() {
        dao = assertDoesNotThrow(SQLGameDAO::new);
        gameData = new GameData(null, null, null, "GameName", null);
        assertDoesNotThrow(() -> dao.clearGames());
    }

    @AfterEach
    @Test
    public void positiveClear() {
        assertDoesNotThrow(() -> dao.clearGames());
    }

    @Test
    public void positiveCreateGame() {
        int gameID = assertDoesNotThrow(() -> dao.createGame(gameData));
        assertInstanceOf(Integer.class, gameID);
    }

    @Test
    public void createGameNoName() {
        GameData gameNoName = new GameData(null, "w", "b", null, new ChessGame());
        assertThrows(Exception.class, () -> dao.createGame(gameNoName));
    }

    @Test
    public void positiveListGames() {
        Collection<GameData> games = new ArrayList<>();
        games.add(gameData.addID(1));

        assertDoesNotThrow(() -> dao.createGame(gameData));
        Collection<GameData> listResult = assertDoesNotThrow(() -> dao.listGames());
        assertEquals(games, new ArrayList<>(listResult));
    }

    @Test
    public void positiveGetGame() {
        int id = assertDoesNotThrow(() -> dao.createGame(gameData));
        GameData game = assertDoesNotThrow(() -> dao.getGame(id));
        assertEquals(gameData.addID(1), game);
    }

    @Test
    public void getGameNoID() {
        assertDoesNotThrow(() -> dao.createGame(gameData));
        assertThrows(Exception.class, () -> dao.getGame(null));
    }

    @Test
    public void positiveAddPlayer() {
        int id = assertDoesNotThrow(() -> dao.createGame(gameData));
        assertDoesNotThrow(() -> dao.addPlayer("user", ChessGame.TeamColor.WHITE, id));

        GameData game = assertDoesNotThrow(() -> dao.getGame(1));
        assertEquals("user", game.whiteUsername());
    }

    @Test
    public void addPlayerGameNotExist() {
        assertThrows(Exception.class, () -> dao.addPlayer("user", ChessGame.TeamColor.BLACK, 1));
    }
}
