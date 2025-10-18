package mg.razherana.framework.haricot.containers;

import java.util.HashMap;

public class InstancedHaricotContainer {
  private HashMap<String, Object> instances;

  public InstancedHaricotContainer() {
    this.instances = new HashMap<>();
  }

  public HashMap<String, Object> getInstances() {
    return instances;
  }

  public void addInstance(String haricotName, Object instance) {
    this.instances.put(haricotName, instance);
  }

  public boolean containInstance(String haricotName) {
    return this.instances.containsKey(haricotName);
  }

  public Object getInstance(String haricotName) {
    return this.instances.get(haricotName);
  }
}
