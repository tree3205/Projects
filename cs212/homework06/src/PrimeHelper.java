import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * Various helper methods for dealing with prime numbers. Requires log4j.
 */
public class PrimeHelper {

	/** Used for log4j debug messages. */
	public static final Logger log = Logger.getLogger(PrimeHelper.class);

	/**
	 * Determines whether the specified number is prime using trial
	 * division and direct search factorization. See
	 * <a href="http://mathworld.wolfram.com/DirectSearchFactorization.html">
	 * http://mathworld.wolfram.com/DirectSearchFactorization.html</a> for
	 * more information.
	 *
	 * @param number value to test if prime
	 * @return <code>true</code> if value is prime
	 */
	public static boolean isPrime(int number) {

		// The only even prime is 2.
		if(number == 2) {
			return true;
		}

		// All other even numbers are not prime, as well as any number
		// less than 2.
		if(number % 2 == 0 || number < 2) {
			return false;
		}

		// Only need to perform trial division up to the square root
		int end = (int) Math.ceil(Math.sqrt(number));

		// Check for any odd divisors.
		for(int i = 3; i <= end; i += 2) {
			if(number % i == 0) {
				return false;
			}
		}

		// Otherwise, no divisors and number is prime.
		return true;
	}

	/**
	 * Returns a collection of all primes found between the start and end
	 * values using trial division. See
	 * <a href="http://mathworld.wolfram.com/DirectSearchFactorization.html">
	 * http://mathworld.wolfram.com/DirectSearchFactorization.html</a> for
	 * more information.
	 *
	 * @param start first value to evaluate if prime
	 * @param end last value to evaluate if prime
	 * @return list of all prime numbers found
	 */
	public static Collection<Integer> primesByTrial(int start, int end) {

		log.debug("Finding primes between " + start + " and " + end + ".");

		ArrayList<Integer> primes = new ArrayList<Integer>();

		for(int i = start; i <= end; i++) {
			if(isPrime(i)) {
				log.debug("Found prime " + i + ".");
				primes.add(i);
			}
		}

		log.debug("Found " + primes.size() + " prime numbers between " +
				start + " and " + end + ".");

		return primes;
	}

	/**
	 * Uses the Sieve of Eratosthenes to find all primes less than or
	 * equal to the maximum number. See
	 * <a href="http://mathworld.wolfram.com/SieveofEratosthenes.html>
	 * http://mathworld.wolfram.com/SieveofEratosthenes.html</a> for
	 * more information.
	 *
	 * @param max maximum number to consider
	 * @return collection of prime numbers
	 */
	public static Collection<Integer> primesBySieve(int max) {

		log.debug("Finding all primes less than or equal to " + max + ".");

		TreeSet<Integer> primes = new TreeSet<Integer>();
		primes.add(Integer.valueOf(2));

		for(Integer i = 3; i <= max; i += 2) {
			primes.add(i);
		}

		int end = (int) Math.ceil(Math.sqrt(max));

		for(Integer i = 3; i != null && i <= end; i = primes.higher(i)) {
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

		Integer start = multiple * multiple;
		Integer end = numbers.last();

		log.debug("Removing multiples of " + multiple + " between " +
				start + " and " + end + ".");

		for(Integer i = start; i <= end; i += multiple) {
			numbers.remove(i);
		}
	}
}
