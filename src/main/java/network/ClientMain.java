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









