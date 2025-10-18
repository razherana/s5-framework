package mg.razherana.framework.haricot.exceptions;

/**
 * Exception thrown when a dependency required for Haricot instantiation cannot be found
 */
public class HaricotDependencyNotFoundException extends HaricotInstantiationException {
  private final Class<?> dependencyType;
  private final String dependencyName;
  private final Class<?> requiredByClass;

  public HaricotDependencyNotFoundException(Class<?> dependencyType, String dependencyName, Class<?> requiredByClass) {
    super(buildMessage(dependencyType, dependencyName, requiredByClass));
    this.dependencyType = dependencyType;
    this.dependencyName = dependencyName;
    this.requiredByClass = requiredByClass;
  }

  public HaricotDependencyNotFoundException(Class<?> dependencyType, String dependencyName, Class<?> requiredByClass, Throwable cause) {
    super(buildMessage(dependencyType, dependencyName, requiredByClass), cause);
    this.dependencyType = dependencyType;
    this.dependencyName = dependencyName;
    this.requiredByClass = requiredByClass;
  }

  private static String buildMessage(Class<?> dependencyType, String dependencyName, Class<?> requiredByClass) {
    StringBuilder message = new StringBuilder();
    message.append("Dependency not found for Haricot instantiation:");
    message.append("\n  - Dependency Type: ").append(dependencyType != null ? dependencyType.getName() : "Unknown");
    message.append("\n  - Dependency Name: ").append(dependencyName != null && !dependencyName.isEmpty() ? dependencyName : "Default");
    message.append("\n  - Required By: ").append(requiredByClass != null ? requiredByClass.getName() : "Unknown");
    message.append("\n  - Suggestion: Ensure the dependency is annotated with @Haricot and is in the scanned packages");
    return message.toString();
  }

  public Class<?> getDependencyType() { return dependencyType; }
  public String getDependencyName() { return dependencyName; }
  public Class<?> getRequiredByClass() { return requiredByClass; }
}