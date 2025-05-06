package Io;

import database.TaskController;
import database.UserController;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientHandler extends Thread {
    private static final Object DB_LOCK = new Object();
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private UserController userController;
    private TaskController taskController;

    public ClientHandler(Socket socket) throws SQLException {
        this.socket = socket;
        this.userController = new UserController();
        this.taskController = new TaskController();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your ID:");
            String userId = in.readLine();
            out.println("Enter your password:");
            String password = in.readLine();

            User user;
            synchronized (DB_LOCK) {
                user = userController.validateLogin(userId, password);
            }

            if (user == null) {
                out.println("Sorry, incorrect ID or password.");
                socket.close();
                return;
            }

            out.println("Login successful! Welcome " + user.getName());

            if (user.getRole().equals("employee")) {
                showAssignedTasks(user.getId());
                handleEmployeeTasks();
            } else if (user.getRole().equals("manager")) {
                handleManagerTasks();
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            try {
                out.println("An error occurred. Please try again later.");
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showAssignedTasks(String userId) throws SQLException {
        List<Task> tasks = taskController.getTasksForUser(userId);
        if (tasks.isEmpty()) {
            out.println("You have no assigned tasks.");
            return;
        }
        out.println("\n== Your Assigned Tasks ==");
        for (Task task : tasks) {
            String status = task.isCompleted() ? "[Completed]" : "[Pending]";
            out.println(task.getId() + ". " + status + " " + task.getTitle() + " | Due: " + task.getDueDate());
        }
    }

    private void handleEmployeeTasks() throws IOException, SQLException {
        String message;
        while (true) {
            out.println("\n=== Employee Task  ===");
            out.println("1. Show all tasks");
            out.println("2. Mark task as complete");
            out.println("3. Exit");
            out.println("Enter your choice:");

            message = in.readLine();
            switch (message) {
                case "1": synchronized (DB_LOCK) { listTasks(); } break;
                case "2": synchronized (DB_LOCK) { markTaskComplete(); } break;
                case "3": out.println("Goodbye!"); socket.close(); return;
                default: out.println("Invalid option!");
            }
        }
    }

    private void handleManagerTasks() throws IOException, SQLException {
        String message;
        while (true) {
            out.println("\n=== Manager Task  ===");
            out.println("1. Add new task");
            out.println("2. Show all tasks");
            out.println("3. Mark task as complete");
            out.println("4. Delete task");
            out.println("5. Exit");
            out.println("Enter your choice:");

            message = in.readLine();
            switch (message) {
                case "1": synchronized (DB_LOCK) { addTask(); } break;
                case "2": synchronized (DB_LOCK) { listTasks(); } break;
                case "3": synchronized (DB_LOCK) { markTaskComplete(); } break;
                case "4": synchronized (DB_LOCK) { deleteTask(); } break;
                case "5": out.println("Goodbye!"); socket.close(); return;
                default: out.println("Invalid option!");
            }
        }
    }

    private void addTask() throws SQLException, IOException {
        out.println("Enter task title:");
        String title = in.readLine();

        out.println("Enter due date (yyyy-MM-dd HH:mm):");
        String dueDateStr = in.readLine();
        LocalDateTime dueDate;
        try {
            dueDate = LocalDateTime.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            out.println("Invalid date format.");
            return;
        }

        out.println("Enter employee ID to assign the task to:");
        String assignedTo = in.readLine();

        Task task = new Task(title);
        task.setDueDate(dueDate);
        task.setAssignedTo(assignedTo);
        taskController.addTask(task);

        out.println("Task assigned successfully to employee ID: " + assignedTo);
    }

    private void listTasks() throws SQLException {
        List<Task> tasks = taskController.getAllTasks();
        if (tasks.isEmpty()) {
            out.println("No tasks found!");
            return;
        }

        out.println("\nAll Tasks ");
        for (Task task : tasks) {
            String status = task.isCompleted() ? "[Completed]" : "[UnComplet]";
            out.println(task.getId() + ". " + status + " " + task.getTitle());
        }
    }

    private void markTaskComplete() throws SQLException, IOException {
        out.println("Enter task ID to mark as complete:");
        int id;
        try {
            id = Integer.parseInt(in.readLine().trim());
        } catch (NumberFormatException e) {
            out.println("Please enter a valid numeric ID.");
            return;
        }
        if (taskController.markTaskAsComplete(id)) {
            out.println("Task marked as complete!");
        } else {
            out.println("No task found with ID " + id);
        }
    }

    private void deleteTask() throws SQLException, IOException {
        out.println("Enter task ID to delete:");
        int id;
        try {
            id = Integer.parseInt(in.readLine().trim());
        } catch (NumberFormatException e) {
            out.println("Please enter a valid numeric ID.");
            return;
        }
        if (taskController.deleteTask(id)) {
            out.println("Task deleted successfully!");
        } else {
            out.println("No task found with ID " + id);
        }
    }
}
