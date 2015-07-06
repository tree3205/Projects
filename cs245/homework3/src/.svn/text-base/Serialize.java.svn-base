import java.io.*;

class Serialize {
    
    
    public static void print(GenNode tree, int offset) {
	if (tree != null) {
	    for (int i=0; i<offset; i++)
		System.out.print("   ");
	    System.out.println(tree.element());
	    print(tree.leftchild(),offset+1);
	    print(tree.rightsib(),offset);
	}
    }

    /* Print out the tree to standard out, using System.out.print,
       using ')' as the end-of-child marker */
    public static void serialize(GenNode tree) {
    	
    	
    	/*if (tree != null) {
    		System.out.print(tree.element());
    		
    		if ((tree.leftchild() != null) && (tree.rightsib() == null) ) {
    			serialize(tree.leftchild());
    			System.out.print(')');
    		}
    		if ((tree.leftchild() == null) && (tree.rightsib() != null)) {
    			serialize(tree.rightsib());
    			System.out.print(')');
    		}
    		if ((tree.leftchild() != null) && (tree.rightsib() != null)) {
    			serialize(tree.leftchild());
    			serialize(tree.rightsib());
    			System.out.print(')');
    			
    		}
    		if ((tree.leftchild() == null) && (tree.rightsib() == null)) {
    			System.out.print(')');
    		}
    		
    	}*/
    	if (tree != null) {
    		System.out.print(tree.element());
    		serialize(tree.leftchild());
    		System.out.print(')');
    		serialize(tree.rightsib());
    			    			
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
	System.out.println("Tree1:");
	print(tree1,0);
	serialize(tree1);
	System.out.println();
	System.out.println("Tree2:");
	print(tree2,0);
	serialize(tree2);
	System.out.println();
	System.out.println("Tree3:");
	print(tree3,0);
	serialize(tree3);
	System.out.println();
    }

}