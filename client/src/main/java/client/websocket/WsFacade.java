package client.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import gsonbuilder.GameGsonBuilder;
import jakarta.websocket.*;
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

    public void connectToGame(String authToken, int id) throws Exception {
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

            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, id);
            this.session.getBasicRemote().sendText(serializer.toJson(command));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
