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
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ClientHandler clientHandler = new ClientHandler(socket, dataInputStream, dataOutputStream, objectOutputStream);
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
        DataOutputStream outputStream;
        DataInputStream inputStream;
        ObjectOutputStream objectOutputStream;
        Socket socket;
        private int count = 0;

        public ClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream, ObjectOutputStream objectOutputStream) {
            this.socket = socket;
            this.inputStream = inputStream;
            this.outputStream = outputStream;
            this.objectOutputStream = objectOutputStream;
        }        
        
        @Override 
        public void run() {
            String receive = "";
            String name = "";
            try {
                name = inputStream.readUTF();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getCause());
            }
            System.out.println("User(Socket :"+ this.socket +")'s name : " + name);
            while (true) {
                try {
                    outputStream.writeUTF("ENTER THE REQUEST : ");
                    outputStream.flush();
                    outputStream.writeUTF("JAVASQL>");
                    outputStream.flush();
                    receive = inputStream.readUTF();
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
                        break;         
                    } else {
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
            try {
                this.inputStream.close();
                this.outputStream.close();
                this.objectOutputStream.close();    
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getCause());
            }
        }
    }
}
