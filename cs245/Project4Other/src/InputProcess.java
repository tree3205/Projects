import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class InputProcess {
	
	public int count;
	private static final int size = 13;
	public DataList[] table;
	public DataList[] graph;
	public String[] nameList;
	
	public InputProcess() {
		table = new DataList[size];
		for (int i = 0; i < table.length; i++) {
			table[i] = null;
		}
	}
	
	public void inputProcess(String[] args) {
		BufferedReader input;
		String nextLine;
		int index, intValue, distance;
		String value;

		try {
			//Initialize Name List and Graph
		    input = new BufferedReader(new FileReader(args[0]));
		    nextLine = input.readLine();
		    
		    while (nextLine.compareTo(".") != 0) {
				nextLine = input.readLine();
				count++;
		    }
		    nameList = new String[count];
		    graph = new DataList[count];
			for (int i = 0; i < graph.length; i++) {
				graph[i] = null;
			}
		    
		    // Generate Name List and Hash Table
		    input = new BufferedReader(new FileReader(args[0]));
		    nextLine = input.readLine();
		    for (int i = 0; i < nameList.length; i++) {
		    	nameList[i] = nextLine;
		    	hashTableAdd(nextLine, i, table);
		    	nextLine = input.readLine();
		    }	       
		    
		    //Generate Adjacency List(Graph)
		    nextLine = input.readLine();
		    while (nextLine != null) {
		    	index = findValue(nextLine, table);
		    	nextLine = input.readLine();
		    	intValue = findValue(nextLine, table);
		    	value = Integer.toString(intValue);
		    	nextLine = input.readLine();
		    	distance = Integer.valueOf(nextLine).intValue();
		        graphAdd(index, value, distance, graph);
		    	nextLine = input.readLine();
		    }
		}
		catch (IOException e) {
			System.out.println("File Error");
		}
	}
	
	private DataList[] graphAdd(int index, String value, int distance,
			DataList[] graph) {
		if (graph[index] == null) {		
			graph[index] = new DataList();
		}
		graph[index].add(value, distance);
		
		return graph;
	}
	
	public DataList[] display(DataList[] data) {
		int i,j;
		if (data.length == 0)
			System.out.println("Empty Hash Table");
		
		for ( i = 0; i < data.length; i++) {	
			if (data[i] != null) {
				for (j = 0; j < data[i].size();j++) {	
					System.out.println(i + ": " + data[i].getString(j) + "/" + data[i].getInt(j));
				}		
			}
		}
		return data;
	}

	public int hash(String key) {
		int hashValue = 0;
		for (int i = 0; i < key.length(); i++){
			hashValue += (int) key.charAt(i); 
		}
		
		return hashValue % size;	
	}
	
	public DataList[] hashTableAdd(String key, int intValue, DataList[] table) {
		int index = hash(key);
		if (table[index] == null) {		
			table[index] = new DataList();
		}
		table[index].add(key, intValue);
			
		return table;
	}
	
	public int findValue(String key, DataList[] table) {
		int i;
		int index = hash(key);
		int intValue = 0;

		for (i = 0; i < table[index].size(); i++) {
			if (table[index].getString(i).equals(key)) {
				intValue = (Integer) table[index].getInt(i);
				return intValue;
			}
		}
	
		return intValue;
	}
	
	public static void main(String[] args) {
		InputProcess n = new InputProcess();
		n.inputProcess(args);
		
		System.out.println("Name List: ");
		System.out.println(n.nameList.length);
		for (int i = 0; i < n.nameList.length; i++) {
			System.out.print(i + ":" +n.nameList[i] + " ");
		}
		System.out.println();
		System.out.println();
		
		System.out.println("Hash Table: ");
		System.out.println(n.table.length);
		n.display(n.table);
		System.out.println();
		
		System.out.println("Adjacency List: ");
		System.out.println(n.graph.length);
		n.display(n.graph);
		
		System.out.println(n.count);			
	}
}
