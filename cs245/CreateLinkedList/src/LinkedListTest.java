import java.util.Iterator;
import java.util.ListIterator;


public class LinkedListTest 
{

	public static void main(String[] args) 
	{
		LinkedList list = new LinkedList();
		//test add(object value) method
		System.out.println("test add(object value) method:");
		list.add(1);
		list.add(2);
		list.add(3);
		list.add("a");
		list.add("b");
		list.add("c");
		list.printList();
		System.out.println();
		System.out.println(list.size());
		
		
		
		//test add(int index, object value) method
		System.out.println();
		System.out.println("test add(int index, object value) method:");
		list.add(2,6);
		list.printList();
		System.out.println();
		
		list.add(4,6);
		list.printList();
		System.out.println();
		
		list.add(1,0);
		list.printList();
		System.out.println();
		
		list.add(0,3);
		list.printList();
		System.out.println();
		
		list.add(3,9);
		list.printList();
		System.out.println();
		System.out.println(list.size());
		System.out.println();
		
		list.add(0,1);
		list.add(0,3);
		list.add(0,6);
		list.printList();
		System.out.println();
		
		//test remove(int index) method
		System.out.println();
		System.out.println("test remove(int index) method");
		list.remove(9);
		list.printList();
		System.out.println();
		
		list.remove(0);
		list.printList();
		System.out.println();
		
		list.remove(4);
		list.printList();
		System.out.println();
		System.out.println();
		
		//test remove(Object value)
		System.out.println();
		System.out.println("test remove(Object value):");
		list.remove(new Integer(3));
		list.printList();
		System.out.println();
		
		Object b = new Integer(0);
		list.remove(b);
		list.printList();
		System.out.println();
		
		list.remove("a");
		list.printList();
		System.out.println();
		System.out.println(list.size());
		System.out.println();
		
		//test Object get(int index)
		System.out.println();
		System.out.println("test Object get(int index):");
		list.printList();
		System.out.println();
		System.out.println(list.get(0));
		System.out.println(list.get(1));
		System.out.println(list.get(2));
		System.out.println(list.get(3));
		
		System.out.println();
		
	
		
		
		//test clear() method
		System.out.println();
		System.out.println("test clear() method:");
		list.clear();
		list.printList();
		System.out.println();
		System.out.println(list.size());
		
		
	}
}
