
public class g
{

	
	public static void main(String[] args) 
	{
		int sum = 0;
		for (int tmp1 = 0; tmp1 < 7; tmp1++)
		{
			System.out.println("tmp01: " + tmp1);
			for(int tmp2 = 0; tmp2 <tmp1; tmp2++)
			{	System.out.println("tmp02: " + tmp2);
				for (int tmp3 = 1; tmp3 < 7; tmp3 = tmp3*3)
				{	
					System.out.println("tmp03: " + tmp3);
					sum++;
					System.out.println("sum: " + sum);
					System.out.println("tmp3: " + tmp3);
				}
				System.out.println("tmp2: " + tmp2);
			}
			System.out.println("tmp1: " + tmp1);
		}

	}

}
