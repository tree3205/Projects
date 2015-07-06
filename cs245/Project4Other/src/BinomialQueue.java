
public class BinomialQueue {
	
	private Node[] bTrees;
	private int currentSize;
	
	public BinomialQueue(int size) {
		bTrees = new Node[size];
		currentSize = 0;
		initialize( );
	}
	
	private void initialize() {
		 currentSize = 0;
         for( int i = 0; i < bTrees.length; i++ )
             bTrees[ i ] = null;
	}

	
	public void insert(int elem, int priority) {
        currentSize += 1;
        bTrees[elem] = new Node(elem, priority);

        merge(bTrees[elem]);
	}

	private void merge(Node addNew) {
		int i,j;
		
		
	}

	public int remove_min() {
		return 0;
	}
	
	public void reduce_key(int elem, int new_priority) {
		
	}
	
	//Inner class Node
	private class Node {
		
		private Node parent;
		private Node lchild;
		private Node rsibling;
		private int degree;
		private int cost;
		private int path;
		
		public Node(int cost, int path) {
			parent = null;
			lchild = null;
			rsibling = null;
			degree = 0;
			this.cost = cost;
			this.path = path;
		}
		
		public String toString() {
			return "path-"+path +"/"+ "cost-"+ cost;
		}
	}
	
	public static void main(String[] args) {
		int size;
		InputProcess source = new InputProcess();
		source.inputProcess(args);
		size = source.count;
		BinomialQueue bQ = new BinomialQueue(size);
		bQ.insert(0, 0);
		//bQ.insert(1, 347);
		//bQ.insert(2, 649);
		for (int i = 0; i < bQ.bTrees.length; i++) {
			if (bQ.bTrees[i] == null)
				System.out.print("null");	
			else
				System.out.println(i+": " + bQ.bTrees[i].toString()+
						" (parent:"+bQ.bTrees[i].parent+
						" lchild:"+bQ.bTrees[i].lchild+
						" rsibling:"+bQ.bTrees[i].rsibling +")");
				System.out.println();
				System.out.println();
		}
		
	}

}
