package mg.razherana.framework.soja.finders.containers;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import mg.razherana.framework.soja.annotations.Soja;

public class SojaConstructorContainer {
  private Constructor<?> constructor;
  private SojaClassContainer parentSojaClassContainer;
  private Soja[] sojaAnnotations;

  /**
   * Key: index of the parameter in the constructor
   */
  private HashMap<String, SojaParameterContainer> sojaParameters;

  public SojaConstructorContainer(Constructor<?> constructor, SojaClassContainer parentSojaClassContainer,
      HashMap<String, SojaParameterContainer> sojaParameters, Soja[] sojaAnnotations) {
    this.constructor = constructor;
    this.parentSojaClassContainer = parentSojaClassContainer;
    this.sojaParameters = sojaParameters;
    this.sojaAnnotations = sojaAnnotations;
  }

  public Constructor<?> getConstructor() {
    return constructor;
  }

  public void setConstructor(Constructor<?> constructor) {
    this.constructor = constructor;
  }

  public SojaClassContainer getParentSojaClassContainer() {
    return parentSojaClassContainer;
  }

  public void setParentSojaClassContainer(SojaClassContainer parentSojaClassContainer) {
    this.parentSojaClassContainer = parentSojaClassContainer;
  }

  public HashMap<String, SojaParameterContainer> getSojaParameters() {
    return sojaParameters;
  }

  public void setSojaParameters(HashMap<String, SojaParameterContainer> sojaParameters) {
    this.sojaParameters = sojaParameters;
  }

  public Soja[] getSojaAnnotations() {
    return sojaAnnotations;
  }

  public void setSojaAnnotations(Soja[] sojaAnnotations) {
    this.sojaAnnotations = sojaAnnotations;
  }

}
