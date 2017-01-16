/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;

import java.util.ArrayList;

class Node {
    boolean grouped = false;
    int id;
    Node[] adjacent;
    int sumDistance;
    int gravity;
    int[] nodeDistance;
    Node[] path;
    ArrayList<Taxi> taxiList = new ArrayList<>();
    ArrayList<Customer> customers = new ArrayList<>();

    Node(int id, int n){
        this.id = id;
        this.path = new Node[n];
    }
    
    public void addCustomer(Customer customer){
        this.customers.add(customer);
    }
    public boolean hasCustomerDest(int dest){
        for(Customer c: customers){
            if(c.getDest().id == dest){
                return true;
            }
        }
        return false;
    }

    public int getId(){
        return this.id;
    }

    public void setAdjacent(Node[] adjacent) {
        this.adjacent = adjacent;
    }

    Node[] getAdjacent(){
        return this.adjacent;
    }
    
    public void setNodeDistance(int[] d) {
        nodeDistance = d;
    }

    public int[] getNodeDistance() {
        return nodeDistance;
    }

    public void setPath(int id, Node next){
        path[id] = next;
    }

    Node[] getPath(){
        return path;
    }

    public void addTaxi(Taxi taxi){
        taxiList.add(taxi);
    }

    public ArrayList<Taxi> getTaxi(){
        return taxiList;//Temp solution
    }

    void clearTaxis(){
        taxiList.clear();
    }

    public boolean hasTaxi(){
        return !taxiList.isEmpty();
    }
    
}
