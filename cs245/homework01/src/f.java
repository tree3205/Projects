
public class f 
{

	public static void main(String[] args) 
	{
		int tmp1 = 6;
		while (tmp1 > 1)
		{
			int tmp2 = 0;
			
			while (tmp2 < 6/2)
			{
				System.out.println("tmp02: " + tmp2);
				tmp2++;
				System.out.println("tmp2: " + tmp2);
			}
			tmp1 = tmp1/2;
			System.out.println("tmp1:" + tmp1);
		}

	}

}
