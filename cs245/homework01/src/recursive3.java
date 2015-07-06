
public class recursive3 
{
	static int recursive3 (int n)
	{
		if (n > 1)
		{
			return 3 * recursive3(n-1);
		}
		else
			return n;
	}
	
	public static void main(String[] args) 
	{
		System.out.println(recursive3(1));
		System.out.println(recursive3(2));
		System.out.println(recursive3(3));
		System.out.println(recursive3(4));
		System.out.println(recursive3(5));
		System.out.println(recursive3(6));
		System.out.println(recursive3(7));
		System.out.println(recursive3(8));
		System.out.println(recursive3(9));
		System.out.println(recursive3(10));
		System.out.println(recursive3(11));
		System.out.println(recursive3(12));

	}

}
