package mg.razherana.framework.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.razherana.framework.haricot.annotations.Haricot;

@Haricot(autoInstantiate = true, type = "controller")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {
  public String TYPE = "controller";

  /**
   * Contains the URI of where the controller will be mapped.
   */
  public String value() default "";
}
