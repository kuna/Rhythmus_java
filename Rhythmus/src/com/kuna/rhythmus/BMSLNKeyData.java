package com.kuna.rhythmus;

public class BMSLNKeyData implements Comparable<BMSLNKeyData> {
	double beatStart, beatEnd;
	int key;
	double value;
	double time;
	int attr;
	
	@Override
	public int compareTo(BMSLNKeyData o) {
		if (this.beatStart < o.beatStart)
			return -1;
		else if (this.beatStart > o.beatStart)
			return 1;
		else
			return 0;
	}
}
