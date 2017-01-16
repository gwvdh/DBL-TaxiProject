/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxi;

import java.util.*;


public final class TaxiScheduling {
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
    int diameter;
    LinkedList<Node> initialPosQ = new LinkedList<>();

    // data sets
    Node[] nodes;
    Taxi[] taxis;
    
    List<Node> avDistNodes = new ArrayList<>();

    TaxiScanner scanner = TaxiScanner.getInstance();

    Queue<Customer> orderQueue = new LinkedList<>();

    // constructor that fetches the input parameters
    private TaxiScheduling() {
        time = 1;
        totalCost=0;

        l = Integer.parseInt(scanner.nextLine());
        alpha = Double.parseDouble(scanner.nextLine());
        
        System.out.println(alpha);
        
        m = Integer.parseInt(scanner.nextLine());
        String[] parts = scanner.nextLine().split(" ");
        x = Integer.parseInt(parts[0]);
        
        taxis = new Taxi[x]; //Initialize the taxi's
        c = Integer.parseInt(parts[1]);
        for(int i = 0 ; i < x; i++){
            Taxi taxi = new Taxi(c,n);
            taxi.ID = i + 1;
            taxis[i] = taxi;
        }

        
        n = Integer.parseInt(scanner.nextLine());
        nodes = new Node[n];

        for(int i=0;i<n;i++) {
            Node node = new Node(i, n);
            nodes[i] = node;
            avDistNodes.add(node);
        }

        for(Node node : nodes){
            String[] adjacent = scanner.nextLine().split(" ");
            int d_i = Integer.parseInt(adjacent[0]);
            Node[] adj = new Node[d_i];
            for(int i = 0; i < d_i; i++) {
                adj[i] = nodes[Integer.parseInt(adjacent[i+1])];
            }
            node.setAdjacent(adj);
        } 

        parts = scanner.nextLine().split(" ");
        trainT = Integer.parseInt(parts[0]);
        totalT = Integer.parseInt(parts[1]);
        
        calculateAllDistances();
        setInitialPos();
    }
    
    /*void printAdMat() { //Method for printing an adjacency matrix.
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
    }*/
    
    void bfsFindAllDist(Node start){
        Queue<Node> nodeQueue = new LinkedList<>();
        int[] distance = new int[n];
        for(int i=0; i<distance.length; i++){//Set all node distances to infinity (or -1 also does the trick)
            distance[i] = n*2;
        }

        boolean done = false;
        Node current = start;
        distance[current.getId()] = 0;
        while(!done){
            if(current.nodeDistance != null){//Add already known distances to the distance array
                for(int j=0;j<current.nodeDistance.length;j++){
                    if(current.getNodeDistance()[j]+current.getNodeDistance()[start.getId()] < distance[j]){//If it is shorter, add
                        distance[j] = current.getNodeDistance()[j]+current.getNodeDistance()[start.getId()];
                    }
                }
            }

            Node[] adj = current.getAdjacent();
            for(Node node : adj){
                if(distance[node.getId()] > distance[current.getId()]+1){
                    distance[node.getId()] = distance[current.getId()]+1;
                    nodeQueue.add(node);
                }
            }

            if(nodeQueue.isEmpty()){
                done = true;
            } else {
                current = nodeQueue.poll();
            }
        }
        start.setNodeDistance(distance);//Put the distances in the object
    }
    
    void getOrders(String s) { //Get the client information from input.
        String[] parts = s.split(" ");
        int p = Integer.parseInt(parts[0]); //Get the first element indicating the amount of orders
        if(parts.length%2 != 0){
            for(int i = 0; i < p ;i++){
                int loc = Integer.parseInt(parts[i*2+1]);
                int dest = Integer.parseInt(parts[i*2+2]);
                Customer customer = new Customer(loc,nodes[dest],time,alpha);
                orderQueue.add(customer); //Add the customer to the queue
            }
        }
    }
    
