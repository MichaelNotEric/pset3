package pset3;
import java.util.*;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
public class CFG {
	Set<Node> nodes = new HashSet<Node>();
	Map<Node, Set<Node>> edges = new HashMap<Node, Set<Node>>();
	
	public static class Node {
		int position;
		Method method;
		JavaClass clazz;
		
		Node(int p, Method m, JavaClass c) {
			position = p;
			method = m;
			clazz = c;
		}
		
		public Method getMethod() {
			return method;
		}
		
		public JavaClass getClazz() {
			return clazz;
		}
		
		public boolean equals(Object o) {
			if (!(o instanceof Node)) return false;
			Node n = (Node)o;
			return (position == n.position) && method.equals(n.method) && clazz.equals(n.clazz);
		}
		
		public int hashCode() {
			return position + method.hashCode() + clazz.hashCode();
		}
		
		public String toString() {
			return clazz.getClassName() + "." + method.getName() + method.getSignature() + ": " + position;
		}
	}
	
	public void addNode(int p, Method m, JavaClass c) {
		addNode(new Node(p, m, c));
	}
	
	private void addNode(Node n) {
		nodes.add(n);
		Set<Node> nbrs = edges.get(n);
		if (nbrs == null) {
			nbrs = new HashSet<Node>();
			edges.put(n, nbrs);
		}
	}
	
	public void addEdge(int p1, Method m1, JavaClass c1, int p2, Method m2, JavaClass c2) {
		Node n1 = new Node(p1, m1, c1);
		Node n2 = new Node(p2, m2, c2);
		addNode(n1);
		addNode(n2);
		Set<Node> nbrs = edges.get(n1);
		nbrs.add(n2);
		edges.put(n1, nbrs);
	}
	
	public void addEdge(int p1, int p2, Method m, JavaClass c) {
		addEdge(p1, m, c, p2, m, c);
	}
	
	public String toString() {
		return nodes.size() + " nodes\n" + "nodes: " + nodes + "\n" + "edges: " + edges;
	}
	
	public boolean isReachable(String methodFrom, String clazzFrom,
			String methodTo, String clazzTo) {
		// you will implement this method in Question 2.2
		Node nodeFrom = null;
		Node nodeTo = null;
		Set<Node> fNodes = new HashSet<Node>();
		Set<Node> tNodes = new HashSet<Node>();
		
		for(Node n: nodes){
			if(n.method.getName().equals(methodFrom) && n.clazz.getClassName().equals(clazzFrom)){
				//found fromMethod, add this node to list of nodes in fromMethod
				fNodes.add(n);
			}
			if(n.method.getName().equals(methodTo) && n.clazz.getClassName().equals(clazzTo)){
				//found toMethod, add this ndoe to list of nodes in toMethod
				tNodes.add(n);
			} 
		}
		
		for(Node nFrom: fNodes){
			for(Node mTo: tNodes){
				if(mTo.equals(nFrom)){
					//if the methods are the same
					return true;
				}
				//look at 2 nodes, try to find connection between them.
				//System.out.println("Checking from node " + nFrom.position + " in " + nFrom.method.getName());
				//System.out.println("Checking to node " + mTo.position + " in " + mTo.method.getName());
				ArrayList<Node> testNodes = new ArrayList<Node>();
				HashSet<Node> seenNodes = new HashSet<Node>();
				testNodes.add(nFrom);
				while(testNodes.size() > 0){
					Node test = testNodes.remove(0);
					//check nodes 1 by 1
					//System.out.println("Looking at node " + test.position + " " + test.method.getName());
					seenNodes.add(test);
					if(!(test.position == -1)){
						for(Node check: edges.get(test)){
							//check the nodes each edge connects to
							//System.out.println("Checking an edge to " + check.position + " " + check.method.getName());
							if(check.equals(mTo)){
								//we found a node that is connected to an edge
								//System.out.println("Found matching node " + check.position + " " + check.method.getName() + " and " + mTo.position + " " + mTo.method.getName());
								return true;
							} 
							if(!seenNodes.contains(check)){
								//add node to nodes that I need to check the edges of
								testNodes.add(check);
							}
						}
					} else{
						//don't want to check edges coming out of a dummy node (return doesn't count as reachability)
					}
					
				}
			}
		}
		//couldn't find a connection
		return false;
	}
	
	//Print method to look at a CFG 
	public void printCFG(){
		System.out.println("Printing out a CFG:");
		for(Node n: this.nodes){
			System.out.println("\toNode:");
			System.out.println("\t\t"+"Position: "+n.position);
			System.out.println("\tEdges:");
			System.out.println("\t\t"+edges.get(n)+"\n");
		}
	}
}
