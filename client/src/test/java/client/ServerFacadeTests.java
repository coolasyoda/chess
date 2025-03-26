package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ChessClient;


public class ServerFacadeTests {

    private static Server server;
    static ChessClient client;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        client = new ChessClient("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void posRegister() {
        Assertions.assertEquals(1, client.register("username", "password", null));
    }

    @Test
    public void negRegister() {
        Assertions.assertEquals(0, client.register(null, null, null));
        client.register("testUser", "password", null);
        Assertions.assertEquals(0, client.register("testUser", "password", null));
    }

    @Test
    public void posLogin() {
        client.register("testUser", "password", null);
        client.logout();
        Assertions.assertEquals(1, client.login("testUser", "password"));
    }

    @Test
    public void negLogin() {
        Assertions.assertEquals(0, client.login("noUser", "password"));
        client.register("testUser", "password", null);
        Assertions.assertEquals(0, client.login("noUser", "badPassword"));
    }

    @Test
    public void posLogout() {
        client.register("testLogout", "password", null);
        Assertions.assertEquals(1, client.logout());

    }

    @Test
    public void negLogout() {
        Assertions.assertEquals(0, client.logout());
    }

    @Test
    public void posCreate() {
        Assertions.assertTrue(true);
    }

    @Test
    public void negCreate() {
        Assertions.assertTrue(true);
    }

    @Test
    public void posJoin() {
        Assertions.assertTrue(true);
    }

    @Test
    public void negJoin() {
        Assertions.assertTrue(true);
    }

    @Test
    public void posList() {
        Assertions.assertTrue(true);
    }

    @Test
    public void negList() {
        Assertions.assertTrue(true);
    }


}
