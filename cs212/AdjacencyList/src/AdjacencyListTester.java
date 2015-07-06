import java.util.Arrays;

public class AdjacencyListTester {

	public static void main(String[] args) {
				
		AdjacencyList list = new AdjacencyList();
		
		System.out.println();
		
		System.out.println("Empty Adjacency List:");
		(list).print();
				
		System.out.println("Populating Graph...");
		list.addNode("A");
		list.addNode("B");
		
		list.addEdge("A", "B");
		list.addEdge("A", "C");
		list.addEdge("B", "C");
		
		System.out.println();
		
		System.out.println("Populated Adjacency List:");
		list.print();
				
		System.out.print("All Nodes: ");
		System.out.print(Arrays.toString(list.getNodes()) + "\n");
		
		System.out.print("Edges for A: ");
		System.out.print(Arrays.toString(list.getEdges("A")));
	}
}
