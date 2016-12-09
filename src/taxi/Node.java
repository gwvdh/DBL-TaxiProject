/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;

import java.util.ArrayList;

/**
 *
 * @author s151341
 */
class Node {
    int id;
    boolean[] adjacent;
    int sumDistance;
    int distance;
    int[] nodeDistance;
    Node[] path;
    Taxi priorityTaxi;
    Node parent;
    ArrayList<Taxi> taxiList = new ArrayList<>();
    ArrayList<Customer> customers = new ArrayList<>();

    Node(int id, boolean[] adj, int n){
        this.id = id;
        this.adjacent = adj;
        priorityTaxi = null; // temporary solution
        this.path = new Node[n];
    }
    
    public void addCustomer(Customer customer){
        this.customers.add(customer);
    }
    public boolean hasCustomerDest(int dest){
        for(Customer c: customers){
            if(c.getDest().id==dest){
                return true;
            }
        }
        return false;
    }

    public boolean isAdj(int node){
        return adjacent[node];
    }
    
    public void setNodeDistance(int[] d) {
        nodeDistance = d;
    }

    public int[] getNodeDistance() {
        return nodeDistance;
    }
    
    public void setDistance(int d) {
        distance = d;
    }
    public int getDistance() {
        return distance;
    }

    public void setParent(Node node) {
        parent = node;
    }

    public Node getParent(){
        return parent;
    }

    public void addTaxi(Taxi taxi){
        taxiList.add(taxi);
    }

    public ArrayList<Taxi> getTaxi(){
        return taxiList;//Temp solution
    }

    public boolean hasTaxi(){
        return !taxiList.isEmpty();
    }
    
    public int compareTo(Node node){
        return (this.sumDistance-node.sumDistance);
    }
    
}
