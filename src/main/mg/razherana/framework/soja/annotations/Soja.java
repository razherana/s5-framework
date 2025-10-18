package mg.razherana.framework.soja.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Bean annotation for the framework.
 * <p>
 * Used to mark a method, a field, a constructor, or a parameter for soja
 * injection.
 * </p>
 * <p>
 * Soja is also used to mark an another annotation as a soja annotation. No effect on classes.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.CONSTRUCTOR,
    ElementType.TYPE
})
public @interface Soja {
  String name() default "";

  /**
   * Type of the soja.
   */
  String type() default "";

  /**
   * Custom properties to add to the Soja information container.
   * Only the first ':' character will be used to split the key and value.
   * Syntax : {"key1:value1", "key2:value2"}
   * 
   * @return
   */
  String[] customProperties() default {};
}
