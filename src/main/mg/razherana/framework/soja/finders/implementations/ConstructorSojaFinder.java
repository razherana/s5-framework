package mg.razherana.framework.soja.finders.implementations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mg.razherana.framework.soja.annotations.Soja;
import mg.razherana.framework.soja.finders.SojaFinderInterface;

public class ConstructorSojaFinder implements SojaFinderInterface<Constructor<?>> {
  @Override
  public Soja[] findSoja(Constructor<?> constructor) {
    return findSoja(constructor, new HashSet<>());
  }

  private Soja[] findSoja(Constructor<?> constructor, Set<Class<? extends Annotation>> visited) {
    if (constructor == null) {
      return new Soja[0];
    }
    
    List<Soja> sojas = new ArrayList<>();
    
    // Direct annotation check
    if (constructor.isAnnotationPresent(Soja.class)) {
      sojas.add(constructor.getAnnotation(Soja.class));
    }

    // Check for meta-annotations (exclude direct Soja annotations)
    List<Annotation> nonSojaAnnotations = new ArrayList<>();
    for (Annotation annotation : constructor.getDeclaredAnnotations()) {
      if (!annotation.annotationType().equals(Soja.class)) {
        nonSojaAnnotations.add(annotation);
      }
    }
    
    Soja[] metaSojas = findSojaRecursive(nonSojaAnnotations.toArray(new Annotation[0]), visited);
    for (Soja metaSoja : metaSojas) {
      sojas.add(metaSoja);
    }
    
    return sojas.toArray(new Soja[0]);
  }

  private Soja[] findSojaRecursive(Annotation[] annotations, Set<Class<? extends Annotation>> visited) {
    List<Soja> sojas = new ArrayList<>();
    
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType();

      // Skip if already visited to prevent cycles
      if (visited.contains(annotationType)) {
        continue;
      }

      // Skip java.lang annotations to avoid unnecessary recursion
      if (annotationType.getName().startsWith("java.lang.annotation.")) {
        continue;
      }

      // Add to visited set
      visited.add(annotationType);

      // Check if the annotation is Soja
      if (annotationType.equals(Soja.class)) {
        sojas.add((Soja) annotation);
      } else {
        // Recursively check meta-annotations
        Soja[] metaSojas = findSojaRecursive(annotationType.getDeclaredAnnotations(), visited);
        for (Soja metaSoja : metaSojas) {
          sojas.add(metaSoja);
        }
      }

      // Remove from visited set after processing
      visited.remove(annotationType);
    }
    
    return sojas.toArray(new Soja[0]);
  }
}
