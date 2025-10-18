package mg.razherana.framework.haricot;

import java.util.Arrays;
import java.util.HashMap;

import mg.razherana.framework.haricot.annotations.Haricot;
import mg.razherana.framework.haricot.containers.InstancedHaricotContainer;
import mg.razherana.framework.haricot.exceptions.HaricotInstantiationException;
import mg.razherana.framework.injection.annotations.Inject;
import mg.razherana.framework.injection.annotations.Injection;
import mg.razherana.framework.injection.annotations.InjectionMethod;
import mg.razherana.framework.injection.enums.InjectionType;
import mg.razherana.framework.soja.finders.containers.SojaClassContainer;
import mg.razherana.framework.soja.finders.containers.SojaConstructorContainer;
import mg.razherana.framework.soja.finders.containers.SojaMethodContainer;

public class HaricotFactory {
  private InstancedHaricotContainer haricotContainer;
  private HashMap<Class<?>, Haricot[]> haricotClasses;
  private HashMap<Class<?>, SojaClassContainer> sojaClasses;
  private HaricotInstantiator instantiator;

  public HaricotFactory(InstancedHaricotContainer haricotContainer,
      HashMap<Class<?>, Haricot[]> haricotClasses,
      HashMap<Class<?>, SojaClassContainer> sojaClasses,
      HaricotInstantiator instantiator) {
    this.haricotContainer = haricotContainer;
    this.haricotClasses = haricotClasses;
    this.sojaClasses = sojaClasses;
    this.instantiator = instantiator;
  }

  public static Object instantiateHaricotNoDepsWithDefaultConstructor(Class<?> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (NoSuchMethodException e) {
      throw new HaricotInstantiationException(
          "No default constructor found for Haricot: " + clazz.getName(), e);
    } catch (Exception e) {
      throw new HaricotInstantiationException("Failed to instantiate Haricot: " + clazz.getName(), e);
    }
  }

  public Object instantiateHaricotWithInjectionMethod(SojaMethodContainer sojaMethodContainer) {
    Object instance = HaricotFactory
        .instantiateHaricotNoDepsWithDefaultConstructor(sojaMethodContainer
            .getParentSojaClassContainer()
            .getClazz());

    injectionMethod(instance, sojaMethodContainer);
    return instance;
  }

  public void injectionMethod(Object object, SojaMethodContainer sojaMethodContainer) {
    try {
      var method = sojaMethodContainer.getMethod();
      method.setAccessible(true);
      method.invoke(object);
    } catch (Exception e) {
      throw new HaricotInstantiationException(
          "Failed to invoke injection method: " + sojaMethodContainer.getMethod().getName()
              + " in class "
              + sojaMethodContainer.getParentSojaClassContainer().getClazz().getName(),
          e);
    }
  }

  /**
   * Generate a composite key for Haricot instances with injection method names
   * 
   * @param baseHaricotName the base Haricot name
   * @param injectionMethodName the injection method name (can be null or empty)
   * @return composite key in format "baseHaricotName#injectionMethodName" or just baseHaricotName if no method name
   */
  private String generateHaricotKey(String baseHaricotName, String injectionMethodName) {
    if (injectionMethodName != null && !injectionMethodName.trim().isEmpty()) {
      return baseHaricotName + "#" + injectionMethodName.trim();
    }
    return baseHaricotName;
  }

  /**
   * Check for @Injection annotation and instantiate accordingly
   * 
   * @param executable
   * @param haricotAnnotations
   * @param sojaClassContainer
   * @param haricotName
   * @param injectName
   * @return
   */
  public Object checkInjectionMethodAndInstantiate(Class<?> clazz, boolean isInjectAnnotationPresent,
      Haricot[] haricotAnnotations,
      SojaClassContainer sojaClassContainer, String haricotName, final String injectName) {
    Object instance;
    // Handle parameter-specific injection
    if (isInjectAnnotationPresent) {
      instance = handleAnnotationLevelInjection(haricotAnnotations, sojaClassContainer,
          haricotName, injectName, clazz);
    } else {
      // Handle class-level injection
      instance = handleClassLevelInjection(clazz, haricotAnnotations, sojaClassContainer, haricotName);
    }

    // Execute injection method if specified
    executeInjectionMethod(instance, sojaClassContainer, injectName);

    return instance;
  }

