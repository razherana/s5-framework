package mg.razherana.framework.haricot.exceptions;

/**
 * Exception thrown when the Haricot configuration is invalid
 */
public class HaricotConfigurationException extends HaricotException {
  private final Class<?> haricotClass;
  private final String configurationIssue;

  public HaricotConfigurationException(Class<?> haricotClass, String configurationIssue) {
    super(buildMessage(haricotClass, configurationIssue));
    this.haricotClass = haricotClass;
    this.configurationIssue = configurationIssue;
  }

  public HaricotConfigurationException(Class<?> haricotClass, String configurationIssue, Throwable cause) {
    super(buildMessage(haricotClass, configurationIssue), cause);
    this.haricotClass = haricotClass;
    this.configurationIssue = configurationIssue;
  }

  private static String buildMessage(Class<?> haricotClass, String configurationIssue) {
    StringBuilder message = new StringBuilder();
    message.append("Invalid Haricot configuration:");
    message.append("\n  - Class: ").append(haricotClass != null ? haricotClass.getName() : "Unknown");
    message.append("\n  - Issue: ").append(configurationIssue != null ? configurationIssue : "Unknown configuration problem");
    message.append("\n  - Suggestion: Check the @Haricot annotation and dependency injection setup");
    return message.toString();
  }

  public Class<?> getHaricotClass() { return haricotClass; }
  public String getConfigurationIssue() { return configurationIssue; }
}