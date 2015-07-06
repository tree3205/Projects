import java.util.Iterator;


	public class LinkedList 
	{
	    public Object value;
	  
	    public LinkedList nextLink;

	    //Link constructor
	    public LinkedList(Object value) 
	    {
		    this.value = value;
	    }

	    //Print Link data
	    public void printLink() 
	    {
		    System.out.print("{" + value + "} ");
	    }
	}

	class LinkList 
	{
	    private LinkedList first;

	    //LinkList constructor
	    public LinkList() 
	    {
		    first = null;
	    }

	    //Returns true if list is empty
	    public boolean isEmpty() 
	    {
		    return first == null;
	    }

	    //Inserts a new Link at the first of the list
	    public void insert(Object value) 
	    {
		    LinkedList link = new LinkedList(value);
		    link.nextLink = first;
		    first = link;
	    }

	    //Deletes the link at the first of the list
	    public LinkedList delete() 
	    {
		    LinkedList temp = first;
		    first = first.nextLink;
		    return temp;
	    }
	    
	    //Prints list data
	    public void printList() 
	    {
		    LinkedList currentLink = first;
		    System.out.print("List: ");
		    while(currentLink != null) 
		    {
			    currentLink.printLink();
			    currentLink = currentLink.nextLink;
		    }
		    System.out.println("");
	    }
	}  

	class LinkListTest 
	{
		
		
	    public static void main(String[] args) 
	    {
		    LinkList list = new LinkList();
		    LinkList l = new LinkList();
		    
		    list.insert(9);
		    list.insert(2);
		    list.insert(7);
		    list.insert(4);
		    list.insert(5);

		    list.printList();

		   
		    
		    while(!list.isEmpty()) 
		    {
			    LinkedList deletedLink = list.delete();
			    System.out.print("deleted: ");
			    deletedLink.printLink();
			    list.printList();
			    System.out.println("");
		    }
		    list.printList();
	    }
}
