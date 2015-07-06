import java.io.*;

class GenTreeOps {
    
    
    public static int MAX(int x, int y) {
	if (x > y)
	    return x;
	else
	    return y;
    }
    
    public static int height(GenNode tree) {
	if (tree == null)
	    return 0;
	return  MAX(1 + height(tree.leftchild()),
		       height(tree.rightsib()));
    }
    
        
    public static int numNodes(GenNode tree) {
	if (tree == null)
	    return 0;
	return 1 + numNodes(tree.leftchild()) + 
	    numNodes(tree.rightsib());
    }


    /* Input:  A general tree
       Return value: The number of leaves in the tree */
    public static int numLeaves(GenNode tree) {
    	if (tree == null) {
    		return 0;
    	}
    	if (tree.leftchild() == null) {
    		return 1;
    	}
    	int leaves = 0;
	    for (GenNode tmp = tree.leftchild(); tmp != null; tmp = tree.rightsib()) {
	    	leaves += numLeaves(tmp);
	    }
	    return leaves;
    }


    /* Input:  A general tree, and an offset 
                (offset initially 0)
       Output: The contents of the tree, printed to
               standard out */
    public static void print(GenNode tree, int offset) {
    	if (tree != null) {
    		for(int i = 0; i < offset; i++) {
    			System.out.println("\t");
    		}
    		System.out.println(tree.element().toString());
    		print(tree.leftchild(), offset + 1);
    		print(tree.rightsib(), offset + 1);
    		
    	}	
    }



    public static void main(String args[]) throws IOException {
	GenNode tree1, tree2, tree3;
	
	tree1 = new GenNode(new Integer(1),
			    new GenNode(new Integer(2),
					null,
					new GenNode(new Integer(3),
					      new GenNode(new Integer(5),
							  null,
							  new GenNode(new Integer(6),
								      null,
								      new GenNode(new Integer(7),
									       null,
									       null))),
						    new GenNode(new Integer(4),
								null,
								null))),
			    null);
	


	tree2 = new GenNode(new Integer(1),
			 new GenNode(new Integer(2),
				  new GenNode(new Integer(3),
					   new GenNode(new Integer(4),
						    new GenNode(new Integer(5),
							     null,
							     null),
						    null),
					   null),
				  null),
			 null);

	tree3 = new GenNode(new Integer(1),
			    new GenNode(new Integer(2),
					new GenNode(new Integer(5),
						    null,
						    new GenNode(new Integer(6),
								null,
						       null)),
					new GenNode(new Integer(3),
						    null,
						    new GenNode(new Integer(4),
								new GenNode(new Integer(7),
									    null,
									    new GenNode(new Integer(8),
											null,
											new GenNode(new Integer(9),
												    null,
												    null))),
								null))),
			    null);
	System.out.println("Height of tree 1 = " + height(tree1));
	System.out.println("Height of tree 2 = " + height(tree2));
	System.out.println("Height of tree 3 = " + height(tree3));
	System.out.println("numNodes of tree 1 = " + numNodes(tree1));
	System.out.println("numNodes of tree 2 = " + numNodes(tree2));
	System.out.println("numNodes of tree 3 = " + numNodes(tree3));
	System.out.println("numLeaves of tree 1 = " + numLeaves(tree1));
	System.out.println("numLeaves of tree 2 = " + numLeaves(tree2));
	System.out.println("numLeaves of tree 3 = " + numLeaves(tree3));

	System.out.println("tree 1:");
	print(tree1, 0);
	System.out.println("tree 2:");
	print(tree2, 0);
	System.out.println("tree 3:");
	print(tree3, 0);

    }

}