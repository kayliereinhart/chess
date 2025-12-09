package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import gsonbuilder.GameGsonBuilder;
import client.websocket.ServerMessageObserver;
import client.websocket.WsFacade;
import com.google.gson.Gson;
import model.*;
import java.util.*;

import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class Client implements ServerMessageObserver {

    private final ServerFacade server;
    private final WsFacade ws;
    private State state = State.LOGGEDOUT;
    private HashMap<Integer, Integer> gameMap = null;
    private String username = null;
    private String authToken = null;
    private ChessGame currentGame = null;
    private ChessGame.TeamColor currentColor = null;
    private Integer currentID = null;

    public Client(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        ws = new WsFacade(serverUrl, this);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type help to get started. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n[" + state + "] >>> ");
    }

    private String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "register" -> register(params);
                case "login" -> login(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observe(params);
                case "redraw" -> redrawBoard();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "legal" -> "legal output";
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> "command not recognized\nvalid commands:\n" + help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String help() {
        if (state == State.LOGGEDOUT) {
            return "   register <USERNAME> <PASSWORD> <EMAIL>" + " - to create an account\n" +
                    "   login <USERNAME> <PASSWORD> - to play chess\n" +
                    "   quit - playing chess\n" +
                    "   help - with possible commands";
        } else if (state == State.LOGGEDIN) {
            return "   create <NAME> - a game\n" +
                    "   list - games\n" +
                    "   join <ID> [WHITE|BLACK] - a game\n" +
                    "   observe <ID> - a game\n" +
                    "   logout - when you are done\n" +
                    "   quit - playing chess\n" +
                    "   help - with possible commands";
        } else {
            return "   redraw - board\n" +
                    "   leave - game\n" +
                    "   move <START> <END> - piece\n" +
                    "   resign - forfeit game\n" +
                    "   legal <POSITION> - highlight legal moves\n" +
                    "   help - with possible commands";
        }
    }

    private String register(String... params) throws Exception {
        if (params.length == 3) {
            UserData user = new UserData(params[0], params[1], params[2]);
            authToken = server.register(user).authToken();
            username = params[0];
            state = State.LOGGEDIN;

            return "You registered as " + username;
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String login(String... params) throws Exception {
        if (params.length == 2) {
            UserData user = new UserData(params[0], params[1], null);
            authToken = server.login(user).authToken();
            username = params[0];
            state = State.LOGGEDIN;

            return "You logged in as " + username;
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD>");
    }

    private String createGame(String... params) throws Exception {
        assertLoggedIn();

        if (params.length == 1) {
            CreateGameRequest request = new CreateGameRequest(params[0]);
            server.createGame(request, authToken);

            return username + " created game " + params[0];
        }
        throw new Exception("Expected: <NAME>");
    }

    private String listGames() throws Exception {
        assertLoggedIn();

        ArrayList<GameData> games  = new ArrayList<>(server.listGames(authToken).games());
        gameMap = new HashMap<>();
        StringBuilder strBuilder = new StringBuilder();

        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            gameMap.put(i + 1, game.gameID());

            String whiteUser = game.whiteUsername() == null ? "None" : game.whiteUsername();
            String blackUser = game.blackUsername() == null ? "None" : game.blackUsername();

            strBuilder.append(i+1 + ": " + game.gameName() + "\n");
            strBuilder.append("    White Player: " + whiteUser + "\n");
            strBuilder.append("    Black Player: " + blackUser + "\n");
        }
        return strBuilder.toString();
    }

    private String joinGame(String... params) throws Exception {
        assertLoggedIn();

        if (params.length == 2) {
            ChessGame.TeamColor color;
            int id;

            if (Objects.equals(params[1], "white")) {
                color = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(params[1], "black")) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                throw new Exception("Expected: <ID> [WHITE|BLACK]");
            }

            try {
                id = Integer.parseInt(params[0]);
            } catch (Exception e) {
                throw new Exception("Error: ID should be an integer");
            }

            if ( id > gameMap.size() || id <= 0) {
                throw new Exception("Error: No game with ID");
            }
            currentColor = color;
            currentID = gameMap.get(id);
            ws.connectToGame(authToken, currentID, color);

            JoinGameRequest request = new JoinGameRequest(username, color, currentID);
            server.joinGame(request, authToken);
            state = State.INGAME;

            return "";
        }
        throw new Exception("Expected: <ID> [WHITE|BLACK]");
    }

    private String observe(String... params) throws Exception {
        assertLoggedIn();

        if (params.length == 1) {
            int id = 1;
            try {
                try {
                    id = Integer.parseInt(params[0]);
                } catch (Exception e) {
                    throw new Exception("Error: ID should be an integer");
                }

                if (id > gameMap.size() || id <= 0) {
                    throw new Exception("Error: No game with ID");
                }
                ws.connectToGame(authToken, gameMap.get(id), null);
                state = State.INGAME;
                currentColor = ChessGame.TeamColor.WHITE;
                currentID = gameMap.get(id);

                return "";
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        throw new Exception("Expected: <ID>");
    }

    private String logout() throws Exception {
        assertLoggedIn();

        String name = username;
        server.logout(authToken);
        state = State.LOGGEDOUT;
        authToken = null;
        username = null;

        return name + " logged out";
    }

    private String redrawBoard() {
        return printBoard(currentGame.getBoard(), currentColor);
    }

    private String leave() throws Exception {
        ws.leaveGame(authToken, currentID);
        state = State.LOGGEDIN;
        currentGame = null;
        currentColor = null;
        currentID = null;
        return "";
    }

    private String resign() throws Exception {
        System.out.println("Do you want to resign?");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        String line = "";

        while (!line.equals("yes") && !line.equals("no")) {
            printPrompt();
            line = scanner.nextLine().toLowerCase();

            try {
                if (line.equals("yes")) {
                    result = "";
                    ws.resign(authToken, currentID);
                } else if (line.equals("no")) {
                    result = "";
                }
                else {
                    result = "Expected YES or NO";
                }
                System.out.print(result);
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
        return "";
    }

    private String move(String... params) throws Exception {
        assertLoggedIn();

        if (params.length == 2) {
            String start = params[0];
            String end = params[1];

            if (!validPosition(start) || !validPosition(end)) {
                throw new Exception("Error: positions must be 2 characters long. A letter then a number.");
            }
            ws.makeMove(authToken, currentID, start, end);

            return "";
        } else {
            throw new Exception("Expected: <START> <END>");
        }
    }

    private boolean validPosition(String position) {
        return position.length() == 2 &&
                Character.isLetter(position.charAt(0)) &&
                Character.isDigit(position.charAt(1));
    }

    private void assertLoggedIn() throws Exception {
        if (state == State.LOGGEDOUT) {
            throw new Exception("You must log in");
        }
    }

    private String printBoard(ChessBoard board, ChessGame.TeamColor color) {
        StringBuilder strBuilder = new StringBuilder();
        int light = 1;

        if (color == ChessGame.TeamColor.WHITE) {
            board = board.flipBoard();
        }

        switch (color) {
            case WHITE -> strBuilder.append("\n   a  b  c  d  e  f  g  h\n");
            case BLACK -> strBuilder.append("\n   h  g  f  e  d  c  b  a\n");
        }

        for (int i = 1; i < 9; i++) {
            switch (color) {
                case WHITE -> strBuilder.append(9 - i + " ");
                case BLACK -> strBuilder.append(i + " ");
            }

            for (int j = 8; j >= 1; j--) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));

                switch (light) {
                    case 0:
                        strBuilder.append(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                        light = 1;
                        break;
                    case 1:
                        strBuilder.append(EscapeSequences.SET_BG_COLOR_RED);
                        light = 0;
                        break;
                }

                if (piece == null) {
                    strBuilder.append("   ");
                } else {
                    switch (piece.getTeamColor()) {
                        case WHITE -> strBuilder.append(EscapeSequences.SET_TEXT_COLOR_WHITE);
                        case BLACK -> strBuilder.append(EscapeSequences.SET_TEXT_COLOR_GREEN);
                    }
                    switch (piece.getPieceType()) {
                        case ROOK -> strBuilder.append(" R ");
                        case KNIGHT -> strBuilder.append(" N ");
                        case BISHOP -> strBuilder.append(" B ");
                        case QUEEN -> strBuilder.append(" Q ");
                        case KING -> strBuilder.append(" K ");
                        case PAWN -> strBuilder.append(" P ");
                    }
                }
                strBuilder.append(EscapeSequences.RESET_TEXT_COLOR);
                strBuilder.append(EscapeSequences.RESET_BG_COLOR);
            }
            switch (light) {
                case 0 -> light = 1;
                case 1 -> light = 0;
            }
            switch (color) {
                case WHITE -> strBuilder.append(" " + (9 - i) + "\n");
                case BLACK -> strBuilder.append(" " + i + "\n");
            }
        }
        switch (color) {
            case WHITE -> strBuilder.append("   a  b  c  d  e  f  g  h\n");
            case BLACK -> strBuilder.append("   h  g  f  e  d  c  b  a\n");
        }
        return strBuilder.toString();
    }

    public void notify(String message) {
        GameGsonBuilder builder = new GameGsonBuilder();
        Gson serializer = builder.createSerializer();

        ServerMessage notification = serializer.fromJson(message, ServerMessage.class);
        switch (notification.getServerMessageType()) {
            case LOAD_GAME -> System.out.println(loadGame(message));
            case ERROR -> System.out.println((serializer.fromJson(
                    message, ErrorMessage.class)).getMessage());
            case NOTIFICATION -> System.out.println((serializer.fromJson(
                    message, NotificationMessage.class)).getMessage());
        }
        printPrompt();
    }

    public String loadGame(String message) {
        GameGsonBuilder builder = new GameGsonBuilder();
        Gson serializer = builder.createSerializer();

        LoadGameMessage loadMsg = serializer.fromJson(message, LoadGameMessage.class);

        currentGame = loadMsg.getGame();
        return printBoard(currentGame.getBoard(), currentColor);
    }
}
