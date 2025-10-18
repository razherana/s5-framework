package mg.razherana.framework.soja.finders;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import mg.razherana.framework.soja.annotations.Soja;
import mg.razherana.framework.soja.finders.containers.SojaClassContainer;
import mg.razherana.framework.soja.finders.containers.SojaConstructorContainer;
import mg.razherana.framework.soja.finders.containers.SojaFieldContainer;
import mg.razherana.framework.soja.finders.containers.SojaMethodContainer;
import mg.razherana.framework.soja.finders.containers.SojaParameterContainer;
import mg.razherana.framework.soja.finders.implementations.ConstructorSojaFinder;
import mg.razherana.framework.soja.finders.implementations.FieldSojaFinder;
import mg.razherana.framework.soja.finders.implementations.MethodSojaFinder;
import mg.razherana.framework.soja.finders.implementations.ParameterSojaFinder;

public class SojaFinder {
  @SuppressWarnings("unchecked")
  private static <T> SojaFinderInterface<T> getFinder(T type) {
    if (type == null) {
      throw new IllegalArgumentException("Type cannot be null");
    }

    if (type instanceof Field) {
      return (SojaFinderInterface<T>) new FieldSojaFinder();
    } else if (type instanceof Method) {
      return (SojaFinderInterface<T>) new MethodSojaFinder();
    } else if (type instanceof Parameter) {
      return (SojaFinderInterface<T>) new ParameterSojaFinder();
    } else if (type instanceof Constructor<?>) {
      return (SojaFinderInterface<T>) new ConstructorSojaFinder();
    } else {
      throw new IllegalArgumentException("Unsupported type: " + type.getClass());
    }
  }

  public static <T> Soja[] findSoja(T type) {
    SojaFinderInterface<T> finder = (SojaFinderInterface<T>) getFinder(type);

    return finder.findSoja(type);
  }

  public static SojaClassContainer findAllSojaInClass(Class<?> clazz) {
    // Initialize for SojaClassContainer
    SojaClassContainer sojaClassContainer = new SojaClassContainer(clazz, new HashMap<>(), new HashMap<>(),
        new HashMap<>());

    // Fields
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      Soja[] sojaAnnotations = findSoja(field);
      if (sojaAnnotations.length > 0) {
        sojaClassContainer.getSojaFields().put(field.getName(),
            new SojaFieldContainer(field, sojaClassContainer, sojaAnnotations));
      }
    }

    // Methods
    Method[] methods = clazz.getDeclaredMethods();
    for (Method method : methods) {
      Soja[] sojaAnnotations = findSoja(method);
      if (sojaAnnotations.length == 0)
        continue;

      SojaMethodContainer sojaMethodContainer = new SojaMethodContainer(method, sojaClassContainer, sojaAnnotations,
          new HashMap<>());

      Parameter[] parameters = method.getParameters();

      int i = 0;
      for (Parameter parameter : parameters) {
        Soja[] paramSojaAnnotations = findSoja(parameter);
        if (paramSojaAnnotations.length > 0) {
          sojaMethodContainer.getSojaParameters().put(i + "",
              new SojaParameterContainer(parameter, sojaMethodContainer, null, i, paramSojaAnnotations));
        }

        i++;
      }

      String methodName = method.getName() + "(" + Arrays.stream(method.getParameterTypes())
          .map(e -> e.getSimpleName())
          .collect(Collectors.joining(",")) + ")";

      sojaClassContainer.getSojaMethods().put(
          methodName,
          sojaMethodContainer);
    }

    // Constructors
    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      Soja[] sojaAnnotations = findSoja(constructor);
      if (sojaAnnotations.length == 0)
        continue;

      SojaConstructorContainer sojaConstructorContainer = new SojaConstructorContainer(
          constructor,
          sojaClassContainer,
          new HashMap<>(),
          sojaAnnotations);

      Parameter[] parameters = constructor.getParameters();

      int i = 0;
      for (Parameter parameter : parameters) {
        Soja[] paramSojaAnnotations = findSoja(parameter);
        if (paramSojaAnnotations.length > 0) {
          sojaConstructorContainer.getSojaParameters().put(i + "",
              new SojaParameterContainer(parameter, null, sojaConstructorContainer, i, paramSojaAnnotations));
        }

        i++;
      }

      String constructorName = "(" + Arrays.stream(constructor.getParameterTypes())
          .map(e -> e.getSimpleName())
          .collect(Collectors.joining(",")) + ")";

      sojaClassContainer.getSojaConstructors().put(constructorName, sojaConstructorContainer);
    }

    return sojaClassContainer;
  }
}
