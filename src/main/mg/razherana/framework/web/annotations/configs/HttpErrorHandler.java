package mg.razherana.framework.web.annotations.configs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.razherana.framework.haricot.annotations.Haricot;

/**
 * This annotation marks a class as the HTTP error handler for the web
 * framework.
 * The annotated class will be used to handle HTTP errors globally.
 * 
 * <p>
 * You may create a class annotated with @HttpErrorHandler or create a class and
 * extend
 * on one of the existing implementations such as
 * {@link mg.razherana.framework.web.errors.DefaultHttpErrorHandler
 * DefaultHttpErrorHandler}.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Haricot(name = HttpErrorHandler.TYPE, type = HttpErrorHandler.TYPE, autoInstantiate = true)
public @interface HttpErrorHandler {
  public static final String TYPE = "system/http-error-handler";

  /**
   * The name of the method in the class that will handle HTTP errors.
   * This method should have the appropriate signature to process errors.
   * The method signature is:
   * 
   * <pre>
   * public void handleError(HttpServletRequest, HttpServletResponse, HttpException) throws Exception
   * </pre>
   * 
   * @return the method name
   */
  public String methodName() default "handleError";
}