  /**
   * Handle parameter-specific injection with @Inject annotation
   */
  private Object handleAnnotationLevelInjection(Haricot[] haricotAnnotations,
      SojaClassContainer sojaClassContainer, String haricotName, String injectName, Class<?> clazz) {
    // Get the method
    var sojaInjectionMethod = sojaClassContainer.getSojaMethods().values().stream()
        .filter(m -> Arrays.stream(m.getSojaAnnotations())
            .anyMatch(s -> s.type().equals(InjectionMethod.TYPE)))
        .toList();

    boolean found = false;
    Object instance = null;

    if (!sojaInjectionMethod.isEmpty() && injectName != null && !injectName.isBlank()) {
      for (var methodContainer : sojaInjectionMethod) {
        var injectionMethod = methodContainer.getMethod().getAnnotation(InjectionMethod.class);

        if (injectionMethod.name().equals(injectName)) {
          var compositeKey = generateHaricotKey(haricotName, injectName);
          
          // Check if SINGLETON instance already exists with composite key
          if (injectionMethod.injectionType().equals(InjectionType.SINGLETON) 
              && haricotContainer.containInstance(compositeKey)) {
            instance = haricotContainer.getInstance(compositeKey);
          } else {
            // Create new instance
            instance = instantiator.instantiateHaricot(clazz, haricotAnnotations,
                sojaClassContainer, haricotName, injectionMethod.injectionType().equals(InjectionType.PROTOTYPE));
            
            // Store SINGLETON instances with composite key
            if (injectionMethod.injectionType().equals(InjectionType.SINGLETON)) {
              haricotContainer.addInstance(compositeKey, instance);
            }
          }

          found = true;
          break;
        }
      }

      // If no injection method found with the given name, throw exception
      if (!found)
        throw new HaricotInstantiationException(
            "No injection method found with name: " + injectName + " in Haricot: " + clazz.getName());
    } else {
      // No injection method found, default to singleton
      instance = instantiator.instantiateHaricot(clazz, haricotAnnotations,
          sojaClassContainer, haricotName, false);

      if (!haricotContainer.containInstance(haricotName))
        haricotContainer.addInstance(haricotName, instance);
    }

    return instance;
  }

  /**
   * Handle class-level injection based on @Injection annotation
   */
  private Object handleClassLevelInjection(Class<?> clazz, Haricot[] haricotAnnotations,
      SojaClassContainer sojaClassContainer, String haricotName) {
    Object instance;

    if (clazz.isAnnotationPresent(Injection.class)) {
      var injectionType = HaricotFactory.getInjectionTypeFromAnnotation(clazz);

      switch (injectionType) {
        case PROTOTYPE:
          instance = instantiator.instantiateHaricot(clazz, haricotAnnotations,
              sojaClassContainer, haricotName, true);
          break;

        case SINGLETON:
          instance = instantiator.instantiateHaricot(clazz, haricotAnnotations,
              sojaClassContainer, haricotName, false);
          if (!haricotContainer.containInstance(haricotName))
            haricotContainer.addInstance(haricotName, instance);
          break;

        default:
          throw new HaricotInstantiationException(
              "Unsupported injection type for Haricot: " + clazz.getName());
      }
    } else {
      // No @Injection annotation, default to singleton
      instance = instantiator.instantiateHaricot(clazz, haricotAnnotations,
          sojaClassContainer, haricotName, false);
      if (!haricotContainer.containInstance(haricotName))
        haricotContainer.addInstance(haricotName, instance);
    }

    return instance;
  }

  /**
   * Execute injection method if specified
   */
  private void executeInjectionMethod(Object instance, SojaClassContainer sojaClassContainer, String injectName) {
    if (injectName == null || injectName.isBlank())
      return;

    // Get the method
    var sojaInjectionMethod = sojaClassContainer.getSojaMethods().values().stream()
        .filter(m -> Arrays.stream(m.getSojaAnnotations())
            .anyMatch(s -> s.type().equals(InjectionMethod.TYPE)))
        .toList();

    if (!sojaInjectionMethod.isEmpty())
      for (var methodContainer : sojaInjectionMethod)
        if (methodContainer.getMethod().getAnnotation(InjectionMethod.class).name().equals(injectName)) {
          injectionMethod(instance, methodContainer);
          break;
        }

  }

