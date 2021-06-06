
package server;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import Models.Job;
import Models.Machine;
import Models.Users;

class ClientHandler extends Thread {
    private Socket client;
    private Scanner inp;
    private PrintWriter out;
    private Users currentUser;

    public ClientHandler(Socket socket) {

        this.client = socket;
        try {
          inp = new Scanner(client.getInputStream());
          out = new PrintWriter(client.getOutputStream(),true);
            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
               System.out.println(this.getName());
               String command; //girilen komut -
               
                
                //KOMUT 
               while(true) {
                ////////////////////////
             /**/command = inp.next();/**/
                ////////////////////////
                
                switch(command) {   
                    case "LOGIN":
                        String msg = checkLoginControl(inp.nextLine());
                        out.println(msg);
                        break;
                        
                    case "LOGOUT":
                          try{
                            if(client != null) {
                            System.out.println("Connection closing on:" + client.getInetAddress());
                            DB.thread_user.get(this.getName()).setStatus(false); //ilgili user'ın giriş bilgisi tekrar false yapıldı.                         
                            out.println("102 : Cikis Yapildi.");    
                            client.close();
                                }
                                   } catch(IOException e) {
                                        e.printStackTrace();
                                        out.println("103 : Cikis islemi hatali");
                                       }
                          break;
                          
                          case "CONNECT":        
                        //out.println("Doğru bölge");
                            machineConnectControl(inp.nextLine());
                         break;
                    case "GETMACHINELIST":
                       // out.println("Makine Listesi Getirilecek."); //makine listesi getir.
                        String json = new Gson().toJson(DB.machineList);
                        out.println("300 : " + json); //veri json tipinde gönderildi.
                        break;
                        
                    case "SENDJOB":                
                        takeJobFromPlanner(inp.nextLine());
                        
                    default:
                        out.print("900 : Tanımlanamayan anahtar kelime!"); 
                }
               
              }
                
        }
       
    public String checkLoginControl(String userInfo) {
               int a = userInfo.indexOf('/');
               String userName = userInfo.substring(1,a);
               String password = userInfo.substring(a+1,userInfo.length());
               String responseMessage;
               //LOGIN username/password
                
               //out.println("Username:" + userName + "Password:" + password);
               
               
               for(int i=0;i<DB.userList.size();i++) {
                  
                   if(DB.userList.get(i).getUserName().equals(userName) &&
                           DB.userList.get(i).getPassword().equals(password)
                           ) {
                       
                       if(!DB.userList.get(i).isStatus()){ //çevrimiçi değilse.
                       responseMessage = "100 :  Giriş Başarılı. Hoşgeldiniz : " + userName; //Giriş Başarılı.
                       currentUser = DB.userList.get(i); //yapılacak işlemler için kişi bilgisi, bu hesap için tanımlandı.
                       DB.userList.get(i).setStatus(true);//giriş bilgisi true tanımlandı.
                       DB.thread_user.put(this, DB.userList.get(i)); //ilgili thread, user ile ilişkilendirildi. Bu, kullanıcının durumunun takip edilebilmesi için gerekli.
                       return responseMessage;
                       }
                       else {
                           responseMessage = "104 : Bu hesap şuan başka ekranda çevrimiçi.";
                           return responseMessage;
                       }
                       
                   }
               }
               responseMessage = "101 : Kullanici adi veya sifre hatali."; //hatalı giriş
               
               return responseMessage;
           }

    private void machineConnectControl(String requestMessage) {
      //CONNECT 1/machine1/2/20 (id,isim,tip,hız)
      String isim;
      int id,tip,hiz;
      ArrayList<Integer> tmp = new ArrayList();
      
      for(int i=0;i<requestMessage.length();i++) {
          if(requestMessage.charAt(i) == '/') {
              tmp.add(i); // "/" içeren kısımları al.
          }
      }
      
      // "/" işareti içeren tüm indisler bulundu. Bunlar veriler için ayraç görevi görmektedir.
      // arada kalan veriler ile ilgili atamalar yapılıp yeni bir makine objesi oluşturulacak.
      
      
      String strID = requestMessage.substring(1, (int) tmp.get(0));
      isim = requestMessage.substring(tmp.get(0)+1,tmp.get(1));
      String strTip = requestMessage.substring(tmp.get(1)+1,tmp.get(2));
      String strHiz = requestMessage.substring(tmp.get(2)+1,requestMessage.length());
      id = Integer.parseInt(strID);
       tip = Integer.parseInt(strTip);
      hiz = Integer.parseInt(strHiz);
      
      
      //makine eklendi ve bağlandı
      for(int i=0;i<DB.machineList.size();i++) {
          if(DB.machineList.get(i).getMachineID() == id) {
              out.println("201 : Bu id ile makine daha önce oluşturuldu. Lütfen farklı id giriniz.");
              return;
          }
      }
      Machine m = new Machine(id, isim, tip, hiz);
      DB.machineList.add(m); //makine eklendi ve bağlandı. ilk iş için kuyruk kontrol edilecek.
     
      try {
      
      out.println("203 : Ekleme işlemi başarılı.");
      DB.thread_machine.put(id, this); //makine ile thread sözlük ile bağlandı.
      ////////////////////////////////
      //Makine bağlandı. Makine tipine ait görev olup olmadığı kontrol edilmeli, varsa yapılmalı.
      
      for(int i=0;i<DB.jobQueue.size();i++) {
          if(DB.jobQueue.get(i).getJobType() == tip) {//bu makine tipine ait iş kuyrukta varsa
           System.out.println("600 : Gelen iş : "+ DB.jobQueue.get(i).getJobName() + ". Bittikten sonra rapor gönderilecektir.");
           int beklemeSuresi = (DB.jobQueue.get(i).getJobTime() / m.getProduceSpeed()) * 1000; //Makinenin meşgul kalacağı süre, hızına göre hesaplandı. (saniye türünden.)
           m.setStatus(true); //meşgul konuma geçti.
           
              System.out.println("Makine kuyruktan iş aldı. İşleniyor...");
           this.sleep(10000); //bekleme hızına göre beklemeye geçti. (şimdilik 10 sn)
          
           
           DB.jobQueue.remove(DB.jobQueue.get(i)); //iş tamamlandı, listeden silindi.
           System.out.println("650 : Görev Başarılı. Yeni görev bekleniyor...");
           m.setStatus(false);
              
          }
          
      }
      
      
      
      synchronized(this) { //thread i uyandıracak olan başka bir thread'in uyarıcısı ile aynı anahtar kelime kullanılacak.
      while(true) { //makine komut bekleme durumunda. İstemciden talep gelene kadar beklemede duracak.
           if(checkJobQueue(tip) == null) // iş yoksa null döner.
           {
               System.out.println("İlgili thread, yeni iş için beklemede.");
           this.wait();// iş yoksa bekle.
           }
           
           //Thread, bir başka thread tarafından uyandırıldığında, kuyruktan yeni bir iş alır. 
           
           Job j = checkJobQueue(tip);
           System.out.println("600 : Gelen iş : "+ j.getJobName() + ". Bittikten sonra rapor gönderilecektir."); //ilgili makineye iş gönderilldi.
           
           
           int beklemeSuresi = (j.getJobTime() / m.getProduceSpeed()) * 1000; //Makinenin meşgul kalacağı süre, hızına göre hesaplandı. (saniye türünden.)
           m.setStatus(true); //meşgul konuma geçti.
           out.println("141 : " + beklemeSuresi);
           this.sleep(10000); //hızına göre beklemeye geçti. (şimdilik 10 sn)
           
           
           //if(inp.nextLine().equals("535"));
           
           DB.jobQueue.remove(j); //iş tamamlandı, listeden silindi.
           
           System.out.println("535 : Görev Başarılı. Yeni görev bekleniyor...");
           
           m.setStatus(false);
          
               

           //meşgul değil konumuna geçti.
        } 
     }

      } catch(Exception e) {
          out.println("202 : " + e.toString());
          return;
      }

        
    }

    private void takeJobFromPlanner(String takenData) {
        //SENDJOB İD/NAME/TİME/TYPE
        String data = takenData.substring(1,takenData.length());
        ArrayList<Integer> tmpInd = new ArrayList();
        
        for(int i=0;i<data.length();i++) {
            if(data.charAt(i) == '/') {
                tmpInd.add(i);
            }
        }
     
        int id = Integer.parseInt(data.substring(0, tmpInd.get(0)));
        String name = data.substring(tmpInd.get(0)+1,tmpInd.get(1));
        int time = Integer.parseInt(data.substring(tmpInd.get(1)+1, tmpInd.get(2)));
        int type = Integer.parseInt(data.substring(tmpInd.get(2)+1,data.length()));
        
        /*
        System.out.println("id = " + id);
        System.out.println("name = " + name);
        System.out.println("time = " + time);
        System.out.println("type = " + type);
*/
        
        Job job = new Job(name,time,id,type,currentUser); //gönderilecek iş tanımlandı.
        DB.jobQueue.add(job);
        Thread machineThread;
        
        if(DB.machineList.size() == 0) {
            out.println("610 : Uygun makine bulunmamakta. İş kuyrukta beklemededir.");
        }
        
        
         for(int k=0;k<DB.machineList.size();k++) { //makine listesi taranıp ilgili tipe ait makinenin uygunlukları kontrol edilir.
             if(DB.machineList.get(k).getMachineTypeID() == type && !(DB.machineList.get(k).isStatus())) {
               //ilgili tipe ait makineler tarandı.
               //bulunursa ilgili iş o makineye gönderilecek.
                machineThread = DB.thread_machine.get(DB.machineList.get(k).getMachineID());
                out.println("600 : İş makineye gönderildi. Bittikten sonra durum raporu gönderilecektir.");
                synchronized(machineThread) {
                    machineThread.notify(); //thread uyandırıldı.    
                    break; //uygun makinelerden yalnızca birini uyandırmak yeterli.
                }
            }
            else if(!(DB.machineList.get(k).getMachineTypeID() == type && !(DB.machineList.get(k).isStatus()) && k==DB.machineList.size())) {
                out.println("Uygun makine bulunmamakta, iş isteği kuyruktadır.");
            }
         }

        
    while(true) {//gönderilen iş sonuçlanana kadar ilgili thread burada kalmalıdır.
        
        for(int i=0;i<DB.machineList.size();i++) { //makine listesi taranıp ilgili tipe ait makinenin uygunlukları kontrol edilir.
            if(DB.machineList.get(i).getMachineTypeID() == type && !(DB.machineList.get(i).isStatus())) {
               //ilgili tipe ait makineler tarandı.
               //bulunursa ilgili iş o makineye gönderilecek.
                //machineThread = DB.thread_machine.get(DB.machineList.get(i).getMachineID());
                out.println("600 : İş makineye gönderildi. Bittikten sonra durum raporu gönderilecektir.");
                synchronized(DB.thread_machine.get(DB.machineList.get(i).getMachineID())) {
                    
                        DB.thread_machine.get(DB.machineList.get(i).getMachineID()).notify(); //thread uyandırıldı.
                        break; //threadlerden birinin uyandırılması yeterli.
                      
                        
                  
                }
                
            }
        }

        
    }     
       
    }
    
    private Job checkJobQueue(int typeID) { //makine TypeID
        for(int i=0;i<DB.jobQueue.size();i++) {
            if(DB.jobQueue.get(i).getJobType() == typeID) {
                //makinenin ait olduğu tipe bağlı iş var
                return DB.jobQueue.get(i);
            }
        }
        return null;
        
    }
   
   
    
}