    /*void directWalk(Taxi t, Customer c) { //Direct walk algorithm which goes to the first customer in queue and brings her to her destination
        if(t.getLoc().getId() == c.getDest().getId() && t.isIn(c)){ //If the taxi is at the destination of the customer and the customer is in the taxi
            totalCost += c.arrived(time);

        } else if(t.getLoc().getId() != c.getLoc() && !t.isIn(c)) {
            if(t.path.isEmpty()){
                t.setPath(bfsShortestPath(t.getLoc(), nodes[c.getLoc()]));
            }
            t.setLoc(t.getPath());

        } else if(t.getLoc().getId() == c.getLoc() && !t.isIn(c)){
            t.addPas(c);
            Node[] path = bfsShortestPath(t.getLoc(), c.getDest());
            t.setPath(path);
            c.setShortest(path.length);

        } else if(t.getLoc().getId() != c.getDest().getId() && t.isIn(c)){
            if(t.path.isEmpty()){
                t.setPath(bfsShortestPath(t.getLoc(), c.getDest()));
            }
            t.setLoc(t.getPath());
        }
    }*/
    
    void calculateAllDistances(){//Calculate all distances from all nodes
        for(Node node : nodes){
            int sum=0;
            bfsFindAllDist(node);
            for(int i : node.getNodeDistance()){
                sum += i;
                if(i>diameter){
                    diameter = i;
                }
            }
            node.sumDistance = sum;
        }
        Collections.sort(avDistNodes, (Node o1, Node o2) -> (o1.sumDistance-o2.sumDistance));//make a priority array for center nodes
//        System.out.printf("[");
//        for(Node node:avDistNodes){
//            System.out.printf("%d, ", node.sumDistance);
//        }
//        System.out.printf("]\n");
    }
    
    double getEstCost(Customer cust, Taxi taxi){
        int pathCost = 0;
        int distance = taxi.getLoc().getNodeDistance()[cust.getLoc()];
        for(Node node: taxi.getPath()){
            if(node.getNodeDistance()[cust.getLoc()]<distance){
                distance = node.getNodeDistance()[cust.getLoc()];
            }
        }
        pathCost += distance;
        pathCost += cust.getDest().getNodeDistance()[cust.getLoc()];
        distance = taxi.getLoc().getNodeDistance()[cust.getDest().id];
        for(Node node: taxi.getPath()){
            if(node.getNodeDistance()[cust.getDest().getId()]<distance){
                distance = node.getNodeDistance()[cust.getDest().getId()];
            }
        }
        pathCost += distance;
        //System.out.println(alpha);
        if(alpha>0.5){
            return (taxi.getLoc().getNodeDistance()[cust.getLoc()]+cust.getDest().getNodeDistance()[cust.getLoc()]+taxi.path.peek().getNodeDistance()[cust.getDest().id])*Math.max(taxi.clients.size(),1);
        } else{
            double tempCost = (pathCost*Math.max(taxi.clients.size(),1))/(Math.pow((pathCost+2)*Math.max(taxi.clients.size(),1), alpha));
            return tempCost*tempCost;//      pathCost*Math.max(taxi.clients.size(),1);
        }
    }
    
    boolean getFairCost(Customer c, Taxi taxi){
        int eXpathCost = 0;
        int distance = taxi.getLoc().getNodeDistance()[c.getLoc()];
        for(Node node: taxi.getPath()){
            if(node.getNodeDistance()[c.getLoc()]<distance){
                distance = node.getNodeDistance()[c.getLoc()];
            }
        }
        eXpathCost += distance;
        eXpathCost += c.getDest().getNodeDistance()[c.getLoc()];
        distance = taxi.getLoc().getNodeDistance()[c.getDest().id];
        for(Node node: taxi.getPath()){
            if(node.getNodeDistance()[c.getDest().getId()]<distance){
                distance = node.getNodeDistance()[c.getDest().getId()];
            }
        }
        eXpathCost += distance;
        double taxiCost=0;
        for(Customer taxiC : taxi.getClients()){
            double value = (time-taxiC.startTime+eXpathCost);
            taxiCost += value*value;
        }
        double customerCost=0;
        customerCost+=time-c.startTime+taxi.getPath().get(taxi.getPath().size()-1).getNodeDistance()[c.getLoc()]+c.getDest().getNodeDistance()[c.getLoc()];
        Node currNode = taxi.getLoc();
        for(Node node : taxi.getPath()){
            customerCost += currNode.getNodeDistance()[node.id];
            currNode = node;
        }
        //System.out.println("Taxi"+taxi.ID+" ("+taxi.getClients().size()+"): "+taxi.getLoc().id);
        //System.out.println("TaxiCost: "+taxiCost+" | CustomerCost: "+(customerCost*customerCost));
        return Math.pow(taxiCost, alpha)<=Math.pow(customerCost*customerCost,alpha);
    }

