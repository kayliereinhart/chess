package client;

import chess.ChessGame;
import model.*;
import server.ServerFacade;

import java.util.*;

public class Client {

    private final ServerFacade server;
    private State state = State.LOGGEDOUT;
    private ArrayList<GameData> gameList = null;
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

        gameList = new ArrayList<>(server.listGames(authToken).games());
        StringBuilder strBuilder = new StringBuilder();

        for (int i = 0; i < gameList.size(); i++) {
            GameData game = gameList.get(i);
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
            Integer id;

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

            JoinGameRequest request = new JoinGameRequest(username, color, id);
            server.joinGame(request, authToken);

            return username + " joined game " + id + " as " + params[1];
        }
        throw new Exception("Expected: <ID> [WHITE|BLACK]");
    }

    private String observe(String... params) throws Exception {
        assertLoggedIn();

        if (params.length == 1) {
            try {
                Integer id = Integer.parseInt(params[0]);
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
}
