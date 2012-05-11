public class TestingLoop {
	
	public static long countTo(long max) {
		int i = 0;
		while (i < max+1) i++;
		return i;
	}
	public static void main (String[] args) {
		TxCache.initializeTxCache();
		long res = TxCache.wrap("TestingLoop","countTo","",1000000);
		System.out.println(res);
		res = TxCache.wrap("TestingLoop","countTo","",1000001);
		System.out.println(res);
		res = TxCache.wrap("TestingLoop","countTo","",1000000);
		System.out.println(res);
		res = TxCache.wrap("TestingLoop","countTo","",1000001);		
		System.out.println(res);
	}
}