    void assignTaxi(Customer c){
        Taxi closest = taxis[0];
        double estCost = 2000*n;
        boolean full = true;
        for(Taxi taxi : taxis){ //Get the nearest taxi
            //System.out.println(taxi+" "+taxi.getCap()+": "+taxi.getClients());
            //if(taxi.getLoc().getNodeDistance()[c.getLoc()] < closest.getLoc().getNodeDistance()[c.getLoc()])
            if(taxi.getClients().size() < taxi.getCap()){
                if(taxi.clients.isEmpty() || taxi.path.isEmpty()){
                    if(alpha>0.5){
                        if(taxi.getLoc().nodeDistance[c.getLoc()] < estCost){
                            closest = taxi;
                            estCost = taxi.getLoc().nodeDistance[c.getLoc()];
                        }
                    }else{
                        double tempCost = (taxi.getLoc().nodeDistance[c.getLoc()]*Math.max(taxi.clients.size(),1))/(Math.pow((taxi.getLoc().nodeDistance[c.getLoc()]+2)*Math.max(taxi.clients.size(),1), alpha));
                        if(tempCost*tempCost<estCost){//taxi.getLoc().nodeDistance[c.getLoc()] < estCost){
                            closest = taxi;
                            estCost = tempCost*tempCost;
                        }
                    }
                    full = false;
                }else if(getEstCost(c,taxi) <estCost){
                    if(getFairCost(c,taxi)){
                        closest = taxi;
                        estCost = getEstCost(c,taxi);
                        full = false;
                    }
                }
            } 
        }
//            if(taxi.getClients().size() < taxi.getCap()){
//                if(taxi.clients.isEmpty()){
//                    if(taxi.getLoc().nodeDistance[c.getLoc()] < estCost){
//                        closest = taxi;
//                        estCost = taxi.getLoc().nodeDistance[c.getLoc()];
//                    }
//                }else if((taxi.getLoc().nodeDistance[c.getLoc()]+c.getDest().getNodeDistance()[c.getLoc()]+taxi.path.peek().nodeDistance[c.getDest().id])*Math.max(taxi.clients.size(),1) <estCost){
//                    closest = taxi;
//                    estCost = (taxi.getLoc().nodeDistance[c.getLoc()]+c.getDest().getNodeDistance()[c.getLoc()]+taxi.path.peek().nodeDistance[c.getDest().id])*Math.max(taxi.clients.size(),1);
//                }
//                full = false;
//            } 
//        }
        if((closest.getClients().size() < closest.getCap()) && !full){
            if(closest.clients.isEmpty() && !closest.path.isEmpty())//If the taxi was walking without having scheduled customers
                closest.path.clear();//Remove the current walking goal
            //System.out.println("Chosen taxi: "+closest);
            closest.clients.add(c);//Add the customer to the taxi
            closest.greedyInsertSalesman(nodes[c.getLoc()], c.getDest());//InserSalesman of the nodes.
        } else {
            orderQueue.add(c);
        }
    }
    
