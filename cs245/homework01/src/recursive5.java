
public class recursive5 
{
	static int recursive5(int n)
	{
		int no_op = 0;
		if (n > 1)
		{
			for (int i = 1; i <= n; i = i*2)
			{
				System.out.println("begin: " + no_op);
				no_op++;
				System.out.println("now: " + no_op);
			}
			return recursive5(n/4) * recursive5 (n/4) * recursive5(n/4) + recursive5(n/4);
		}
		else
		{
			return 1;
		}
		
	}
	
	public static void main(String[] args) 
	{
		System.out.println("n=1:");
		System.out.println(recursive5(1));
		System.out.println("============");
		System.out.println("n=2:");
		System.out.println(recursive5(2));
		System.out.println("============");
		System.out.println("n=3:");
		System.out.println(recursive5(3));
		System.out.println("============");
		System.out.println("n=4:");
		System.out.println(recursive5(4));
		System.out.println("============");
		System.out.println("n=5:");
		System.out.println(recursive5(5));
		System.out.println("============");
		System.out.println("n=16:");
		System.out.println(recursive5(16));
		System.out.println("============");
		System.out.println("n=63:");
		System.out.println(recursive5(63));
		System.out.println("============");
		
	}

}
