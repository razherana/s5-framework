package mg.razherana.framework.haricot.finders;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mg.razherana.framework.haricot.annotations.Haricot;

import java.util.HashSet;

/**
 * Utility class to find all classes annotated with @Haricot
 * This includes classes directly annotated with @Haricot and those
 * annotated with meta-annotations like @Controller
 */
public class HaricotFinder {

  /**
   * Find all classes annotated with @Haricot in the given package and
   * Haricot meta-annotations
   * 
   * @param packageName the package to scan
   * @return Set of classes annotated with @Haricot
   */
  public static HashMap<Class<?>, Haricot[]> findHaricotClasses(String packageName) {
    HashMap<Class<?>, Haricot[]> haricotClasses = new HashMap<>();

    try {
      // Get all classes in the package
      List<Class<?>> classes = getClassesInPackage(packageName);

      for (Class<?> clazz : classes) {
        Haricot[] haricots = getHaricots(clazz);
        haricotClasses.put(clazz, haricots);
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
   * @return array of Haricot annotations found
   */
  public static Haricot[] getHaricots(Class<?> clazz) {
    return getHaricots(clazz, new HashSet<>());
  }

  /**
   * Check if a class is annotated with @Haricot (directly or through
   * meta-annotations) with cycle detection
   * 
   * @param clazz the class to check
   * @param visited set of already visited annotation types to prevent infinite recursion
   * @return array of Haricot annotations found
   */
  private static Haricot[] getHaricots(Class<?> clazz, Set<Class<?>> visited) {
    if (clazz == null || visited.contains(clazz)) {
      return new Haricot[] {};
    }

    // Add current class to visited set to prevent cycles
    visited.add(clazz);

    List<Haricot> haricots = new ArrayList<>();

    // Direct annotation check
    if (clazz.isAnnotationPresent(Haricot.class)) {
      haricots.add(clazz.getAnnotation(Haricot.class));
    }

    // Check for meta-annotations
    // (like @Controller which is annotated with @Haricot)
    for (Annotation annotation : clazz.getDeclaredAnnotations()) {
      Class<? extends Annotation> annotationType = annotation.annotationType();

      // Skip java.lang annotations to avoid unnecessary recursion
      if (annotationType.getName().startsWith("java.lang.annotation.")) {
        continue;
      }

      // Recursive check with visited set
      Haricot[] metaHaricots = getHaricots(annotationType, visited);
      if (metaHaricots.length > 0) {
        for (Haricot metaHaricot : metaHaricots) {
          haricots.add(metaHaricot);
        }
      }
    }

    // Remove current class from visited set after processing
    visited.remove(clazz);

    return haricots.toArray(Haricot[]::new);
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

    if (files == null)
      return;

    for (File file : files) {
      if (file.isDirectory()) {
        // Recursively scan subdirectories
        String subPackage = packageName + "." + file.getName();
        scanDirectory(file, subPackage, classes);
      }

      if (file.getName().endsWith(".class")) {
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

  /**
   * Print information about all found Haricot classes
   * 
   * @param packageName the package to scan
   */
  public static void printHaricotInfo(String packageName) {
    HashMap<Class<?>, Haricot[]> haricotClasses = findHaricotClasses(packageName);

    System.out.println("Found " + haricotClasses.size() + " Haricot classes:");
    for (Map.Entry<Class<?>, Haricot[]> entry : haricotClasses.entrySet()) {
      Class<?> clazz = entry.getKey();
      Haricot[] haricots = entry.getValue();

      for (Haricot haricot : haricots) {
        String type = haricot.type();
        System.out.println("- " + clazz.getName() + " (Type: " + type + ")");
      }
    }
  }
}
