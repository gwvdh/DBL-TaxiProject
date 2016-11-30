/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;

/**
 *
 * @author s151341
 */
public class Node {
    int id;
    boolean[] adjacent;
    Integer[] distance;
    
    Node(int id, boolean[] adj){
        this.id = id;
        this.adjacent = adj;
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
