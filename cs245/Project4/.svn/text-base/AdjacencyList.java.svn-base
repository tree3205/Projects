

public class AdjacencyList {
	
	private int size;
	private LinkedNode[] list;
	
	public AdjacencyList(int size) {
		this.size = size;
		list = new LinkedNode[size];
		for (int i = 0; i < size; i++) {
			list[i] = new LinkedNode();
		}
	}
	
	public void put(int start, int end, int distance) {
		LinkedNode node = new LinkedNode(end, distance);
		LinkedNode head = list[start];
		while (head.hasNext())
			head = head.next();
		head.setNext(node);
	}
	
	public LinkedNode get(int start) {
		return list[start];
	}
	
	public int length() {
		return size;
	}
	
	public static void main(String[] args) {
		
	}

}
