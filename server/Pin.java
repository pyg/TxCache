package server;
public class Pin {
	private int id; //which is also timestamp;
	private int users;
	public Pin(int id) {
		this.id = id;
		this.users = 0;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return this.id;
	}
	public void useIt() {
		++users;
	}
	public void releaseIt() {
		if (users > 0) --users;
	}
	public int countUsers() {
		return users;
	}
	public int getTimestamp() {
		return id;
	}
}

	
