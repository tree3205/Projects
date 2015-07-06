import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.LinkedList;

public class CollectionTester {

	// parses a line into words, and places words into different types of
	// data structures
	public static void parseLine(String line) {
		// example list
		ArrayList<String> wordList = new ArrayList<String>();
        LinkedList<String> wordsList = new LinkedList<String>();
		// example sets
		HashSet<String> wordHashSet = new HashSet<String>();
		TreeSet<String> wordTreeSet = new TreeSet<String>();

		// example maps
		HashMap<String, Integer> wordHashMap = new HashMap<String, Integer>();
		TreeMap<String, Integer> wordTreeMap = new TreeMap<String, Integer>();

		// example nested data structure
		HashMap<Integer, HashSet<String>> wordNestedHash = new HashMap<Integer, HashSet<String>>();

		// String[] words = line.split("\\s");	// split by spaces
		String[] words = line.split("\\W");		// split by non-word characters

		// loop through all of the words
		for (String word : words) {
			word = word.trim();
			word = word.toLowerCase();

			// don't try to enter empty string into data structures
			if (word.isEmpty()) {
				continue;
			}

			// add word to list and set data structures
			wordList.add(word);
			wordHashSet.add(word);
			wordTreeSet.add(word);

			wordsList.add(word);
			
			// try to get current count for word
			Integer count = wordHashMap.get(word);

			// if word does not exist
			if(count == null) {
				// add string with count of 1
				wordHashMap.put(word, new Integer(1));
				wordTreeMap.put(word, new Integer(1));
			}
			else {
				// example of autoboxing and unboxing
				count = count + 1;

				wordHashMap.put(word, count);
				wordTreeMap.put(word, count);
			}

			// get number of characters in word
			Integer numChars = new Integer(word.length());

			// test if must initialize inner maps
			if(wordNestedHash.containsKey(numChars) == false) {
				// add number of characters with empty set
				wordNestedHash.put(numChars, new HashSet<String>());
			}

			// add word to nested map (after making sure properly initialized)
			HashSet<String> nestedHashSet = wordNestedHash.get(numChars);
			nestedHashSet.add(word);
		}

		// print out number of items and string representation of each
		String format = "%-10s : %02d items : %s\n";
		System.out.printf("\n");
		System.out.printf(format, "ArrayList", wordList.size(), wordList);
		System.out.printf(format, "HashSet", wordHashSet.size(), wordHashSet);
		System.out.printf(format, "TreeSet", wordTreeSet.size(), wordTreeSet);
		System.out.printf(format, "HashMap", wordHashMap.size(), wordHashMap);
		System.out.printf(format, "TreeMap", wordTreeMap.size(), wordTreeMap);
		System.out.printf(format, "NestedHash", wordNestedHash.size(), wordNestedHash);
		System.out.printf(format, "LinkedList", wordsList.size(), wordsList);
	}

	public static void main(String[] args) {
		// demonstrates ordering clearly
		// arraylist will be in order entered
		// hashset has no ordering
		// treeset is sorted

		String test1 = "egg dill carrot banana banana apple";
		parseLine(test1);

		// demonstrates capitalization and symbols

		String test2 = "Hello, hello! How are you today? It is a bright-bright morning.";
		parseLine(test2);
	}
}
