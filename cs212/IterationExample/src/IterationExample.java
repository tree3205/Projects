import java.util.*;

/*
 * This class demonstrates several approaches (some that work, some that 
 * don't) to iterating through a nested data structure.
 * 
 * The level of nesting is usually a clue to how many nested loops you
 * will need to iterate through all of the values.
 * 
 * Read and understand the example code before looking at the 
 * discussion!
 */
public class IterationExample {
	private TreeMap<Integer, LinkedList<String>> results;

	/*
	 * Default constructor. Initializes the nested data structure.
	 */
	public IterationExample() 	{
        results = new TreeMap<Integer, LinkedList<String>>();
	}
	
	/*
	 * Simple parsing of strings into words. This is not the focus of
	 * this code example.
	 */
	public void addLine(String line) {
		String[] words = line.split("\\W+");
		addWords(words);
	}

	/*
	 * Standard approach to adding values to nested data structure. This
	 * is not the focus of this code example.
	 */
	public void addWords(String[] words) {
		for(String word : words) {
			Integer key = Integer.valueOf(word.length());
			
			
			if(!results.containsKey(key))
				results.put(key, new LinkedList<String>());
			
			results.get(key).add(word);		
			
		}
		System.out.println(results);
	}
	
	/*
	 * This loads a good example for demonstration purposes.
	 */
	public void loadAnimals() {
		addLine("alligator ant ape");
		addLine("bat bear bison");
		addLine("cat camel");
		addLine("deer dog dragonfly");
		addLine("hippopotamus");
		
		/* Expected Output:
		 * 
		 * 3 = ant ape bat cat dog
		 * 4 = bear deer 
		 * 5 = bison camel 
		 * 9 = alligator dragonfly 
		 * 12 = hippopotamus 
		 */
	}
	
	/*
	 * This is an incorrect and inefficient example.
	 */
	public void incorrectExample() {
		System.out.println("\nIncorrect Example");
		
		for(int i = 1; i <= results.size(); i++) {
			if(results.containsKey(Integer.valueOf(i))) {
				System.out.print(i + " = ");
				
				LinkedList<String> list = results.get(Integer.valueOf(i));
				String[] words = list.toArray(new String[0]);
				
				for(int j = 0; j < words.length; j++)
					System.out.print(words[j] + " ");
				
				System.out.print("\n");
			}
		}
		
		/* [ DISCUSSION ]
		 * 
		 * Incorrect:
		 * Many of you tried to use a "traditional" looking for loop,
		 * but this approach does not produce the correct output. It 
		 * incorrectly assumes that the last key value is less than the
		 * size of the map. In the demo, the last key value is 12, but
		 * the size of the map is 5.
		 * 
		 * Inefficient:
		 * The conversion of the list into an array is "unnecessarily 
		 * inefficient". It takes extra space AND time that can be
		 * easily avoided. 
		 */
	}
	
	/*
	 * This is an "unnecessarily inefficient" example.
	 */
	public void inefficientExample()
	{
		System.out.println("\nInefficient Example");
		
		int i = 1;
		int count = 0;
		
		while(count < results.size()) {
			if(results.containsKey(Integer.valueOf(i)))	{
				count++;
				System.out.print(i + " = ");
				
				for(int j = 0; j < results.get(Integer.valueOf(i)).size(); j++)
					System.out.print(results.get(Integer.valueOf(i)).get(j) + " ");
				
				System.out.print("\n");
			}
			
			i++;
		}
		
		/* [ DISCUSSION ]
		 * 
		 * Correct:
		 * Unlike the previous example, this does produce the correct
		 * output. Instead of looping until a key larger than the size
		 * is reached, it loops until results.size() keys have been
		 * found.
		 * 
		 * Inefficient:
		 * This is unnecessarily inefficient. What happens if there are
		 * only two keys... 2 and 100. We loop through 100 times to get
		 * two values out of our data structure!
		 * 
		 * And a minor note... there are two calls to results.get(key). 
		 * You can save this reference for reuse later (see the 
		 * previous example) and improve the readability of your code.
		 */
	}
	
