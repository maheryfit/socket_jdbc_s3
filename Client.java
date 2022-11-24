package mg.socket;

import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class Client implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private ObjectInputStream objectInputStream;
    private boolean first = true;
    public Client(String host, int adress) {
        try {
            this.socket = new Socket(host, adress);
            this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
            this.dataInputStream = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            this.objectInputStream = new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()));
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
            if (first) {
                System.out.print("Enter your name: ");
                String name = bufferedReader.readLine();
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();
                first = false;
            }
            while (true) {
                String req = dataInputStream.readUTF();
                System.out.print(req);
                String toSend = bufferedReader.readLine();
                dataOutputStream.writeUTF(toSend);
                dataOutputStream.flush();
                if (toSend.toLowerCase().equals("fin") || toSend.toLowerCase().equals("quit") || toSend.toLowerCase().equals("exit")) {
                    System.out.println("Server> " + dataInputStream.readUTF());
                    System.out.println("Connection to close...");
                    socket.close();
                    this.bufferedReader.close();
                    this.dataInputStream.close();
                    this.dataOutputStream.close();
                    System.out.println("Connection closed");
                    break;
                } else {
                    if (dataInputStream.readUTF().equals("table")) {
                        LinkedList<String> datas = (LinkedList<String>) objectInputStream.readObject();
                        String row = (String) objectInputStream.readObject();
                        displayResult(datas, row);
                    } else if(dataInputStream.readUTF().equals("message")) {
                        String message = (String) objectInputStream.readObject();
                        displayMessage(message);
                    }
                }
            }   
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
        }
    }

    private void displayResult(LinkedList<String> datas, String row) {
        datas.forEach(data -> {
            System.out.println(data);
        });
        System.out.println(row);
    }

    private void displayMessage(String message) {
        System.out.println(message);
    }
}
