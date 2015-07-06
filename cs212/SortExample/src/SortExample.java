import java.util.*;

public class SortExample {

	public SortExample()
	{
		ArrayList<Widget> list = new ArrayList<Widget>();
		list.add(new Widget(10, "ant"));
		list.add(new Widget( 7, "hippopotamus"));
		list.add(new Widget(14, "dragonfly"));
		list.add(new Widget( 3, "camel"));
		
		// unsorted
		System.out.println("ArrayList, Unsorted:");
		printCollection(list);

		// sorted by name length
		System.out.println("\nArrayList, Sorted by ID:");
		Collections.sort(list);
		printCollection(list);		
		
		// sorted by name length
		System.out.println("\nArrayList, Sorted by Name Length:");
		Collections.sort(list, new WidgetComparator());
		printCollection(list);
		
		// sorted by name
		System.out.println("\nArrayList, Sorted by Name");
		Collections.sort(list, new Comparator<Widget>(){
			public int compare(Widget w1, Widget w2)
			{
				return String.CASE_INSENSITIVE_ORDER.compare(w1.name, w2.name);
			}
		});
		printCollection(list);
		
		// sorted by id
		System.out.println("\nTreeSet, Sorted by ID:");
		TreeSet<Widget> set1 = new TreeSet<Widget>();
		set1.addAll(list);	
		printCollection(set1);

		// sorted by name length
		System.out.println("\nTreeSet, Sorted by Name Length:");
		TreeSet<Widget> set2 = new TreeSet<Widget>(new WidgetComparator());
		set2.addAll(list);
		printCollection(set2);
	}
	
	// prints any collection type
	public void printCollection(Collection<Widget> c)
	{
		for(Widget w : c)
			System.out.printf("id: %2d name: %s\n", w.id, w.name);
	}
	
	public static void main(String[] args)
	{
		new SortExample();
	}
	
	private class Widget implements Comparable<Widget>
	{
		public int id;
		public String name;
		
		public Widget(int id, String name)
		{
			this.id = id;
			this.name = name;
		}
		
		// sort by id
		public int compareTo(Widget other) 
		{
			if(this.id < other.id)
				return -1;
			else if(this.id == other.id)
				return 0;
			else
				return 1;
		}
	}
	
	private class WidgetComparator implements Comparator<Widget>
	{
		// sort by name length
		public int compare(Widget w1, Widget w2) 
		{
			int length1 = w1.name.length();
			int length2 = w2.name.length();
			
			if(length1 < length2)
				return -1;
			else if(length1 == length2)
				return 0;
			else
				return 1;
		}	
	}
}
