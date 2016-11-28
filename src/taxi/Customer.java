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
    // do we even use ID?
    //int ID;
    int location;
    int destination;

    int startTime;
    int shortest;
    int endTime;

    Status status;

    Customer(int location, int destination, int startTime){
        this.location = location;
        this.destination = destination;
        this.startTime = startTime;
        status = Status.WAITING;
    }
    
    public Status getStatus(){
        return status;
    }
    public int getLoc(){
        return this.location;
    }
    public int getDest(){
        return this.destination;
    }
    public void setShortest(int shortest){
        this.shortest = shortest;
    }
    public void setLoc(int l){
        this.location = l;
    }
    public void setStatus(Status s){
        status = s;
    }
    public void arrived(int time){
        status = Status.ARRIVED;
        TaxiScheduling.addCost(TaxiScheduling.cost(time, startTime, shortest));
    }
    public void setDest(int d){
        this.destination = d;
    }
    
}
