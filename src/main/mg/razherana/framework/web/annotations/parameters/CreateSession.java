package mg.razherana.framework.web.annotations.parameters;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import mg.razherana.framework.soja.annotations.Soja;

/**
 * Annotation to mark a method parameter as a create session indicator.
 * If this annotation is present, a new session will be created for the request if one does not already exist.
 */
@Soja(type = CreateSession.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.PARAMETER })
public @interface CreateSession {
  public static final String TYPE = "web/create-session";
}
