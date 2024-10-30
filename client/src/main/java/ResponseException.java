public class ResponseException extends Exception{
  transient int statusCode;
  String message;
  public ResponseException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
    this.message = message;
  }

  public int statusCode() {return statusCode;
  }

  public String getMessage(){
    return this.message;
  }



}
