package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private ServerMessage.ServerMessageType type;
    private final String errorMessage;

    public ErrorMessage(ServerMessage.ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return errorMessage;
    }
}
