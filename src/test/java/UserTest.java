import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class UserTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void all_emptyAtFirst() {
    assertEquals(User.all().size(), 0);
  }

  @Test
  public void equals_returnsTrueIfDescriptionsAretheSame() {
    User firstUser = new User("Mow the lawn", "password");
    User secondUser = new User("Mow the lawn", "password");
    assertTrue(firstUser.equals(secondUser));
  }

  @Test
  public void save_savesObjectIntoDatabase() {
    User myUser = new User("Mow the lawn", "password");
    myUser.save();
    User savedUser = User.all().get(0);
    assertTrue(savedUser.equals(myUser));
  }

  @Test
  public void save_assignsIdToObject() {
    User myUser = new User("Mow the lawn", "password");
    myUser.save();
    User savedUser = User.all().get(0);
    assertEquals(myUser.getId(), savedUser.getId());
  }

  @Test
  public void find_findsUserInDatabase_true() {
    User myUser = new User("Mow the lawn", "password");
    myUser.save();
    User savedUser = User.find(myUser.getId());
    assertTrue(myUser.equals(savedUser));
  }
  @Test
  public void addCategory_addsCategoryToUser() {
    Category myCategory = new Category("Household chores");
    myCategory.save();

    User myUser = new User("Mow the lawn", "password");
    myUser.save();

    myUser.addCategory(myCategory);
    Category savedCategory = myUser.getCategories().get(0);
    assertTrue(myCategory.equals(savedCategory));
  }

  @Test
  public void getCategories_returnsAllCategories_ArrayList() {
    Category myCategory = new Category("Household chores");
    myCategory.save();

    User myUser = new User("Mow the lawn", "password");
    myUser.save();

    myUser.addCategory(myCategory);
    List savedCategories = myUser.getCategories();
    assertEquals(savedCategories.size(), 1);
  }
}
