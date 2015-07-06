import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Dijkstra {

	public int cost;
	public int path;
	
	public Dijkstra() {
		cost = Integer.MAX_VALUE;
		path = -1;
	}
	
	public void output(String[] args)  {
		PrintWriter out;
		String from, to;
		int tmp;
		int distance;
		int i,j;
		
		InputProcess source = new InputProcess();
		source.inputProcess(args);
		
		try {
			out = new PrintWriter(new FileWriter("outputfile.txt")); 
			out.println();
			out.println("Original Graph");
		
			for (i = 0; i < source.graph.length; i++) {
				from = lookUp(i, source);
				out.print(from + ": ");
				System.out.println("from: "+from);
				for (j = 0; j < source.graph[i].size(); j++) {
					tmp = Integer.parseInt((String) source.graph[i].getString(j));
					to = lookUp(tmp, source);
					System.out.println("to: "+to);
					distance = (Integer) source.graph[i].getInt(j);
					System.out.println("distance: "+distance);
					if (source.graph[i].size() == 1 || j == source.graph[i].size() -1)
						out.print(to + " " + distance);
					else
						out.printf(to + " " + distance + ", ");
				}
				System.out.println();
				out.println();
			}
			out.println();
			out.print("Shortest Paths");
			out.println();
			
			//need work
			
			out.close();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private String lookUp(int num, InputProcess source) {
		String city;
		city = source.nameList[num];
		
		return city;
	}

	public static void main(String[] args) {
		Dijkstra d = new Dijkstra();
		d.output(args);
		
	}
}
