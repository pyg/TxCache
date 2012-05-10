package server;

import java.util.List;

class Value {
    private Object val;
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
    public Value(Object val) {
    	this.val = val;
    }
    public Object getValue() {
    	return this.val;
    }
}