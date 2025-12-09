package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import gsonbuilder.GameGsonBuilder;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.ConnectCommand;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

public class WsHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final SQLAuthDAO authDAO;
    private final SQLGameDAO gameDAO;

    public WsHandler() throws Exception {
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        int id = -1;
        Session session = ctx.session;

        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, new Gson().fromJson(ctx.message(), ConnectCommand.class));
                case LEAVE -> leave(session, command);
                case RESIGN -> resign(command);
                case MAKE_MOVE -> makeMove(session, new Gson().fromJson(ctx.message(), MoveCommand.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, ConnectCommand command) throws Exception {
        GameGsonBuilder builder = new GameGsonBuilder();
        Gson serializer = builder.createSerializer();

        try {
            String username = authDAO.getAuth(command.getAuthToken()).username();
            ChessGame game = gameDAO.getGame(command.getGameID()).game();

            connections.add(command.getGameID(), session);

            var loadMsg = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            String msg = serializer.toJson(loadMsg);
            session.getRemote().sendString(msg);

            ChessGame.TeamColor color = command.getColor();
            String message;

            if (color == null) {
                message = String.format("%s joined the game as an observer", username);
            } else {
                message = String.format("%s joined the game as %s", username, color);
            }
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(command.getGameID(), session, notification);
        } catch (Exception e) {
            String message = e.getMessage();
            var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(new Gson().toJson(errorMsg));
        }
    }

    private void leave(Session session, UserGameCommand command) throws Exception {
        connections.remove(command.getGameID(), session);
        String username = authDAO.getAuth(command.getAuthToken()).username();

        GameData gameData = gameDAO.getGame(command.getGameID());

        if (Objects.equals(gameData.whiteUsername(), username)) {
            gameDAO.addPlayer(null, ChessGame.TeamColor.WHITE, command.getGameID());
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            gameDAO.addPlayer(null, ChessGame.TeamColor.BLACK, command.getGameID());
        }

        String message = String.format("%s left the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), null, notification);
    }

    private void resign(UserGameCommand command) throws Exception {
        ChessGame game = gameDAO.getGame(command.getGameID()).game();
        game.changeStatus(ChessGame.GameStatus.OVER);
        gameDAO.updateGame(command.getGameID(), game);

        String username = authDAO.getAuth(command.getAuthToken()).username();

        String message = String.format("%s resigned", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), null, notification);
    }

    private void makeMove(Session session, MoveCommand command) throws Exception {
        String username = authDAO.getAuth(command.getAuthToken()).username();
        GameData gameData = gameDAO.getGame(command.getGameID());
        ChessGame game = gameData.game();

        try {
            game.makeMove(command.getMove());
            gameDAO.updateGame(command.getGameID(), game);

            var loadMsg = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcast(command.getGameID(), null, loadMsg);

            String message = String.format("%s made move: %s", username, command.getMove());
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(command.getGameID(), session, notification);

            if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                message = "White is in check";
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(command.getGameID(), null, notification);
            } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                message = "Black is in check";
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(command.getGameID(), null, notification);
            } else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                message = "White is in checkmate";
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(command.getGameID(), null, notification);
            } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                message = "Black is in checkmate";
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(command.getGameID(), null, notification);
            } else if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                message = "White is in stalemate";
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(command.getGameID(), null, notification);
            } else if (game.isInStalemate(ChessGame.TeamColor.BLACK)) {
                message = "Black is in stalemate";
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(command.getGameID(), null, notification);
            }
        } catch (InvalidMoveException e) {
            String message = String.format(e.getMessage());
            var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(new Gson().toJson(errorMsg));
        }
    }
}
