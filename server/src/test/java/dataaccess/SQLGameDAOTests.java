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
        gameData = new GameData(null, "white", "black", "GameName", new ChessGame());
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
        assertEquals(games, listResult);
    }
}
