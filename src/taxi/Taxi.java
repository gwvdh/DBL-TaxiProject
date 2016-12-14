/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;




public class Taxi {
    TaxiScanner scanner = TaxiScanner.getInstance();
    int ID;
    Node location;
    int capacity; //How many spots are available
    Node destination;
    List<Customer> clients = new ArrayList<>();
    LinkedList<Node> path = new LinkedList<>();

    Taxi(int cap) {
        capacity = cap;
    }
    
    void greedySalesman(){//Order the path such that from each node to the next, it is the shortest distance
        Node[] nodes = new Node[path.size()];
        for(int i=0; i<path.size(); i++){//Get all nodes from the path
            nodes[i] = path.poll();
        }
        Node current = location;
        for(int i=0; i<nodes.length; i++){//Reorder nodes
            int smallest = 0;
            for(int j=1; j<nodes.length; j++){
                if(nodes[smallest] == null){
                    smallest = j;
                } else if(nodes[j] != null ){
                    if(nodes[smallest].nodeDistance[current.id] > nodes[j].nodeDistance[current.id]){
                        smallest = j;
                    }
                }
            }
            if(nodes[smallest] != null){
                path.add(nodes[smallest]);//Add the node to the queue
            }
            current = nodes[smallest];
            nodes[smallest] = null;//Make sure we won't find the same node twice
        }
    }



    int getId(){
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
    LinkedList<Node> getPath(){
        return this.path;
    }

    void clearAll(){
        clients.clear();
        path.clear();
    }

    boolean isEmpty() {
        return clients.isEmpty();
    }

    void setPath(LinkedList<Node> path){
        this.path = path;
    }
    void setCap(int c){
        capacity = c;
    }

    boolean isIn(Customer c){
        return clients.contains(c) && c.status == Customer.Status.TRANSIT;
    }

    List<Customer> getClients() {
        return this.clients;
    }

    void addPas(Customer customer) {
        
        this.path.add(customer.getDest());
        customer.setStatus(Customer.Status.TRANSIT);
        scanner.println("p "+ this.ID + " "+ customer.getDest().getId()+" ");
    }
    void setLoc(Node l){
        l.addTaxi(this);
        location = l;
        scanner.println("m "+ this.ID + " " + l.id + " ");
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
