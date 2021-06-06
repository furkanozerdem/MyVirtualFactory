package Cleints;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import static com.sun.org.apache.xalan.internal.lib.ExsltMath.random;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import static java.lang.Math.random;
import static java.lang.StrictMath.random;
import java.net.InetAddress;
import java.net.Proxy.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.nashorn.internal.objects.NativeMath.random;
import server.DB;
import Models.Machine;

import server.Server;



public class Planning_Screen {
    static Socket s;
    private static InetAddress host;

    public static void main(String[] args) throws IOException {
        PrintWriter out;
        Scanner inp;
        
        boolean check = true;
        s = null;    
        
        try {
            // TODO code application logic here
            host = InetAddress.getLocalHost();
            
            s = new Socket(InetAddress.getLocalHost(),4000);
            
            out = new PrintWriter(s.getOutputStream(),true);
            inp = new Scanner(s.getInputStream());
            
            System.out.println("Port uygun.");
            System.out.println("***********");
            
        } catch (IOException ex) {
            System.out.println("Server'a ulaşilamiyor. Daha sonra tekrar deneyin.");
            return;
        }
        
       login(inp,out);
       //sendData();
        
        while(check)  {
        System.out.println("Secim yapin");
        System.out.println("1 - Görev Gönder");
        System.out.println("2 - Makine Listesi");//eksik
        System.out.println("3 - Çıkış yap");
        
        Scanner scanner = new Scanner(System.in);
        int choose = scanner.nextInt();
            
        switch(choose) {
            case 1:
            sendJob(inp,out);
                break;
            case 2:
            machineList(inp,out);
                break;
            case 3:
            logout(inp, out);
                break;
            
            }
        }
    }

   
    private static void sendJob(Scanner inp, PrintWriter out) {
       //out.println("SENDJOB");
       int jobID, jobType, jobTime;
       String jobName;
        System.out.println("İş id girin");
        jobID = new Scanner(System.in).nextInt();
        
        System.out.println("İş adını girin");
        jobName = new Scanner(System.in).next();
        
       System.out.println("İş tipini girin.");
        System.out.println("1 - cnc");
        System.out.println("2 - 3d_yazici");
        System.out.println("3 - döküm");
        System.out.println("4 - kaplama");
        jobType = new Scanner(System.in).nextInt();
        
        System.out.println("İş süresini girin");
        jobTime = new Scanner(System.in).nextInt();
        
        String request = "SENDJOB " + jobID + "/" + jobName + "/" + jobTime + "/" + jobType;
        
        out.println(request);
        
        System.out.println(inp.nextLine());
        return;
         
    }
    
    
    private static void machineList(Scanner inp, PrintWriter out) {
        out.println("GETMACHINELIST");
        
        String response = inp.nextLine();

        if(response.substring(0,3).equals("300")) { //makine listesi baÅŸarÄ±yla getirildi.
            String jsonData = response.substring(6,response.length());
            Gson gson = new Gson();
            ArrayList<Machine> machineList = new ArrayList<Machine>();
            java.lang.reflect.Type listType = new TypeToken<ArrayList<Machine>>(){}.getType(); //Model class Ä±n tipine ait tanÄ±mlama  (Json dan arraylist e dÃ¶ndÃ¼rmek iÃ§in gerekli.)
            
            machineList  = gson.fromJson(jsonData,listType);
            
            System.out.println("Makine ID  |   Makine adi |  Makine Türü |  Hız     |    Durum  ");
            System.out.println("-------------------------------------------------------------------");
            String tempType = "";
            for(int i=0;i<machineList.size();i++) {
                switch(machineList.get(i).getMachineTypeID()) {
                    case 1:
                        tempType = "cnc";
                        break;
                    case 2:
                        tempType = "3d_yazici";
                        break;
                    case 3:
                        tempType = "döküm";
                        break;
                    case 4:
                        tempType = "kaplama";
                        break;
                }
                System.out.println(machineList.get(i).getMachineID() + "      |      " + machineList.get(i).getMachineName() + "      |      " + tempType + "      |      " + machineList.get(i).getProduceSpeed() + "     |     " +  (machineList.get(i).isStatus() ? "Meşgul" : "Uygun"));
                
            }
            
          
        }
        
        
        
        
    }
    
    private static void login(Scanner inp, PrintWriter out) {
  
        Scanner userName, password;
       
        String response;
        String message;
        //Scanner scn = new Scanner(System.in);
        //String machineType = scn.next();
        
            while(true) { //giriÅŸ red edildiÄŸi sÃ¼rece.
           
            System.out.println("Kullanici Adinizi Girin");
            userName = new Scanner(System.in);
            String userNameString = userName.nextLine();
            
            System.out.println("Parolanizi girin");
            password = new Scanner(System.in);
            String passwordString = userName.nextLine();
            
            //PrintWriter out = new PrintWriter(s.getOutputStream(),true); //veri Ã§Ä±kÄ±ÅŸ akÄ±ÅŸÄ±
            //Scanner inp = new Scanner(s.getInputStream()); //veri giriÅŸ akÄ±ÅŸÄ±
            
            message = "LOGIN " + userNameString + "/" + passwordString;
            //LOGIN username/password
            
            out.println(message); //mesaj server'a gÃ¶nderildi.
            out.flush();
            response = inp.nextLine();
            //   System.out.println(response);
            System.out.println("Server message: " + response);
            if(response.substring(0,3).equals("100")){
                //giriÅŸ kabul edilmiÅŸse.
                return;
            }
            
            
        }
        
        
    }
    
    private static void logout(Scanner inp, PrintWriter out) {
  
        out.println("LOGOUT");
        out.flush();
        
        String response = inp.nextLine();
        System.out.println("Server Message : " + response);
        
        if(response.substring(0, 3).equals("102")) 
            System.exit(0);
        
        
        
        
    }
    
    
    
   
}
