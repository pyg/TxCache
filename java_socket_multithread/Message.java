import java.io.*;
import java.util.*;

public class Message implements Serializable {
	public String msg;
	
	Message(String _msg) {
		msg = _msg;
	}
	void set(String _msg) {
		msg = _msg;
	}
	void sent() {
		System.out.println("sent:"+msg);
	}
	void got() {
		System.out.println("got:"+msg);
	}
}