  /**
   * Instantiate using the given constructor and inject dependencies
   * 
   * @param sojaConstructorContainer
   * @param haricotAnnotations
   * @param haricotName
   * @return
   */
  public Object instantiateWithConstructor(SojaConstructorContainer sojaConstructorContainer,
      Haricot[] haricotAnnotations,
      String haricotName) {
    try {
      var constructor = sojaConstructorContainer.getConstructor();
      var parameters = constructor.getParameters();
      Object[] paramInstances = new Object[parameters.length];

      for (int i = 0; i < parameters.length; i++) {
        var paramType = parameters[i].getType();
        String injectName = null;

        if (parameters[i].isAnnotationPresent(Inject.class)) {
          injectName = parameters[i].getAnnotation(Inject.class).name();
        }

        System.out.println("Instantiating dependency for parameter of type: " +
            paramType.getSimpleName()
            + " in Haricot: " +
            sojaConstructorContainer.getParentSojaClassContainer().getClazz().getSimpleName());

        var haricotNameInject = HaricotInstantiator.getHaricotName(paramType, haricotClasses.get(paramType));

        paramInstances[i] = checkInjectionMethodAndInstantiate(paramType,
            parameters[i].isAnnotationPresent(Inject.class), haricotAnnotations,
            sojaClasses.get(paramType), haricotNameInject, injectName);
      }

      return constructor.newInstance(paramInstances);
    } catch (Exception e) {
      throw new HaricotInstantiationException(
          "Failed to instantiate Haricot with dependencies: "
              + sojaConstructorContainer.getParentSojaClassContainer().getClazz().getName(),
          e);
    }
  }

  public static InjectionType getInjectionTypeFromAnnotation(Class<?> clazz) {
    var annotation = clazz.getAnnotation(Injection.class);

    if (annotation == null) {
      // Should not happen because we check before calling this method
      throw new HaricotInstantiationException(
          "Class " + clazz.getName() + " is not annotated with @Injection.");
    }

    return annotation.injectionType();
  }

  public InstancedHaricotContainer getHaricotContainer() {
    return haricotContainer;
  }

  public void setHaricotContainer(InstancedHaricotContainer haricotContainer) {
    this.haricotContainer = haricotContainer;
  }

  public HashMap<Class<?>, Haricot[]> getHaricotClasses() {
    return haricotClasses;
  }

  public void setHaricotClasses(HashMap<Class<?>, Haricot[]> haricotClasses) {
    this.haricotClasses = haricotClasses;
  }

  public HashMap<Class<?>, SojaClassContainer> getSojaClasses() {
    return sojaClasses;
  }

  public void setSojaClasses(HashMap<Class<?>, SojaClassContainer> sojaClasses) {
    this.sojaClasses = sojaClasses;
  }

  public HaricotInstantiator getInstantiator() {
    return instantiator;
  }

  public void setInstantiator(HaricotInstantiator instantiator) {
    this.instantiator = instantiator;
  }

  void injectFields(Object instance, SojaClassContainer sojaClassContainer, String haricotName) {
    // Get the fields to inject
    var fields = sojaClassContainer.getSojaFields();

    for (var field : fields.values()) {

      // Do not have an @Inject annotation
      if (Arrays.stream(field.getSojaAnnotations()).noneMatch(s -> s.type().equals(Inject.TYPE)))
        continue;

      // Get the @Inject annotation
      var injectAnnotation = field.getField().getAnnotation(Inject.class);

      // Instantiate the dependency
      var fieldReflect = field.getField();
      var fieldType = field.getField().getType();
      var haricotNameInject = HaricotInstantiator.getHaricotName(fieldType, haricotClasses.get(fieldType));
      var injectName = injectAnnotation.name();

      System.out.println("Injecting field of type: " +
          fieldType.getSimpleName() + " with Haricot name: " + haricotNameInject);

      Object o = checkInjectionMethodAndInstantiate(fieldType, true, haricotClasses.get(fieldType),
          sojaClasses.get(fieldType),
          haricotNameInject, injectName);

      fieldReflect.setAccessible(true);

      try {
        fieldReflect.set(instance, o);
      } catch (IllegalArgumentException e) {
        throw new HaricotInstantiationException(
            "Failed to inject field: " + field.getField().getName()
                + " in Haricot: " + sojaClassContainer.getClazz().getName(),
            e);
      } catch (IllegalAccessException e) {
        throw new HaricotInstantiationException(
            "Failed to inject field: " + field.getField().getName()
                + " in Haricot: " + sojaClassContainer.getClazz().getName(),
            e);
      }
    }
  }
}