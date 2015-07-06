import java.util.PriorityQueue;


public class Huffman {
	
	public static void compress(boolean verbose, boolean force, TextFile in, BinaryFile out) {
		char tmpChar;
		String tmpCode;
		int[] charFreq = new int[256];
		PriorityQueue<HuffmanNode> queue = new PriorityQueue<HuffmanNode>();
		int originalFileSize = 0;
		/* Count char frequency and the file size */
		while (!in.EndOfFile()) {
			tmpChar = in.readChar();
			charFreq[(int) tmpChar]++;
			originalFileSize += 8; 
		}
		/* Verbose output: Frequency */
		if (verbose) {
			System.out.println("Char Frequency:");
			int printCount = 0;
			for (int i = 0; i < 256; i++) {
				if (charFreq[i] > 0) {
					printCount++;
					ASCIIzero(i);
					System.out.print(i + ": " + charFreq[i] + '\t');
					if (printCount == 5) {
						printCount = 0;
						System.out.println();
					}
					else {
						System.out.print(" | ");
					}
				}
			}
			System.out.println();
			if (printCount != 0) {
				System.out.println();
			}
		}
		/* Create HuffmanNode for each char and push them into a PriorityQueue */
		for (int i = 0; i < 256; i++) {
			if (charFreq[i] > 0) {
				HuffmanNode node = new HuffmanNode(true);
				node.setCode((char) i);
				node.setFreq(charFreq[i]);
				queue.add(node);
			}
		}
		/* Build the Huffman Tree */
		while(queue.size() > 1) {
			HuffmanNode tmpLeft = queue.remove();
			HuffmanNode tmpRight = queue.remove();
			HuffmanNode tmp = new HuffmanNode(false);
			tmp.setFreq(tmpLeft.freq() + tmpRight.freq());
			tmp.setLeft(tmpLeft);
			tmp.setRight(tmpRight);
			queue.add(tmp);
		}
		HuffmanNode root = queue.remove();
		/* Verbose output: Huffman Tree */
		if (verbose) {
			System.out.println("Huffman Tree:");
			HuffmanTree.print(root, 0);
			System.out.println();
			System.out.println();
		}
		/* Build the lookup table */
		String[] lookupTable = new String[256];
		HuffmanTree.lookupTable(root, new StringBuffer(), lookupTable);
		/* Verbose output: Huffman Code */
		if (verbose) {
			System.out.println("Huffman Code:");
			int printCount = 0;
			for (int i = 0; i < 256; i++) {
				if (charFreq[i] > 0) {
					printCount++;
					ASCIIzero(i);
					System.out.print(i + ": " + lookupTable[i] + '\t');
					if (printCount == 5) {
						printCount = 0;
						System.out.println();
					}
					else {
						System.out.print(" | ");
					}
				}
			}
			System.out.println();
		}
		/* Calculate the size of compressed file */
		int compressedFileSize = 6 + HuffmanTree.size(root);
		for (int i = 0; i < 256; i++) {
			if (charFreq[i] > 0) {
				compressedFileSize += charFreq[i] * lookupTable[i].length();
			}
		}
		compressedFileSize = compressedFileSize + (8 - compressedFileSize % 8);
		/* Compress file */
		//System.out.println("Orignal:" + originalFileSize);
		//System.out.println("Compressed:" + compressedFileSize);
		if (force || compressedFileSize < originalFileSize) {
			out.writeChar('H');
			out.writeChar('F');
			HuffmanTree.encode(root, out);
			in.rewind();
			while (!in.EndOfFile()) {
				tmpChar = in.readChar();
				tmpCode = lookupTable[(int) tmpChar];
				for (int i = 0; i < tmpCode.length(); i++) {
					if (tmpCode.charAt(i) == '0') {
						out.writeBit(false);
					} else {
						out.writeBit(true);
					}
				}
			}
		}
		else {
			System.out.println("Warning: File cannot be compressed.");
		}
	}
	
	public static void uncompress(boolean verbose, BinaryFile in, TextFile out) {
		/* Read the Magic Number */
		char magicNumberHigh = '0';
		char magicNumberLow = '0';
		if (!in.EndOfFile()) {
			magicNumberHigh = in.readChar();
		}
		if (!in.EndOfFile()) {
			magicNumberLow = in.readChar();
		}
		if (magicNumberHigh == 'H' && magicNumberLow == 'F') {
			/* Decode Huffman Tree */
			HuffmanNode root = new HuffmanNode();
			HuffmanTree.decode(root, in);
			/* Verbose output: Huffman Tree */
			if (verbose) {
				System.out.println("Huffman Tree:");
				HuffmanTree.print(root, 0);
			}
			/* Uncompress File */
			char tmp;
			while(!in.EndOfFile()) {
				tmp = HuffmanTree.decodeNext(root, in);
				out.writeChar(tmp);
			}
		}
		else {
			System.out.println("Format Error: Cannot uncompress file.");
		}
	}
	
	private static void ASCIIzero(int tmp) {
		if (tmp > 0) {
			while (tmp < 100) {
				tmp *= 10;
				System.out.print("0");
			}
		}
		else {
			System.out.print("00");
		}
	}

	public static void main(String[] args) {
		
		char mode = 'x';
		boolean verbose = false;
		boolean force = false;
		boolean isInfile = true;
		String infile = new String();
		String outfile = new String();
		for (String arg : args) {
			if (arg.startsWith("-")) {
				if (arg.equals("-c")) {
					mode = 'c';
				}
				if (arg.equals("-u")) {
					mode = 'u';
				}
				if (arg.equals("-v")) {
					verbose = true;
				}
				if (arg.equals("-f")) {
					force = true;
				}
			}
			else {
				if (isInfile) {
					infile = arg;
					isInfile = false;
				}
				else {
					outfile = arg;
				}
			}
		}
		if (mode == 'c') {
			TextFile in = new TextFile(infile, 'r');
			BinaryFile out = new BinaryFile(outfile, 'w');
			compress(true, true, in, out);
			in.close();
			out.close();
		}
		else if (mode == 'u') {
			BinaryFile in = new BinaryFile(infile, 'r');
			TextFile out = new TextFile(outfile, 'w');
			uncompress(true, in, out);
			in.close();
			out.close();
		}
		else {
			System.out.println("USAGE:");
			System.out.println("java Huffman (-c|-u) [-v] [-f]  infile outfile");
		}
		
		/*
		TextFile in = new TextFile("test", 'r');
		BinaryFile out = new BinaryFile("comp.huff", 'w');
		compress(true, true, in, out);
		in.close();
		out.close();
		*/
		
		/*
		BinaryFile in = new BinaryFile("comp.huff", 'r');
		TextFile out = new TextFile("result.txt", 'w');
		uncompress(true, in, out);
		in.close();
		out.close();
		*/
	}

}