    void setInitialPos3(){//Inefficient
        Node currentNode = avDistNodes.get(0);
        int dia = diameter/2;
        int total = 0;
        for(Node node : avDistNodes){
            total += node.sumDistance;
        }
        int avAvDistNodes=total/avDistNodes.size();
        total = 0;
        int n_nodes=0;
        for(int i=0; i<n; i++){
            if(currentNode.getNodeDistance()[i]==(dia/2)){
                total += currentNode.sumDistance;
                n_nodes++;
            }
        } 
        double multiplier = (double) avAvDistNodes/((double)total/n_nodes);
        int radius = 0;
        for(int i=0; i<currentNode.getNodeDistance().length; i++){
            if(currentNode.getNodeDistance()[i]>radius){
                radius = currentNode.getNodeDistance()[i];
            }
        }
        
        //System.out.println(avAvDistNodes+"/("+total+"/"+n_nodes+")="+multiplier);
        dia = (int) ((radius)*multiplier);
        Node secondNode = null;
        initialPosQ.add(currentNode);
        int counter=0;
        while(initialPosQ.size()<x){
//            total = 0;
//            for(Node node : avDistNodes){
//                total += node.sumDistance;
//            }
//            avAvDistNodes=total/avDistNodes.size();
//            total = 0;
//            n_nodes=0;
//            for(int i=0; i<n; i++){
//                if(currentNode.getNodeDistance()[i]==(dia/2)){
//                    total += currentNode.sumDistance;
//                    n_nodes++;
//                }
//            } 
//            multiplier = (double) avAvDistNodes/((double)total/n_nodes);
            //System.out.println("Actual Diameter: "+radius+" | Dia: "+dia);
            for(int i=0; i<n; i++){
                //System.out.println(((dia/(2))*multiplier));
                if(secondNode == null && currentNode.getNodeDistance()[i]==(int)((dia/(2))*multiplier)){
                    System.out.println(secondNode);
                    secondNode = nodes[i];
                    initialPosQ.add(nodes[i]);
                } else if(currentNode.getNodeDistance()[i]==(int)((dia/(2))*multiplier) && secondNode.getNodeDistance()[i]>(int)((dia/(4))*multiplier)){
                    secondNode = nodes[i];
                    initialPosQ.add(nodes[i]);
                }
            }
            
//            for(int i=0; i<n; i++){
//                if(currentNode.getNodeDistance()[i]==(dia/2)){
//                    initialPosQ.add(nodes[i]);
//                }
//            }
            //System.out.println(dia);
            //System.out.println(initialPosQ);
            dia = (int) ((dia/(2))*multiplier);
            currentNode = initialPosQ.get(counter);
            counter++;
        }  
        counter = 0;
        for(Taxi taxi : taxis){
            taxi.setLoc(initialPosQ.get(counter));
            taxi.setBase(initialPosQ.get(counter));
            counter++;
        }
//        for(Taxi taxi : taxis){
//            for(Node node : avDistNodes){
//                if(!node.hasTaxi()){
//                    taxi.setLoc(node);
//                    break;
//                }
//            }
//        }
        scanner.println("c");
        
    }
    
    void setInitialPos2(){//Newtons universal gravity law (M_i*M_j/(D^2_ij))
        List<Node> newDistNodes = avDistNodes;
        Node currentNode;
        System.out.println("Middle Node: "+avDistNodes.get(0).id);
        while(initialPosQ.size()<x){
            currentNode = newDistNodes.get(0);
            initialPosQ.add(currentNode);
            for(int i=0; i<n; i++){
                if((newDistNodes.get(i).getNodeDistance()[currentNode.getId()]) != 0){
                    if(currentNode.sumDistance == 0){
                        currentNode.sumDistance = 1;
                    }
                    int d = (newDistNodes.get(i).getNodeDistance()[currentNode.getId()]);
                    //System.out.println("Sumdistance: "+currentNode.sumDistance);
                    newDistNodes.get(i).sumDistance -= d*d;//*d*d*(Math.pow(newDistNodes.get(i).sumDistance/currentNode.sumDistance,2/5));
                }
            }
            
            //System.out.printf("Gravity: ");
            for(Node node : nodes){
                node.gravity = 0;
                //System.out.printf("%d-%d: %d | ",newDistNodes.get(i).id,newDistNodes.get(i-1).id,((newDistNodes.get(i).sumDistance*newDistNodes.get(i-1).sumDistance)/((newDistNodes.get(i).getNodeDistance()[newDistNodes.get(i-1).getId()])*(newDistNodes.get(i).getNodeDistance()[newDistNodes.get(i-1).getId()]))));
                for(Node node2: nodes){
                    if(!node.equals(node2)){
                        node.gravity += ((node.sumDistance*node2.sumDistance)/((node.getNodeDistance()[node2.getId()])));//*(node.getNodeDistance()[node2.getId()])));
                    }
                }
            }
            //System.out.printf("\n");
            //((o2.sumDistance*o1.sumDistance)/((o2.getNodeDistance()[o1.getId()])*(o2.getNodeDistance()[o1.getId()])))
            
            Collections.sort(newDistNodes, (Node o1, Node o2) -> (o1.sumDistance-o2.sumDistance));
            
            //Collections.sort(newDistNodes, (Node o1, Node o2) -> (o1.gravity-o2.gravity));
            System.out.printf("[");
            for(int i=0; i<n; i++){
                System.out.printf("%d(%d), ",newDistNodes.get(i).id,newDistNodes.get(i).gravity);
            }
            System.out.printf("]\n");
        }
        int counter = 0;
        for(Taxi taxi : taxis){
            taxi.setLoc(initialPosQ.get(counter));
            taxi.setBase(initialPosQ.get(counter));
            counter++;
        }
        scanner.println("c");
    }
    
