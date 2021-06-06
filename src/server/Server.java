/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static server.DB.userList;
import Models.MachineType;
import Models.Users;

public class Server {
        private static ServerSocket svSocket;
      
    public static void main(String[] args) throws IOException {
     
      
        Users u1 = new Users("admin","admin");
        Users u2 = new Users("admin1","admin1");
        Users u3 = new Users("admin2","admin2");
        
        MachineType mType = new MachineType(1,"cnc");
        MachineType mType2 = new MachineType(2,"3d_yazici");
        MachineType mType3 = new MachineType(3,"döküm");
        MachineType mType4 = new MachineType(4,"kaplama");
        
        
        DB.userList.add(u1);
        DB.userList.add(u2);
        DB.userList.add(u3);
        /*******************/
        DB.machineTypeList.add(mType);
        DB.machineTypeList.add(mType2);
        DB.machineTypeList.add(mType3);
        DB.machineTypeList.add(mType4);
        
        
        System.out.println("*********");
      
        try {
            svSocket = new ServerSocket(4000);
            System.out.println("Listening...");

        }    
                   catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while(true) {
            Socket client = svSocket.accept(); //client bağlantısı kabul edildi.
            
            System.out.println("Connection accepted on ip : " + client.getInetAddress().getHostAddress());
            
            ClientHandler handler = new ClientHandler(client);
            handler.start();
            
        }
        
       
        
       
    }

 

    
}
