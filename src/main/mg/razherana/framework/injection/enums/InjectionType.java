package mg.razherana.framework.injection.enums;

/**
 * Enumeration for different types of injection.
 */
public enum InjectionType {
  /** Singleton injection type. Only one instance shared. */
  SINGLETON,

  /** Prototype injection type. A new instance is created each time. */
  PROTOTYPE;
}
