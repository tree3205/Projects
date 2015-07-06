import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class HashSetFindExample
{
	public static void main(String[] args)
	{
		HashSet<String> animals = new HashSet<String>();

		Collections.addAll(animals, new String[] {
			"alligator",
			"ant",
			"ape",
			"bat",
			"bear",
			"bison",
			"cat",
			"camel",
			"deer",
			"dog",
			"dragonfly"});
		
		

		// Collections operates on List, must convert
		ArrayList<String> sorted = new ArrayList<String>(animals);

		// sort once
		Collections.sort(sorted);

		String[] letters = { "a", "b", "c", "d", "e" };
		int iterations = 0;

		for(String letter : letters)
		{
			System.out.println("Animals that start with \"" + letter + "\":");

			// find starting point
			int start = Collections.binarySearch(sorted, letter);

			// if negative, convert to insertion point
			if(start < 0)
				start = -(start + 1);

			for(int i = start; i < sorted.size(); i++)
			{
				if(!sorted.get(i).startsWith(letter))
					break;

				iterations++;
				System.out.println(sorted.get(i));
			}

			System.out.println();
		}

		System.out.println("Finished! Used " + iterations + " iterations for " + animals.size() + " objects.");
	}
}

