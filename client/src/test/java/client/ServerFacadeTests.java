//package client;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.gamedatabase.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private ServerFacade facade = new ServerFacade("http://localhost:8080");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        server.clear();

    }



    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }


    @Test
    public void register() throws ResponseException {
        ResponseObject response = facade.register("jed", "p", "email");
        assert(response.errorCode() == 200);
        logout();
    }

    @Test
    public void invalidRegister() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.register("BadUsername", null, "email"));
    }

    @Test
    public void login() throws ResponseException {
        facade.register("goodUser", "g", "email");
        facade.logout();
        ResponseObject response = facade.login("goodUser", "g");
        assert(response.errorCode() == 200);
        logout();
    }

    @Test
    void nullLogin(){
        assertThrows(ResponseException.class,() -> facade.login("bad username", null));
    }

    @Test
    void invalidLogin(){
        assertThrows(ResponseException.class,() -> facade.login("bad username", "not even a password"));
    }


    @Test
    void createGame() throws ResponseException {
        facade.register("anotherUser", "another", "email");
        int sze = facade.listGames().size();
        ResponseObject response = facade.createGame("newgame");
        int newSize = facade.listGames().size();
        assert newSize == sze + 1;
        logout();
    }

    @Test
    void invalidCreation(){
        assertThrows(ResponseException.class,() -> facade.createGame("gameName"));
    }

    @Test
    void logout() throws ResponseException {
        server.clear();
        facade.register("logoutUser1000", "pass20000", "email3000");
        facade.logout();
        assertThrows(ResponseException.class, () -> facade.createGame("logged out game"));
    }

    @Test
    void listGames() throws ResponseException {
        facade.register("list user", "rando pass", "email");
        int sze = facade.listGames().size();
        ResponseObject response = facade.createGame("newgame");
        int newSize = facade.listGames().size();
        assert newSize >= 1;
        logout();
    }

    @Test
    void invalidListGames(){
        assertThrows(ResponseException.class, () -> facade.listGames());
    }



    @Test
    void joinGame() throws ResponseException {
        facade.register("joinGameuser", "rando pass", "email");
        facade.createGame("game");
        facade.createGame("game2");
        HashSet<GameData> games = facade.listGames();
        facade.joinGame("BLACK", games.iterator().next().gameID());
        logout();
    }

    @Test
    void invalidJoin() throws ResponseException {
        server.clear();
        facade.register("joinGameuser", "rando pass", "email");
        facade.createGame("game");
        facade.listGames();
        assertThrows(ResponseException.class, () -> facade.joinGame("invalid color", 1));
        logout();
    }

    @Test
    void invalidJoinID() throws ResponseException {
        server.clear();
        facade.register("joinGameuser", "rando pass", "email");
        facade.createGame("game");
        facade.listGames();
        assertThrows(ResponseException.class, () -> facade.joinGame("invalid color", 1000000));
        logout();
    }

}
