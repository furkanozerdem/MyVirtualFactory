import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import server.DB;
import Models.Machine;


public class Machine_Screen {
    static Socket s;
    private static InetAddress host;
    
    
    public static void main(String[] args) {
        final int machineSpeed; //gelen iş büyüklüğü bu hız ile hesaplanıp  bekleme süresi ayarlanır.
        PrintWriter out;
        Scanner inp;
  
        try {
            // TODO code application logic here
            host = InetAddress.getLocalHost();
            s = new Socket(InetAddress.getLocalHost(),4000);
            out = new PrintWriter(s.getOutputStream(),true);
            inp = new Scanner(s.getInputStream());
            
            System.out.println("Port uygun.");
            System.out.println("***********");
            
        } catch (IOException ex) {
            System.out.println("Server'a ulaÅŸilamiyor. Daha sonra tekrar deneyin.");
            return;
        }
        while(true) {
                System.out.println("**********");
                System.out.println("1- Makine Oluştur/Bağla");
                System.out.println("2- Çıkış yap");
                System.out.println("*********");
        
        int choose;
   
            Scanner scn = new Scanner(System.in);
            choose = scn.nextInt();
            
            switch(choose) {
                case 1:
                     
                    createAndConnect(inp,out);
                    
                    break;
                case 2:
                    System.out.println("Sistem kapatılıyor...");
                    System.exit(1);
                    break;
                default:
                    System.out.println("Geçersiz giriş.");
            }
        }
    }

    private static void createAndConnect(Scanner inp, PrintWriter out) {
        String mName;
        int mID, typeID, speed;
        
  
        System.out.println("Makine id si girin:");
        mID = new Scanner(System.in).nextInt();
        
        System.out.println("Makine adini girin:");
        mName = new Scanner(System.in).next();
        
        System.out.println("**************");
        System.out.println("1 - cnc");
        System.out.println("2 - 3d_yazici");
        System.out.println("3 - döküm");
        System.out.println("4 - kaplama");
        System.out.println("*************** \n");
        System.out.println("Makine tipini girin:");
        typeID = new Scanner(System.in).nextInt();
        
        System.out.println("Makine hızını girin:");
         speed = new Scanner(System.in).nextInt();
       
        
        while(true) {
            
        try {
            System.out.println("Makine oluşturuldu. Server'a bağlamak için 1'e, çıkmak için 0 a basin.");

            switch(new Scanner(System.in).nextInt()) {
                case 1:
                String requestMessage = "CONNECT " + mID + "/" +mName + "/" + typeID + "/" + speed; 
                out.println(requestMessage);
                String response = inp.nextLine();
                if(response.substring(0,3).equals("203")) {
                    ready(inp,out);
                }
                else {
                    System.out.println(response);
                    System.exit(1);
                }
                case 0:
                    System.out.println("Sistem kapatılıyor...");
                    System.exit(1);
                    break;
                default:
                    System.out.println("Geçersiz girdi.");     
            }
                 
        } catch (Exception e) {
            System.out.println("Oluşturma başarısız.");
        }
        }
        
    }

    private static void ready(Scanner inp, PrintWriter out) {
        System.out.println("Makine hazır. İş bekleniyor...");
        while(true) {
            if(inp.nextLine().equals("141")) {
                System.out.println("gorev ulaştı.");
            }
            
            
        }
        
        
    }

    

  
}
