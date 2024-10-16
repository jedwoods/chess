package dataAccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    transient int statusCode;
    String message;
    public DataAccessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int StatusCode() {return statusCode;
    }

    public String getMessage(){
        return this.message;
    }

}
