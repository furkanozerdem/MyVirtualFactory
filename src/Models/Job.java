/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

public class Job {

    String jobName;
    Users sender;
    int jobTime; 

    public Job(String jobName, int jobTime, int jobID, int jobType, Users sender) {
        this.sender = sender;
        this.jobName = jobName;
        this.jobTime = jobTime;
        this.jobID = jobID;
        this.jobType = jobType;
    }
    int jobID;
    int jobType;

    public int getJobTime() {
        return jobTime;
    }

    public void setJobTime(int jobTime) {
        this.jobTime = jobTime;
    }

    public int getJobType() {
        return jobType;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }
    


    
}
