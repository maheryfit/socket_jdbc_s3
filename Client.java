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
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            System.out.print("Enter your name: ");
            String name = b.readLine();
            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();
            while (true) {
                String req = dataInputStream.readUTF();
                System.out.println("Server> " + req);
                String console = dataInputStream.readUTF();
                System.out.println(console);
                System.out.println("Client> ");
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
