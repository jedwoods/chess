package dataaccess.authDataBase;

public interface dbClass<S, T> {


  public void remove(T tokenType);


  public S get(T tokenType);


  public void add(S token);

  public int size();

}
