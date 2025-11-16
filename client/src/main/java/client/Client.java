package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.*;
import server.ServerFacade;

import java.util.*;

import ui.EscapeSequences;

public class Client {

    private final ServerFacade server;
    private State state = State.LOGGEDOUT;
    private HashMap<Integer, Integer> gameMap = null;
    private String username = null;
    private String authToken = null;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
        }
        return "   create <NAME> - a game\n" +
                "   list - games\n" +
                "   join <ID> [WHITE|BLACK] - a game\n" +
                "   observe <ID> - a game\n" +
                "   logout - when you are done\n" +
                "   quit - playing chess\n" +
                "   help - with possible commands";
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

            JoinGameRequest request = new JoinGameRequest(username, color, gameMap.get(id));
            server.joinGame(request, authToken);

            return printBoard();
        }
        throw new Exception("Expected: <ID> [WHITE|BLACK]");
    }

    private String observe(String... params) throws Exception {
        assertLoggedIn();

        if (params.length == 1) {
            try {
                int id = Integer.parseInt(params[0]);
                return "observe game" + id;
            } catch (Exception e) {
                throw new Exception("Error: ID should be an integer");
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

    private void assertLoggedIn() throws Exception {
        if (state == State.LOGGEDOUT) {
            throw new Exception("You must log in");
        }
    }

    private String printBoard() {
        StringBuilder strBuilder = new StringBuilder();
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        int light = 1;

        strBuilder.append("   a  b  c  d  e  f  g  h\n");

        for (int i = 1; i < 9; i++) {
            strBuilder.append(9 - i + " ");

            for (int j = 1; j < 9; j++) {
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
            strBuilder.append(" " + (9 - i) + "\n");
        }
        strBuilder.append("   a  b  c  d  e  f  g  h\n");
        return strBuilder.toString();
    }
}
