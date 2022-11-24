package mg.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class Server implements Runnable {

    private ServerSocket serverSocket;

    public Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
        }
    }
    
    @Override
    public void run() {
        while (true) {
            System.out.println("Wait for request...");
            Socket socket = null;
            try {
                socket = this.serverSocket.accept();
                System.out.println("A new client is connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                System.out.println("Assigning a new thread to this client...");
                Thread thread = new Thread(clientHandler);
                thread.start();        
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getCause());
            }
        }
    }

    class ClientHandler implements Runnable {
        Socket socket;
        private int count = 0;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }        
        
        @Override 
        public void run() {
            String receive = "";
            String name = "";
            try {
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
                name = inputStream.readUTF();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getCause());
            }
            System.out.println("User(Socket :"+ this.socket +")'s name : " + name);
            while (true) {
                try {
                    DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
                    DataInputStream inputStream = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
                    outputStream.writeUTF("ENTER THE REQUEST : ");
                    outputStream.flush();
                    outputStream.writeUTF("JAVASQL>");
                    outputStream.flush();
                    receive = inputStream.readUTF();
                    System.out.println(receive);
                    if (receive.toLowerCase().equals("fin") || receive.toLowerCase().equals("quit") || receive.toLowerCase().equals("exit")) {
                        String message = "";
                        if (count <= 1) 
                            message = "Today " + LocalDate.now() + ", you executed " + count + " request";   
                        else 
                            message = "Today " + LocalDate.now() + ", you executed " + count + " requests";          
                        outputStream.writeUTF(message);
                        outputStream.flush();
                        outputStream.writeUTF("HAVE A NICE DAY :D");
                        outputStream.flush();
                        this.socket.close();
                        outputStream.close();
                        inputStream.close();
                        objectOutputStream.close();
                        break;         
                    } else {
                        outputStream.writeUTF("ENTER THE REQUEST : ");
                        outputStream.flush();
                        outputStream.writeUTF("JAVASQL>");
                        outputStream.flush();
                        outputStream.writeUTF(receive);
                        outputStream.flush();
                        count++;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println(e.getLocalizedMessage());
                    System.out.println(e.getCause());
                }
            }
        }
    }
}
