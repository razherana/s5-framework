package mg.razherana.framework.web.annotations.parameters;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import mg.razherana.framework.soja.annotations.Soja;

/**
 * Annotation to mark a method parameter as the path variables.
 * The type should be Map<String, String> or else it will throw an exception.
 */
@Soja(type = PathVars.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.PARAMETER })
public @interface PathVars {
  public static final String TYPE = "pathvars";
}
