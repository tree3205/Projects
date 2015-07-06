
import java.util.*;

public class AdjacencyList {
	/** Stores a directed adjacency list of nodes */
	private HashMap<String, HashSet<String>> list;
	
	/**
	 * Default constructor. Initializes adjacency list.
	 */
	public AdjacencyList() {
		// initialize list
		list = new HashMap<String, HashSet<String>>();
		System.out.println("New AdjacencyList created.");
	}

	/**
	 * Returns the number of nodes stored in the adjacency list.
	 * @return number of nodes
	 */
	public int numNodes() {
		return list.size();
	}
	
	/**
	 * Returns the number of edges stored in the adjacency list.
	 * @return number of edges
	 */
	public int numEdges() {
		int count = 0;
		Set<String> nodes = list.keySet();
		
		for(String node : nodes) {
			HashSet<String> edges = list.get(node);
			count += edges.size();

			// We can rewrite this code as follows:
			// count += list.get(node).size();
		}
		
		return count;
	}

	/**
	 * Adds a node with the given label to the adjacency list if it does not
	 * already exist.
	 * 
	 * @param label		node label
	 */
	public void addNode(String label) {
		// check if a node with this label already exists
		if(list.containsKey(label)) {
			System.out.println("Node " + label + " already exists.");
		}
		// otherwise add node with an empty list of edges
		else {
			list.put(label, new HashSet<String>());
			System.out.println("Node " + label + " added.");
		}
	}
	
	/**
	 * Adds a directed edge from start node to edge node if that edge does not
	 * already exist.
	 * 
	 * @param start		label of start node
	 * @param end		label of end node
	 */
	public void addEdge(String start, String end) {
		// add start and end node if they do not exist
		if(!list.containsKey(start)) addNode(start);
		if(!list.containsKey(end)) addNode(end);
		
		// since HashSet does not allow duplicate entries,
		// we don't need to check if the edge exists here
		HashSet<String> edges = list.get(start);
		edges.add(end);
		
		System.out.println("Added edge from " + start + " --> " + end + ".");
	}
	
	/**
	 * Returns all nodes as a String array.
	 * @return	array of nodes
	 */
	public String[] getNodes() {
		Set<String> nodes = list.keySet();
		return nodes.toArray(new String[0]);
		
	}
	
	/**
	 * Returns all edges from start node.
	 * 
	 * @param start		label of start node
	 * @return			list of nodes adjacent to start node
	 */
	public String[] getEdges(String start) {
		HashSet<String> edges = list.get(start);
		return edges.toArray(new String[0]);
	}
	
	/**
	 * Provides a string representation of this adjacency list.
	 * @return			adjacency list in String form
	 */
	public String toString() {
		Set<String> nodes = list.keySet();
		StringBuffer output = new StringBuffer();
		
		for(String node : nodes) {
			String[] edges = getEdges(node);
			output.append(node + " --> ");
			output.append(Arrays.toString(edges));
			output.append("\n");
		}
		
		return output.toString();
	}
	
	/**
	 * Prints the adjacency list to console.
	 */
	public void print() {
		System.out.println(toString());
	}
}
