import java.util.*;
import org.sql2o.*;

public class Task {
  private int id;
  private String name;
  private boolean isdone = false;

  public int getId() {
    return id;
  }

  public boolean getisdone() {
    return isdone;
  }

  public String getName() {
    return name;
  }

  public Task(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object otherTask){
    if (!(otherTask instanceof Task)) {
      return false;
    } else {
      Task newTask = (Task) otherTask;
      return this.getName().equals(newTask.getName()) &&
             this.getId() == newTask.getId();
    }
  }

  public static List<Task> all() {
    String sql = "SELECT id, name FROM tasks";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Task.class);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO tasks(name) VALUES (:name)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", name)
        .executeUpdate()
        .getKey();
    }
  }

  public static Task find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM tasks where id=:id";
      Task task = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Task.class);
      return task;
    }
  }

  public void addCategory(Category category) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO tasks_categories (category_id, task_id) VALUES (:category_id, :task_id)";
      con.createQuery(sql)
      .addParameter("category_id", category.getId())
      .addParameter("task_id", id)
      .executeUpdate();
    }
  }

  public ArrayList<Category> getCategories() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT category_id FROM tasks_categories WHERE task_id=:id";
      List<Integer> categoryIds = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetch(Integer.class);
      ArrayList<Category> myCategories = new ArrayList<Category>();
      for(Integer categoryId : categoryIds) {
        String categoryQuery = "SELECT * FROM categories where id=:id";
        Category category = con.createQuery(categoryQuery)
          .addParameter("id", categoryId)
          .executeAndFetchFirst(Category.class);
          myCategories.add(category);
      }
    return myCategories;
    }
  }

  public void done() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE tasks SET isdone = true WHERE id=:id";
      con.createQuery(sql)
      .addParameter("id", id)
      .executeUpdate();
    }
  }

  public void update(String name) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE tasks SET name = :name WHERE id = :id";
      con.createQuery(sql)
        .addParameter("name", name)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
    String sql = "DELETE FROM tasks WHERE id = :id;";
      con.createQuery(sql)
        .addParameter("id", id)
        .executeUpdate();

    String deleteQuery = "DELETE FROM tasks_categories WHERE task_id = :id";
      con.createQuery(deleteQuery)
        .addParameter("id", id)
        .executeUpdate();
    }
  }
}
