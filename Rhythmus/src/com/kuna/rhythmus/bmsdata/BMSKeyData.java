package com.kuna.rhythmus.bmsdata;

import java.util.Comparator;

// every note must be multiplicatoin of 1/192
// that is, 192/96/64/48/32/24/16/12/8/6/4/3/2/1 supported.

public class BMSKeyData implements Comparable<BMSKeyData> {
	double beat;
	int numerator;
	
	int key;
	double value;
	double time;
	boolean isLNfirst = false;
	int layernum;	// used such as BGM
	
	double posy;
	int attr;
	
	public boolean isLNFirst() {
		return isLNfirst;
	}
	
	public double getBeat() {
		return beat;
	}
	
	public void setBeat(double beat, BMSData bd) {
		// we need to reset numerator,
		// so BMSData is necessary
		this.beat =beat;
		numerator = (int) (192 * bd.getBeatLength((int)beat) * (beat%1));
	}

	public int getChannel() {
		return key;
	}
	
	public void setChannel(int val) {
		key = val;
	}

	public double getValue() {
		return value;
	}
	
	public void setValue(double val) {
		value = val;
	}
	
	public void setValue(int val) {
		value = val;
	}
	
	public int getAttr() {
		return attr;
	}
	
	public void setAttr(int val) {
		attr = val;
	}
	
	public boolean isProcessed() {
		return (attr != 0);
	}
	
	public void setProcessed() {
		attr = 1;
	}
	
	public void setPosY(double val) {
		posy = val;
	}
	
	public double getPosY()  {
		return posy;
	}
	
	public int getPosY(double precision)  {
		return (int)(posy*precision);
	}
	
	public void set1PKey(int val) { // 1~7: normal key, 8:SC
		int r = convertPlayerKey(val);
		if (r > 0) {
			key = 16+r;
		}
	}
	
	public void set2PKey(int val) { // 1~7: normal key, 8:SC
		int r = convertPlayerKey(val);
		if (r > 0) {
			key = 32+r;
		}
	}
	
	public void set1PTransKey(int val) { // 1~7: normal key, 8:SC
		int r = convertPlayerKey(val);
		if (r > 0) {
			key = 48+r;
		}
	}
	
	public void set2PTransKey(int val) { // 1~7: normal key, 8:SC
		int r = convertPlayerKey(val);
		if (r > 0) {
			key = 64+r;
		}
	}
	
	public void set1PLNKey(int val) { // 1~7: normal key, 8:SC
		int r = convertPlayerKey(val);
		if (r > 0) {
			key = 80+r;
		}
	}
	
	public void set2PLNKey(int val) { // 1~7: normal key, 8:SC
		int r = convertPlayerKey(val);
		if (r > 0) {
			key = 96+r;
		}
	}
	
	private int convertPlayerKey(int val) {
		if (val >= 1 && val <= 5) {
			return val;
		} else if (val == 6) {
			return 8;
		} else if (val == 7) {
			return 9;
		} else if (val == 8) {
			return 6;
		} else {
			return -1;
		}
	}
	
	public int getKey() {
		return key;
	}
	
	public int getNumerator() {
		return numerator;
	}
	
	public void setNumerator(int val) {
		numerator = val;
	}
	
	public double getTime() {
		return time;
	}
	
	public int getLayerNum() {
		return layernum;
	}

	public boolean isBGMChannel() {
		return (key == 1);
	}

	public void setBGMChannel(int layer) {
		key = 1;
		layernum = layer;
	}
		
	public boolean isBPMChannel() {
		return (key == 3);
	}

	public void setBPMChannel() {
		key = 3;
	}
	
	public boolean isBPMExtChannel() {
		return (key == 8);
	}

	public void setBPMExtChannel() {
		key = 8;
	}
	
	public boolean isBGAChannel() {
		return (key == 4);
	}

	public void setBGAChannel() {
		key = 4;
	}
	
	public boolean isPoorChannel() {
		return (key == 6);
	}

	public void setPoorChannel() {
		key = 6;
	}
	
	public boolean isBGALayerChannel() {
		return (key == 7);
	}

	public void setBGALayerChannel() {
		key = 7;
	}
	
	public boolean isSTOPChannel() {
		return (key == 9);
	}

	public void setSTOPChannel() {
		key = 9;
	}
	
	public boolean is1PChannel() {
		return (key > 16 && key < 32);
	}

	public boolean is2PChannel() {
		return (key > 32 && key < 48);
	}
	
	public boolean is1PTransChannel() {
		return (key > 48 && key < 64);
	}

	public boolean is2PTransChannel() {
		return (key > 64 && key < 80);
	}
	
	public boolean is1PLNChannel() {
		return (key > 80 && key < 96);
	}
	
	public boolean is2PLNChannel() {
		return (key > 96 && key < 112);
	}
	
	public int getKeyNum() {
		if (!is1PChannel() && !is2PChannel() && !is1PLNChannel() && !is2PLNChannel() && !is1PTransChannel() && !is2PTransChannel())
			return -1;
		
		int rk = key % 16;
		if (rk == 7)
			return -1;	// we won't support this
		else if (rk == 8)
			return 6;
		else if (rk == 9)
			return 7;
		else if (rk == 6)
			return 8;	// scratch
		else
			return rk;
	}
	
	/*
	 * comparision
	 */
	@Override
	public int compareTo(BMSKeyData o) {
		if (this.beat < o.beat)
			return -1;
		else if (this.beat > o.beat)
			return 1;
		else
			return 0;
	}

	public static Comparator<BMSKeyData> KeyComparator 
                          = new Comparator<BMSKeyData>() {
	    public int compare(BMSKeyData d1, BMSKeyData d2) {
    		return d1.key - d2.key;
 
	      //ascending order
	      //return fruitName1.compareTo(fruitName2);
 
	      //descending order
	      //return fruitName2.compareTo(fruitName1);
	    }
 
	};
}
