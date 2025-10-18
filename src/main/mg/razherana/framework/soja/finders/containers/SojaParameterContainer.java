package mg.razherana.framework.soja.finders.containers;

import java.lang.reflect.Parameter;

import mg.razherana.framework.soja.annotations.Soja;

public class SojaParameterContainer {
  private Parameter parameter;
  private SojaMethodContainer parentSojaMethodContainer;
  private SojaConstructorContainer parentSojaConstructorContainer;

  private int index;
  private Soja[] sojaAnnotations;

  public SojaParameterContainer(Parameter parameter, SojaMethodContainer parentSojaMethodContainer,
      SojaConstructorContainer parentSojaConstructorContainer, int index, Soja[] sojaAnnotations) {
    this.parameter = parameter;
    this.parentSojaMethodContainer = parentSojaMethodContainer;
    this.parentSojaConstructorContainer = parentSojaConstructorContainer;
    this.index = index;
    this.sojaAnnotations = sojaAnnotations;
  }

  public boolean isMethodParameter() {
    return parentSojaMethodContainer != null;
  }

  public boolean isConstructorParameter() {
    return parentSojaConstructorContainer != null;
  }

  public Parameter getParameter() {
    return parameter;
  }

  public void setParameter(Parameter parameter) {
    this.parameter = parameter;
  }

  public SojaMethodContainer getParentSojaMethodContainer() {
    return parentSojaMethodContainer;
  }

  public void setParentSojaMethodContainer(SojaMethodContainer parentSojaMethodContainer) {
    this.parentSojaMethodContainer = parentSojaMethodContainer;
  }

  public SojaConstructorContainer getParentSojaConstructorContainer() {
    return parentSojaConstructorContainer;
  }

  public void setParentSojaConstructorContainer(SojaConstructorContainer parentSojaConstructorContainer) {
    this.parentSojaConstructorContainer = parentSojaConstructorContainer;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public Soja[] getSojaAnnotations() {
    return sojaAnnotations;
  }

  public void setSojaAnnotations(Soja[] sojaAnnotations) {
    this.sojaAnnotations = sojaAnnotations;
  }
}
