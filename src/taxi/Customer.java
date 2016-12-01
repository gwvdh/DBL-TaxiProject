/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;



public class Customer {
    enum Status {
        WAITING, TRANSIT, ARRIVED
    }
    
    int ID;
    int location;
    int destination;
    int startTime;
    int shortest;
    double alpha;
    Status status;
    
    Taxi targetTaxi;//Assign customer to taxi
    
    Customer(int location, int destination, int startTime, double alpha){
        this.location = location;
        this.destination = destination;
        this.startTime = startTime;
        this.alpha = alpha;
        status = Status.WAITING;
    }
    
    double cost(int a, int c, int shortest){
        double aa = (double)(a);
        double cc = (double)(c);
        double ss = (double)(shortest);
        return Math.pow((aa-cc)/Math.pow(ss+2,alpha), 2.0);
    }
    
    Status getStatus(){
        return status;
    }
    int getLoc(){
        return this.location;
    }
    int getDest(){
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
    double arrived(int time){
        status = Status.ARRIVED;
        return cost(time, startTime, shortest);
    }
    public void setDest(int d){
        this.destination = d;
    }
    public void setTargetTaxi(Taxi taxi){//Assign customer to taxi
        this.targetTaxi = taxi;
    }
    public Taxi getTargetTaxi(){
        return targetTaxi;
    }
}
