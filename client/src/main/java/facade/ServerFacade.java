package facade;

import java.io.*;
import java.net.*;
import java.util.HashSet;

import com.google.gson.Gson;
import records.*;
import ui.LogoutObject;
import ui.ServerObserver;


public class ServerFacade {
  private final String serverUrl;
  private String authToken = null;
  private String userName;
//  private int joinedGame;

  public ServerFacade (String serverUrl){
    this.serverUrl=serverUrl;
  }

  public String getAuthToken(){
    return this.authToken;
  }


  public ResponseObject createGame(String gameName) throws ResponseException {
    String path = "/game";
    try {
      record NewGame(String gameName){}
      record GameResponse(int gameID){}
      makeRequest("POST", path,new NewGame(gameName), GameResponse.class, this.authToken);
      return new ResponseObject(200, "New Game Created");
    } catch (Exception e){
      throw new ResponseException(500, e.getMessage());
    }
  }

  public ResponseObject login(String username, String password) throws ResponseException {
    String path = "/session";
    try {

      AuthToken user = makeRequest("POST", path,new User(username, password, null), AuthToken.class, null );
      this.authToken = user.authToken();
      this.userName = user.username();
      return new ResponseObject(200, "You are now logged in brotha");
    } catch (Exception e){
      throw new ResponseException(500, "failed to login, incorrect user or password");
    }
  }


  public ResponseObject register(String username, String password, String email) throws ResponseException {
    String path="/user";
    try {
      AuthToken user=makeRequest("POST", path, new User(username, password, email), AuthToken.class, this.authToken);
      this.authToken=user.authToken();
      this.userName=user.username();
      return new ResponseObject(200, "You are now logged in brotha");
    } catch (Exception e) {
      throw new ResponseException(500, e.getMessage());
    }
  }


  public void logout() throws ResponseException {
    String path = "/session";
    try {
      AuthToken user = makeRequest("DELETE", path, new Gson().toJson(new LogoutObject(this.authToken)), null, this.authToken);
      this.authToken = null;
      new ResponseObject(200, "You are now logged out brotha");
    } catch (Exception e){
      throw new ResponseException(500, e.getMessage());
    }

  }


  public HashSet<GameData> listGames() throws ResponseException {
    var path="/game";
    record ListGameResponse(HashSet<GameData> games) {
    }
    record Token(String authorization) {
    }
    var response=this.makeRequest("GET", path, new Token(this.authToken), ListGameResponse.class, this.authToken);
    return response.games();
  }


  public ResponseObject joinGame(String playerColor, int gameID) throws ResponseException {
    var path = "/game";
    record JoinGameObject(String playerColor, int gameID) {
    }
    return this.makeRequest("PUT", path, new JoinGameObject(playerColor, gameID), null,this.authToken);
  }

  private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String auth) throws ResponseException {
    try {
      URL url = (new URI(serverUrl + path)).toURL();
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setRequestMethod(method);
      if (auth != null){
        http.setRequestProperty ("Authorization", auth);
      }
      if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
        http.setDoOutput(true);
        writeBody(request, http); // Write body only for POST/PUT requests
      } else {
        http.setDoOutput(false); // Ensure no output for GET requests
      }


//      writeBody(request, http);

      http.connect();
      throwIfNotSuccessful(http);
      return readBody(http, responseClass);
    } catch (Exception ex) {
      throw new ResponseException(500, ex.getMessage());
    }

  }

  private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
    T response = null;
    if (http.getContentLength() < 0) {
      try (InputStream respBody = http.getInputStream()) {
        InputStreamReader reader = new InputStreamReader(respBody);
        if (responseClass != null) {
          response = new Gson().fromJson(reader, responseClass);
        }
      }
    }
    return response;
  }

  private static void writeBody(Object request, HttpURLConnection http) throws IOException {

    if (request != null) {
      http.addRequestProperty("Content-Type", "application/json");
      String reqData = new Gson().toJson(request);
      try (OutputStream reqBody = http.getOutputStream()) {
        reqBody.write(reqData.getBytes());
      }
    }
  }



  private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
    int status = http.getResponseCode();
    if (!isSuccessful(status)) {
      // Read the error stream to get the response message
      StringBuilder message = new StringBuilder();
       messageReader(message, http);
        }
      }

  private void messageReader(StringBuilder message, HttpURLConnection http) throws IOException, ResponseException {
    int status = http.getResponseCode();
    try (var errorStream = http.getErrorStream()) {
      if (errorStream != null) {
        try (var reader = new BufferedReader(new InputStreamReader(errorStream))) {
          String line;
          while ((line = reader.readLine()) != null) {
            message.append(line).append("\n");
          }
        }
      } else {
        message.append("No error message available.");
      }
    }
    ErrorMessage message2 = new Gson().fromJson(message.toString().trim(), ErrorMessage.class);
    throw new ResponseException(status, message2.message());
  }


  private boolean isSuccessful(int status) {
    return status / 100 == 2;
  }
}