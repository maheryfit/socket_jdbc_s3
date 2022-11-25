/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principale;

import mg.socket.*;

/**
 *
 * @author ITU
 */
public class ClientMain {
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 5050);
        Thread thread = new Thread(client);
        thread.start();
    }   
}