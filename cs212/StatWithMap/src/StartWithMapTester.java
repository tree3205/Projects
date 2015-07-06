import java.util.TreeMap;
import java.util.TreeSet;


public class StartWithMapTester {

	public static void main(String[] args) {

		String[] words = {"ant", "ape", "bee", "bAt", "CAT", "cow", "dog",
				"elk", "eel" };

		StartWithMap map = new StartWithMap();
		map.addWords(words);

		System.out.println(map);

		TreeMap<Character, TreeSet<String>> mapReference = map.getMap();
		mapReference.remove(Character.valueOf('e'));

		System.out.println(map);

		TreeSet<String> setReference = map.getWords('d');
		setReference.clear();

		System.out.println(map);
	}
}
