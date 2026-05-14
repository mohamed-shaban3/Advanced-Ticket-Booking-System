package networking;

import java.io.*;
import java.net.*;
import java.net.Socket;

public class StudentClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);
        System.out.println("Connected to Admin!");

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader studentInput = new BufferedReader(new InputStreamReader(System.in));

        String message;
        while (true) {
            // ابعت رسالة للـ Admin
            System.out.print("Student: ");
            message = studentInput.readLine();
            output.println(message);

            // استقبل رد من الـ Admin
            String reply = input.readLine();
            if (reply == null) break;
            System.out.println("Admin: " + reply);
        }

        socket.close();
    }
}