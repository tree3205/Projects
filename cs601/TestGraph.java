package cs601.graph;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class TestGraph extends TestCase {
	String edges =
		"110 -> 112\n" +
		"112 -> 210\n" +
		"110 -> 220\n" +
		"112 -> 245\n" +
		"210 -> 315\n" +
		"245 -> 315\n" +
		"210 -> 326\n" +
		"220 -> 326\n" +
		"245 -> 326\n" +
		"112 -> 336\n" +
		"245 -> 336\n" +
		"245 -> 342\n" +
		"112 -> 345\n" +
		"245 -> 345\n" +
		"210 -> 414\n" +
		"245 -> 414\n" +
		"342 -> 490\n" +
		"498\n";

    public void testPrint() throws IOException {
        String g =
                "110 -> 112\n" +
                "112 -> 326\n" +
                "110 -> 212\n" +
                "print\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "Graph:\n" +
                "110 -> 112\n"+
                "110 -> 212\n"+
                "112 -> 326\n";
        assertEquals(expecting, found);
    }

	public void testLen() throws IOException {
		String g =
			"110 -> 112\n" +
			"112 -> 326\n" +
			"len 110 326\n";
		StringReader in = new StringReader(g);
		String found = exec(in);
		String expecting =
			"len 110 -> 326 = 2\n";
		assertEquals(expecting, found);
	}

	public void testNodes() throws IOException {
		String g = edges+"nodes 110 336\n";
		StringReader in = new StringReader(g);
		String found = exec(in);
		String expecting =
			"nodes 110 -> 336 = [110, 112, 245, 336]\n";
		assertEquals(expecting, found);
	}

	public void testReach() throws IOException {
		String g = edges+"reach 210\n";
		StringReader in = new StringReader(g);
		String found = exec(in);
		String expecting =
			"reach 210 = [210, 315, 326, 414]\n";
		assertEquals(expecting, found);
	}

	public void testRoots() throws IOException {
		String g = edges+"roots\n";
		StringReader in = new StringReader(g);
		String found = exec(in);
		String expecting =
			"roots = [110, 498]\n";
		assertEquals(expecting, found);
	}

    /** My test for root, leaf and intermediate node*/
    // Root
    public void testNode498() throws IOException {
        String g = edges + "roots\n" + "len 498 498\n" + "len 498 490\n" + "nodes 498 326\n" + "reach 498\n" + "print\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "roots = [110, 498]\n" +
                "len 498 -> 498 = 0\n" +
                "len 498 -> 490 = -1\n" +
                "nodes 498 -> 326 = []\n" +
                "reach 498 = [498]\n" +
                "Graph:\n" +
                "110 -> 112\n" +
                "110 -> 220\n" +
                "112 -> 210\n" +
                "112 -> 245\n" +
                "112 -> 336\n" +
                "112 -> 345\n" +
                "210 -> 315\n" +
                "210 -> 326\n" +
                "210 -> 414\n" +
                "220 -> 326\n" +
                "245 -> 315\n" +
                "245 -> 326\n" +
                "245 -> 336\n" +
                "245 -> 342\n" +
                "245 -> 345\n" +
                "245 -> 414\n" +
                "342 -> 490\n" +
                "498\n";
        assertEquals(expecting, found);
    }

    // Leaf one
    public void testNode490() throws IOException{
        String g = edges   + "len 112 490\n" + "len 490 112\n" + "nodes 110 490\n" + "nodes 490 110\n" + "reach 490\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 112 -> 490 = 3\n" +
                "len 490 -> 112 = -1\n" +
                "nodes 110 -> 490 = [110, 112, 245, 342, 490]\n" +
                "nodes 490 -> 110 = []\n" +
                "reach 490 = [490]\n"
                ;
        assertEquals(expecting, found);
    }

    // Leaf two
    public void testNode326() throws IOException {
        String g = edges  + "len 110 326\n" + "len 326 110\n"  + "nodes 110 326\n" + "nodes 326 110\n" + "reach 326\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 110 -> 326 = 2\n" +
                "len 326 -> 110 = -1\n" +
                "nodes 110 -> 326 = [110, 112, 210, 220, 245, 326]\n" +
                "nodes 326 -> 110 = []\n" +
                "reach 326 = [326]\n"
                ;
        assertEquals(expecting, found);
    }

    // Intermediate node
    public void testNode245() throws IOException {
        String g = edges  + "len 110 245\n" + "len 245 110\n"  + "nodes 110 245\n" + "nodes 245 110\n" + "reach 245\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 110 -> 245 = 2\n" +
                "len 245 -> 110 = -1\n" +
                "nodes 110 -> 245 = [110, 112, 245]\n" +
                "nodes 245 -> 110 = []\n" +
                "reach 245 = [245, 315, 326, 336, 342, 345, 414, 490]\n"
                ;
        assertEquals(expecting, found);

    }


    // My tests：Lance
    public void testLenFW1() throws IOException {
        String g = edges + "len 112 112\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 112 -> 112 = 0\n";
        assertEquals(expecting, found);
    }

    public void testLenFW2() throws IOException {
        String g = edges + "len 110 112\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 110 -> 112 = 1\n";
        assertEquals(expecting, found);
    }

    public void testLenFW3() throws IOException {
        String g = edges + "len 110 345\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 110 -> 345 = 2\n";
        assertEquals(expecting, found);
    }

    public void testLenFW4() throws IOException {
        String g = edges + "len 110 490\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 110 -> 490 = 4\n";
        assertEquals(expecting, found);
    }

    public void testNodesFW1() throws IOException {
        String g = edges + "nodes 110 490\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "nodes 110 -> 490 = [110, 112, 245, 342, 490]\n";
        assertEquals(expecting, found);
    }

    public void testNodesFW2() throws IOException {
        String g = edges + "nodes 110 345\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "nodes 110 -> 345 = [110, 112, 245, 345]\n";
        assertEquals(expecting, found);
    }

    public void testReachFW1() throws IOException {
        String g = edges + "reach 110\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "reach 110 = [110, 112, 210, 220, 245, 315, 326, 336, 342, 345, 414, 490]\n";
        assertEquals(expecting, found);
    }

    public void testReachFW2() throws IOException {
        String g = edges + "reach 326\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "reach 326 = [326]\n";
        assertEquals(expecting, found);
    }

    public void testLenNullFW1() throws IOException {
        String g = edges + "len 110 1000\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 110 -> 1000 = -1\n";
        assertEquals(expecting, found);
    }

    public void testLenNullFW2() throws IOException {
        String g = edges + "len 100 220\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 100 -> 220 = -1\n";
        assertEquals(expecting, found);
    }

    public void testLenNullFW3() throws IOException {
        String g = edges + "len 100 200\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 100 -> 200 = -1\n";
        assertEquals(expecting, found);
    }

    public void testLenNullFW4() throws IOException {
        String g = edges + "len 110 498\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "len 110 -> 498 = -1\n";
        assertEquals(expecting, found);
    }

    public void testPrintFW2() throws IOException {
        String g =
                "110 -> abc\n" +
                        "abc -> 326\n" +
                        "110 -> 212\n" +
                        "print\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "Graph:\n" +
                        "110 -> abc\n"+
                        "110 -> 212\n"+
                        "abc -> 326\n";
        assertEquals(expecting, found);
    }

    public void testNodesNullFW1() throws IOException {
        String g = edges+"nodes 110 1000\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "nodes 110 -> 1000 = []\n";
        assertEquals(expecting, found);
    }

    public void testReachNullFW1() throws IOException {
        String g = edges+"reach 1000\n";
        StringReader in = new StringReader(g);
        String found = exec(in);
        String expecting =
                "reach 1000 = []\n";
        assertEquals(expecting, found);
    }


    public static String exec(Reader in) throws IOException {
		Lexer lex = new Lexer(in);
		Parser parser = new Parser(lex);
		String output = parser.prog();
		return output;
	}
}
