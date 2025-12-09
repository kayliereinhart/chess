package client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WsFacade extends Endpoint  {

    Session session;
    ServerMessageObserver observer;
    URI socketURI;
    WebSocketContainer container;

    public WsFacade(String url, ServerMessageObserver observer) throws Exception {
        try {
            url = url.replace("http", "ws");
            this.socketURI = new URI(url + "/ws");
            this.observer = observer;
            this.container = ContainerProvider.getWebSocketContainer();

//            //set message handler
//            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
//                @Override
//                public void onMessage(String message) {
//                    try {
//                       ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
//                        observer.notify(message);
//                    } catch (Exception e) {
//                        observer.notify(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
//                    }
//                }
            //});
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void connectToGame(String authToken, int id) throws Exception {
        try {
            this.session = container.connectToServer(this, socketURI);
            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    try {
//                        ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                        observer.notify(message);
                    } catch (Exception e) {
                        observer.notify(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR)));
                    }
                }
            });

            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, id);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
