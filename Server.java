package mg.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.LinkedList;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import mg.base.SQL;

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
                System.out.println("A new client is connected " + socket);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                SQL sql = new SQL();
                ClientHandler clientHandler = new ClientHandler(socket, dataOutputStream, dataInputStream, objectOutputStream, sql);
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
        private Socket socket;
        private int count = 0;
        private String name;
        private SQL sql;
        private DataOutputStream outputStream;
        private DataInputStream inputStream;
        private ObjectOutputStream objectOutputStream;

        public ClientHandler(Socket socket, DataOutputStream outputStream, DataInputStream inputStream, ObjectOutputStream objectOutputStream, SQL sql) {
            this.socket = socket;
            this.outputStream = outputStream;
            this.inputStream = inputStream;
            this.objectOutputStream = objectOutputStream;
            this.sql = sql;
        }        
        
        @Override 
        public void run() {
            String receive = "";
            try {
                this.name = inputStream.readUTF();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getCause());
            }
            LinkedList<String> dataFetch = new LinkedList<>();
            boolean first = true; 
            System.out.println("User's name : " + name);
            while (true) {
                try {
                    if (first) {
                        outputStream.writeUTF("ENTER THE REQUEST : \nJAVASQL> ");
                        outputStream.flush();
                        first = false;   
                    }
                    receive = inputStream.readUTF();
                    System.out.println(name + " >" + receive);
                    if (receive.toLowerCase().equals("fin") || receive.toLowerCase().equals("quit") || receive.toLowerCase().equals("exit")) {
                        String toSend = "";
                        if (count <= 1) 
                            toSend = "Today " + LocalDate.now() + ", you executed " + count + " request";   
                        else 
                            toSend = "Today " + LocalDate.now() + ", you executed " + count + " requests";          
                        outputStream.writeUTF(toSend + "\nHAVE A NICE DAY :D");
                        outputStream.flush();
                        break;         
                    } else {
                        Object obj = sql.doRequest(receive);
                        objectOutputStream.writeObject(obj);
                        objectOutputStream.flush();
                        outputStream.writeUTF("ENTER THE REQUEST : \nJAVASQL> ");
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
                this.outputStream.close();
                this.inputStream.close();
                this.socket.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getCause());
            } 
        }
    }
}
