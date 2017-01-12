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
    int n;
    int ID;
    Node location;
    Node baseLoc;
    int capacity; //How many spots are available
    Node destination;
    List<Customer> clients = new ArrayList<>();
    LinkedList<Node> path = new LinkedList<>();

    Taxi(int cap, int nodes) {
        capacity = cap;
        n = nodes;
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

    void greedyInsertSalesman(Node loc, Node dest){
        //System.out.println("Before path "+this.ID+": "+this.getPath());
        int counter = this.path.size()-1;
        if(counter == -1){//If nothing is in the path, just add the location and destination
            this.path.add(loc);
            this.path.add(dest);
        } else{
            int distance = 2*n;
            //Look for shortest distance from current nodes in the path
            for(int i=counter; i>0; i--){//Destination
                if(distance>dest.getNodeDistance()[this.getPath().get(i).getId()]){
                    distance = dest.getNodeDistance()[this.getPath().get(i).getId()];
                    counter = i;
                }
            }
            if(counter == 0){
                //Check best arrangement for the three nodes (found node, node before found node (current location of the taxi) and new node)
                //If currentLoc-Node -> new-Node -> found-Node is the shortest:
                if(this.location.getNodeDistance()[dest.getId()]+dest.getNodeDistance()[this.getPath().get(counter).id] < this.location.getNodeDistance()[this.getPath().get(counter).id]+dest.getNodeDistance()[this.getPath().get(counter).id]){
                    this.path.add(counter, dest);
                } else {
                    this.path.add(counter+1, dest);
                }
            //Check best arrangement for the three nodes (found node, node before found node (base) and new node)
            //If base-Node -> new-Node -> found-Node is the shortest:
            } else if(this.getPath().get(counter-1).getNodeDistance()[dest.getId()]+dest.getNodeDistance()[this.getPath().get(counter).id] < this.getPath().get(counter-1).getNodeDistance()[this.getPath().get(counter).id]+dest.getNodeDistance()[this.getPath().get(counter).id]){
                this.path.add(counter, dest);
            } else {//else...
                this.path.add(counter+1, dest);
            }
//------------------------------------------------------------------------------            
            if(!(loc.equals(this.getLoc()))){//Location
                int counter2 = counter;
                //Look for shortest distance from current nodes in the path
                for(int i=counter; i>0; i--){
                    if(distance>loc.getNodeDistance()[this.getPath().get(i).getId()]){
                        distance = loc.getNodeDistance()[this.getPath().get(i).getId()];
                        counter2 = i;
                    }
                }
                if(counter == counter2){
                    this.path.add(counter2,loc);
                } else if(counter == 0){
                    //Check best arrangement for the three nodes (found node, node before found node (current location of the taxi) and new node)
                    //If currentLoc-Node -> new-Node -> found-Node is the shortest:
                    if(this.location.getNodeDistance()[loc.getId()]+loc.getNodeDistance()[this.getPath().get(counter2).id] < this.location.getNodeDistance()[this.getPath().get(counter2).id]+loc.getNodeDistance()[this.getPath().get(counter2).id]){
                        this.path.add(counter2, loc);
                    } else {
                        this.path.add(counter2+1, loc);
                    }
                //Check best arrangement for the three nodes (found node, node before found node (base) and new node)
                //If base-Node -> new-Node -> found-Node is the shortest:
                } else if(this.getPath().get(counter2-1).getNodeDistance()[loc.getId()]+loc.getNodeDistance()[this.getPath().get(counter2).id] < this.getPath().get(counter2-1).getNodeDistance()[this.getPath().get(counter2).id]+loc.getNodeDistance()[this.getPath().get(counter2).id]){
                    this.path.add(counter2, loc);
                } else {
                    this.path.add(counter2+1, loc);
                }
            }
        }
        //System.out.println("After path "+this.ID+": "+this.getPath());
        
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
    void setBase(Node n){
        baseLoc = n;
    }
    Node getBase(){
        return this.baseLoc;
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
        if(!this.path.contains(customer.getDest())){
            this.path.add(customer.getDest());
        }
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
