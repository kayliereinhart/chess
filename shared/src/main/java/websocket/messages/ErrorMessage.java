package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private ServerMessage.ServerMessageType type;
    private final String message;

    public ErrorMessage(ServerMessage.ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
