# TaskManagementApp

**TaskManagementApp** is a robust and efficient task management system designed to streamline task assignment, tracking, and completion in a client-server architecture. With this application, managers can effortlessly assign tasks to employees, set deadlines, mark tasks as complete, and delete tasks. Employees can view their assigned tasks and mark them as completed, enhancing collaboration and productivity in organizations.

## Features

- **Task Assignment**: Managers can assign tasks to employees based on their roles and expertise.
- **Task Deadline Management**: Tasks can be assigned specific deadlines, ensuring better time management and project tracking.
- **Task Status**: Employees can mark tasks as complete once finished. Managers can view the status of all tasks.
- **Task Deletion**: Tasks can be removed when no longer needed or after completion.
- **Real-time Interaction**: The application works in real-time, providing immediate feedback when changes occur.

## Architecture

This application follows a **Client-Server architecture**:
- **Client**: The client application (running on the user's machine) connects to the server to interact with tasks.
- **Server**: The server application listens for incoming client connections, manages the tasks, and communicates with the database.

The system is built using **Java**, with a focus on performance, security, and scalability.

## Requirements

- **Java 11** or higher
- **MySQL** for the database
- The application uses **JDBC** for database connectivity
- Maven: Used for dependency management and building the project.

## How to Run

### 1. Clone the Repository:
To get started, first clone the repository to your local machine:

```bash
git clone https://github.com/Munthir76/Final-Project-CombanyTask.git;
```

### 2. Set up the Database:
You need to set up the MySQL database and create the required tables. Here's how to do that:

- **Create the Database**:
    ```sql
    CREATE DATABASE company_db;
    ```

- **Create the `users` table**:
    This table will store user details such as name, role, and password.
    ```sql
    CREATE TABLE users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(100),
        role VARCHAR(50),
        password VARCHAR(255)
    );
    ```

- **Create the `tasks` table**:
    This table will store task details like title, completion status, due date, and the assigned employee.
    ```sql
    CREATE TABLE tasks (
        id INT AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(255),
        completed BOOLEAN DEFAULT FALSE,
        due_date DATETIME,
        assigned_to VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    ```

### 3. Build the Project:
Ensure that you have **Java 11 or above** installed. Then, build the project using Maven or an IDE:

#### If using **Maven**:
Run the following command to clean and build the project:
```bash
mvn clean install
 ```
### 4. Run the Server:
Start the server by running the ServerMain class
```java
package network;

import Io.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class ServerMain {
        public static void main(String[] args) {

            try (ServerSocket serverSocket = new ServerSocket(10003)) {


                System.out.println("Server started on port 10003");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());


                    new ClientHandler(clientSocket).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
```
The server will listen for incoming client connections on port 10003. You should see:


نسخ

  

