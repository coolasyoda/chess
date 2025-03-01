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

    // REGISTRATION:
    public Object register(Request req, Response res){
        UserData userData = gson.fromJson(req.body(), UserData.class);
        System.out.println("Registering User: " + userData.username());

        if(userService.getUser(userData.username()) != null){
            System.out.println("Username: " + userData.username() + " is already taken.");
            res.status(400);
            return gson.toJson(Map.of("error", "Username is already taken"));
        }

        AuthData authData = userService.registerUser(userData);

        return gson.toJson(authData);
    }

    // LOGIN:
    // login(LoginRequest)

    // LOGOUT:
    // delete(SessionRequest)
}
