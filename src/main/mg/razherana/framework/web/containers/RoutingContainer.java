package mg.razherana.framework.web.containers;

import java.lang.reflect.Method;

public class RoutingContainer {
  public static enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    OPTIONS,
    HEAD;
  }

  private HttpMethod httpMethod;
  private String path;
  private ControllerContainer parentControllerContainer;

  /**
   * The routing annotation (e.g., @Get, @Post, etc.)
   * Cast to the appropriate annotation when needed
   */
  private Object routingAnnotation;
  private Method methodReflection;

  public RoutingContainer(HttpMethod httpMethod, String path, ControllerContainer parentControllerContainer,
      Object routingAnnotation, Method methodReflection) {
    this.httpMethod = httpMethod;
    this.path = path;
    this.parentControllerContainer = parentControllerContainer;
    this.routingAnnotation = routingAnnotation;
    this.methodReflection = methodReflection;
  }

  public RoutingContainer(String httpMethod, String path, ControllerContainer parentControllerContainer,
      Object routingAnnotation, Method methodReflection) {
    this.httpMethod = HttpMethod.valueOf(httpMethod.toUpperCase());
    this.path = path;
    this.parentControllerContainer = parentControllerContainer;
    this.routingAnnotation = routingAnnotation;
    this.methodReflection = methodReflection;
  }

  public void setMethodReflection(Method methodReflection) {
    this.methodReflection = methodReflection;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public ControllerContainer getParentControllerContainer() {
    return parentControllerContainer;
  }

  public void setParentControllerContainer(ControllerContainer parentControllerContainer) {
    this.parentControllerContainer = parentControllerContainer;
  }

  public Object getRoutingAnnotation() {
    return routingAnnotation;
  }

  public void setRoutingAnnotation(Object routingAnnotation) {
    this.routingAnnotation = routingAnnotation;
  }

  public Method getMethodReflection() {
    return methodReflection;
  }

}
