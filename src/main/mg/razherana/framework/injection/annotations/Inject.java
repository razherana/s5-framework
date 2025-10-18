package mg.razherana.framework.injection.annotations;

import java.lang.annotation.Target;

import mg.razherana.framework.soja.annotations.Soja;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used to mark fields or parameters for injection.
 * It indicates that the annotated element should be automatically populated
 * with an instance of the required type.
 * 
 * <p>
 * The optional 'name' parameter can be used to specify a particular
 * implementation to use during injection, allowing for more fine-grained control
 * over the injection process.
 * </p>
 * 
 * <p>
 * <b>Important:</b> If you use this annotation with a Field, the field injected can't be used during construction. 
 * </p>
 * 
 * @see mg.razherana.framework.injection.annotations.Injection
 * @see mg.razherana.framework.injection.annotations.InjectionMethod
 */
@Documented
@Soja(type = "inject")
@Target({
    ElementType.FIELD, ElementType.PARAMETER, ElementType.CONSTRUCTOR
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
  String TYPE = "inject";
  
  /**
   * Optional name to specify a particular implementation of injection to use.
   */
  String name() default "";
}
