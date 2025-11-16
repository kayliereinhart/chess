package client;

import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private final ServerFacade server;
    private State state = State.LOGGEDOUT;

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
                case "register" -> "register result";
                case "login" -> "login result";
                case "quit" -> "quit";
                default -> "not recognized";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String help() {
        if (state == State.LOGGEDOUT) {
            return "   register <USERNAME> <PASSWORD> <EMAIL> - to create an account\n" +
                    "   login <USERNAME> <PASSWORD> - to play chess\n" +
                    "   quit - playing chess\n" +
                    "   help - with possible commands";
        }
        return "    loggedin";
    }
}
