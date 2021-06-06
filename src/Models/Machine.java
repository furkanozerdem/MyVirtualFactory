/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import java.util.ArrayList;

public class Machine {
    
    int machineID;
    String machineName;
    int machineTypeID;
    int produceSpeed;
    boolean status; //isBusy?????
    ArrayList <Job> jobList;
    
    public Machine(int id, String name, int typeID, int speed) {
        this.machineID = id;
        this.machineName = name;
        this.machineTypeID = typeID;
        this.produceSpeed = speed; //birim zaman
        this.status = false; //----(NOT BUSY)------
        jobList = new ArrayList<>();
    }
    
   public void addJobList(Job j) {
       this.jobList.add(j);
   }
   
    public ArrayList<Job> getJobList() {
        return jobList;
    }

    public void setJobList(ArrayList<Job> jobList) {
        this.jobList = jobList;
    }
    

    public int getMachineID() {
        return machineID;
    }

    public void setMachineID(int machineID) {
        this.machineID = machineID;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public int getMachineTypeID() {
        return machineTypeID;
    }

    public void setMachineTypeID(int machineTypeID) {
        this.machineTypeID = machineTypeID;
    }

    public int getProduceSpeed() {
        return produceSpeed;
    }

    public void setProduceSpeed(int produceSpeed) {
        this.produceSpeed = produceSpeed;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    
    
    
    

    
}
