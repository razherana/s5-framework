package mg.razherana.framework.haricot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import mg.razherana.framework.haricot.annotations.Haricot;
import mg.razherana.framework.haricot.containers.InstancedHaricotContainer;
import mg.razherana.framework.haricot.exceptions.HaricotCircularDependencyException;
import mg.razherana.framework.haricot.exceptions.HaricotConfigurationException;
import mg.razherana.framework.haricot.exceptions.HaricotInstantiationException;
import mg.razherana.framework.haricot.finders.HaricotFinder;
import mg.razherana.framework.injection.annotations.Inject;
import mg.razherana.framework.soja.finders.SojaFinder;
import mg.razherana.framework.soja.finders.containers.SojaClassContainer;

public class HaricotInstantiator {
  public static HaricotInstantiator runOnPackages(String[] packageNames) {
    HaricotInstantiator haricotInstantiator = new HaricotInstantiator();
    HashMap<Class<?>, Haricot[]> haricots = new HashMap<>();

    for (String packageName : packageNames) {
      var haricotsInPackage = HaricotFinder.findHaricotClasses(packageName);
      haricots.putAll(haricotsInPackage);
    }

    System.out.println("[Fruits] : Haricot classes found: " + haricots.keySet());

    HashMap<Class<?>, SojaClassContainer> sojaClasses = new HashMap<>();

    for (var haricotClass : haricots.entrySet()) {
      var sojaClassContainer = SojaFinder.findAllSojaInClass(haricotClass.getKey());
      sojaClasses.put(haricotClass.getKey(), sojaClassContainer);
      System.out.println("[Fruits] : Soja scanned for Haricot class: " + haricotClass.getKey().getName());
      System.out.println("[Fruits] : " + sojaClassContainer.debugInfo());
    }

    haricotInstantiator.instantiateHaricots(
        haricots,
        sojaClasses);

    return haricotInstantiator;
  }

  public static HaricotInstantiator runOnPackage(String packageName) {
    return runOnPackages(new String[] { packageName });
  }

  /**
   * Resolve the Haricot name from annotations or use class simple name as
   * fallback
   * 
   * @param clazz              the class
   * @param haricotAnnotations the Haricot annotations
   * @return the resolved Haricot name
   * @throws HaricotConfigurationException if required parameters are null
   */
  public static String getHaricotName(Class<?> clazz, Haricot[] haricotAnnotations) {
    if (clazz == null) {
      throw new HaricotConfigurationException(null, "Class cannot be null when resolving Haricot name");
    }
    if (haricotAnnotations == null) {
      throw new HaricotConfigurationException(clazz, "Haricot annotations cannot be null");
    }

    // Check if any annotation has a custom name
    for (Haricot haricot : haricotAnnotations) {
      if (haricot != null && !haricot.name().isEmpty()) {
        return haricot.name();
      }
    }

    // Fallback to class simple name
    return clazz.getSimpleName();
  }

  /**
   * Generate a composite key for Haricot instances with injection method names
   * 
   * @param baseHaricotName the base Haricot name
   * @param injectionMethodName the injection method name (can be null or empty)
   * @return composite key in format "baseHaricotName#injectionMethodName" or just baseHaricotName if no method name
   */
  public static String generateHaricotKey(String baseHaricotName, String injectionMethodName) {
    if (injectionMethodName != null && !injectionMethodName.trim().isEmpty()) {
      return baseHaricotName + "#" + injectionMethodName.trim();
    }
    return baseHaricotName;
  }

  private InstancedHaricotContainer haricotContainer;
  private HaricotFactory factory;

  // Track currently instantiating Haricots to detect circular dependencies
  private List<String> currentlyInstantiating;

  public HaricotInstantiator() {
    this.haricotContainer = new InstancedHaricotContainer();
    this.currentlyInstantiating = new ArrayList<>();
  }

  public HaricotFactory getFactory() {
    return factory;
  }

  public void setFactory(HaricotFactory factory) {
    this.factory = factory;
  }

