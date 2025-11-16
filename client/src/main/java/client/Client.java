package client;

import model.UserData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private final ServerFacade server;
    private State state = State.LOGGEDOUT;
    private String username = null;

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
                case "quit" -> "quit";
                default -> "not recognized";
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
        return "    loggedin";
    }

    private String register(String... params) throws Exception {
        if (params.length == 3) {
            UserData user = new UserData(params[0], params[1], params[2]);
            server.register(user);
            state = State.LOGGEDIN;
            return "You registered as " + params[0];
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String login(String... params) throws Exception {
        if (params.length == 2) {
            UserData user = new UserData(params[0], params[1], null);
            server.login(user);
            state = State.LOGGEDIN;
            return "You logged in as " + params[0];
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD>");
    }
}
