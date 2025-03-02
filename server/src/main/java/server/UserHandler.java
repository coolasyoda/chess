package server;
import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import spark.Request;
import spark.Response;
import service.UserService;

import java.util.Map;

public class UserHandler {

    private static final Gson gson = new Gson();
    UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public Object register(Request request, Response response){
        UserData userData = gson.fromJson(request.body(), UserData.class);

        // Check if the username and password fields are filled
        if(userData.username() == null || userData.password() == null){
            System.out.println("Username or Password empty");
            response.status(400);
            return gson.toJson(Map.of("message", "Error: bad request"));
        }

        System.out.println("Registering User: " + userData.username());

        // Make sure the username isn't taken
        if(userService.getUser(userData.username()) != null){
            System.out.println("Username: " + userData.username() + " is already taken.");
            response.status(403);
            return gson.toJson(Map.of("message", "Error: already taken"));
        }

        AuthData authData = userService.registerUser(userData);
        return gson.toJson(authData);
    }

    public Object login(Request request, Response response) {
        UserData userData = gson.fromJson(request.body(), UserData.class);
        System.out.println("Logging In User: " + userData.username());

        // Check to see if the user exists
        if(userService.getUser(userData.username()) == null){
            System.out.println("Username: " + userData.username() + " does not exist.");
            response.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        }

        // Returns the authData if login is valid, null otherwise
        AuthData authData = userService.loginUser(userData);
        if(authData == null){
            response.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        }

        response.status(200);
        return  gson.toJson(authData);
    }

    public Object logout(Request request, Response response){
        String authToken = request.headers("authorization");

        if(userService.logoutUser(authToken)){
            response.status(200);
            return "";
        }

        response.status(401);
        return gson.toJson(Map.of("message", "Error: unauthorized"));
    }

}
