
public class recursive4 
{
	static int recursive4(int n)
	{
		int no_op = 0;
		if (n > 1)
		{
			for (int i = 1; i <= n; i++)
			{
				System.out.println("begin: " + no_op);
				no_op++;
				System.out.println("now: " + no_op);
				
			}
			return recursive4(n/2) * recursive4 (n/2);
		}
		else
		{
			return 1;
		}
		
	}
	
	public static void main(String[] args) 
	{
		System.out.println("n=1:");
		System.out.println(recursive4(1));
		System.out.println("============");
		System.out.println("n=2:");
		System.out.println(recursive4(2));
		System.out.println("============");
		System.out.println("n=3:");
		System.out.println(recursive4(3));
		System.out.println("============");
		System.out.println("n=4:");
		System.out.println(recursive4(4));
		System.out.println("============");
		System.out.println("n=5:");
		System.out.println(recursive4(5));
		System.out.println("============");
		System.out.println("n=6:");
		System.out.println(recursive4(6));
		System.out.println("============");
	}

}
