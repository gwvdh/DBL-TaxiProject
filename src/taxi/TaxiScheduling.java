/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import taxi.Customer.Status;

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
        c = Integer.parseInt(parts[1]);
        for(int i=1; i<=x; i++){
            Taxi taxi = new Taxi(c);
            taxi.ID = i;
            taxis[i-1] = taxi;
        }

        
        n = Integer.parseInt(scanner.nextLine());

        nodes = new Node[n];

        for(int i=0;i<n;i++){
            String[] adjacent = scanner.nextLine().split(" ");
            int p = Integer.parseInt(adjacent[0]);
            boolean[] adj = new boolean[n]; //Boolean array, because it has increased performance since we will not manipulate the array, just find items
            for(int j=1; j<=p; j++) {
                adj[Integer.parseInt(adjacent[j])] = true;
            }

            Node node = new Node(i,adj,n);
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
    
    public Node[] bfsShortestPath(Node start, Node goal){//Get shortest path from node start to node goal
        if (start.nodeDistance == null){
            bfsFindAllDist(start);
        }
        int[] distance = start.getNodeDistance();
        Node[] path = new Node[distance[goal.id]];
        Node current = goal;
        for(int i=distance[goal.id]-1; i>=0; i--) { //Walk backwards from the goal to the source to find the shortest path
            //System.out.println(i);
            path[i] = current;
            int smallest = 2*n;
            int index = -1;
            for(int j=0; j<n; j++) {
                if(distance[j]<smallest && current.isAdj(j)) {
                    smallest = distance[j];
                    index = j;
                }
            }
            current = nodes[index];
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
                if(current.isAdj(i) && nodes[i].getDistance() == -1){
                    nodes[i].setDistance(current.getDistance()+1);
                    nodes[i].setParent(current);
                    Q.add(nodes[i]);
                    
                    if(nodes[i].hasTaxi()){
                        ArrayList<Taxi> taxiList = nodes[i].getTaxi();
                        for(Taxi taxi :taxiList){
                            //System.out.println("clients.size(): "+taxi.clients.size()+" | capacity: "+taxi.getCap());
                            if(taxi.clients.size()<taxi.getCap()){
                                return taxi;
                            }
                        }
                    }
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
            Customer c = new Customer(loc,nodes[dest],time,alpha);
            orderQueue.add(c); //Add the customer to the queue
        }
    }
    
    void directWalk(Taxi t, Customer c) { //Direct walk algorithm which goes to the first customer in queue and brings her to her destination
        if(t.getLoc().id == c.getDest().id && t.isIn(c)){ //If the taxi is at the destination of the customer and the customer is in the taxi
            totalCost += c.arrived(time);
            //t.dropPas();
            return;

        } else if(t.getLoc().id != c.getLoc() && !t.isIn(c)) {
            if(t.path.isEmpty()){
                t.setPath(bfsShortestPath(t.getLoc(), nodes[c.getLoc()]));
            }
            t.setLoc(t.getPath());

        } else if(t.getLoc().id == c.getLoc() && !t.isIn(c)){
            //System.out.println("C");
            t.addPas(c);
            Node[] path = bfsShortestPath(t.getLoc(), c.getDest());
            t.setPath(path);
            c.setShortest(path.length);

        } else if(t.getLoc().id != c.getDest().id && t.isIn(c)){
            if(t.path.isEmpty()){
                t.setPath(bfsShortestPath(t.getLoc(), c.getDest()));
            }
            t.setLoc(t.getPath());
        }
        return;
    }
    
    void calculateAllDistances(){
        for(Node node : nodes){
            bfsFindAllDist(node);
        }
    }
    
    void greedySalesmanWalk(){
        calculateAllDistances();
        int orderQueueSize = orderQueue.size();
        for(int i=0; i<orderQueueSize; i++){
            //System.out.println("orderQueueSize: "+orderQueueSize+" | i: "+i+" | orderQueue: "+orderQueue.size());
            Customer customer = orderQueue.poll();
            //System.out.println("Customer: "+customer+" | node: "+nodes[customer.getLoc()]);
            Taxi currentTaxi = BreadthFirstSearch(nodes[customer.getLoc()]);
            if(currentTaxi != null){
                currentTaxi.clients.add(customer);
                currentTaxi.path.add(nodes[customer.getLoc()]);
                currentTaxi.greedySalesman();
            } else {
                orderQueue.add(customer);
            }
            //System.out.println(currentTaxi.path);
        } 
        for(Taxi taxi: taxis){
            if(taxi.path.peek() == taxi.getLoc()){
                
                for(int i=0; i<taxi.clients.size(); i++){
                    if(taxi.clients.get(i).getDest().id == taxi.location.id && taxi.clients.get(i).getStatus().equals(Status.TRANSIT)){
                        totalCost += taxi.clients.get(i).arrived(time);
                        taxi.dropPas(i);
                        i--;
                    }
                }
                for(Customer customer: taxi.clients){
                    if(customer.getStatus().equals(Status.WAITING) && customer.getLoc() == taxi.getLoc().id){
                        taxi.addPas(customer);
                    }
                }
                taxi.path.poll();
            } else if(!taxi.path.isEmpty()){
                int dest = taxi.path.peek().id;
                for(int i=0; i<n; i++){
                    //System.out.println("Nodes.length: "+nodes.length+"i: "+i);
                    if(taxi.getLoc().isAdj(i) && taxi.getLoc().getNodeDistance()[dest]>nodes[i].getNodeDistance()[dest]){
                        //System.out.println(taxi.path.peek().id);
                        taxi.setLoc(nodes[i]);
                        break;
                    }
                }
            }
        }
    }
    
    void setInitialPos(){
        for(Taxi taxi:taxis){
            taxi.setLoc(nodes[(int) (Math.random()*n)]);
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
            greedySalesmanWalk();
//            for (Taxi taxi : taxis) {
//                if (taxi.isEmpty() && !orderQueue.isEmpty()) {
//                    taxi.clients.add(orderQueue.poll());
//                    directWalk(taxi, taxi.clients.get(0));
//                } else if (!taxi.isEmpty()) {
//                    directWalk(taxi, taxi.clients.get(0));
//                }
//            }
            scanner.println("c");

            time++;
            System.out.println(time);
            System.out.println(totalCost);

            boolean empty = true;
            for (Taxi taxi : taxis) {
                empty &= taxi.isEmpty();
            }
            //System.out.println("NextLine: "+scanner.hasNextLine()+" | orderQueue: "+orderQueue.isEmpty()+" | empty: "+empty);
            if(!scanner.hasNextLine() && orderQueue.isEmpty() && empty || time>200){
                done=true;
            }
        }
    }
    public static void main(String[] args) {
        (new TaxiScheduling()).run();
    }
    
}
