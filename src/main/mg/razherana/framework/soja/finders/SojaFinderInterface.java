package mg.razherana.framework.soja.finders;

import mg.razherana.framework.soja.annotations.Soja;

/**
 * Abstract class for finding soja instances in the framework.
 * 
 * @param <T> Type of element for the soja to find. 
 */
public interface SojaFinderInterface<T> {
  public Soja[] findSoja(T type);
}
