
public class HuffmanTree {
	
	public static void print(HuffmanNode node, int indent) {
		for (int i = 0; i < indent; i++) {
			System.out.print(' ');
		}
		if (node.isLeaf()) {
			System.out.println((int) node.code());
		}
		else {
			System.out.println('.');
			print(node.left(), indent + 1);
			print(node.right(), indent + 1);
		}
	}
	
	public static void decode(HuffmanNode node, BinaryFile in) {
		if (!in.readBit()) {
			node.setIsLeaf(false);
			HuffmanNode leftChild = new HuffmanNode();
			node.setLeft(leftChild);
			decode(node.left(), in);
			HuffmanNode rightChild = new HuffmanNode();
			node.setRight(rightChild);
			decode(node.right(), in);
		}
		else {
			node.setIsLeaf(true);
			node.setCode(in.readChar());
		}
	}
	
	public static void encode(HuffmanNode node, BinaryFile out) {
		if (node.isLeaf()) {
			out.writeBit(true);
			out.writeChar(node.code());
		}
		else {
			out.writeBit(false);
			encode(node.left(), out);
			encode(node.right(), out);
		}
	}
	
	public static int size(HuffmanNode node) {
		if (node.isLeaf()) {
			return 9;
		}
		else {
			return 1 + size(node.left()) + size(node.right());
		}
	}
	
	public static void lookupTable(HuffmanNode node, StringBuffer code, String[] table) {
		if (node.isLeaf()) {
			table[(int) node.code()] = code.toString();
		}
		else {
			StringBuffer leftCode = new StringBuffer(code);
			leftCode.append('0');
			lookupTable(node.left(), leftCode, table);
			StringBuffer rightCode = new StringBuffer(code);
			rightCode.append('1');
			lookupTable(node.right(), rightCode, table);
		}
	}
	
	public static char decodeNext(HuffmanNode node, BinaryFile in) {
		if (node.isLeaf()) {
			return node.code();
		}
		else {
			boolean tmp = in.readBit();
			if (tmp) {
				return decodeNext(node.right(), in);
			}
			else {
				return decodeNext(node.left(), in);
			}
		}
	}

	public static void main(String[] args) {
		BinaryFile in = new BinaryFile("test", 'r');
		HuffmanNode root = new HuffmanNode();
		decode(root, in);
		in.close();
		
		String[] t = new String[256];
		StringBuffer buffer = new StringBuffer();
		lookupTable(root, buffer, t);
		
		for (int i = 0; i < 256 ; i++) {
			if (t[i] != null) {
				System.out.print((char) i);
				for (int index = 0; index < t[i].length(); index++) {
					System.out.print(t[i].charAt(index));
				}
				System.out.println();
			}
		}
		
		BinaryFile out = new BinaryFile("result", 'w');
		encode(root, out);
		out.close();
		
		System.out.println();
		System.out.println(size(root));
		
		print(root, 0);
	}

}
