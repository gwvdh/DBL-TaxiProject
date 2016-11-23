/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.taxi;



public class Customer {
    public enum Status {
        WAITING, TRANSIT, ARRIVED
    }
    Status status = Status.WAITING;
    int ID;
    int location;
    int destination;
    int startTime;


    
    public Status getStatus(){
        return status;
    }
    public int getLoc(){
        return this.location;
    }
    public int getDest(){
        return this.destination;
    }
    public void setLoc(int l){
        this.location = l;
    }
    public void setStatus(Status s){
        status = s;
    }
    public void setDest(int d){
        this.destination = d;
    }
    
}
