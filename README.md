# TaskManagementApp

**TaskManagementApp** is a robust and efficient task management system designed to streamline task assignment, tracking, and completion in a client-server architecture. With this application, managers can effortlessly assign tasks to employees, set deadlines, mark tasks as complete, and delete tasks. Employees can view their assigned tasks and mark them as completed, enhancing collaboration and productivity in organizations.





## Features

- **Task Assignment**: Managers can assign tasks to employees based on their roles and expertise.
- **Task Deadline Management**: Tasks can be assigned specific deadlines, ensuring better time management and project tracking.
- **Task Status**: Employees can mark tasks as complete once finished. Managers can view the status of all tasks.
- **Task Deletion**: Tasks can be removed when no longer needed or after completion.
- **Task Export to File**:
Manager: Can export all tasks to a file (company_task_for_all.txt), which includes the task details such as title, status, due date, and assigned employee.

Employee: Can export only their assigned tasks to a file (named with their employee ID, e.g., employeeID_tasks.txt), containing similar details.
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
git clone https://github.com/Munthir76/Final-Project-CombanyTask.git
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
        password VARCHAR(255)
        role VARCHAR(50),
    );
    ```

    
 - **Assign the users**:
    ```sql
   INSERT INTO users (name, password, role)
   VALUES 
    ('Munthir', '12', 'employee'),
    ('Rauan', '23', 'employee'),
    ('Sami', '34', 'employee'),
    ('Khalid', '00', 'manager');
    ```

    

- **Create the `tasks` table**:
    This table will store task details like title, completion status, due date, and the assigned employee.
    ```sql
    CREATE TABLE tasks (
        id INT AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(255),
        completed BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        due_date DATETIME,
        assigned_to VARCHAR(255),
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
```nginx
Server started on port 10003
```




### 5. Run the Client:
 run the client by executing the ClientMain class
```java
package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

        public class ClientMain implements Runnable {
            private final String serverAddress = "localhost";
            private final int serverPort = 10003;

            public static void main(String[] args) {
                int clients = 1;
                for (int i = 0; i < clients; i++) {
                    Thread t = new Thread(new ClientMain(), "Client-" + i);
                    t.start();
                }
            }

            @Override
            public void run() {
                try (
                        Socket socket = new Socket(serverAddress, serverPort);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader console = new BufferedReader(new InputStreamReader(System.in))
                ) {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        synchronized (System.out) {
                            System.out.println(serverMessage);
                        }
                        String msg = serverMessage.trim();
                        if (msg.startsWith("Enter") && msg.endsWith(":")) {
                            String userInput = console.readLine();
                            if (userInput == null) break;
                            out.println(userInput);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
```

The client will connect to the server, where you can log in as either an employee or a manager.


## Example Interaction

When you run the client, you will interact with the system through the terminal. Here's an example of what you might see:

### Client Startup:


You will be asked to enter your user ID. For example:
```bash
Enter your ID: 4
```
After entering your ID, you will be asked to enter your password For example:
```bash
Enter your password: 00
```




Manager Role Example:
If you log in as a manager, you will see the welcoming message and options like:
```bash
Login successful! Welcome Khalid

=== Manager Task  ===
1. Add new task
2. Show all tasks
3. Mark task as complete
4. Delete task
5. Export tasks to file
6. Exit
Enter your choice:
```
### Option 1: Add new task

As a manager, you can add new tasks to the system.

You will be prompted to enter the task title , due date and assigned employee For example: 
```bash
Enter task title:
CPIT-305-PROJECT
Enter due date (yyyy-MM-dd HH:mm):
2025-05-06 01:00
Enter employee ID to assign the task to:
1
Task assigned successfully to employee ID: 1
```




### Option 2: Show all tasks

View all tasks in the system, whether they are completed or not For example:
```bash
All Tasks 
23. [Incomplete] CPIT-305-PROJECT
22. [Incomplete] last dance 
9. [Incomplete] السلام عليكم !
8. [Incomplete] f
7. [Incomplete] finish
```




### Option 3: Mark task as complete

Mark a task as completed by entering its task ID For example:
```bash
Enter task ID to mark as complete:
23
Task marked as complete!
```




### Option 4: Delete task

Delete a task that is no longer needed by entering its task ID For example:
```bash
Enter task ID to delete:
8
Task deleted successfully!
```
###  Option 5: Export tasks to file
As a manager, you can export all tasks to a file named company_task_for_all.tx
```bash
=== Manager Task  ===
1. Add new task
2. Show all tasks
3. Mark task as complete
4. Delete task
5. Export tasks to file
6. Exit
Enter your choice:
5
Tasks have been successfully exported to company_task_for_all.txt
```

### Option 6: Exit

Exit the task management system.

```bash
Enter your choice:
6
Goodbye!

Process finished with exit code 0

```




### Employee Role Example:
If you log in as a Employee, you will see the welcoming message , and options like:

```bash
Login successful! Welcome Munthir


=== Employee Task  ===
1. Show my tasks
2. Mark task as complete
3. Print my tasks to file
4. Exit
Enter your choice:

```


## AI-Assisted
The AllImportantTests class was created with the help of ChatGPT to verify application functionality and ensure all required test cases are handled.












  

