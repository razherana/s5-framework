package mg.razherana.framework.soja.finders.containers;

import java.util.Arrays;
import java.util.HashMap;

public class SojaClassContainer {
  private Class<?> clazz;
  private HashMap<String, SojaMethodContainer> sojaMethods;
  private HashMap<String, SojaFieldContainer> sojaFields;

  /**
   * Key: name of the constructor
   */
  private HashMap<String, SojaConstructorContainer> sojaConstructors;

  public SojaClassContainer(Class<?> clazz, HashMap<String, SojaMethodContainer> sojaMethods,
      HashMap<String, SojaFieldContainer> sojaFields, HashMap<String, SojaConstructorContainer> sojaConstructors) {
    this.clazz = clazz;
    this.sojaMethods = sojaMethods;
    this.sojaFields = sojaFields;
    this.sojaConstructors = sojaConstructors;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  public HashMap<String, SojaMethodContainer> getSojaMethods() {
    return sojaMethods;
  }

  public void setSojaMethods(HashMap<String, SojaMethodContainer> sojaMethods) {
    this.sojaMethods = sojaMethods;
  }

  public HashMap<String, SojaFieldContainer> getSojaFields() {
    return sojaFields;
  }

  public void setSojaFields(HashMap<String, SojaFieldContainer> sojaFields) {
    this.sojaFields = sojaFields;
  }

  public HashMap<String, SojaConstructorContainer> getSojaConstructors() {
    return sojaConstructors;
  }

  public void setSojaConstructors(HashMap<String, SojaConstructorContainer> sojaConstructors) {
    this.sojaConstructors = sojaConstructors;
  }

  public String debugInfo() {
    StringBuilder sb = new StringBuilder();
    sb.append("SojaClassContainer for class: ").append(clazz.getName()).append("\n");

    sb.append("Methods with Soja:\n");
    for (SojaMethodContainer methodContainer : sojaMethods.values()) {
      sb.append("  - ").append(methodContainer.getMethod().getName()).append("\n");
      sb.append("    Annotations: ").append(Arrays.toString(methodContainer.getSojaAnnotations())).append("\n");

      // Parameters with Soja
      for (SojaParameterContainer paramContainer : methodContainer.getSojaParameters().values()) {
        sb.append("      * Parameter index ").append(paramContainer.getIndex()).append(" with type ")
            .append(paramContainer.getParameter().getType().getName()).append("\n");
        sb.append("        Annotations: ").append(Arrays.toString(paramContainer.getSojaAnnotations())).append("\n");
      }
    }

    sb.append("Fields with Soja:\n");
    for (SojaFieldContainer fieldContainer : sojaFields.values()) {
      sb.append("  - ").append(fieldContainer.getField().getName()).append("\n");
      sb.append("    Annotations: ").append(Arrays.toString(fieldContainer.getSojaAnnotations())).append("\n");
    }

    sb.append("Constructors with Soja:\n");
    for (SojaConstructorContainer constructorContainer : sojaConstructors.values()) {
      sb.append("  - ").append(constructorContainer.getConstructor().getName()).append("\n");
      sb.append("    Annotations: ").append(Arrays.toString(constructorContainer.getSojaAnnotations())).append("\n");

      // Parameters with Soja
      for (SojaParameterContainer paramContainer : constructorContainer.getSojaParameters().values()) {
        sb.append("      * Parameter index ").append(paramContainer.getIndex()).append(" with type ")
            .append(paramContainer.getParameter().getType().getName()).append("\n");
        sb.append("        Annotations: ").append(Arrays.toString(paramContainer.getSojaAnnotations())).append("\n");
      }
    }

    return sb.toString();
  }
}
