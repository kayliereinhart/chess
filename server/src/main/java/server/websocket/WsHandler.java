package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import gsonbuilder.GameGsonBuilder;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

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
            id = command.getGameID();
            String username = authDAO.getAuth(command.getAuthToken()).username();
            //connections.add(id, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, new Gson().fromJson(ctx.message(), ConnectCommand.class));
                case LEAVE -> leave(session, username, command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, ConnectCommand command) throws Exception {
        connections.add(command.getGameID(), session);
        GameGsonBuilder builder = new GameGsonBuilder();
        Gson serializer = builder.createSerializer();

        ChessGame game = gameDAO.getGame(command.getGameID()).game();
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
    }

    private void leave(Session session, String username, UserGameCommand command) throws Exception {
        connections.remove(command.getGameID(), session);

        String message = String.format("%s left the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), null, notification);
    }
}
