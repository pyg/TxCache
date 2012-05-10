package server;

import java.util.List;

public class CacheResult {
	Value value;
	List<Integer> pinset;
	String message;
	public CacheResult(String message, Value val, List<Integer> pset) {
		this.message = message;
		this.value = val;
		this.pinset = pset;
	}
	public CacheResult(String message) {
		this.message = message;
	}
}