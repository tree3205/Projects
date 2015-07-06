import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Demonstrates the danger of passing references to private data.
 */

public class StartWithMap {

	// stores strings by letter they start with
	// key: letter, value: set of strings starting with that letter
	private TreeMap<Character, TreeSet<String>> internal;

	public StartWithMap() {
		// only initializes OUTER collection
		internal = new TreeMap<Character, TreeSet<String>>();
	}

	// demonstrates how to add something to internal collection
	public void addWord(String word) {
		// convert word to lowercase and trim off spaces
		String value = word.toLowerCase().trim();

		// make sure we still have a valid word
		if(value.length() > 0) {
			// get key as a Character object
			Character key = new Character(value.charAt(0));

			// initialize INNER collection if necessary
			if(!internal.containsKey(key)) {
				internal.put(key, new TreeSet<String>());
			}

			// add current word to map
			internal.get(key).add(value);
		}
	}

	// convenience method to add multiple words at once
	public void addWords(String[] words) {
		for(String word : words) {
			addWord(word);
		}
	}

	// returns whether key exists in internal data structure
	public boolean hasKey(char letter) {
		return internal.containsKey(Character.valueOf(letter));
	}

	// converts entire internal data structure into a string representation
	public String toString() {
		StringBuffer output = new StringBuffer();

		for(Map.Entry<Character, TreeSet<String>> entry : internal.entrySet()) {
			System.out.print(entry.getKey() + ": ");

			for(String word : entry.getValue()) {
				System.out.print(word + " ");
			}

			System.out.println();
		}

		return output.toString();
	}

	// unsafe method returning reference to internal data structure
	public TreeMap<Character, TreeSet<String>> getMap() {
		return internal;
	}

	// unsafe method returning reference to internal nested data structure
	public TreeSet<String> getWords(char letter) {
		Character key =Character.toLowerCase(letter);
		return internal.get(key);
	}
}
