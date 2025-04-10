package websocket.messages;

public class NotificationMessage {
    private String message;

    public NotificationMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

}
