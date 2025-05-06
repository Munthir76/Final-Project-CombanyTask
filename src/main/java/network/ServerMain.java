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

