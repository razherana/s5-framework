package mg.razherana.framework.soja.finders.containers;

import java.lang.reflect.Method;
import java.util.HashMap;

import mg.razherana.framework.soja.annotations.Soja;

public class SojaMethodContainer {
  private Method method;
  private SojaClassContainer parentSojaClassContainer;
  private Soja[] sojaAnnotations;
  
  /**
   * Key: index of the parameter in the method
   */
  private HashMap<String, SojaParameterContainer> sojaParameters;

  public SojaMethodContainer(Method method, SojaClassContainer parentSojaClassContainer, Soja[] sojaAnnotations,
      HashMap<String, SojaParameterContainer> sojaParameters) {
    this.method = method;
    this.parentSojaClassContainer = parentSojaClassContainer;
    this.sojaAnnotations = sojaAnnotations;
    this.sojaParameters = sojaParameters;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public SojaClassContainer getParentSojaClassContainer() {
    return parentSojaClassContainer;
  }

  public void setParentSojaClassContainer(SojaClassContainer parentSojaClassContainer) {
    this.parentSojaClassContainer = parentSojaClassContainer;
  }

  public Soja[] getSojaAnnotations() {
    return sojaAnnotations;
  }

  public void setSojaAnnotations(Soja[] sojaAnnotations) {
    this.sojaAnnotations = sojaAnnotations;
  }

  public HashMap<String, SojaParameterContainer> getSojaParameters() {
    return sojaParameters;
  }

  public void setSojaParameters(HashMap<String, SojaParameterContainer> sojaParameters) {
    this.sojaParameters = sojaParameters;
  }

}
