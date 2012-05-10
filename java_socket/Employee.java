import java.io.*;
import java.util.*;

public class Employee implements Serializable {
	public int ID;
	public String name;
	public String message;
	
	Employee(int _id, String _name) {
		ID = _id;
		name = _name;
	}
	void set(int _id, String _name) {
		ID = _id;
		name = _name;
	}
	void print() {
		System.out.println("id:"+ID+" "+"name:"+name);
	}
	void said() {
		System.out.println("said:"+message);
	}
	void heard() {
		System.out.println("heard:"+message);
	}
}
