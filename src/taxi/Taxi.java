/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import taxi.Customer.Status;




public class Taxi {
    TaxiScanner scanner = TaxiScanner.getInstance();
    int ID;
    int location;
    int capacity; //How many spots are available
    int destination;
    List<Customer> clients = new ArrayList<>();
    Queue<Integer> path = new LinkedList<>();

    Taxi(int cap) {
        capacity = cap;
    }
    
    int getNum(){
        return this.ID;
    }
    int getLoc(){
        return this.location;
    }
    int getCap(){
        return this.capacity;
    }
    int getDest(){
        return destination;
    }
    boolean wantsDrop(){
        for(Customer client: clients){
            if(client.getDest()==this.getLoc()){
                return true;
            }
        }
        return false;
    }
    int getPath(){
        return path.poll();
    }
    boolean isEmpty() {
        return clients.isEmpty();
    }
    void setPath(int[] p){
        if(path.isEmpty()){
            destination = p[p.length-1];
        }
        for(int i=0; i<p.length; i++){
            path.add(p[i]);
        }
    }
    void setCap(int c){
        capacity = c;
    }
    boolean isIn(Customer c){
        return clients.contains(c) && c.status == Status.TRANSIT;
    }
    void addPas(Customer customer) {
        //clients.add(customer);
        customer.setStatus(Customer.Status.TRANSIT);
        scanner.println("p "+ this.ID+" "+ this.location+" ");
    }
    void setLoc(int l){
        location = l;
        scanner.println("m "+ this.ID+" "+ l+" ");
    }
    boolean pasDest(){
        for(int i=0; i<clients.size(); i++){
            if(clients.get(i).getDest() == this.location){
                return true;
            }
        }
        return false;
    }
    void dropPas(){
        for(int i=0; i<clients.size(); i++){
            if(clients.get(i).getDest() == this.location){
                scanner.println("d "+ this.ID+" "+ this.location+" ");
                clients.get(i).setStatus(Customer.Status.ARRIVED);
                clients.remove(i);
            }
        }
    }
}
