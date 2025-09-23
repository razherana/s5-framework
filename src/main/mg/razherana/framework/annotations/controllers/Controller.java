package mg.razherana.framework.annotations.controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.razherana.framework.annotations.haricots.Haricot;

@Haricot
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {
  /**
   * Contains the URI of where the controller will be mapped.
   */
  public String value() default "";

  public String[] exclude() default {};
}
