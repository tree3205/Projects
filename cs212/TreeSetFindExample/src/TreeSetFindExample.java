import java.util.*;

public class TreeSetFindExample 
{
	public static void main(String[] args)
	{
		TreeSet<String> animals = new TreeSet<String>();
		
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

		String[] letters = { "a", "b", "c", "d", "e" };
		int iterations = 0;
		
		for(String letter : letters)
		{
			System.out.println("Animals that start with \"" + letter + "\":");
		
			for(String cur = animals.ceiling(letter); cur != null; cur = animals.higher(cur))
			{
				if(!cur.startsWith(letter))
					break;
				
				iterations++;
				System.out.println(cur);
			}
			
			System.out.println();
		}
		
		System.out.println("Finished! Used " + iterations + " iterations for " + animals.size() + " objects.");
	}
}

