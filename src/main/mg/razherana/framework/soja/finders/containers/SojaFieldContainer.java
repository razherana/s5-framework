package mg.razherana.framework.soja.finders.containers;

import java.lang.reflect.Field;

import mg.razherana.framework.soja.annotations.Soja;

public class SojaFieldContainer {
  private Field field;
  private SojaClassContainer parentSojaClassContainer;
  private Soja[] sojaAnnotations;

  public SojaFieldContainer(Field field, SojaClassContainer parentSojaClassContainer, Soja[] sojaAnnotations) {
    this.field = field;
    this.parentSojaClassContainer = parentSojaClassContainer;
    this.sojaAnnotations = sojaAnnotations;
  }

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
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

}
