package mg.razherana.framework.web.annotations.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.razherana.framework.soja.annotations.Soja;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Soja(type = "controller/get")
public @interface GetRouting {
  public String TYPE = "controller/get";
  
  /**
   * The URI path for the GET request mapping.
   * Any trailing or leading slashes will be ignored.
   * <p>
   *  For example, "/users/list" or "users/list" or "users/list/" are all valid and equivalent.
   * </p>
   */
  String value() default "";
}
