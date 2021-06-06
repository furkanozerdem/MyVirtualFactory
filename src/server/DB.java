
package server;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import Models.Job;
import Models.Machine;
import Models.MachineType;
import Models.Users;
import sun.security.provider.NativePRNG;

public class DB {
    
    public static ArrayList<Machine> machineList = new ArrayList();
    public static ArrayList<Users> userList = new ArrayList();;
    public static ArrayList <MachineType> machineTypeList = new ArrayList();
    public static ArrayList<Job> jobQueue = new ArrayList<Job>(); //makineler meşgulken işlerin tutulacağı kuyruk.
    
   

    
    public static Hashtable<Thread,Users> thread_user = new Hashtable<>();//hangi user hangi thread'e bağlı. logout işlemi ve kullanıcıların oturumlarının takip edilmesi için gerekli.
    public static Hashtable<Integer, Thread> thread_machine = new Hashtable<>(); //hangi makine id si hangi thread e bağlı. (görev geldiğinde bu yapı üzerinden thread e erişilecek.)
    
          
}
