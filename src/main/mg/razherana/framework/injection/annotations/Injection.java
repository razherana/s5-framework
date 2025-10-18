package mg.razherana.framework.injection.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import mg.razherana.framework.haricot.annotations.Haricot;
import mg.razherana.framework.injection.enums.InjectionType;

/**
 * This annotation is used to add additional information for injection.
 */
@Haricot(type = "injection")
@Target(ElementType.TYPE)
public @interface Injection {
  String TYPE = "injection";
  
  /**
   * Type of injection to be used for this class.
   * <p>
   * This is overridden by method-level injection types if injection method is used.
   * </p>
   */
  public InjectionType injectionType() default InjectionType.SINGLETON;
}
