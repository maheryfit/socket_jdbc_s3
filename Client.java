package mg.socket;

import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client implements Runnable {
    private Socket socket;
    private int count = 0;
    public Client(String host, int adress) {
        try {
            this.socket = new Socket(host, adress);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
        }
    }
    @Override    
    public void run() {
        try {
            BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
            if (count == 0) {
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                System.out.print("Enter your name: ");
                String name = b.readLine();
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();
                count++;
            }
            while (true) {
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                String req = dataInputStream.readUTF();
                System.out.println(req);
                String console = dataInputStream.readUTF();
                System.out.println(console);
                System.out.print("Client> ");
                String toSend = b.readLine();
                if (toSend.toLowerCase().equals("fin") || toSend.toLowerCase().equals("quit") || toSend.toLowerCase().equals("exit")) {
                    System.out.println("Server> " + dataInputStream.readUTF());
                    System.out.println("Server> " + dataInputStream.readUTF());
                    System.out.println("Connection to close...");
                    socket.close();
                    System.out.println("Connection closed");
                    break;
                }
                dataOutputStream.writeUTF(toSend);
                dataOutputStream.flush();
                System.out.println(toSend);
            }   
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
        }
    }
}
