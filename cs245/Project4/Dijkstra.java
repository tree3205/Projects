

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Dijkstra {
	
	public static void printPath(String[] nameList, BiNode[] vertices, int key) {
		int nextKey = vertices[key].path();
		if (nextKey != -1)
			printPath(nameList, vertices, nextKey);
		if (key == 0)
			System.out.print(nameList[key]);
		else
			System.out.print(" " + nameList[key]);
	}

	public static void main(String[] args) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(args[0]));
			
			String line;
			line = in.readLine();
			StringBuffer names = new StringBuffer();
			HashTable nameTable = new HashTable(19);
			int count = 0;
			while (!line.equals(".")){
				if (count == 0) {
					names.append(line);
					nameTable.put(line, count);
				} else {
					names.append(";" + line);
					nameTable.put(line, count);
				}
				count++;
				line = in.readLine();
			}
			String[] nameList = names.toString().split(";");
			
			/* Test Code for NameList & NameTable
			for (int i = 0; i < nameList.length; i++) {
				System.out.println(i + " => " + nameList[i] + " | " + nameList[i] + " => " + nameTable.get(nameList[i]));
			}
			*/
			
			String start;
			String end;
			String distance;
			AdjacencyList neighbors = new AdjacencyList(nameList.length);
			
			while (true) {
				start = in.readLine();
				if (start == null)
					break;
				end = in.readLine();
				distance = in.readLine();
				neighbors.put(nameTable.get(start), nameTable.get(end), Integer.valueOf(distance));
			}
			
			/* Test Code For Adjacency List
			for (int i = 0; i < neighbors.length(); i++) {
				LinkedNode n = neighbors.get(i);
				System.out.print(i + " => ");
				while (n.hasNext()) {
					n = n.next();
					System.out.print(n.getKey() + "(" + n.getDistance() + ") -> ");
				}
				System.out.println();
			}
			*/
			
			BiNode[] vertices = new BiNode[nameList.length];
			vertices[0] = new BiNode(0, 0);
			for (int i = 1; i < vertices.length; i++) {
				vertices[i] = new BiNode(i, Integer.MAX_VALUE);
			}
			
			BiNode BiQueue = new BiNode();
			
			for (int i = 0; i < vertices.length; i++) {
				BinomialHeap.add(BiQueue, vertices[i]);
			}
			while (!BinomialHeap.isEmpty(BiQueue)) {
				BiNode current = BinomialHeap.remove(BiQueue);
				LinkedNode currentNeighbor = neighbors.get(current.key());
				while (currentNeighbor.hasNext()) {
					currentNeighbor = currentNeighbor.next();
					int potentialDistance = current.distance() + currentNeighbor.getDistance();
					if (vertices[currentNeighbor.getKey()].distance() > potentialDistance) {
						BinomialHeap.decreaseDistance(vertices, currentNeighbor.getKey(), potentialDistance);
						vertices[currentNeighbor.getKey()].setPath(current.key());
					}
				}
			}
			
			/* Standard Output */
			System.out.println("Original Graph");
			System.out.println();
			
			for (int i = 0; i < nameList.length; i++) {
				System.out.print(nameList[i] + ": ");
				LinkedNode vn = neighbors.get(i);
				boolean first = true;
				while (vn.hasNext()) {
					vn = vn.next();
					if (first) {
						first = false;
						System.out.print(nameList[vn.getKey()] + " " + vn.getDistance());
					} else {
						System.out.print(", " + nameList[vn.getKey()] + " " + vn.getDistance());
					}
				}
				System.out.println();
			}
			
			System.out.println();
			System.out.println("Shortest Paths");
			System.out.println();
			for (int i = 0; i < vertices.length; i++) {
				BiNode v = vertices[i];
				System.out.print(nameList[i] + "  " + v.distance() + ": ");
				printPath(nameList, vertices, i);
				System.out.println();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
