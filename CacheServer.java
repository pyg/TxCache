import java.util.*;

public class CacheServer {
	final int MAX_VALUE_NUM = 1000000;
	final int RECENT_TIME = -1;
	
	private CacheServer theCache = null;
	private int number_of_values;
	private Map<String, List<Value>> invertedIndex;
	
	private Map<String, List<Value>> cache;
	
	class CacheResult {
		Value value;
		List<Integer> pinset;
		public CacheResult(Value val, List<Integer> pset) {
			this.value = val;
			this.pinset = pset;
		}
	}
	
	class Value {
		private Interval itv;
		public Interval getInterval() {
			return itv;
		}
		public int getItvBegin() {
			return itv.getBegin();
		}
		public int getItvEnd() {
			return itv.getEnd();
		}
		public Value(Object val, Interval itv, List<String> tags) {
			this.itv = itv;
		}
	}
	
	class Interval {
		private int begin, end;
		public int getBegin() {
			return begin;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}
		public void setBegin(int begin) {
			this.begin = begin;
		}
	}
	
	class Invalidation {
		private String tag;
		private int timestamp;
		public String getTag() {
			return tag;
		}
		public int getTimestamp() {
			return timestamp;
		}
	}
	
	public CacheServer getTheCache() {
		if (theCache == null) theCache = new CacheServer();
		return theCache;
	}
	private CacheServer() {
		number_of_values = 0;
		cache = new HashMap<String, List<Value>>();
		invertedIndex = new HashMap<String, List<Value>>();
	}
	
	private String commitTags(String message) {
		// **The message should include: a set of tags.
		// **The return message should be SUCCESS. (unless we want some more info in the future)
	
		//get the tagset and timestamps from message;
		List<Invalidation> invalidations = null;
		for (Invalidation inv : invalidations) {
			String tag = inv.getTag();
			for (Value value : invertedIndex.get(tag)) 
				if (value != null) value.getInterval().setEnd(inv.getTimestamp()); //it could be null because removeOneValue()
			invertedIndex.remove(tag);
		}
		return "SUCCESS";
	}
	
	private void removeOneValue() {
		// **Remove a value according to the strategy (for example, LeastRecentUsed)
	
		// **We don't need to remove it from invertedIndex because commitTags() will check whether the values are null
		// **Remove it from cache
		Set<String> keys = cache.keySet();
		Iterator it = keys.iterator();
        while (it.hasNext()) {
        	String key = (String)it.next();
        	if (cache.get(key).size() != 0) {
        		cache.get(key).remove(0);
        		if (cache.get(key).size() == 0) cache.remove(key);
        		break;
        	}
        }
	}

	private String addValue(String key, Object val, Interval itv, List<String> tags) {
		if (number_of_values == MAX_VALUE_NUM) removeOneValue();
		else ++number_of_values;

		Value value = new Value(val, itv, tags);	
		if (!cache.containsKey(key)) cache.put(key, new LinkedList<Value>());
		LinkedList<Value> values = (LinkedList<Value>)cache.get(key);
		int len = values.size();
		int i = 0;
		if (itv.getEnd() == RECENT_TIME) i = len - 1; //RECENT_TIME == -1
		
		for ( ; i < len; ++i) 
			if (values.get(i).getItvBegin() >= itv.getEnd()) {
				if (i == 0) values.addFirst(value);
				else 
					if (values.get(i - 1).getItvEnd() > itv.getBegin()) return "INVALID INTERVAL";
					else values.add(i, value);
			}
	
		if (itv.getEnd() == RECENT_TIME)
			for (String tag : tags) {
				if (!invertedIndex.containsKey(tag)) invertedIndex.put(tag, new ArrayList<Value>());
				invertedIndex.get(tag).add(value);
			}
	
		return "SUCCESS";
	}

	private CacheResult readCache(String message) {
		// **The message should include: 1) the hash key; 2) a list of timestamps (sorted).
		// **The return message should include: 1) the value with a list of timestamps, or 2) NOT_FOUND.
	
		// **Scan the cache to find out a valid value
		//get the key and timestamps;
		String key = null;
		List<Integer> timestamps = null;
		if (!cache.containsKey(key)) return null;
		LinkedList<Value> values = (LinkedList<Value>)cache.get(key);
		
		int time_len = timestamps.size();
		int time_idx = 0;
		int val_len = values.size();
		int val_idx = 0;
		while (time_idx < time_len && val_idx < val_len) 
			if (values.get(val_idx).getItvEnd() <= timestamps.get(time_idx)) ++val_idx;
			else if (values.get(val_idx).getItvBegin() > timestamps.get(time_idx)) ++time_idx;
			else { //Found!
				int end = values.get(val_idx).getItvEnd();
				int idx_end = time_idx + 1;
				for ( ; idx_end < time_len && timestamps.get(idx_end) < end; ++idx_end) ;
				return new CacheResult(values.get(val_idx), timestamps.subList(time_idx, idx_end + 1));
			}
		return null;
	
		// **possible optimization: get the one that covers most timestamps.
	}

	private String writeCache(String message) {
		// **The message should include: 1) the hash key; 2) the value; 3) the interval; 4) the tags;
		// **The rtn_message should include: 1) success, or 2) conflict.
	
		//get key, val, itv, tags
		String key = null;
		Value val = null;
		Interval itv = null;
		List<String> tags = null;
		return addValue(key, val, itv, tags);
	}

	private String respondClient(String message) {
		//if (read) readCache(message, rtn_message);
		//else if (write) writeCache(message, rtn_message);
		//else if (commit) commiteTags(message, rtn_message);
		//else if (abort || begin-ro || begin-rw) DONOTHING;
		//else 
		return "COMMAND NOT FOUND";
	}


	public static void test() {
		LinkedList<String> shit = new LinkedList<String>();
		shit.addFirst("three");
		shit.addFirst("two");
		shit.addFirst("one");
		System.out.println(shit);
		shit.add(1, "one.five");
		System.out.println(shit);
		shit.add(0, "zero");
		System.out.println(shit);
	}
		
	public static void main(String []args) {
	
		//test();
	
		//listenToClient();
		return;
	}
}

