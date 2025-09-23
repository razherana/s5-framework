package mg.razherana.framework.annotations.haricots;

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
}
