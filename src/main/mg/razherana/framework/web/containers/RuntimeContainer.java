package mg.razherana.framework.web.containers;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.razherana.framework.haricot.HaricotInstantiator;
import mg.razherana.framework.web.exceptions.http.HttpException;
import mg.razherana.framework.web.finders.WebFinder;

public class RuntimeContainer {
  private WebFinder webFinder;
  private HaricotInstantiator haricotInstantiator;

  private Object httpErrorHandlerInstance;
  private Method httpErrorHandlerMethod;

  public RuntimeContainer(
      WebFinder webFinder,
      HaricotInstantiator haricotInstantiator,
      Object httpErrorHandlerInstance,
      Method httpErrorHandlerMethod) {
    this.webFinder = webFinder;
    this.haricotInstantiator = haricotInstantiator;
    this.httpErrorHandlerInstance = httpErrorHandlerInstance;
    this.httpErrorHandlerMethod = httpErrorHandlerMethod;
  }

  public void handleHttpError(HttpServletRequest request, HttpServletResponse response, HttpException exception) {
    try {
      if (httpErrorHandlerInstance != null && httpErrorHandlerMethod != null) {
        httpErrorHandlerMethod.invoke(httpErrorHandlerInstance, request, response, exception);
      } else {
        // Default behavior if no custom handler is set
        response.sendError(exception.getStatus(), exception.getMessage());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public WebFinder getWebFinder() {
    return webFinder;
  }

  public void setWebFinder(WebFinder webFinder) {
    this.webFinder = webFinder;
  }

  public HaricotInstantiator getHaricotInstantiator() {
    return haricotInstantiator;
  }

  public void setHaricotInstantiator(HaricotInstantiator haricotInstantiator) {
    this.haricotInstantiator = haricotInstantiator;
  }

  public void setHttpErrorHandlerInstance(Object httpErrorHandlerInstance) {
    this.httpErrorHandlerInstance = httpErrorHandlerInstance;
  }

  public void setHttpErrorHandlerMethod(Method httpErrorHandlerMethod) {
    this.httpErrorHandlerMethod = httpErrorHandlerMethod;
  }
}
