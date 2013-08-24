package com.kuna.rhythmus;

public class BMSKeyData implements Comparable<BMSKeyData> {
	double beat;
	int key;
	double value;
	double time;
	int attr;
	
	@Override
	public int compareTo(BMSKeyData o) {
		if (this.beat < o.beat)
			return -1;
		else if (this.beat > o.beat)
			return 1;
		else
			return 0;
	}
}