  public void instantiateHaricots(final HashMap<Class<?>, Haricot[]> haricotClasses,
      final HashMap<Class<?>, SojaClassContainer> sojaClasses) {

    // Input validation
    if (haricotClasses == null) {
      throw new HaricotConfigurationException(null, "Haricot classes map cannot be null");
    }
    if (sojaClasses == null) {
      throw new HaricotConfigurationException(null, "Soja classes map cannot be null");
    }

    // Clear any previous circular dependency tracking state
    currentlyInstantiating.clear();

    // Initialize the factory with required dependencies
    this.factory = new HaricotFactory(haricotContainer, haricotClasses, sojaClasses, this);

    for (Entry<Class<?>, Haricot[]> haricotClazz : haricotClasses.entrySet()) {
      var clazz = haricotClazz.getKey();
      var haricotAnnotations = haricotClazz.getValue();

      try {
        var sojaClassContainer = sojaClasses.get(clazz);
        var haricotName = getHaricotName(clazz, haricotAnnotations);

        // Skip if auto-instantiation is disabled
        if (!hasAutoInstantiateAnnotation(haricotAnnotations)) {
          continue;
        }

        Object instance = instantiateHaricot(clazz, haricotAnnotations, sojaClassContainer, haricotName, false);
        
        haricotContainer.addInstance(haricotName, instance);

      } catch (Exception e) {
        // Wrap and re-throw with context about which class failed
        throw new HaricotInstantiationException(
            "Failed to instantiate Haricot class: " + (clazz != null ? clazz.getName() : "Unknown") +
                ". Original error: " + e.getMessage(),
            e);
      }
    }
  }

  public InstancedHaricotContainer getHaricotContainer() {
    return haricotContainer;
  }

  public void setHaricotContainer(InstancedHaricotContainer haricotContainer) {
    this.haricotContainer = haricotContainer;
  }

  Object instantiateHaricot(Class<?> clazz, Haricot[] haricotAnnotations,
      SojaClassContainer sojaClassContainer, String haricotName, boolean forceNewInstance) {

    // Input validation
    if (clazz == null) {
      throw new HaricotConfigurationException(null, "Class cannot be null for Haricot instantiation");
    }
    if (haricotName == null || haricotName.trim().isEmpty()) {
      throw new HaricotConfigurationException(clazz, "Haricot name cannot be null or empty");
    }

    if (haricotContainer.containInstance(haricotName) && !forceNewInstance)
      return haricotContainer.getInstance(haricotName);

    if (sojaClassContainer == null)
      throw new HaricotConfigurationException(clazz,
          "Class was not scanned through SojaFinder. Ensure the class is in the scanned packages.");

    // Check for circular dependency
    if (currentlyInstantiating.contains(haricotName)) {
      throw new HaricotCircularDependencyException(haricotName, new ArrayList<>(currentlyInstantiating));
    }

    // Add to currently instantiating stack
    currentlyInstantiating.add(haricotName);

    try {
      // Try no dependencies instantiation
      Object noDependenciesResult = handleNoDependencies(clazz, sojaClassContainer);
      if (noDependenciesResult != null) {
        return noDependenciesResult;
      }

      // Try no dependencies with only field injections
      Object onlyFieldInjectionsResult = handleOnlyFieldInjections(sojaClassContainer, haricotName);
      if (onlyFieldInjectionsResult != null) {
        return onlyFieldInjectionsResult;
      }

      // Try constructor injection
      Object constructorInjectionResult = handleConstructorInjection(sojaClassContainer, haricotAnnotations,
          haricotName);
      if (constructorInjectionResult != null) {
        return constructorInjectionResult;
      }

      // If we reach here, no instantiation method worked
      throw new HaricotInstantiationException(
          "Unable to instantiate Haricot '" + haricotName + "' of type " + clazz.getName() +
              ". No suitable constructor or injection method found. " +
              "Ensure the class has either a default constructor, an @Inject constructor, or an @InjectionMethod.");

    } finally {
      // Always remove from currently instantiating stack when done (success or
      // failure)
      currentlyInstantiating.remove(haricotName);
    }
  }

  /**
   * Check if any of the Haricot annotations has auto-instantiation enabled
   * 
   * @param haricotAnnotations the Haricot annotations to check
   * @return true if auto-instantiation is enabled, false otherwise
   * @throws HaricotConfigurationException if annotations array is null or empty
   */
  private boolean hasAutoInstantiateAnnotation(Haricot[] haricotAnnotations) {
    if (haricotAnnotations == null) {
      throw new HaricotConfigurationException(null, "Haricot annotations cannot be null");
    }

    return Arrays.stream(haricotAnnotations)
        .anyMatch(h -> h.autoInstantiate());
  }

