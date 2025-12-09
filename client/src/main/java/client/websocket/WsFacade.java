package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import gsonbuilder.GameGsonBuilder;
import jakarta.websocket.*;
import websocket.commands.ConnectCommand;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WsFacade extends Endpoint  {

    Session session;
    ServerMessageObserver observer;
    URI socketURI;
    WebSocketContainer container;
    Gson serializer;

    public WsFacade(String url, ServerMessageObserver observer) throws Exception {
        try {
            url = url.replace("http", "ws");
            this.socketURI = new URI(url + "/ws");
            this.observer = observer;
            this.container = ContainerProvider.getWebSocketContainer();

            GameGsonBuilder builder = new GameGsonBuilder();
            serializer = builder.createSerializer();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void connectToGame(String authToken, int id, ChessGame.TeamColor color) throws Exception {
        try {
            this.session = container.connectToServer(this, socketURI);
            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    try {
                        observer.notify(message);
                    } catch (Exception e) {
                        observer.notify(serializer.toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    }
                }
            });

            var command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, id, color);
            this.session.getBasicRemote().sendText(serializer.toJson(command));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void leaveGame(String authToken, Integer id) throws Exception {
        var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, id);
        this.session.getBasicRemote().sendText(serializer.toJson(command));
        this.session.close();
    }

    public void resign(String authToken, Integer id) throws Exception {
        var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, id);
        this.session.getBasicRemote().sendText(serializer.toJson(command));
    }

    public void makeMove(String authToken, Integer id, String start, String end,
                         ChessPiece.PieceType promotion) throws Exception {
        int startRow = Character.getNumericValue(start.charAt(1));
        int startCol = convertToNum(start.charAt(0));

        int endRow = Character.getNumericValue(end.charAt(1));
        int endCol = convertToNum(end.charAt(0));

        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ChessPosition endPosition = new ChessPosition(endRow, endCol);

        ChessMove move = new ChessMove(startPosition, endPosition, promotion);

        MoveCommand command = new MoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, id, move);
        this.session.getBasicRemote().sendText(serializer.toJson(command));
    }

    private int convertToNum(Character c) {
        int num;
        switch (c) {
            case 'a' -> num = 1;
            case 'b' -> num = 2;
            case 'c' -> num = 3;
            case 'd' -> num = 4;
            case 'e' -> num = 5;
            case 'f' -> num = 6;
            case 'g' -> num = 7;
            case 'h' -> num = 8;
            default -> num = -1;
        }
        return num;
    }
}
