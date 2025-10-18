package mg.razherana.framework.web.exceptions.http;

import mg.razherana.framework.web.exceptions.WebExecutionException;

public class HttpException extends WebExecutionException {
  private int status;

  public HttpException(String message, int status) {
    super(message);
    this.status = status;
  }

  public HttpException(String message, int status, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}
