package server;

		
public class Invalidation {
			private String tag;
			private int timestamp;
			public String getTag() {
				return tag;
			}
			public int getTimestamp() {
				return timestamp;
			}
			public void setTag(String tag) {
				this.tag = tag;
			}
			public void setTimestamp(int timestamp) {
				this.timestamp = timestamp;
			}
		}