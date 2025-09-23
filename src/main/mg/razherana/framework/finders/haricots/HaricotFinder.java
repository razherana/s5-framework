package mg.razherana.framework.finders.haricots;

import mg.razherana.framework.annotations.haricots.Haricot;
import mg.razherana.framework.annotations.controllers.Controller;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Utility class to find all classes annotated with @Haricot
 * This includes classes directly annotated with @Haricot and those
 * annotated with meta-annotations like @Controller
 */
public class HaricotFinder {

  /**
   * Find all classes annotated with @Haricot in the given package
   * 
   * @param packageName the package to scan
   * @return Set of classes annotated with @Haricot
   */
  public static Set<Class<?>> findHaricotClasses(String packageName) {
    Set<Class<?>> haricotClasses = new HashSet<>();

    try {
      // Get all classes in the package
      List<Class<?>> classes = getClassesInPackage(packageName);

      for (Class<?> clazz : classes) {
        if (isHaricot(clazz)) {
          haricotClasses.add(clazz);
        }
      }
    } catch (Exception e) {
      System.err.println("Error scanning package " + packageName + ": " + e.getMessage());
    }

    return haricotClasses;
  }

  /**
   * Check if a class is annotated with @Haricot (directly or through
   * meta-annotations)
   * 
   * @param clazz the class to check
   * @return true if the class is a Haricot
   */
  public static boolean isHaricot(Class<?> clazz) {
    // Direct annotation check
    if (clazz.isAnnotationPresent(Haricot.class)) {
      return true;
    }

    // Check for meta-annotations (like @Controller which is annotated with
    // @Haricot)
    for (Annotation annotation : clazz.getAnnotations()) {
      Class<? extends Annotation> annotationType = annotation.annotationType();
      if (annotationType.isAnnotationPresent(Haricot.class)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Check if a class is specifically a Controller
   * 
   * @param clazz the class to check
   * @return true if the class is annotated with @Controller
   */
  public static boolean isController(Class<?> clazz) {
    return clazz.isAnnotationPresent(Controller.class);
  }

  /**
   * Get the type of Haricot (e.g., "Controller", "Haricot")
   * 
   * @param clazz the class to analyze
   * @return String description of the haricot type
   */
  public static String getHaricotType(Class<?> clazz) {
    if (isController(clazz)) {
      return "Controller";
    } else if (clazz.isAnnotationPresent(Haricot.class)) {
      return "Haricot";
    }
    return "Unknown";
  }

  /**
   * Get all classes in a package
   * 
   * @param packageName the package name
   * @return List of classes in the package
   */
  private static List<Class<?>> getClassesInPackage(String packageName) {
    List<Class<?>> classes = new ArrayList<>();

    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      String path = packageName.replace('.', '/');
      URL resource = classLoader.getResource(path);

      if (resource != null) {
        File directory = new File(resource.getFile());
        if (directory.exists()) {
          scanDirectory(directory, packageName, classes);
        }
      }
    } catch (Exception e) {
      System.err.println("Error loading classes from package " + packageName + ": " + e.getMessage());
    }

    return classes;
  }

  /**
   * Recursively scan directory for class files
   * 
   * @param directory   the directory to scan
   * @param packageName the current package name
   * @param classes     the list to add found classes to
   */
  private static void scanDirectory(File directory, String packageName, List<Class<?>> classes) {
    File[] files = directory.listFiles();

    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          // Recursively scan subdirectories
          String subPackage = packageName + "." + file.getName();
          scanDirectory(file, subPackage, classes);
        } else if (file.getName().endsWith(".class")) {
          // Load class file
          String className = packageName + "." + file.getName().replace(".class", "");
          try {
            Class<?> clazz = Class.forName(className);
            classes.add(clazz);
          } catch (ClassNotFoundException e) {
            // Skip classes that can't be loaded
            System.err.println("Could not load class: " + className);
          }
        }
      }
    }
  }

  /**
   * Print information about all found Haricot classes
   * 
   * @param packageName the package to scan
   */
  public static void printHaricotInfo(String packageName) {
    Set<Class<?>> haricotClasses = findHaricotClasses(packageName);

    System.out.println("Found " + haricotClasses.size() + " Haricot classes:");
    for (Class<?> clazz : haricotClasses) {
      String type = getHaricotType(clazz);
      System.out.println("- " + clazz.getName() + " (Type: " + type + ")");
    }
  }
}
