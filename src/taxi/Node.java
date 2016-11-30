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
public class Node {
    int id;
    boolean[] adjacent;
    int distance;
    Node parent;
    ArrayList<Taxi> taxiList = new ArrayList<>();

    Node(int id, boolean[] adj){
        this.id = id;
        this.adjacent = adj;
    }
    
    public boolean isAdj(int node){
        return adjacent[node];
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

    public ArrayList<Taxi> getTaxis(){
        return taxiList;
    }

    public boolean hasTaxi(){
        return !taxiList.isEmpty();
    }
}
