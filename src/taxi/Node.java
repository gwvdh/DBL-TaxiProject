/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author s151341
 */
public class Node {
    int id;
    boolean[] adjacent;
    Integer[] distance;
    List<Taxi> taxiInbound = new ArrayList<>();
    List<Customer> customers = new ArrayList<>();
    
    Node(int id, boolean[] adj){
        this.id = id;
        this.adjacent = adj;
    }
    
    public void addCustomer(Customer customer){
        this.customers.add(customer);
    }
    public boolean hasCustomerDest(int dest){
        for(Customer c: customers){
            if(c.getDest()==dest){
                return true;
            }
        }
        return false;
    }
    public boolean isInbound(){
        return !taxiInbound.isEmpty();
    }
    public Taxi getInbount(){//Get first inbound
        return taxiInbound.get(0);
    }
    public boolean isAdj(int node){
        return adjacent[node];
    }
    public void setDistance(Integer[] d){
        this.distance = d;
    }
    public Integer[] getDistance(){
        return distance;
    }
}
