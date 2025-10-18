package mg.razherana.framework.web.servlets;

import java.io.IOException;
import java.net.MalformedURLException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.razherana.framework.RunApplication;
import mg.razherana.framework.web.containers.RuntimeContainer;
import mg.razherana.framework.web.exceptions.WebExecutionException;
import mg.razherana.framework.web.exceptions.http.HttpException;
import mg.razherana.framework.web.exceptions.http.NotFoundException;
import mg.razherana.framework.web.routing.WebExecutor;
import mg.razherana.framework.web.routing.WebMapper;

@WebServlet("/")
public class FrontServlet extends HttpServlet {
  RuntimeContainer runtimeContainer;
  WebMapper webMapper;

  @Override
  public void init(ServletConfig config) throws ServletException {
    runtimeContainer = RunApplication.before(config);

    var webFinder = runtimeContainer.getWebFinder();
    webMapper = new WebMapper(webFinder);

    super.init(config);
  }

  @Override
  public void destroy() {
    RunApplication.after(runtimeContainer);
    runtimeContainer = null;
    webMapper = null;

    super.destroy();
  }

  private boolean resourceExists(HttpServletRequest request) {
    try {
      var resource = getServletContext().getResource(request.getRequestURI());
      return resource != null;
    } catch (MalformedURLException e) {
      return false;
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!resourceExists(req)) {
      handleRequest(req, resp);
      return;
    }
    super.doGet(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!resourceExists(req)) {
      handleRequest(req, resp);
      return;
    }
    super.doPost(req, resp);
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!resourceExists(req)) {
      handleRequest(req, resp);
      return;
    }
    super.doPut(req, resp);
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!resourceExists(req)) {
      handleRequest(req, resp);
      return;
    }
    super.doOptions(req, resp);
  }

  @Override
  protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!resourceExists(req)) {
      handleRequest(req, resp);
      return;
    }
    super.doHead(req, resp);
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!resourceExists(req)) {
      handleRequest(req, resp);
      return;
    }
    super.doDelete(req, resp);
  }

  @Override
  protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!resourceExists(req)) {
      handleRequest(req, resp);
      return;
    }
    super.doTrace(req, resp);
  }

  private void handleRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      var webRouteContainer = webMapper.findRouteMethod(request);

      if (webRouteContainer == null) {
        // Handle 404
        send404(response);
        return;
      }

      var webExecutor = new WebExecutor(webRouteContainer, runtimeContainer.getHaricotInstantiator());
      webExecutor.execute(request, response);
    } catch (HttpException httpEx) {
      this.runtimeContainer.handleHttpError(request, response, httpEx);
    } catch (Exception e) {
      throw new WebExecutionException("Error executing web request: " + e.getMessage(), e);
    }
  }

  private void send404(HttpServletResponse response) {
    throw new NotFoundException("Resource not found");
  }
}