	/*
	 * This is a correct example that avoids being unnecessarily
	 * inefficient, although the use of iterators is "clunky". The
	 * next examples show how to improve on this.
	 */
	public void iteratorExample() {
		System.out.println("\nIterator Example");
		
		Set<Integer> keys = results.keySet();
		Iterator<Integer> setIterator = keys.iterator();
		
		while(setIterator.hasNext()) {
			Integer key = setIterator.next();
			Iterator<String> listIterator = results.get(key).iterator();
			
			System.out.print(key + " = ");
						
			while(listIterator.hasNext())
				System.out.print(listIterator.next() + " ");
			
			System.out.print("\n");
		}
		
		/* [ DISCUSSION ]
		 * 
		 * By using iterators, we never loop more times than necessary.
		 * With a sorted data structure, the iterators will go through
		 * the collection in sorted order. 
		 * 
		 * However, the use of iterators for such a simple case may be
		 * considered unnecessarily "clunky" since there are other
		 * approaches that involve less code and objects.
		 */
	}
	
	/*
	 * This example demonstrates how to use the methods specific to a
	 * sorted map, like TreeMap.
	 */
	public void treemapExample() {
		System.out.println("\nSorted Map Example");

		for(Integer i = results.firstKey(); i != null; i = results.higherKey(i)) {
			System.out.print(i + " = ");
			LinkedList<String> words = results.get(i);
			
			
			for(int j = 0; j < words.size(); j++)
				System.out.print(words.get(j) + " ");
			
			System.out.print("\n");
			System.out.println(words);
		}
		
		/* [ DISCUSSION ]
		 * 
		 * We can take advantage of the firstKey(), lastKey(), 
		 * higherKey(), and lowerKey() methods to explicitly iterate
		 * through the map in sorted order. We can even use these to
		 * iterate through the keys in reverse.
		 * 
		 * Note that we must check whether ( i != null ) and not 
		 * whether ( i <= results.lastKey() ). Can you figure out why?
		 */
	}
	
	/*
	 * This example demonstrates a basic nested for each loop. 
	 */
	public void foreachExample() {
		System.out.println("\nFor Each Example");
		
		for(Integer key : results.keySet()) 	{
			System.out.print(key + " = ");
			
			for(String word : results.get(key))
				System.out.print(word + " ");
			
			System.out.print("\n");
		}
		
		/* [ DISCUSSION ]
		 * 
		 * Again, for a sorted map, we will iterate through the keys in
		 * sorted order. This results in much cleaner code that avoids
		 * the "unnecessarily inefficient" and "clunky" problem of
		 * previous approaches. However, for each loop may not always be
		 * the appropriate approach for your problem.
		 * 
		 * You can use the for each loop on just about any type of
		 * collection, including arrays.
		 */
	}
	
	/*
	 * This example demonstrates another approach (which may be slightly
	 * more efficient) to using a for each loop. 
	 */
	public void mapentryExample() {
		System.out.println("\nMap Entry Example");
		
		for(Map.Entry<Integer, LinkedList<String>> entry : results.entrySet()) {
			System.out.print(entry.getKey() + " = ");
			
			for(String word : entry.getValue())
				System.out.print(word + " ");
			
			System.out.print("\n");
		}
		
		/* [ DISCUSSION ]
		 * 
		 * This approach grabs both the key and value at the same time,
		 * avoiding multiple accesses to the nested data structure. 
		 */
	}

	/*
	 * Shows the output of the examples above.
	 */
	public static void main(String[] args)
	{
		IterationExample example = new IterationExample();
		example.loadAnimals();
		
		example.incorrectExample();
		example.inefficientExample();
		
		example.iteratorExample();
		example.treemapExample();
		
		example.foreachExample();
		example.mapentryExample();
	}
}
