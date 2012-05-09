//javac JNIFoo.java
//javah -jni JNIFoo

public class JNIFoo {    
    public native void nativeFoo(char array[],int len,String str);    

    static {
        System.loadLibrary("foo");
    }

    public void print () {
	//char[] array = new char[15];
	String tmp = "whatthefuck?";
	char[] array = tmp.toCharArray();
        String str = "test";
	nativeFoo(array,array.length,str);
        //System.out.println(str);
    }
    
    public static void main(String[] args) {
    (new JNIFoo()).print();
    return;
    }
}
