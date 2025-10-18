package mg.razherana.framework;

import java.lang.reflect.Method;

import jakarta.servlet.ServletConfig;
import mg.razherana.framework.haricot.HaricotInstantiator;
import mg.razherana.framework.haricot.exceptions.HaricotConfigurationException;
import mg.razherana.framework.web.annotations.configs.HttpErrorHandler;
import mg.razherana.framework.web.containers.RuntimeContainer;
import mg.razherana.framework.web.finders.WebFinder;

public class RunApplication {
  public static RuntimeContainer before(ServletConfig config) {
    var packageString = config.getServletContext().getInitParameter("haricot.scan.package");
    System.out.println("[Fruits] : Haricot scan package: " + packageString);

    if (packageString == null || packageString.isEmpty()) {
      throw new HaricotConfigurationException(null,
          "Haricot scan package not defined in web.xml, Please define it using 'haricot.scan.package' in servlet context init config.");
    }

    var haricotInstantiator = HaricotInstantiator
        .runOnPackages(packageString.trim().split(","));

    var webFinder = WebFinder.register(haricotInstantiator);

    System.out.println("[Fruits] : Registering Endpoints:");

    webFinder.printEndpoints();

    Object httpErrorHandlerInstance = haricotInstantiator
        .getHaricotContainer()
        .getInstance(HttpErrorHandler.TYPE);

    System.out.println("[Fruits] : Haricot instances : " + haricotInstantiator.getHaricotContainer().getInstances());

    Method httpErrorHandlerMethod = null;

    if (httpErrorHandlerInstance != null) {
      Class<?> httpErrorHandlerClass = httpErrorHandlerInstance.getClass();
      var httpErrorHandlerAnnot = httpErrorHandlerClass.getAnnotation(HttpErrorHandler.class);

      if (httpErrorHandlerAnnot != null) {
        try {
          httpErrorHandlerMethod = httpErrorHandlerClass.getMethod(
              httpErrorHandlerAnnot.methodName(),
              jakarta.servlet.http.HttpServletRequest.class,
              jakarta.servlet.http.HttpServletResponse.class,
              mg.razherana.framework.web.exceptions.http.HttpException.class);

        } catch (NoSuchMethodException | SecurityException e) {
          throw new HaricotConfigurationException(httpErrorHandlerClass,
              "The method '" + httpErrorHandlerAnnot.methodName()
                  + "' with the required parameters was not found in the HttpErrorHandler class.");
        }
      }
    }

    return new RuntimeContainer(webFinder, haricotInstantiator, httpErrorHandlerInstance, httpErrorHandlerMethod);
  }

  public static void after(RuntimeContainer runtimeContainer) {
    // TODO: Implement after run
  }
}
