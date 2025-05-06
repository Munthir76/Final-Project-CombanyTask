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
git clone https://github.com/Munthir76/Final-Project-CombanyTask.git
2. Set up the Database:

