import chess.*;
import client.Client;

public class Main {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";

        try {
            new Client(serverUrl).run();
        } catch (Throwable e) {
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }
    }
}