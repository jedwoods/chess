import java.io.*;
import java.net.*;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.authdatabase.AuthToken;
import dataaccess.userdatabase.User;
import ui.logoutObject;


public class ServerFacade {
  private final String serverUrl;
  private String authToken;

  public ServerFacade ( String serverUrl){
    this.serverUrl=serverUrl;
  }


  public ResponseObject login(String username, String password) throws ResponseException {
    String path = "/session";
    try {
      AuthToken user = makeRequest("POST", path,new User(username, password, null), AuthToken.class );
      this.authToken = user.authToken();
      return new ResponseObject(200, "You are now logged in brotha");
    } catch (Exception e){
      throw new ResponseException(500, e.getMessage());
    }
  }


  public void logout() throws ResponseException {
    String path = "/session";
    try {
      AuthToken user = makeRequest("POST", path,new Gson().toJson(new logoutObject(this.authToken)), null);
      this.authToken = null;
      new ResponseObject(200, "You are now logged out brotha");
    } catch (Exception e){
      throw new ResponseException(500, e.getMessage());
    }

  }


  public ChessGame[] listGames() throws ResponseException {
    var path = "/game";
    record listGameResponse(ChessGame[] game) {
    }
    var response = this.makeRequest("GET", path, null, listGameResponse.class);
    return response.game();
  }





  private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
    try {
      URL url = (new URI(serverUrl + path)).toURL();
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setRequestMethod(method);
      http.setDoOutput(true);

      writeBody(request, http);
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
    var status = http.getResponseCode();
    if (!isSuccessful(status)) {
      throw new ResponseException(status, "failure: " + status);
    }
  }


  private boolean isSuccessful(int status) {
    return status / 100 == 2;
  }
}
