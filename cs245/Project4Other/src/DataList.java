
public class DataList {
	
	// Private Data Members -- DataList  
	private Link head;
	private Link tail;
	private int length;
	
	//Constructor -- DataList                            
	DataList() 
	{
		head = tail = new Link();
		length = 0;
	}
	
	// Public Methods -- DataList                         
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

	public void add(String stringValue, Integer integerValue) 
	{
		tail.setNext(new Link(stringValue, integerValue, null));
		tail = tail.next();
		length++;
	}

	public Object getInt(int index)
	{
		Link tmp = head.next;
		for (int i = 0; i < index; i++)
		{
			tmp = tmp.next;
		}		
		return tmp.intValue;
	}
	
	public Object getString(int index)
	{
		Link tmp = head.next;
		for (int i = 0; i < index; i++)
		{
			tmp = tmp.next;
		}
		return tmp.strValue;
	}
            
	private class Link {
		
		// Private Data Members -- Link                      
		private String strValue;
		private Integer intValue;
		private Link next;
		
		//  Constructors -- Link                             
		Link(String stringValue,Integer integerValue, Link nextelem) {
			strValue = stringValue;
			intValue = integerValue;
			next = nextelem;
		}

		Link(Link nextelem) {
			next = nextelem;
		}

		Link() { }
	
		//  Access Methods -- Link                             
		Link next() {
			return next; 
		}

		String strValue() {
			return strValue;
		}
		
		Integer intValue() {
			return intValue;
		}

		void setNext(Link nextelem) {
			next = nextelem;
		}

		void setString(String stringValue) {
			strValue = stringValue;
		}
		
		void setInteger(Integer integerValue) {
			intValue = integerValue;
		}
	}
	
	public static void main(String[] args) {
		DataList l = new DataList();
		System.out.println(l.size() == 0);
		
		for (int i = 0; i < l.size(); i++) {
			System.out.println(l.getString(i) + "/" + l.getInt(i));
		}
		
		l.add("LA", 2);
		l.add("DNV", 3);
		l.add("NY", 7);
		for (int i = 0; i < l.size(); i++) {
			System.out.println(i + ": " + l.getString(i) + "/" + l.getInt(i));
			
		}
		
	}

}
