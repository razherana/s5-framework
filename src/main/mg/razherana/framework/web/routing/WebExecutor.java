package mg.razherana.framework.web.routing;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.razherana.framework.haricot.HaricotInstantiator;
import mg.razherana.framework.injection.annotations.Inject;
import mg.razherana.framework.web.annotations.parameters.CreateSession;
import mg.razherana.framework.web.annotations.parameters.ParamVar;
import mg.razherana.framework.web.annotations.parameters.PathVar;
import mg.razherana.framework.web.annotations.parameters.PathVars;
import mg.razherana.framework.web.containers.WebRouteContainer;
import mg.razherana.framework.web.exceptions.MalformedWebAnnotationException;
import mg.razherana.framework.web.exceptions.http.BadRequestException;
import mg.razherana.framework.web.utils.ConversionUtils;

public class WebExecutor {
  private WebRouteContainer webRouteContainer;
  private HaricotInstantiator haricotInstantiator;

  public WebExecutor(WebRouteContainer webRouteContainer) {
    this.webRouteContainer = webRouteContainer;
  }

  public WebExecutor(WebRouteContainer webRouteContainer,
      HaricotInstantiator haricotInstantiator) {
    this.webRouteContainer = webRouteContainer;
    this.haricotInstantiator = haricotInstantiator;
  }

  public void execute(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    var method = webRouteContainer.getMethod();
    var controllerInstance = webRouteContainer
        .getControllerInstance();

    var pathParameters = webRouteContainer.getPathParameters();

    System.out.println("[Fruits] : Executing method "
        + method.getName() + " of controller "
        + controllerInstance.getClass().getName());

    System.out
        .println("[Fruits] : Path parameters: " + pathParameters);

    var methodArgs = resolveMethodArgs(method, pathParameters,
        request, response);

    method.invoke(controllerInstance, methodArgs);
  }

  private Object[] resolveMethodArgs(Method method,
      HashMap<String, String> pathParameters,
      HttpServletRequest request, HttpServletResponse response) {
    var args = method.getParameters();
    var argInstances = new Object[args.length];

    for (int i = 0; i < args.length; i++) {
      var arg = args[i];
      var argType = arg.getType();

      // Check if annotated with @PathVar
      if (arg.isAnnotationPresent(PathVar.class)) {
        var pathVar = arg.getAnnotation(PathVar.class);
        var varName = pathVar.value();

        var varValue = pathParameters.get(varName);

        // Convert to appropriate type
        Object convertedValue = ConversionUtils
            .convertStringToType(varValue, argType);
        argInstances[i] = convertedValue;
        continue;
      }

      // Check for HttpServletRequest and HttpServletResponse
      if (argType == HttpServletRequest.class) {
        argInstances[i] = request;
        continue;
      }

      if (argType == HttpServletResponse.class) {
        argInstances[i] = response;
        continue;
      }

      // Check for @Inject
      if (arg.isAnnotationPresent(Inject.class)) {
        if (haricotInstantiator == null) {
          throw new RuntimeException(
              "HaricotInstantiator not configured for dependency injection in WebExecutor");
        }

        var injectAnnotation = arg.getAnnotation(Inject.class);
        var injectName = injectAnnotation.name();

        Object injectedInstance;

        try {
          // Use the same logic as the HaricotFactory for consistency
          var haricotClasses = haricotInstantiator.getFactory()
              .getHaricotClasses();
          var sojaClasses = haricotInstantiator.getFactory()
              .getSojaClasses();

          var haricotAnnotations = haricotClasses.get(argType);
          var sojaClassContainer = sojaClasses.get(argType);

          if (haricotAnnotations == null) {
            throw new RuntimeException("No Haricot found for type: "
                + argType.getName()
                + ". Ensure the class is annotated with @Haricot and scanned by the framework.");
          }

          var haricotName = HaricotInstantiator
              .getHaricotName(argType, haricotAnnotations);

          // Use the factory's checkInjectionMethodAndInstantiate method
          injectedInstance = haricotInstantiator.getFactory()
              .checkInjectionMethodAndInstantiate(argType, true, // isInjectAnnotationPresent
                  haricotAnnotations, sojaClassContainer, haricotName,
                  injectName);

        } catch (Exception e) {
          throw new RuntimeException(
              "Failed to inject dependency for parameter: "
                  + arg.getName() + " of type: " + argType.getName()
                  + " in web method: " + method.getName(),
              e);
        }

        argInstances[i] = injectedInstance;
        continue;
      }

      // Check for @PathVars for path parameters
      if (arg.isAnnotationPresent(PathVars.class)) {
        // Check if the type is Map<String, String>
        if (argType == Map.class
            && arg.getParameterizedType().getTypeName().equals(
                "java.util.Map<java.lang.String, java.lang.String>")) {
          argInstances[i] = pathParameters;
          continue;
        }

        // If not the correct type, throw an exception
        throw new MalformedWebAnnotationException(
            "@PathVars can only be applied to parameters of type Map<String, String> in method: "
                + method.getName());

      }

      // Check for @ParamVar
      if (arg.isAnnotationPresent(ParamVar.class)) {
        var paramVar = arg.getAnnotation(ParamVar.class);
        var varName = paramVar.value();

        var varValue = request.getParameter(varName);

        if (varValue == null) {
          if (paramVar.required()) {
            // The request object is being stored as additional data in the exception.
            throw new BadRequestException("Missing required parameter: " + varName, request);
          }

          // Use default value
          varValue = paramVar.defaultValue();
        }

        // Convert to appropriate type
        Object convertedValue = ConversionUtils
            .convertStringToType(varValue, argType);

        argInstances[i] = convertedValue;
        continue;
      }

      // Check if ServletContext
      if (argType.equals(ServletContext.class)) {
        argInstances[i] = request.getServletContext();
        continue;
      }

      // Check if HttpSession
      if (argType.equals(HttpSession.class)) {
        argInstances[i] = request
            .getSession(arg.isAnnotationPresent(CreateSession.class));
        continue;
      }

      // Throw exception for unsupported parameter types
      throw new MalformedWebAnnotationException(
          "Unsupported parameter type: " + argType.getName()
              + " in method: " + method.getName());

    }

    return argInstances;
  }

  public WebRouteContainer getWebRouteContainer() {
    return webRouteContainer;
  }

  public void setWebRouteContainer(
      WebRouteContainer webRouteContainer) {
    this.webRouteContainer = webRouteContainer;
  }

  public Object getControllerInstance() {
    return webRouteContainer.getControllerInstance();
  }
}
