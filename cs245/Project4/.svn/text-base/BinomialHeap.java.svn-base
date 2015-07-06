

import java.util.Random;

public class BinomialHeap {
	
	public static BiNode create(BiNode node) {
		BiNode n = new BiNode();
		n.setSibling(node);
		return n;
	}
	
	public static void merge(BiNode self, BiNode other) {
		if (self.sibling() == null) {
			self.setSibling(other.sibling());
			return;
		}
		
		if (other.sibling() == null)
			return;
		else
			other = other.sibling();
					
		while (self.sibling() != null) {
			while (other != null && other.degree() < self.sibling().degree()) {
				BiNode current = other;
				other = current.sibling();
				current.setSibling(self.sibling());
				self.setSibling(current);
				self = self.sibling();
			}
			
			if (other != null && other.degree() == self.sibling().degree()) {
				if (other.distance() < self.sibling().distance()) {
					BiNode tmp = self.sibling();
					BiNode tmpOther = other;
					other = other.sibling();
					tmpOther.setSibling(tmp.sibling());
					self.setSibling(tmpOther);
					tmp.setSibling(null);
					if (tmpOther.child() != null) {
						BiNode tmpChild = tmpOther.child();
						while (tmpChild.sibling() != null)
							tmpChild = tmpChild.sibling();
						tmpChild.setSibling(tmp);
					} else {
						tmpOther.setChild(tmp);
					}
					tmp.setParent(tmpOther);
					tmpOther.setDegree(tmpOther.degree() + 1);
				} else {
					BiNode tmp = self.sibling();
					BiNode tmpOther = other;
					other = other.sibling();
					tmpOther.setSibling(null);
					if (tmp.child() != null) {
						BiNode tmpChild = tmp.child();
						while (tmpChild.sibling() != null)
							tmpChild = tmpChild.sibling();
						tmpChild.setSibling(tmpOther);
					} else {
						tmp.setChild(tmpOther);
					}
					tmpOther.setParent(tmp);
					tmp.setDegree(tmp.degree() + 1);
				}
				while (self.sibling().sibling() != null && self.sibling().degree() == self.sibling().sibling().degree()) {
					BiNode tmpA = self.sibling();
					BiNode tmpB = self.sibling().sibling();
					if (tmpA.distance() < tmpB.distance()) {
						tmpA.setSibling(tmpB.sibling());
						tmpB.setSibling(null);
						if (tmpA.child() != null) {
							BiNode tmpChild = tmpA.child();
							while (tmpChild.sibling() != null)
								tmpChild = tmpChild.sibling();
							tmpChild.setSibling(tmpB);
						} else {
							tmpA.setChild(tmpB);
						}
						tmpB.setParent(tmpA);
						tmpA.setDegree(tmpA.degree() + 1);
					} else {
						self.setSibling(tmpB);
						tmpA.setSibling(null);
						if (tmpB.child() != null) {
							BiNode tmpChild = tmpB.child();
							while (tmpChild.sibling() != null)
								tmpChild = tmpChild.sibling();
							tmpChild.setSibling(tmpA);
						} else {
							tmpB.setChild(tmpA);
						}
						tmpA.setParent(tmpB);
						tmpB.setDegree(tmpB.degree() + 1);
					}
				}
			}
			
			while (other != null && other.degree() > self.sibling().degree()) {
				self = self.sibling();
			}
			
			if (other == null)
				break;
		}
		
		if (self.sibling() == null && other != null) {
			self.setSibling(other);
		}
	}
	
	public static void add(BiNode head, BiNode node) {
		BiNode tmp = create(node);
		merge(head, tmp);
	}
	
	public static boolean isEmpty(BiNode head) {
		return head.sibling() == null;
	}
	
	public static BiNode remove(BiNode head) {
		if (head.sibling() == null)
			return null;
		
		int min = Integer.MAX_VALUE;
		BiNode tmp = head;
		while (tmp.sibling() != null) {
			tmp = tmp.sibling();
			if (tmp.distance() < min)
				min = tmp.distance();
		}
		tmp = head;
		while (tmp.sibling().distance() != min) {
			tmp = tmp.sibling();
		}
		BiNode removedNode = tmp.sibling();
		tmp.setSibling(removedNode.sibling());
		if (removedNode.child() != null) {
			BiNode tmpChild = removedNode.child();
			BiNode left = tmpChild;
			tmpChild.setParent(null);
			while (tmpChild.sibling() != null) {
				tmpChild = tmpChild.sibling();
				tmpChild.setParent(null);
			}
			add(head, left);
		}
		return removedNode;
	}
	
	public static void decreaseDistance(BiNode[] vertices, int key, int distance) {
		BiNode node = vertices[key];
		node.setDistance(distance);
		while (node.parent() != null && node.distance() < node.parent().distance()) {
			int tmp = node.distance();
			node.setDistance(node.parent().distance());
			node.parent().setDistance(distance);
			int nodeKey = node.key();
			int parentKey = node.parent().key();
			node.setKey(parentKey);
			node.parent().setKey(nodeKey);
			BiNode tmpNode = vertices[nodeKey];
			vertices[nodeKey] = vertices[parentKey];
			vertices[parentKey] = tmpNode;
			node = node.parent();
		}
	}
	
	public static void list(BiNode head) {
		System.out.print("HEAP: ");
		if (head.sibling() != null)
			listHeap(head.sibling());
		System.out.println();
	}
	
	private static void listHeap(BiNode root) {
		System.out.print("(" + root.key() + ", " + root.distance() + ", " + root.degree() + ") ");
		if (root.child() != null)
			listHeap(root.child());
		if (root.sibling() != null)
			listHeap(root.sibling());
	}

	public static void main(String[] args) {
		BiNode n;
		BiNode h = new BiNode();
		
		BiNode[] v = new BiNode[20];
		
		Random r = new Random();
		
		for (int i = 0; i < 20; i++) {
			v[i] = new BiNode(i, i * 20);
			add(h, v[i]);
		}
		
		decreaseDistance(v, 2, -5);

		list(h);
		System.out.println(v[2].child().distance());
		System.out.println(v[0].distance());
	}

}
