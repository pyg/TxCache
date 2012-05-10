package server;
import java.util.List;

		class MessageIn {
			
			public char cmd;
			
			// commits
			public List<Invalidation> inv;
			
			// reads
			public String key;
			public List<Integer> timestamps;
			
			// writes
			// ->key
			public Object val;
			public Interval itv;
			public List<String> tags;
		}