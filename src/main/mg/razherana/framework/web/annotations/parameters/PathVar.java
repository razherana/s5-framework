package mg.razherana.framework.web.annotations.parameters;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import mg.razherana.framework.soja.annotations.Soja;

/**
 * Annotation to mark a method parameter as a path variable.
 */
@Soja(type = PathVar.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.PARAMETER })
public @interface PathVar {
  public static final String TYPE = "pathvar";

  /**
   * Name of the path variable.
   * @return
   */
  String value();
}
