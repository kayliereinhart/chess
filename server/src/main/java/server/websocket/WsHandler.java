package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WsHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        int id = -1;
        Session session = ctx.session;

        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            id = command.getGameID();
            //String username = getUsername(command.getAuthToken());
            connections.add(id, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, "username", command);
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
        //connections.add(id, session);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(command.getGameID(), session, notification);
    }

//    private void exit(String visitorName, Session session) throws IOException {
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new ServerMessage(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
//        connections.remove(session);
//    }
//
//    public void makeNoise(String petName, String sound) throws Exception {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast(null, notification);
//        } catch (Exception ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
//    }
}
