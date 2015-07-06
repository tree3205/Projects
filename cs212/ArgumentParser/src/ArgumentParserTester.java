
public class ArgumentParserTester 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String[] args1 = { "-a", "aaa", "-b", "bbb", "-c", "-d", "dddd", "-e" };

		ArgumentParser parser = new ArgumentParser(args1);

		//System.out.println(parser.toString());

		System.out.println("hasFlag -c is " + parser.hasFlag("-c"));
		System.out.println("hasValue -c is " + parser.hasValue("-c"));

		System.out.println();
		System.out.println("hasFlag -d is " + parser.hasFlag("-d"));
		System.out.println("hasValue -d is " + parser.hasValue("-d"));
		System.out.println("getValue -d is " + parser.getValue("-d"));

		System.out.println();
		System.out.println("hasFlag -e is " + parser.hasFlag("-e"));
		System.out.println("hasValue -e is " + parser.hasValue("-e"));
		System.out.println("getValue -e is " + parser.getValue("-e"));

		System.out.println();
		System.out.println("numArguments is " + parser.numArguments());
		System.out.println("numFlags is " + parser.numFlags());

	}

}
