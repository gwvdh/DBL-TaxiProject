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
    int[] nodeDistance;
    Node[] path;
    ArrayList<Taxi> taxiList = new ArrayList<>();
    ArrayList<Customer> customers = new ArrayList<>();

    Node(int id, boolean[] adj, int n){
        this.id = id;
        this.adjacent = adj;
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

    public boolean isAdj(int node){
        return adjacent[node];
    }
    
    public void setNodeDistance(int[] d) {
        nodeDistance = d;
    }

    public int[] getNodeDistance() {
        return nodeDistance;
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
    
}
