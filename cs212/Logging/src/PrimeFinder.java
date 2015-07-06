import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class PrimeFinder {

	/** Class-specific logger. */
	private static Logger log = Logger.getLogger(PrimeFinder.class);

	/**
	 * Uses the Sieve of Eratosthenes to find all primes less than or equal to
	 * the maximum number.
	 *
	 * @param max 	maximum number to consider
	 * @return collection of prime numbers
	 */
	public static TreeSet<Integer> findPrimes(int max) {
            
		log.debug("Finding all primes less than or equal to " + max + ".");

		TreeSet<Integer> primes = new TreeSet<Integer>();
		primes.add(Integer.valueOf(2));

		for (Integer i = 3; i <= max; i += 2) {
			primes.add(i);
		}

		int end = (int) Math.ceil(Math.sqrt(max));

		log.debug("Starting with numbers: " + primes);
		log.debug("Removing multiples up to " + end + ".");

		for (Integer i = 3; i != null && i <= end; i = primes.higher(i)) {
			removeMultiples(i, primes);
		}

		log.debug("Found " + primes.size() + " prime numbers.");

		return primes;
	}

	/**
	 * Removes all multiples from the numbers set starting at the square of the
	 * multiple. For example, if we want to remove all multiples of 5, it will
	 * start at 5 * 5 = 25 and remove all remaining multiples.
	 *
	 * @param multiple 	number to remove multiples of
	 * @param numbers 	collection of numbers to remove from
	 */
	private static void removeMultiples(Integer multiple, TreeSet<Integer> numbers) {

		assert multiple != null;
		assert numbers != null;
		assert multiple > 1 : multiple;

		Integer start = multiple * multiple;
		Integer end = numbers.last();

		assert start <= end : start + ", " + end;

		log.debug("Removing multiples of " + multiple + " between " + start
				+ " and " + end + ".");

		for (Integer i = start; i <= end; i += multiple) {
			numbers.remove(i);
		}

		log.debug("Numbers are now: " + numbers);
	}

	/**
	 * Finds all primes less than the supplied value or 30 if not specified.
	 *
	 * @param args	maximum value (optional)
	 */
	public static void main(String[] args) {

		// load log4j configuration
		PropertyConfigurator.configure("log4j.properties");

		int max;

		try {
			// use first argument as max value if possible
			max = Integer.parseInt(args[0]);
			log.info("Using maximum value " + max + " from command line.");
		}
		catch(Exception ex) {
			// use default max value
			max = 30;
			log.info("Using default maximum value " + max + ".");
		}

		System.out.println("Primes are " + findPrimes(max));
		log.info("Done.");
	}
}