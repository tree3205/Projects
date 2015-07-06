
public class recursive2 
{

	static int recursive2 (int n)
	{
		if (n > 1)
		{
			return recursive2(n-1) + recursive2(n-1) + recursive2(n-1);
		}
		else
		{
			return n;
		}
	}
	public static void main(String[] args) 
	{
		System.out.println(recursive2(1));
		System.out.println(recursive2(2));
		System.out.println(recursive2(3));
		System.out.println(recursive2(4));
		System.out.println(recursive2(5));
		System.out.println(recursive2(6));
		System.out.println(recursive2(7));
		System.out.println(recursive2(8));
		System.out.println(recursive2(9));
		System.out.println(recursive2(10));
		System.out.println(recursive2(11));
		System.out.println(recursive2(12));

 
	}

}
