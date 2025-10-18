package mg.razherana.framework.haricot.exceptions;

/**
 * Exception thrown when an injection method specified for dependency injection cannot be found or is invalid
 */
public class HaricotInjectionMethodException extends HaricotInstantiationException {
  private final String methodName;
  private final Class<?> targetClass;
  private final String reason;

  public HaricotInjectionMethodException(String methodName, Class<?> targetClass, String reason) {
    super(buildMessage(methodName, targetClass, reason));
    this.methodName = methodName;
    this.targetClass = targetClass;
    this.reason = reason;
  }

  public HaricotInjectionMethodException(String methodName, Class<?> targetClass, String reason, Throwable cause) {
    super(buildMessage(methodName, targetClass, reason), cause);
    this.methodName = methodName;
    this.targetClass = targetClass;
    this.reason = reason;
  }

  private static String buildMessage(String methodName, Class<?> targetClass, String reason) {
    StringBuilder message = new StringBuilder();
    message.append("Injection method error:");
    message.append("\n  - Method Name: ").append(methodName != null ? methodName : "Unknown");
    message.append("\n  - Target Class: ").append(targetClass != null ? targetClass.getName() : "Unknown");
    message.append("\n  - Reason: ").append(reason != null ? reason : "Unknown error");
    message.append("\n  - Suggestion: Verify the @InjectionMethod annotation and method accessibility");
    return message.toString();
  }

  public String getMethodName() { return methodName; }
  public Class<?> getTargetClass() { return targetClass; }
  public String getReason() { return reason; }
}