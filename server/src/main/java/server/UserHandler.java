package server;
import com.google.gson.Gson;
import model.UserData;
import spark.Request;
import spark.Response;

public class UserHandler {

    private static final Gson gson = new Gson();

    public UserHandler(){

    }

    // REGISTRATION:
    public Object register(Request req, Response res){
        UserData userData = gson.fromJson(req.body(), UserData.class);
        System.out.println("Registering User: " + userData.username());

        return "";
    }

    // LOGIN:
    // login(LoginRequest)

    // LOGOUT:
    // delete(SessionRequest)
}
