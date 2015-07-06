
public class recursive1 
{

	static int recursive1(int n)
	{
		int sum = 0;
		for (int i =1; i<=n; i++)
		{
			System.out.println("i0: " + i);
			System.out.println("sum0:" + sum);
			sum++;
			System.out.println("sum:" + sum);
			System.out.println("i: "+ i);
		}
		
		if (n > 1)
		{
			return sum + recursive1(n-1);
	
		}
		else
		{
			return n;
		}
	}
		
	
	public static void main(String[] args) 
	{
		recursive1(0);
		System.out.println(recursive1(0));
		System.out.println("===========");
		recursive1(1);
		System.out.println(recursive1(1));
		System.out.println("===========");
		recursive1(2);
		System.out.println(recursive1(2));
		System.out.println("===========");
		recursive1(3);
		System.out.println(recursive1(3));
		System.out.println("===========");
		
	}


}
