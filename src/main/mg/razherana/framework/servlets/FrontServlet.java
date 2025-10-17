package mg.razherana.framework.servlets;

import java.io.IOException;
import java.net.MalformedURLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/")
public class FrontServlet extends HttpServlet {
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
    // Handle 404
    send404(response);
  }

  private void send404(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);

    response.getWriter().write("404 - Resource not found");
  }
}
