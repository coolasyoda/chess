package client.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

//need to extend Endpoint for websocket to work properly
public class WebsocketFacade extends Endpoint {

    Session session;


    public WebsocketFacade(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    messageHandle(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void messageHandle(String message){
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

        if(Objects.equals(jsonObject.get("serverMessageType").getAsString(), "NOTIFICATION")){
            System.out.println("NOTIFICATION: " + jsonObject.get("message").getAsString());
        }

        if(Objects.equals(jsonObject.get("serverMessageType").getAsString(), "LOAD_GAME")){
            System.out.println("NOTIFICATION: " + jsonObject.get("message").getAsString());
        }

        System.out.println("messageHandle: " + message);
    }

    public void sendCommand(String command){
        this.session.getAsyncRemote().sendText(command);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }





}