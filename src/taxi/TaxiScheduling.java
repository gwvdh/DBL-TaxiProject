/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class TaxiScheduling {

    // input variables
    int l;
    double alpha;
    int m;
    int x;
    int c;
    int n;
    int trainT;
    int totalT;

    // bookkeeping variables
    int time;
    double totalCost;

    // data sets
    Node[] nodes;
    Taxi[] taxis;

    TaxiScanner scanner = TaxiScanner.getInstance();

    Queue<Customer> orderQueue = new LinkedList<>();

    // constructor that fetches the input parameters
    private TaxiScheduling() {
        time = 0;
        totalCost=0;

        l = Integer.parseInt(scanner.nextLine());
        alpha = Double.parseDouble(scanner.nextLine());
        m = Integer.parseInt(scanner.nextLine());
        String[] parts = scanner.nextLine().split(" ");
        x = Integer.parseInt(parts[0]);

        taxis = new Taxi[x]; //Initialize the taxi's

        for(int i=1; i<=x; i++){
            Taxi taxi = new Taxi(c);
            taxi.ID = i;
            taxis[i-1] = taxi;
        }

        c = Integer.parseInt(parts[1]);
        n = Integer.parseInt(scanner.nextLine());

        nodes = new Node[n];

        for(int i=0;i<n;i++){
            String[] adjacent = scanner.nextLine().split(" ");
            int p = Integer.parseInt(adjacent[0]);
            boolean[] adj = new boolean[n]; //Boolean array, because it has increased performance since we will not manipulate the array, just find items
            for(int j=1; j<=p; j++) {
                adj[Integer.parseInt(adjacent[j])] = true;
            }

            Node node = new Node(i,adj);
            nodes[i] = node;
        }

        parts = scanner.nextLine().split(" ");
        trainT = Integer.parseInt(parts[0]);
        totalT = Integer.parseInt(parts[1]);
    }
    
    void printAdMat() { //Method for printing an adjacency matrix.
        for(int j=0; j<=n*2; j++) {
                System.out.printf("-");
            }
            System.out.printf("\n");
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                if(nodes[i].isAdj(j)) {
                    System.out.printf("|x");
                } else {
                    System.out.printf("| ");
                }
            }
            System.out.printf("|\n");
        }
        for(int j=0; j<=n*2; j++) {
            System.out.printf("-");
        }
        System.out.printf("\n");
    }

    void bfsFindAllDist(Node start){
        Queue<Node> nodeQueue = new LinkedList<>();
        int[] distance = new int[n];
        for(int i=0; i<distance.length; i++){
            distance[i] = n*2;
        }
        boolean done = false;
        Node current = start;
        distance[current.id] = 0;
        while(!done){
            if(current.nodeDistance != null){//Add already known distances to the distance array
                for(int j=0;j<current.nodeDistance.length;j++){
                    if(current.getNodeDistance()[j]+current.getNodeDistance()[start.id] < distance[j]){//If it is shorter, add
                        distance[j] = current.getNodeDistance()[j]+current.getNodeDistance()[start.id];
                    }
                }
            }
            for(int i=0; i<n; i++){
                if(current.isAdj(i) && (distance[i]>distance[current.id]+1)){//If there is no already found shorter distance:
                    distance[i] = distance[current.id]+1;//Add the distance.
                    nodeQueue.add(nodes[i]);
                }
            }
            if(nodeQueue.isEmpty()){
                done = true;
            } else {
                current = nodeQueue.poll();
            }
        }
        start.setNodeDistance(distance);
    }
    
    public Integer[] bfsShortestPath(Node start, int goal){//Get shortest path from node start to node goal
        if (start.nodeDistance == null){
            bfsFindAllDist(start);
        }
        int[] distance = start.getNodeDistance();
        Integer[] path = new Integer[distance[goal]];
        int current = goal;
        for(int i=distance[goal]-1; i>=0; i--) { //Walk backwards from the goal to the source to find the shortest path
            //System.out.println(i);
            path[i] = current;
            int smallest = 2*n;
            int index = -1;
            for(int j=0; j<n; j++) {
                if(distance[j]<smallest && nodes[current].isAdj(j)) {
                    smallest = distance[j];
                    index = j;
                }
            }
            current = index;
        }
        return path;
    }

    // Overloaded taxi searching BFS, non-recursive implementation
    Taxi BreadthFirstSearch(Node root) {
        for(Node node : nodes) {
            node.setParent(null);
            node.setDistance(-1);
        }

        Queue<Node> Q = new LinkedList<>();

        root.setDistance(0);
        Q.add(root);

        while(!Q.isEmpty()){
            Node current = Q.poll();
            for(int i = 0; i < current.adjacent.length; i++) {
                if(current.isAdj(i) && current.getDistance() == -1){
                    nodes[i].setDistance(current.getDistance()+1);
                    nodes[i].setParent(current);
                    Q.add(nodes[i]);
                    if(nodes[i].hasTaxi())
                        return nodes[i].getTaxi();
                }
            }
        }
        return null;
    }
    
    void getOrders(String s) { //Get the client information from input.
        int p = Integer.parseInt(s.split(" ")[0]); //Get the first element indicating the amount of orders
        for(int i = 0; i < p ;i++){
            int loc = Integer.parseInt(s.split(" ")[i*2+1]);
            int dest = Integer.parseInt(s.split(" ")[i*2+2]);
            Customer c = new Customer(loc,dest,time,alpha);
            orderQueue.add(c); //Add the customer to the queue
        }
    }
    
    void directWalk(Taxi t, Customer c) { //Direct walk algorithm which goes to the first customer in queue and brings her to her destination
        if(t.getLoc() == c.getDest() && t.isIn(c)){ //If the taxi is at the destination of the customer and the customer is in the taxi
            totalCost += c.arrived(time);
            t.dropPas();
            return;

        } else if(t.getLoc() != c.getLoc() && !t.isIn(c)) {
            if(t.path.isEmpty()){
                t.setPath(bfsShortestPath(nodes[t.getLoc()], c.getLoc()));
            }
            t.setLoc(t.getPath());

        } else if(t.getLoc() == c.getLoc() && !t.isIn(c)){
            //System.out.println("C");
            t.addPas(c);
            Integer[] path = bfsShortestPath(nodes[t.getLoc()], c.getDest());
            t.setPath(path);
            c.setShortest(path.length);

        } else if(t.getLoc() != c.getDest() && t.isIn(c)){
            if(t.path.isEmpty()){
                t.setPath(bfsShortestPath(nodes[t.getLoc()], c.getDest()));
            }
            t.setLoc(t.getPath());
        }
        return;
    }
    
    void setInitialPos(){
        for(Taxi taxi:taxis){
            taxi.setLoc((int) (Math.random()*n));
            //System.out.println("Taxi "+taxi.getNum()+" to pos: "+taxi.getLoc());
        }
        scanner.println("c");
    }
    
    private void run(){
        boolean done=false;
        setInitialPos();
        
        while(!done){
            if(scanner.hasNextLine()){
                getOrders(scanner.nextLine());
            }

            for (Taxi taxi : taxis) {
                if (taxi.isEmpty() && !orderQueue.isEmpty()) {
                    taxi.clients.add(orderQueue.poll());
                    directWalk(taxi, taxi.clients.get(0));
                } else if (!taxi.isEmpty()) {
                    directWalk(taxi, taxi.clients.get(0));
                }
            }
            scanner.println("c");

            time++;
            System.out.println(time);
            System.out.println(totalCost);

            boolean empty = true;
            for (Taxi taxi : taxis) {
                empty &= taxi.isEmpty();
            }
            if(!scanner.hasNextLine() && orderQueue.isEmpty() && empty){
                done=true;
            }
        }
    }
    public static void main(String[] args) {
        (new TaxiScheduling()).run();
    }
    
}
