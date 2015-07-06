

public class HashTable {
	
	private int size;
	private LinkNode[] table;

	public HashTable(int size) {
		this.size = size;
		this.table = new LinkNode[size];
		for (int i = 0; i < size; i++) {
			table[i] = new LinkNode();
		}
	}
	
	public void put(String key, int value) {
		int hashV = hash(key);
		LinkNode node = new LinkNode(key, value);
		LinkNode head = table[hashV];
		while (head.next != null)
			head = head.next;
		head.next = node;
	}
	
	public int get(String key) {
		int hashV = hash(key);
		LinkNode head = table[hashV];
		while (head.next != null) {
			head = head.next;
			if (head.key.equals(key))
				return head.value;
		}
		return -1;
	}

	private int hash(String key) {
		int value = 0;
		for (int i = 0; i < key.length(); i++) {
			value += (int) key.charAt(i);
			if (i >= 9)
				break;
		}
		return value % size;
	}
	
	private class LinkNode {
		
		private String key;
		private int value;
		private LinkNode next;
		
		public LinkNode () {
			this.next = null;
		}
		
		public LinkNode (String key, int value) {
			this.key = key;
			this.value = value;
			this.next = null;
		}

	}
	
	public static void main(String[] args) {
		HashTable h = new HashTable(7);
		h.put("SF", 0);
		h.put("LA", 1);
		System.out.println(h.get("SF"));
	}

}
