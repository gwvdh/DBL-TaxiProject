/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;



public class Customer {
    public enum Status {
        WAITING, TRANSIT, ARRIVED
    }
    
    int ID;
    int location;
    Node destination;
    int startTime;
    int shortest;
    double alpha;
    Status status;
    
    Taxi targetTaxi;//Assign customer to taxi
    
    Customer(int location, Node destination, int startTime, double alpha){
        this.location = location;
        this.destination = destination;
        this.startTime = startTime;
        this.alpha = alpha;
        status = Status.WAITING;
    }
    
    Status getStatus(){
        return status;
    }
    int getLoc(){
        return this.location;
    }
    Node getDest(){
        return this.destination;
    }
    void setShortest(int shortest){
        this.shortest=shortest;
    }
    void setLoc(int l){
        this.location = l;
    }
    void setStatus(Status s){
        status = s;
    }
    public void setDest(Node d){
        this.destination = d;
    }
    public void setTargetTaxi(Taxi taxi){//Assign customer to taxi
        this.targetTaxi = taxi;
    }
    public Taxi getTargetTaxi(){
        return targetTaxi;
    }
}