    void setInitialPos(){//Set taxi's at high priority nodes
        Node currentNode = avDistNodes.get(0);
        int currentTaxi = 0;
        if(x>=n){
            int times = x/n;
            for(int i=0; i<times; i++){
                for(Node node : avDistNodes){
                    initialPosQ.add(node);
                }
            }
        } 
        if(taxis.length-currentTaxi >= n/2){
            for(Node node : avDistNodes){
                if(currentNode.getNodeDistance()[node.id]%2 == 0){
                    initialPosQ.add(node);
                }
            }
        }
        
        Node secondNode = null;
        int dia = diameter/2;
        initialPosQ.add(currentNode);
        int counter=0;
        while(initialPosQ.size()<x){
            for(int i=0; i<n; i++){
                if(secondNode == null && currentNode.getNodeDistance()[i]==(dia/2)){
                    secondNode = nodes[i];
                    initialPosQ.add(nodes[i]);
                } else if(currentNode.getNodeDistance()[i]==(dia/2) && secondNode.getNodeDistance()[i]>(dia/4)){
                    secondNode = nodes[i];
                    initialPosQ.add(nodes[i]);
                }
            }
            
//            for(int i=0; i<n; i++){
//                if(currentNode.getNodeDistance()[i]==(dia/2)){
//                    initialPosQ.add(nodes[i]);
//                }
//            }
            //System.out.println(dia);
            //System.out.println(initialPosQ);
            dia = dia/2;
            currentNode = initialPosQ.get(counter);
            counter++;
        }  
        counter = 0;
        for(Taxi taxi : taxis){
            taxi.setLoc(initialPosQ.get(counter));
            taxi.setBase(initialPosQ.get(counter));
            counter++;
        }
//        for(Taxi taxi : taxis){
//            for(Node node : avDistNodes){
//                if(!node.hasTaxi()){
//                    taxi.setLoc(node);
//                    break;
//                }
//            }
//        }
        scanner.println("c");
    }
    
    
    

    void checkTraining(){
        if(time == trainT){
            orderQueue.clear();
            for(Taxi taxi : taxis)
                taxi.clearAll();

            for(Node node : nodes)
                node.clearTaxis();
            
            totalCost = 0;
            setInitialPos();
        }
    }

