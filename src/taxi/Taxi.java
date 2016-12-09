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




public class Taxi {
    TaxiScanner scanner = TaxiScanner.getInstance();
    int ID;
    Node location;
    int capacity; //How many spots are available
    Node destination;
    List<Customer> clients = new ArrayList<>();
    Queue<Node> path = new LinkedList<>();

    Taxi(int cap) {
        capacity = cap;
    }
    
    void greedySalesman(){
        Node[] nodes = new Node[path.size()];
        //System.out.println(this+"| Path1: "+path);
        for(int i=0; i<path.size(); i++){
            nodes[i] = path.poll();
        }
        Node current = location;
        for(int i=0; i<nodes.length; i++){
            int smallest = 0;
            for(int j=1; j<nodes.length; j++){
                //System.out.println(this+" J: "+j+" path.size: "+path.size()+" nodes[].length: "+nodes.length+" I: "+i+" smallest: "+smallest);
                //System.out.println(nodes[smallest]);
                if(nodes[smallest] == null){
                    smallest = j;
                } else if(nodes[j] != null ){
                    //System.out.println(Arrays.toString(nodes[j].nodeDistance));
                    if(nodes[smallest].nodeDistance[current.id] > nodes[j].nodeDistance[current.id]){//Distance may not be initialized
                        smallest = j;
                    }
                }
            }
            if(nodes[smallest] != null){
                path.add(nodes[smallest]);
            }
            current = nodes[smallest];
            nodes[smallest] = null;
        }
        //System.out.println(this+"| Path2: "+path);
    }
    
    int getNum(){
        return this.ID;
    }
    Node getLoc(){
        return this.location;
    }
    int getCap(){
        return this.capacity;
    }
    Node getDest(){
        return destination;
    }
    boolean wantsDrop(){
        for(Customer client: clients){
            if(client.getDest().id==this.getLoc().id){
                return true;
            }
        }
        return false;
    }
    Node getPath(){
        return path.poll();
    }
    boolean isEmpty() {
        return clients.isEmpty();
    }
    void setPath(Node[] p){
        if(path.isEmpty()){
            destination = p[p.length-1];
        }
        path.addAll(Arrays.asList(p));
    }
    void setCap(int c){
        capacity = c;
    }
    boolean isIn(Customer c){
        return clients.contains(c) && c.status == Customer.Status.TRANSIT;
    }
    void addPas(Customer customer) {
        //clients.add(customer);
        this.path.add(customer.getDest());
        //greedySalesman();
        customer.setStatus(Customer.Status.TRANSIT);
        scanner.println("p "+ this.ID+" "+ customer.getDest().id+" ");
    }
    void setLoc(Node l){
        l.addTaxi(this);
        location = l;
        scanner.println("m "+ this.ID+" "+ l.id+" ");
    }
    boolean pasDest(){
        for(int i=0; i<clients.size(); i++){
            if(clients.get(i).getDest().id == this.location.id){
                return true;
            }
        }
        return false;
    }
    void dropPas(int i){
        scanner.println("d "+ this.ID+" "+ this.clients.get(i).getDest().id+" ");
        clients.get(i).setStatus(Customer.Status.ARRIVED);
        clients.remove(clients.get(i));
        //System.out.println(this.ID+" clients: "+clients+" | Path: "+path);
        
      
//        for(int i=0; i<clients.size(); i++){
//            if(clients.get(i).getDest() == this.location.id){
//                scanner.println("d "+ this.ID+" "+ this.location.id+" ");
//                clients.get(i).setStatus(Customer.Status.ARRIVED);
//                clients.remove(i);
//            }
//        }
    }
}
