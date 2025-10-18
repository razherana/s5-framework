package mg.razherana.framework.haricot.exceptions;

import java.util.List;

/**
 * Exception thrown when a circular dependency is detected during Haricot instantiation
 */
public class HaricotCircularDependencyException extends HaricotException {
  private final String haricotName;
  private final List<String> dependencyChain;

  public HaricotCircularDependencyException(String haricotName, List<String> dependencyChain) {
    super(buildMessage(haricotName, dependencyChain));
    this.haricotName = haricotName;
    this.dependencyChain = dependencyChain;
  }

  public HaricotCircularDependencyException(String haricotName, List<String> dependencyChain, Throwable cause) {
    super(buildMessage(haricotName, dependencyChain), cause);
    this.haricotName = haricotName;
    this.dependencyChain = dependencyChain;
  }

  private static String buildMessage(String haricotName, List<String> dependencyChain) {
    StringBuilder message = new StringBuilder();
    message.append("Circular dependency detected during Haricot instantiation:");
    message.append("\n  - Target Haricot: ").append(haricotName);
    message.append("\n  - Current instantiation chain: ").append(dependencyChain);
    message.append("\n  - Issue: ").append(haricotName).append(" is already being instantiated in the current chain");
    message.append("\n  - Suggestion: Review your dependency injection setup to remove circular references.");
    message.append(" Consider refactoring to break the circular dependency.");
    return message.toString();
  }

  public String getHaricotName() { 
    return haricotName; 
  }
  
  public List<String> getDependencyChain() { 
    return dependencyChain; 
  }
}