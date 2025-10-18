package mg.razherana.framework.web.annotations.parameters;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import mg.razherana.framework.soja.annotations.Soja;

/**
 * Annotation to mark a method parameter as a parameter variable.
 */
@Soja(type = ParamVar.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.PARAMETER })
public @interface ParamVar {
  public static final String TYPE = "web/paramvar";

  /**
   * Name of the parameter variable.
   */
  String value();

  /**
   * Whether the parameter is required or not.
   */
  boolean required() default false;

  /**
   * Default value if the parameter is not present.
   */
  String defaultValue() default "";
}
