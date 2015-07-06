import java.util.PriorityQueue;


public class HuffmanNode implements Comparable<HuffmanNode>{
	
	private boolean isLeaf;
	private char code;
	private int freq;
	private HuffmanNode left;
	private HuffmanNode right;

	public HuffmanNode(boolean isLeaf, char code, int freq, HuffmanNode left, HuffmanNode right) {
		this.isLeaf = isLeaf;
		this.code = code;
		this.freq = freq;
		this.left = left;
		this.right = right;
	}
	
	public HuffmanNode(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	public HuffmanNode() {}
	
	public HuffmanNode left() {
		return left;
	}
	
	public void setLeft(HuffmanNode left) {
		this.left = left;
	}
	
	public HuffmanNode right() {
		return right;
	}
	
	public void setRight(HuffmanNode right) {
		this.right = right;
	}
	
	public char code() {
		return code;
	}
	
	public void setCode(char code) {
		this.code = code;
	}
	
	public int freq() {
		return freq;
	}
	
	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}
	
	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	private int height(int depth) {
		if (isLeaf) {
			return depth + 1;
		}
		else {
			return max(left.height(depth + 1), right.height(depth + 1));
		}
	}
	
	private static int max(int a, int b) {
		if (a > b) {
			return a;
		}
		else {
			return b;
		}
	}
	
	@Override
	public int compareTo(HuffmanNode otherNode) {
		if (this.freq > otherNode.freq) {
			return 1;
		}
		else if (this.freq < otherNode.freq) {
			return -1;
		}
		else {
			if (this.height(0) > otherNode.height(0)) {
				return 1;
			}
			else if (this.height(0) < otherNode.height(0)) {
				return -1;
			}
			else {
				return 0;
			}
		}
	}
	
	public static void main(String[] args) {
		HuffmanNode n0 = new HuffmanNode(false);
		n0.setFreq(1);
		n0.setCode('e');
		HuffmanNode n1 = new HuffmanNode(false);
		n1.setFreq(2);
		n1.setCode('a');
		HuffmanNode n2 = new HuffmanNode(true);
		n2.setFreq(3);
		n2.setCode('i');
		HuffmanNode n3 = new HuffmanNode(true);
		n3.setFreq(3);
		n3.setCode('g');
		HuffmanNode n4 = new HuffmanNode(true);
		n4.setFreq(2);
		n4.setCode('f');
		
		n0.setLeft(n1);
		n0.setRight(n2);
		n1.setLeft(n3);
		n1.setRight(n4);
		
		
		PriorityQueue<HuffmanNode> q = new PriorityQueue<HuffmanNode>();
		q.add(n0);
		q.add(n1);
		q.add(n2);
		q.add(n3);
		q.add(n4);
		
		while (!q.isEmpty()) {
			HuffmanNode tmp = q.remove();
			System.out.println(tmp.code);
			if (tmp.code == 'g') {
				tmp.setCode('x');
			}
		}
		
		System.out.println(n3.code);
	}
}