  private boolean hasInjectionField(SojaClassContainer sojaClassContainer) {
    return sojaClassContainer.getSojaFields().values().stream()
        .anyMatch(f -> {
          return Arrays.stream(f.getSojaAnnotations())
              .anyMatch(s -> {
                return s.type().equals(Inject.TYPE);
              });
        });
  }

  private void handleInjectionFields(
      Object instance,
      SojaClassContainer sojaClassContainer,
      String haricotName) {
    if (hasInjectionField(sojaClassContainer)) {
      factory.injectFields(instance, sojaClassContainer, haricotName);
    }
  }


  /**
   * Handle instantiation for classes with no dependencies
   * 
   * @param clazz              the class to instantiate
   * @param sojaClassContainer the Soja class container
   * @return instance if no dependencies are needed, null otherwise
   */
  private Object handleNoDependencies(Class<?> clazz, SojaClassContainer sojaClassContainer) {
    if (hasNoDependencies(sojaClassContainer)) {
      return HaricotFactory.instantiateHaricotNoDepsWithDefaultConstructor(clazz);
    }
    return null;
  }

  /**
   * Check if the class has no dependencies that need injection
   * 
   * @param sojaClassContainer the Soja class container
   * @return true if no dependencies need injection, false otherwise
   */
  private boolean hasNoDependencies(SojaClassContainer sojaClassContainer) {
    boolean hasNoInjectableConstructors = sojaClassContainer.getSojaConstructors().size() == 0;
    boolean hasNoInjectableFields = sojaClassContainer.getSojaFields().size() == 0 ||
        sojaClassContainer.getSojaFields()
            .values()
            .stream()
            .noneMatch(f -> f.getField().isAnnotationPresent(Inject.class));

    return hasNoInjectableConstructors && hasNoInjectableFields;
  }

  private Object handleOnlyFieldInjections(SojaClassContainer sojaClassContainer, String haricotName) {
    if (hasOnlyFieldInjections(sojaClassContainer)) {
      // Instantiate with default constructor
      Object o = HaricotFactory.instantiateHaricotNoDepsWithDefaultConstructor(sojaClassContainer.getClazz());

      // Inject fields
      handleInjectionFields(o, sojaClassContainer, haricotName);
      return o;
    }
    return null;
  }

  /**
   * Check if the class has only field injections (no constructor injections)
   * 
   * @param sojaClassContainer
   * @return
   */
  private boolean hasOnlyFieldInjections(SojaClassContainer sojaClassContainer) {
    boolean hasNoInjectableConstructors = sojaClassContainer.getSojaConstructors().size() == 0;
    boolean hasInjectableFields = sojaClassContainer.getSojaFields().size() > 0 &&
        sojaClassContainer.getSojaFields()
            .values()
            .stream()
            .anyMatch(f -> f.getField().isAnnotationPresent(Inject.class));

    return hasNoInjectableConstructors && hasInjectableFields;
  }

  /**
   * Handle instantiation through constructor injection
   * 
   * @param sojaClassContainer the Soja class container
   * @param haricotAnnotations the Haricot annotations
   * @param haricotName        the Haricot name
   * @return instance if constructor injection is successful, null otherwise
   */
  private Object handleConstructorInjection(SojaClassContainer sojaClassContainer,
      Haricot[] haricotAnnotations, String haricotName) {
    // Check for @Inject constructors
    if (sojaClassContainer.getSojaConstructors().size() > 0) {
      // Find constructor with @Inject
      var injectConstructor = sojaClassContainer.getSojaConstructors().values().stream()
          .filter(c -> Arrays.stream(c.getSojaAnnotations())
              .anyMatch(s -> s.type().equals(Inject.TYPE)))
          .findFirst();

      if (injectConstructor.isPresent()) {
        // Instantiate using the @Inject constructor
        Object o = factory.instantiateWithConstructor(injectConstructor.get(), haricotAnnotations, haricotName);
        handleInjectionFields(o, sojaClassContainer, haricotName);

        return o;
      }
    }

    return null;
  }
}
