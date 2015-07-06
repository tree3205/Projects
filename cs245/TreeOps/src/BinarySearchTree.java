class BinarySearchTree<T extends Comparable <T>> {

   private class BSTNode {
       public T data;
       public BSTNode left;
       public BSTNode right;

       BSTNode(T newdata) 
       {
          data = newdata;
       }
   }

   private BSTNode root;

   public void Insert(T elem) 
   {
      root = Insert(root, elem);
   }

   public boolean Find(T elem) 
   {
      return Find(root,elem);
   }

   public void Delete(T elem) 
   {
      root = Delete(root, elem);
   }

   public T ElementAt(int i) 
   {
        /* Fill me in!! */
	return null;
   }
   
   public int ElemOfLeft()
   {
	   return ElemOfLeft(root);
   }

    private int ElemOfLeft(BSTNode tree) 
    {
    	int tmp;
    	
    	if (tree == null) 
    	{
    		return 0;
    	}
    	
    	if (tree.left == null && tree.right == null)
    	{
    		return 1;
    	}
    	else
    	{
    		if (tree.left != null)
        	{
        		tmp = 1;
        	}
        	else
        	{
        		tmp = 0;
        	}
    		
    		if (tree.left != null && tree.right != null)
    		{
    			tmp += ElemOfLeft(tree.left);
    		}
    	}
    
    	return tmp;
    }

	public int NumBetween(T low, T high) 
   {
       /* Fill me in!! */
	return -1;
   }

   public void Balance()
   {
        /* Fill me in! */
   }

   public void Print() {
       Print(root);
   }

   public int Height()
   {
       return Height(root);
   }
   

   private int Height(BSTNode tree)
   {
      if (tree == null)
      {
         return 0;
      }
      return 1 + Math.max(Height(tree.left), Height(tree.right));
   }

   private boolean Find(BSTNode tree, T elem) 
   {
      if (tree == null)
      {
         return false;
      }
      if (elem.compareTo(tree.data) == 0)
      { 
         return true;
      }
      if (elem.compareTo(tree.data) < 0)
      {
         return Find(tree.left, elem);
      }
      else
      {
         return Find(tree.right, elem);
      }
   }

   private T Minimum(BSTNode tree) 
   {
      if (tree == null)
      {
         return null;
      }
      if (tree.left == null)
      {
         return tree.data;
      }
      else
      {
         return Minimum(tree.left);
      }
   }

   private void Print(BSTNode tree) 
   {
      if (tree != null) 
      {
         Print(tree.left);
         System.out.println(tree.data);
         Print(tree.right);
      }
   }

   private BSTNode Insert(BSTNode tree, T elem) 
   {
	if (tree == null) 
        {
	    return new BSTNode(elem);
	}
	if (elem.compareTo(tree.data) < 0) 
        {
	    tree.left = Insert(tree.left, elem);
	    return tree;
	} 
        else  
        {
	    tree.right = Insert(tree.right, elem);
	    return tree;
	}
    }


   private BSTNode Delete(BSTNode tree, T elem) 
   {
      if (tree == null) 
      {
         return null;
      }
      if (tree.data.compareTo(elem) == 0) 
      {
         if (tree.left == null)
         {
	    return tree.right;
         }
         else if (tree.right == null)
         {
	   return tree.left;
         }
         else 
         {
	    if (tree.right.left == null) 
            {
               tree.data = tree.right.data;
               tree.right = tree.right.right;
               return tree;
            } 
            else 
            {         
               tree.data = RemoveSmallest(tree.right);
               return tree;
            }
         }
      } 
      else  if (elem.compareTo(tree.data) < 0) 
      {
         tree.left = Delete(tree.left, elem);
         return tree;
      } 
      else 
      {
         tree.right = Delete(tree.right, elem);
         return tree;
     }
   }  
 
   T RemoveSmallest(BSTNode tree) 
   {
      if (tree.left.left == null) 
      {
         T smallest = tree.left.data;
         tree.left = tree.left.right;
         return smallest;
      } 
      else 
      {
         return RemoveSmallest(tree.left);
      }
   }
    public static void main(String args[])

    {
	BinarySearchTree<Integer> t= new BinarySearchTree<Integer>();
	for (int x = 0; x < 31; x++)
	    t.Insert(new Integer(x));
	/*System.out.println(t.ElementAt(new Integer(5)));
	System.out.println(t.NumBetween(new Integer(10), new Integer(15)));
	System.out.println(t.Height());
	t.Balance();
	System.out.println(t.ElementAt(new Integer(5)));
	System.out.println(t.NumBetween(new Integer(10), new Integer(15)));
	System.out.println(t.Height());*/
	
	System.out.println(t.ElemOfLeft());
    }

}