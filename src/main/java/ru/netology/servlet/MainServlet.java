package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  public static final String API_POSTS = "/api/posts";
  public static final String API_POSTS_D = "/api/posts/\\d+";
  public static final String STR = "/";


  private PostController controller;

  @Override
  public void init() {
    final var repository = new PostRepository();
    final var service = new PostService(repository);
    controller = new PostController(service);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals("GET") && path.equals(API_POSTS)) {
        controller.all(resp);
        return;
      }
      if (method.equals("GET") && path.matches(API_POSTS_D)) {
        // easy way
        final var id = parserId(path);
        controller.getById(id, resp);
        return;
      }
      if (method.equals("POST") && path.equals(API_POSTS)) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals("DELETE") && path.matches(API_POSTS_D)) {
        // easy way
        final var id = Long.parseLong(path.substring(path.lastIndexOf(STR)));
        controller.removeById(id, resp);
        return;
      }
    } catch (NotFoundException e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  public long parserId (String path) {
    return Long.parseLong(path.substring(path.lastIndexOf(STR) + 1));
  }
}

