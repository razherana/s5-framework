package mg.razherana.framework.haricot.exceptions;

public class HaricotException extends RuntimeException {
  public HaricotException(String message) {
    super(message);
  }

  public HaricotException(String message, Throwable cause) {
    super(message, cause);
  }
}
