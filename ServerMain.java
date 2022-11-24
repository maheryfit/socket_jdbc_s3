/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principale;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import mg.socket.*;

/**
 *
 * @author ITU
 */
public class ServerMain {
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server(5050);
        Thread thread = new Thread(server);
        thread.start();
    }
    
}



