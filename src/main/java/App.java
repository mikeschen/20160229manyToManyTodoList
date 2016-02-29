import java.util.*;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/index.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/home", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String username = request.queryParams("loginUsername");
      User user = User.find(request.session().attribute("userId"));
      model.put("user", user);
      model.put("template", "templates/home.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String username = request.queryParams("username");
      String password = request.queryParams("password");
      User newUser = new User(username, password);
      newUser.save();
      response.redirect("/");
      return null;
    });

    post("/home", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String username = request.queryParams("loginUsername");
      String password = request.queryParams("loginPassword");
      User user = User.findByUserName(username);
      if (user != null) {
        if (user.getPassword().equals(password)) {
          request.session().attribute("userId", null);
          request.session().attribute("userId", user.getId());
          response.redirect("/home");
          return null;
        }
      }
      response.redirect("/");
      return null;
    });

    get("/:userId/tasks", (request,response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      List<Task> tasks = Task.all();
      User user = User.find(Integer.parseInt(request.params(":userId")));
      model.put("user", user);
      model.put("tasks", tasks);
      model.put("template", "templates/tasks.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/:userId/categories", (request, reponse) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      List<Category> categories = Category.all();
      User user = User.find(Integer.parseInt(request.params(":userId")));
      model.put("user", user);
      model.put("categories", categories);
      model.put("template", "templates/categories.vtl");
      return new ModelAndView(model, layout);
      }, new VelocityTemplateEngine());

    get("/:userId/tasks/:id", (request,response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      int id = Integer.parseInt(request.params("id"));
      Task task = Task.find(id);
      User user = User.find(Integer.parseInt(request.params(":userId")));
      model.put("user", user);
      model.put("task", task);
      model.put("allCategories", Category.all());
      model.put("template", "templates/task.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/:userId/categories/:id", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      int id = Integer.parseInt(request.params("id"));
      Category category = Category.find(id);
      System.out.println(category.getTasks());
      User user = User.find(Integer.parseInt(request.params(":userId")));
      model.put("user", user);
      model.put("category", category);
      model.put("allTasks", Task.all());
      model.put("template", "templates/category.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());


    post("/:userId/tasks", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String name = request.queryParams("name");
      String duedate = request.queryParams("duedate");
      Task newTask = new Task(name, duedate);
      newTask.save();
      response.redirect("/" + request.session().attribute("userId") + "/tasks");
      return null;
    });

    post("/:userId/categories", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String name = request.queryParams("name");
      Category newCategory = new Category(name);
      newCategory.save();
      response.redirect("/" + request.session().attribute("userId") + "/categories");
      return null;
    });

    post("/:userId/add_tasks", (request, response) -> {
      int taskId = Integer.parseInt(request.queryParams("task_id"));
      int categoryId = Integer.parseInt(request.queryParams("category_id"));
      Category category = Category.find(categoryId);
      Task task = Task.find(taskId);
      if(!(category.getTasks().contains(task))) {
        category.addTask(task);
      }
      response.redirect("/" + request.session().attribute("userId") + "/categories/" + categoryId);
      return null;
    });

    post("/:userId/add_categories", (request, response) -> {
      int taskId = Integer.parseInt(request.queryParams("task_id"));
      int categoryId = Integer.parseInt(request.queryParams("category_id"));
      Category category = Category.find(categoryId);
      Task task = Task.find(taskId);
      task.addCategory(category);
      response.redirect("/" + request.session().attribute("userId") + "/tasks/" + taskId);
      return null;
    });

    post("/:userId/categories/:id/:taskId/done", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      int categoryId = Integer.parseInt(request.params(":id"));
      int taskId = Integer.parseInt(request.params(":taskId"));
      Category category = Category.find(categoryId);
      Task task = Task.find(taskId);
      task.done();
      response.redirect("/" + request.session().attribute("userId") + "/categories/" + categoryId);
      return null;
    });

    post("/:userId/tasks/:taskId/done", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      int taskId = Integer.parseInt(request.params(":taskId"));
      Task task = Task.find(taskId);
      task.done();
      response.redirect("/" + request.session().attribute("userId") + "/tasks");
      return null;
    });

    put("/:userId/tasks/:id", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Task task = Task.find(Integer.parseInt(request.params("id")));
      String description = request.queryParams("description");
      task.update("description");
      model.put("template", "templates/task.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    delete("/:userId/tasks/:id", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Task task = Task.find(Integer.parseInt(request.params("id")));
      task.delete();
      model.put("template", "templates/task.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());
  }
}
