package networking;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatApp {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - Admin (Server)");
        System.out.println("2 - Student (Client)");
        System.out.print("Enter choice: ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 1) {
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Waiting for Student on port 5000...");
            Socket socket = ss.accept();
            ss.close();
            System.out.println("Student Connected!");
            startChat(socket, "Admin");
        } else {
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connected to Admin!");
            startChat(socket, "Student");
        }
    }

    private static void startChat(Socket socket, String role) throws Exception {
        BufferedReader incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter    outgoing = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader console  = new BufferedReader(new InputStreamReader(System.in));
        String otherRole = role.equals("Admin") ? "Student" : "Admin";

        // Daemon thread reads — never blocks the writer
        Thread reader = new Thread(() -> {
            try {
                String line;
                while ((line = incoming.readLine()) != null) {
                    System.out.println("\n" + otherRole + ": " + line);
                    System.out.print(role + ": ");
                }
            } catch (SocketException e) {
                System.out.println("\n[Other side disconnected]");
            } catch (IOException e) {
                System.out.println("\n[Read error: " + e.getMessage() + "]");
            }
        });
        reader.setDaemon(true);
        reader.start();

        // Main thread sends
        String msg;
        while (true) {
            System.out.print(role + ": ");
            msg = console.readLine();
            if (msg == null || msg.equalsIgnoreCase("exit")) break;
            outgoing.println(msg);
        }
        socket.close();
    }
}