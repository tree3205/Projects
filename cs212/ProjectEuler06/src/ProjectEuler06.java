
public class ProjectEuler06 {

	/**
	 * @param args
	 */
	public static void main(String[] args ) 
	{
		// TODO Auto-generated method stub
		int num = Integer.parseInt(args[0]);
		
		System.out.println("Considering the first " + num + " natural numbers.");
			
		double squareSum = 0;
		for (int i=1; i<=num; i++)
		{
			double tmp = Math.pow(i, 2);
			squareSum += tmp;
		}
		
		System.out.printf("The sum of squares is %.1f.\n", squareSum);	
		
		
		int sum = 0;
		for (int i=1; i<=num; i++)
		{
			sum += i;
		}
			
		double sumSquare = Math.pow(sum, 2);
		
		System.out.printf("The square of the sum is %.1f.\n", sumSquare );
		
		double temp= sumSquare-squareSum;
		System.out.printf("The difference is %.1f.\n", temp);
		
	}

}
