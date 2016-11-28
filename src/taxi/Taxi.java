/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;

import java.util.*;

public class Taxi {
    // I made a comment
    TaxiScanner scanner = TaxiScanner.getInstance();
    int ID;
    int location;
    int capacity; //How many spots are available
    List<Customer> clients = new ArrayList<Customer>();
    Queue<Integer> path = new LinkedList<Integer>();

    public Taxi() {
    }
    
    public int getNum(){
        return this.ID;
    }
    public int getLoc(){
        return this.location;
    }
    public int getCap(){
        return this.capacity;
    }
//    public int[] getPath(){
//        return this.path;
//    }
    public int getPath(){
        return path.poll();
    }
    public void setPath(Integer[] p){
        path.addAll(Arrays.asList(p) );
    }
    public void setCap(int c){
        capacity = c;
    }
    public boolean isIn(Customer c){
        return clients.contains(c);
    }
    public void addPas(Customer customer) {
        clients.add(customer);
        customer.setStatus(Customer.Status.TRANSIT);
        scanner.println("p "+ this.ID+" "+ this.location+" ");
    }
    public void setLoc(int l){
        location = l;
        scanner.println("m "+ this.ID+" "+ l+" ");
    }
    public boolean pasDest(){
        for(int i=0; i<clients.size(); i++){
            if(clients.get(i).getDest() == this.location){
                return true;
            }
        }
        return false;
    }
    public void dropPas(){
        for(int i=0; i<clients.size(); i++){
            if(clients.get(i).getDest() == this.location){
                scanner.println("d "+ this.ID+" "+ this.location+" ");
                clients.remove(i); // needs to be rethought
            }
        }
    }
}
