package mg.razherana.framework.web.finders;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mg.razherana.framework.haricot.HaricotInstantiator;
import mg.razherana.framework.haricot.annotations.Haricot;
import mg.razherana.framework.haricot.containers.InstancedHaricotContainer;
import mg.razherana.framework.haricot.exceptions.HaricotConfigurationException;
import mg.razherana.framework.soja.finders.containers.SojaClassContainer;
import mg.razherana.framework.web.annotations.Controller;
import mg.razherana.framework.web.annotations.routing.GetRouting;
import mg.razherana.framework.web.containers.ControllerContainer;
import mg.razherana.framework.web.containers.RoutingContainer;
import mg.razherana.framework.web.containers.RoutingContainer.HttpMethod;
import mg.razherana.framework.web.routing.WebMapper;

public class WebFinder {
  public static WebFinder register(HaricotInstantiator haricotInstantiator) {
    var web = new WebFinder(haricotInstantiator.getHaricotContainer(),
        haricotInstantiator.getFactory().getHaricotClasses(),
        haricotInstantiator.getFactory().getSojaClasses());

    web.findControllers();
    return web;
  }

  private Set<ControllerContainer> controllerContainers;
  // Other web related containers can be added here (Filters? Middlewares? etc.)
  private InstancedHaricotContainer instancedHaricotContainer;
  private HashMap<Class<?>, Haricot[]> haricotClasses;

  private HashMap<Class<?>, SojaClassContainer> sojaClasses;

  public WebFinder(InstancedHaricotContainer instancedHaricotContainer, HashMap<Class<?>, Haricot[]> haricotClasses,
      HashMap<Class<?>, SojaClassContainer> sojaClasses) {
    this.controllerContainers = new HashSet<>();
    this.instancedHaricotContainer = instancedHaricotContainer;
    this.haricotClasses = haricotClasses;
    this.sojaClasses = sojaClasses;

  }

  public HashMap<Class<?>, SojaClassContainer> getSojaClasses() {
    return sojaClasses;
  }

  public void printEndpoints() {
    for (var controllerContainer : controllerContainers) {
      for (var routingContainer : controllerContainer.getRoutingContainers()) {
        var fullPath = WebMapper.combineAndNormalizePaths(
            controllerContainer.getControllerAnnotation().value(),
            routingContainer.getPath());

        System.out.println("[Fruits] : Registered Endpoint: [" + routingContainer.getHttpMethod() + "] "
            + fullPath
            + " -> "
            + controllerContainer.getClass().getSimpleName()
            + "."
            + routingContainer.getMethodReflection().getName());
      }
    }

    // TODO: Handle other web components (Filters, Middlewares, etc.)
  }

  public Set<ControllerContainer> getControllerContainers() {
    return controllerContainers;
  }

  public void setControllerContainers(Set<ControllerContainer> controllerContainers) {
    this.controllerContainers = controllerContainers;
  }

  private void findControllers() {
    // Filter only what we need
    for (var entry : haricotClasses.entrySet()) {
      Class<?> clazz = entry.getKey();
      var haricotAnnotations = entry.getValue();

      // Check if the class is annotated with @Controller
      boolean isController = false;
      for (var haricotAnnotation : haricotAnnotations)
        if (haricotAnnotation.type().equals(Controller.TYPE)) {
          isController = true;
          break;
        }

      if (isController) {
        handleControllerFound(clazz, haricotAnnotations, sojaClasses.get(clazz));
        continue;
      }

      // TODO: Handle other annotations (e.g. Filters, Middlewares, etc.)
    }
  }

  private void handleControllerFound(Class<?> clazz, Haricot[] haricotAnnotations,
      SojaClassContainer sojaClassContainer) {
    Object controllerInstance = instancedHaricotContainer
        .getInstance(HaricotInstantiator.getHaricotName(clazz, haricotAnnotations));
    ControllerContainer controllerContainer = new ControllerContainer(clazz, controllerInstance, new ArrayList<>(),
        clazz.getAnnotation(Controller.class));
    SojaClassContainer sojaClassContainerInstance = sojaClasses.get(clazz);

    // Find and set RoutingContainers
    if (sojaClassContainerInstance == null)
      throw new HaricotConfigurationException(clazz,
          "No Soja information for Controller class: " + clazz.getSimpleName());

    var routingSojaGets = findGetRoutings(sojaClassContainerInstance, controllerContainer);
    controllerContainer.getRoutingContainers().addAll(routingSojaGets);

    // TODO: Implement more routing types (POST, PUT, DELETE, etc.)

    controllerContainers.add(controllerContainer);
  }

  private List<RoutingContainer> findGetRoutings(SojaClassContainer sojaClassContainer,
      ControllerContainer controllerContainer) {
    class Data {
      Method method;
      GetRouting getRouting;

      public Data(Method method) {
        this.method = method;
        getRouting = method.getAnnotation(GetRouting.class);
      }
    }

    return sojaClassContainer.getSojaMethods()
        .values()
        .stream()
        .filter(s -> Arrays.stream(s.getSojaAnnotations())
            .anyMatch(soja -> soja.type().equals(GetRouting.TYPE)))
        .map(s -> new Data(s.getMethod()))
        .map(data -> new RoutingContainer(HttpMethod.GET, data.getRouting.value(), controllerContainer, data.getRouting,
            data.method))
        .toList();
  }

}
