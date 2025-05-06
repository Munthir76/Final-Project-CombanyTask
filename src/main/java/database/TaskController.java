package database;

import Io.Task;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskController {
    private final Connection connection;

    public TaskController() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public synchronized void addTask(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (title, completed, due_date, assigned_to) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setBoolean(2, task.isCompleted());
            stmt.setTimestamp(3, Timestamp.valueOf(task.getDueDate()));
            stmt.setString(4, task.getAssignedTo());
            stmt.executeUpdate();
        }
    }

    public List<Task> getAllTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Task task = new Task(rs.getString("title"));
                task.setId(rs.getInt("id"));
                task.setCompleted(rs.getBoolean("completed"));
                task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                task.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
                task.setAssignedTo(rs.getString("assigned_to"));
                tasks.add(task);
            }
        }
        return tasks;
    }

    public List<Task> getTasksForUser(String userId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE assigned_to = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task(rs.getString("title"));
                    task.setId(rs.getInt("id"));
                    task.setCompleted(rs.getBoolean("completed"));
                    task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    task.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
                    task.setAssignedTo(rs.getString("assigned_to"));
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public synchronized boolean markTaskAsComplete(int id) throws SQLException {
        String sql = "UPDATE tasks SET completed = true WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public synchronized boolean deleteTask(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}




