
public class test {

	public static void main(String[] args) {
		String S1 = "A B A B";
		String S2 = "B C A";
		int x = S1.length();
		int y = S2.length();
		
		System.out.println(LCS (x, y, S1, S2));

	}

	public static int LCS(int x, int y, String S1, String S2) {
		if (x == 0 || y == 0)
			return 0;
		if (S1.charAt(x-1) == S2.charAt(y-1))  
			return 1 + LCS (x-1, y-1, S1, S2);
		else
			return Math.max( LCS(x-1,y,S1,S2), LCS(x,y-1,S1,S2) );
		
	}

}
