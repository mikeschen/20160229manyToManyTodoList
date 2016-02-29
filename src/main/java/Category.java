import java.util.*;
import org.sql2o.*;

public class Category {
  private int id;
  private String name;

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Category(String name) {
    this.name = name;
  }

  public static List<Category> all() {
    String sql = "SELECT id, name FROM Categories";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Category.class);
    }
  }

  @Override
  public boolean equals(Object otherCategory){
    if (!(otherCategory instanceof Category)) {
      return false;
    } else {
      Category newCategory = (Category) otherCategory;
      return this.getName().equals(newCategory.getName());
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO Categories(name) VALUES (:name)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .executeUpdate()
        .getKey();
    }
  }

  public void addTask(Task newTask) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO Tasks_Categories(task_id, category_id) VALUES (:task_id, :category_id)";
      con.createQuery(sql)
        .addParameter("task_id", newTask.getId())
        .addParameter("category_id", id)
        .executeUpdate();
    }
  }

  public ArrayList<Task> getTasks() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT task_id FROM Tasks_Categories where category_id=:id";
      List<Integer> task_ids = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetch(Integer.class);


      ArrayList<Task> tasks = new ArrayList<Task>();

      for(Integer task_id : task_ids) {
        String taskQuery = "SELECT * FROM tasks WHERE id=:id";
        Task newTask = con.createQuery(taskQuery).addParameter("id", task_id).executeAndFetchFirst(Task.class);
        tasks.add(newTask);
      }
      return tasks;
    }
  }

  public static Category find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM categories where id=:id";
      Category category = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Category.class);
      return category;
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM categories WHERE id=:id";
      con.createQuery(sql)
      .addParameter("id", id)
      .executeUpdate();
      String deleteQuery = "DELETE FROM tasks_categories WHERE category_id=:id";
      con.createQuery(deleteQuery)
      .addParameter("id", id)
      .executeUpdate();
    }
  }
}
