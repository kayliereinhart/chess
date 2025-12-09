package server.websocket;

import com.google.gson.Gson;
import dataaccess.SQLAuthDAO;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WsHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final SQLAuthDAO authDAO;

    public WsHandler() throws Exception {
        authDAO = new SQLAuthDAO();
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
            connections.add(id, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException {
        // connections.add(command.getGameID(), session);
        var loadMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        String msg = new Gson().toJson(loadMsg);
        session.getRemote().sendString(msg);

        String message = String.format("%s joined the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), session, notification);
    }
}
