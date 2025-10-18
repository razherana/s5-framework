package mg.razherana.framework;

import mg.razherana.framework.web.annotations.configs.HttpErrorHandler;

/**
 * Enum for configuration constants used in the framework.
 * This is handy for developers to locate and use configuration keys in xmls, in
 * java code, haricot types, etc.
 */
public enum ConfigConstants {
  /**
   * The package to scan for Haricot components
   * <p>
   * The package should be defined in the web.xml as an init-param
   * with the name "haricot.scan.package".
   * </p>
   * <p>
   * Multiple packages can be defined separated by commas.
   * </p>
   */
  HARICOT_SCAN_PACKAGE("haricot.scan.package"),

  /**
   * The annotation used to mark HTTP error handler classes,
   * so you can customize error handling in your web application.
   * Maybe change the default to Json, Html, etc.
   */
  HTTP_ERROR_HANDLER_ANNOTATION(HttpErrorHandler.class.getName());

  private final String value;

  ConfigConstants(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
