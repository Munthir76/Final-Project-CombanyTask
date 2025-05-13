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
    private User user;

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

    private void handleEmployeeTasks() throws IOException, SQLException {
        String message;
        while (true) {
            out.println("\n=== Employee Task  ===");
            out.println("1. Show my tasks");
            out.println("2. Mark task as complete");
            out.println("3. Print my tasks to file");  
            out.println("4. Exit");
            out.println("Enter your choice:");

            message = in.readLine();
            switch (message) {
                case "1": synchronized (DB_LOCK) { showEmployeeTasks(); } break;
                case "2": synchronized (DB_LOCK) { markTaskComplete(); } break;
                case "3": synchronized (DB_LOCK) { exportEmployeeTasksToFile(); } break; 
                case "4": out.println("Goodbye!"); socket.close(); return;
                default: out.println("Invalid option!");
            }
        }
    }

    private void showEmployeeTasks() throws SQLException {
        List<Task> tasks = taskController.getTasksForUser(user.getId());
        if (tasks.isEmpty()) {
            out.println("You have no tasks assigned.");
            return;
        }

        out.println("\n== Your Assigned Tasks ==");
        for (Task task : tasks) {
            String status = task.isCompleted() ? "[Completed]" : "[InCompleted]";
            out.println("Task ID: " + task.getId() + " | Title: " + task.getTitle() + " | Status: " + status + " | Due: " + task.getDueDate());
        }
    }

    private void exportEmployeeTasksToFile() throws SQLException, IOException {
        List<Task> tasks = taskController.getTasksForUser(user.getId()); // Fetch tasks assigned to the employee
        if (tasks.isEmpty()) {
            out.println("You have no tasks to export.");
            return;
        }

        File file = new File(user.getId() + "Task_Employee_To_File.txt"); // File named after the employee ID
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("=== My Tasks ===\n");
            for (Task task : tasks) {
                String status = task.isCompleted() ? "[Completed]" : "[InCompleted]";
                writer.write("Task ID: " + task.getId() + "\n" +
                        "Title: " + task.getTitle() + "\n" +
                        "Status: " + status + "\n" +
                        "Due Date: " + task.getDueDate() + "\n" +
                        "-------------------\n");
            }
            out.println("Your tasks have been successfully exported to " + file.getName());
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
            out.println("5. Export tasks to file");
            out.println("6. Exit");
            out.println("Enter your choice:");

            message = in.readLine();
            switch (message) {
                case "1": synchronized (DB_LOCK) { addTask(); } break;
                case "2": synchronized (DB_LOCK) { listTasks(); } break;
                case "3": synchronized (DB_LOCK) { markTaskComplete(); } break;
                case "4": synchronized (DB_LOCK) { deleteTask(); } break;
                case "5": synchronized (DB_LOCK) { exportTasksToFile(); } break;
                case "6": out.println("Goodbye!"); socket.close(); return;
                default: out.println("Invalid option!");
            }
        }
    }
    private  void exportTasksToFile() throws SQLException, IOException {
        List<Task> tasks = taskController.getAllTasks();
        if (tasks.isEmpty()) {
            out.println("No tasks to export.");
            return;
        }

        File file = new File("company_task_for_all.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("=== All Tasks ===\n");
            for (Task task : tasks) {
                String status = task.isCompleted() ? "[Completed]" : "[Pending]";
                writer.write("Task ID: " + task.getId() + "\n" +
                        "Title: " + task.getTitle() + "\n" +
                        "Status: " + status + "\n" +
                        "Due Date: " + task.getDueDate() + "\n" +
                        "Assigned To: " + task.getAssignedTo() + "\n" +
                        "-------------------\n");
            }
            out.println("Tasks have been successfully exported to company_task_for_all.txt");
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
            String status = task.isCompleted() ? "[Completed]" : "[InComplet]";
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