    private void run(){
        boolean done = false;

        while(!done){
            

            if(scanner.hasNextLine()){
                if(time<=trainT){
                    scanner.nextLine();
                } else {
                    getOrders(scanner.nextLine());
                }
            }
            //System.out.println(orderQueue);
            // first we assign taxis to the customers in the queue
            
//            while(!orderQueue.isEmpty()){
//                assignTaxi(orderQueue.poll());
//            }


            // carry out all taxis' moves
            if(time <= trainT){
                checkTraining();
                if(time != trainT){
                    scanner.println("c");
                }
                
            } else{
                int lengthQueue = orderQueue.size();
                for(int i=0; i<lengthQueue; i++){
                    assignTaxi(orderQueue.poll());
                }
                for(Taxi taxi: taxis){ //Loop through all taxi's to determine their next move.
                    
                    if(taxi.path.peek() == taxi.getLoc()){ //If the taxi is at its destination.

                        int clientSize = taxi.getClients().size();
                        for(int i=0; i < clientSize; i++){ //Look if any passenger wants to disembark
                            if(taxi.clients.get(i).getDest().getId() == taxi.location.getId() && taxi.clients.get(i).getStatus().equals(Customer.Status.TRANSIT)){
                                totalCost += taxi.clients.get(i).arrived(time);
                                taxi.dropPas(i);//Drop the passenger
                                clientSize = taxi.getClients().size();
                                i--;//Make sure we don't skip a passenger (or get out of bounds)
                            }
                        }
                        
                        for(Customer customer : taxi.clients){
                            if(customer.getLoc() == taxi.getLoc().getId() && customer.getStatus().equals(Customer.Status.WAITING)){
                                taxi.addPas(customer);
                            }
                                
                        }
//                        taxi.clients.stream().filter((customer) -> (customer.getLoc() == taxi.getLoc().getId() && customer.getStatus().equals(Customer.Status.WAITING))).forEach((customer) -> {
//                            taxi.addPas(customer);//add passenger
//                        }); //Look if any passenger wants to get in

                        taxi.path.poll();//Remove the destination from the queue.
                        List<Node> askedNodes = new ArrayList<>();
                        for(Customer cust:taxi.getClients()){//check for unasked destinations in the path
                            if(!askedNodes.contains(nodes[cust.getLoc()])){
                                askedNodes.add(nodes[cust.getLoc()]);
                            }
                            if(!askedNodes.contains(cust.getDest())){
                                askedNodes.add(cust.getDest());
                            }
                        }
                        for(int i=0; i<taxi.getPath().size(); i++){
                            if(!askedNodes.contains(taxi.getPath().get(i))){
                                taxi.getPath().remove(i);
                                i--;
                            }
                        }
                        //taxi.greedySalesman();

                    } else if(!taxi.path.isEmpty()){//If there is something in the path, but we are not there
                        int dest = taxi.path.peek().getId();

                        Node[] adj = taxi.getLoc().getAdjacent();
                        for(Node node : adj){
                            if(taxi.getLoc().getNodeDistance()[dest] > node.getNodeDistance()[dest]){
                                taxi.setLoc(node);
                                break;
                            }
                        }
                    } else if(taxi.clients.isEmpty() && taxi.path.isEmpty()){//If the path is empty, move to the center most node without a taxi
                        taxi.path.add(taxi.getBase());
//                        for(Node node : avDistNodes){
//                            if(!node.hasTaxi()){
//                                taxi.path.add(node);
//                                //System.out.println("Center Path: "+taxi.path);
//                                break;
//                            }
//                        }
                    }
                    //System.out.println(taxi+": "+taxi.getCap()+" | "+taxi.getClients().size());
                }
                
                scanner.println("c");
            }

//            for (Taxi taxi : taxis) {
//                if (taxi.isEmpty() && !orderQueue.isEmpty()) {
//                    taxi.clients.add(orderQueue.poll());
//                    directWalk(taxi, taxi.clients.get(0));
//                } else if (!taxi.isEmpty()) {
//                    directWalk(taxi, taxi.clients.get(0));
//                }
//            
            
            

            time++;
            System.out.println(time);
            System.out.println(totalCost);

            boolean empty = true;
            for (Taxi taxi : taxis) {
                empty &= taxi.isEmpty();
            }
            //System.out.println("NextLine: "+scanner.hasNextLine()+" | orderQueue: "+orderQueue.isEmpty()+" | empty: "+empty);
            if(!scanner.hasNextLine() && orderQueue.isEmpty() && empty){
                done=true;
            }
        }
    }
    public static void main(String[] args) {
        (new TaxiScheduling()).run();
    }
    
}
