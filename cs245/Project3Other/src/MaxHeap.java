
public class MaxHeap {
	    private int[] Heap;
	    private int maxsize;
	    private int size;

	    public MaxHeap(int max) {
			maxsize = max;
			Heap = new int[maxsize];
			size = 0 ;
			Heap[0] = Integer.MAX_VALUE;
	    }

	    private int leftchild(int pos) {
	    	return 2*pos;
	    }
	    
	    private int rightchild(int pos) {
	    	return 2*pos + 1;
	    }

	    private int parent(int pos) {
	    	return  pos / 2;
	    }
	    
	    private boolean isleaf(int pos) {
	    	return ((pos > size/2) && (pos <= size));
	    }

	    private void swap(int pos1, int pos2) {
	    	int tmp;

			tmp = Heap[pos1];
			Heap[pos1] = Heap[pos2];
			Heap[pos2] = tmp;
	    }

	    public void insert(int elem) {
			size++;
			Heap[size] = elem;
			int current = size;
			
			while (Heap[current] > Heap[parent(current)]) {
			    swap(current, parent(current));
			    current = parent(current);
			}	
	    }

	    public void print() {
			int i;
			for (i=1; i<=size;i++)
			    System.out.print(Heap[i] + " ");
			System.out.println();
	    }

	    public int removemax() {
			swap(1,size);
			size--;
			if (size != 0)
			    pushdown(1);
			return Heap[size+1];
	    }

	    private void pushdown(int position) {
			int largestchild;
			while (!isleaf(position)) {
				largestchild = leftchild(position);
			    if ((largestchild < size) && (Heap[largestchild] < Heap[largestchild+1]))
			    	largestchild = largestchild + 1;
			    if (Heap[position] >= Heap[largestchild]) return;
			    swap(position,largestchild);
			    position = largestchild;
			}
		}
}




