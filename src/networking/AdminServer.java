package networking;

import java.io.*;
import java.net.*;
import java.net.ServerSocket;

public class AdminServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Admin Server is running... Waiting for Student to connect.");

        Socket socket = serverSocket.accept();
        System.out.println("Student Connected!");

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader adminInput = new BufferedReader(new InputStreamReader(System.in));

        String message;
        while (true) {
            // استقبل رسالة من الـ Student
            message = input.readLine();
            if (message == null) break;
            System.out.println("Student: " + message);

            // ابعت رد من الـ Admin
            System.out.print("Admin: ");
            String reply = adminInput.readLine();
            output.println(reply);
        }

        socket.close();
        serverSocket.close();
    }
}