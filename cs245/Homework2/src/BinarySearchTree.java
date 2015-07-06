
import java.util.ArrayList;

class BinarySearchTree<T extends Comparable <T>> 
{
      
   private class BSTNode 
   {
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

   public int ElementAt(int i) 
   {
       return ElementAt(root, i);
   }
   
	public int NumBetween(T low, T high) 
   {
		 return NumBetween(root, low, high);
   }
	
	public void Balance()
   {
		ArrayList<Integer> inOrder = new ArrayList<Integer>();
	   	ArrayList<Integer> array = new ArrayList<Integer>();
	   	inOrder = InOrder(array);
	   	root = null;
	   	createTree(inOrder, 0, inOrder.size() - 1);
   }

   public void Print() 
   {
       Print(root);
   }

   public ArrayList<Integer> InOrder(ArrayList<Integer> array) 
   {
	   return collect(root, array);
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
      
   private ArrayList<Integer> collect(BSTNode tree, ArrayList<Integer> array) 
   {   
	   if (tree.left != null)
	   {
		    collect(tree.left, array);
	   }
	   
	   array.add((Integer) tree.data);
	  
	   if (tree.right != null)
	   {
		   collect(tree.right, array);
	   }
	   
	   return array;
   }
   
   private int NodesNum(BSTNode tree) 
   {
	   if (tree == null) 
	  	{
	  		return 0;
	  	}
	  	if ((tree.left == null) && (tree.right == null))
	  		return 1;
	  	
	   return 1 + NodesNum(tree.left) + NodesNum(tree.right);
	      
   }
   
   private int ElementAt(BSTNode tree, int i)
	{	
	   int  currentIndex = NodesNum(tree.left);
	   if (  currentIndex == i)
	   {
		   return (Integer) tree.data;
	   }
	   else
	   {
		   return ElementAt(tree, currentIndex, i);
	   }
	  
   }
   
   private int ElementAt(BSTNode tree, int currentIndex, int i) 
   {
	   if ( currentIndex == i)
	   {
		   return (Integer) tree.data;
	   }
	   if (currentIndex < i)
	   {
		   return ElementAt(tree.right, currentIndex+1+NodesNum(tree.right.left), i);
	   }
	   if (currentIndex > i)
	   {
		   return ElementAt(tree.left, currentIndex-1-NodesNum(tree.left.right), i);
	   }
	
	   return -1;
   }

   private int NumBetween(BSTNode tree, T low, T high) 
   {
	
	    int tmp;
	    
		if ( tree == null)
		{
			return 0;
		}
		
		if (tree.left == null && tree.right == null)
		{
			return 1;
		}
		else
		{
			if (tree.data.compareTo(low) >= 0 && tree.data.compareTo(high) <= 0)
			{
				tmp = 1;
			}
			else
			{
				tmp = 0;
			}
			
			if ( tree.data.compareTo(low)  > 0 )
			{
				  tmp += NumBetween(tree.left, low, high);
			}
			
			if (tree.data.compareTo(high) < 0)
			{
				 tmp += NumBetween(tree.right, low, high);
			}
		}
				
		return tmp;
	}
   
   private void createTree(ArrayList<Integer> inOrder, int start, int end) 
   {
	   int mid = start + (end - start)/2;
	  
	   if (start > end)
	   {
		   return;
	   }
	   if (start == end)
	   {
		    Insert((T)inOrder.get(mid));
	   }
	   else
	   {
		   Insert((T)inOrder.get(mid));
		   createTree(inOrder, start, mid-1);
		   createTree(inOrder, mid+1, end);
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
    	System.out.println(t.ElementAt(new Integer(5)));
    	System.out.println(t.NumBetween(new Integer(10), new Integer(15)));
    	System.out.println(t.Height());
    	System.out.println();
    	t.Balance();
    	System.out.println(t.ElementAt(new Integer(5)));
    	System.out.println(t.NumBetween(new Integer(10), new Integer(15)));
    	System.out.println(t.Height());
    	
    	
    }

}