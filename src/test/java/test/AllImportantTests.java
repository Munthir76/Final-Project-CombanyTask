package test;

import database.TaskController;
import database.UserController;
import database.DatabaseConnection;
import Io.Task;
import Io.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllImportantTests {

    static TaskController taskController;
    static UserController userController;

    @BeforeAll
    static void init() throws SQLException {
        taskController = new TaskController();
        userController = new UserController();
    }

    @Test
    @Order(1)
    void testDatabaseConnection() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
    }



    @Test
    @Order(3)
    void testInvalidLogin() throws SQLException {
        User user = userController.validateLogin("invalid_user", "wrong_pass");
        assertNull(user);
    }

    @Test
    @Order(4)
    void testAddAndRetrieveAssignedTask() throws SQLException {
        Task task = new Task("Assigned Task Test");
        task.setDueDate(LocalDateTime.now().plusDays(2));
        task.setAssignedTo("emp1");
        taskController.addTask(task);

        List<Task> userTasks = taskController.getTasksForUser("emp1");
        assertTrue(userTasks.stream().anyMatch(t -> t.getTitle().equals("Assigned Task Test")));
    }

    @Test
    @Order(5)
    void testMarkTaskAsComplete() throws SQLException {
        Task task = new Task("Complete Me");
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setAssignedTo("emp2");
        taskController.addTask(task);

        int id = taskController.getAllTasks().stream()
                .filter(t -> t.getTitle().equals("Complete Me"))
                .findFirst().get().getId();

        boolean result = taskController.markTaskAsComplete(id);
        assertTrue(result);
    }

    @Test
    @Order(6)
    void testDeleteNonexistentTask() throws SQLException {
        boolean result = taskController.deleteTask(-999); // ID وهمي
        assertFalse(result);
    }

    @Test
    @Order(7)
    void testDueDateParsingFailure() {
        String invalidDate = "31-12-2024 10:00";
        assertThrows(DateTimeParseException.class, () -> {
            LocalDateTime.parse(invalidDate, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        });
    }

    @Test
    @Order(8)
    void testEmployeeSeesOnlyOwnTasks() throws SQLException {
        Task task1 = new Task("Private Task A");
        task1.setAssignedTo("empA");
        task1.setDueDate(LocalDateTime.now().plusDays(3));
        taskController.addTask(task1);

        Task task2 = new Task("Private Task B");
        task2.setAssignedTo("empB");
        task2.setDueDate(LocalDateTime.now().plusDays(3));
        taskController.addTask(task2);

        List<Task> tasksA = taskController.getTasksForUser("empA");
        List<Task> tasksB = taskController.getTasksForUser("empB");

        assertTrue(tasksA.stream().allMatch(t -> "empA".equals(t.getAssignedTo())));
        assertTrue(tasksB.stream().allMatch(t -> "empB".equals(t.getAssignedTo())));
    }
}
