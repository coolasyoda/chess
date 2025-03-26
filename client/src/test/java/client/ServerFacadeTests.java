package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ChessClient;

import java.util.UUID;


public class ServerFacadeTests {

    private static Server server;
    static ChessClient client;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        client = new ChessClient("http://localhost:" + port);
        client.register("username", "password", null);
        client.logout();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void posRegister() {
        String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        Assertions.assertEquals(1, client.register(randomString, "password", null));
    }

    @Test
    public void negRegister() {
        Assertions.assertEquals(0, client.register(null, null, null));
        client.register("testUser", "password", null);
        Assertions.assertEquals(0, client.register("testUser", "password", null));
    }

    @Test
    public void posLogin() {
        Assertions.assertEquals(1, client.login("username", "password"));
        client.logout();
    }

    @Test
    public void negLogin() {
        Assertions.assertEquals(0, client.login("noUser", "password"));
        Assertions.assertEquals(0, client.login("username", "badPassword"));
    }

    @Test
    public void posLogout() {
        client.login("username", "password");
        Assertions.assertEquals(1, client.logout());
    }

    @Test
    public void negLogout() {
        Assertions.assertEquals(0, client.logout());
    }

    @Test
    public void posCreate() {
        client.login("username", "password");
        Assertions.assertNotEquals(0, client.create("GAME"));
        client.logout();
    }

    @Test
    public void negCreate() {
        client.login("username", "password");
        client.create("GAME test");
        Assertions.assertEquals(0, client.create("test", "test"));
        client.logout();
    }

    @Test
    public void posJoin() {
        client.login("username", "password");
        int gameID = client.create("GAME test");
        Assertions.assertEquals(1, client.join(String.valueOf(gameID), "white"));
        Assertions.assertEquals(1, client.join(String.valueOf(gameID), "black"));
        client.logout();
    }

    @Test
    public void negJoin() {
        client.login("username", "password");
        int gameID = client.create("GAME test");
        Assertions.assertEquals(0, client.join(String.valueOf(gameID), "GREEN"));
        Assertions.assertEquals(0, client.join(String.valueOf(gameID + 1), "black"));
        client.logout();
    }

    @Test
    public void posList() {
        client.login("username", "password");
        int gameID = client.create("GAME test");
        Assertions.assertEquals(1, client.list());
        client.logout();
    }

    @Test
    public void negList() {
        Assertions.assertEquals(0, client.list());
    }

}
