package mg.razherana.framework.haricot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bean annotation for the framework.
 * Used to mark a class as a haricot so we can search it and instantiate it.
 * A haricot is a class that will be managed by the framework.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Haricot {
  String name() default "";

  /**
   * If true, the haricot will be automatically instantiated by the framework.
   */
  boolean autoInstantiate() default false;

  /**
   * Type of the haricot.
   */
  String type() default "";

  /**
   * Custom properties to add to the haricot information container.
   * Only the first ':' character will be used to split the key and value.
   * Syntax : {"key1:value1", "key2:value2"}
   */
  String[] customProperties() default {};
}
