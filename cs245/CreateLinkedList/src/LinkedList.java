
public class LinkedList 
{
	public Object value;
	private Link head;
	private Link tail;
	private int length;
	
	public LinkedList()
	{
		head = tail = new Link();
		length = 0;
		
	}
	
	public LinkedList(Object value)
	{
		this.value = value;
	}

	public void printList() 
    {
	    Link currentLink = head;
	    System.out.print("List: ");
	    while(currentLink != null) 
	    {
		    currentLink.printLink();
		    currentLink = currentLink.next;
	    }
	   // System.out.println("");
    }
	
	public void clear() 
	{
		head.setNext(null);
		tail = head;
		length = 0;
	}

	public int size() 
	{
		return length;
	}

	public void add(Object value) 
	{
		tail.setNext(new Link(value));
		tail = tail.next();
		length++;
		    
	}

	public void add(int index, Object value) 
	{
		//Assert.notFalse(index >= 0 && index <= length,"Index not in list");
		Link tmp = head;
		for (int i = 0; i < index; i++)
		{
			tmp = tmp.next;
		}
		tmp.next = new Link(value, tmp.next);
		length++;
	}

	public void remove(int index)
	{
		//Assert.notFalse(index >= 0 && index < length);
		Link tmp = head;
		for (int  i = 0; i < index; i++)
		{
			tmp = tmp.next;
		}
		tmp.next = tmp.next.next;
		length--;
	}

	public void remove(Object value)
	{
		Link tmp = head;
		while (tmp.next != null && !tmp.next.element.equals(value))
		{
			tmp = tmp.next;
		}
		if (tmp.next != null)
		{
			tmp.next = tmp.next.next;
			length--;
		}
	}

	public Object get(int index)
	{
		//Assert.notFalse(index >= 0 && index < length,"Index not in list");
		Link tmp = head.next;
		for (int i = 0; i < index; i++)
		{
				tmp = tmp.next;
			
		}
		return tmp.element;
		
	}
	
	private class Link 
	{
		
		private Object element;
		private Link next;
		
		Link(Object elem, Link nextelem) 
		{
			element = elem;
			next = nextelem;
		}
		
		Link(Object elem)
		{
			element = elem;
		}
		
		Link() { }
		
		Link next() 
		{
			return next; 
		}

		Object element() 
		{
			return element;
		}

		void setNext(Link nextelem) 
		{
			next = nextelem;
		}

		void setElement(Object elem) 
		{
			element = elem;
		}
		
	    public void printLink() 
	    {
		    System.out.print("{" + element + "} ");
	    }
	}
}

