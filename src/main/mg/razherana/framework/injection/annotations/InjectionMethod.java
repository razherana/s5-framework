package mg.razherana.framework.injection.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.razherana.framework.injection.enums.InjectionType;
import mg.razherana.framework.soja.annotations.Soja;

/**
 * Annotation to mark a method for dependency injection.
 * The method will be used as a sort of a constructor.
 * 
 * <b>Note:</b> The method must be non-static, and the default constructor of the class
 * must be accessible (public or protected) to allow instantiation before injection.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Soja(type = "injection_method")
public @interface InjectionMethod {
  String TYPE = "injection_method";
  
  String name() default "";

  /**
   * Type of injection to be used for this method.
   */
  InjectionType injectionType() default InjectionType.SINGLETON;
}
