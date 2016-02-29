import java.util.*;
import org.sql2o.*;

public class User {
  private int id;
  private String username;
  private String password;

  public int getId() {
    return id;
  }

  public String getPassword() {
    return password;
  }

  public String getName() {
    return username;
  }

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public boolean equals(Object otherUser){
    if (!(otherUser instanceof User)) {
      return false;
    } else {
      User newUser = (User) otherUser;
      return this.getName().equals(newUser.getName()) &&
             this.getId() == newUser.getId();
    }
  }

  public static List<User> all() {
    String sql = "SELECT * FROM users";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(User.class);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO users(username, password) VALUES (:username, :password)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("username", username)
        .addParameter("password", password)
        .executeUpdate()
        .getKey();
    }
  }

  public static User find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM users where id=:id";
      User user = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(User.class);
      return user;
    }
  }

  public static User findByUserName(String username) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM users where username=:username";
      User user = con.createQuery(sql)
        .addParameter("username", username)
        .executeAndFetchFirst(User.class);
      return user;
    }
  }

  public void addCategory(Category category) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO users_categories (category_id, user_id) VALUES (:category_id, :user_id)";
      con.createQuery(sql)
      .addParameter("category_id", category.getId())
      .addParameter("user_id", id)
      .executeUpdate();
    }
  }

  public ArrayList<Category> getCategories() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT category_id FROM users_categories WHERE user_id=:id";
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

  public void update(String username) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE users SET username = :username WHERE id = :id";
      con.createQuery(sql)
        .addParameter("username", username)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
    String sql = "DELETE FROM users WHERE id = :id;";
      con.createQuery(sql)
        .addParameter("id", id)
        .executeUpdate();

    String deleteQuery = "DELETE FROM users_categories WHERE user_id = :id";
      con.createQuery(deleteQuery)
        .addParameter("id", id)
        .executeUpdate();
    }
  }
}
