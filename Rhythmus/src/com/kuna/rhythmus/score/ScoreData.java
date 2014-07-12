package com.kuna.rhythmus.score;

public class ScoreData {
	public static final int CLEAR_FULLCOMBO = 5;
	public static final int CLEAR_HARD = 4;
	public static final int CLEAR_GROOVE = 3;
	public static final int CLEAR_EASY = 2;
	public static final int CLEAR_FAILED = 1;
	public static final int CLEAR_NONE = 0;
	
	public String hash;
	public int clear;
	public int combo;
	public int note;
	public int pg;
	public int gr;
	public int gd;
	public int pr;
	public int bd;
	public int key;
	public boolean save = false;	// used when check whether to be saved
	
	public String createData() {
		return String.format("%s,,%d,,%d,,%d,,%d,,%d,,%d,,%d,,%d,,%d\n", hash,note,pg,gr,gd,pr,bd,combo,clear,key);
	}
	
	public void readData(String d) {
		String _d[] = d.split(",,");
		hash = _d[0];
		note = Integer.parseInt(_d[1]);
		pg = Integer.parseInt(_d[2]);
		gr = Integer.parseInt(_d[3]);
		gd = Integer.parseInt(_d[4]);
		pr = Integer.parseInt(_d[5]);
		bd = Integer.parseInt(_d[6]);
		combo = Integer.parseInt(_d[7]);
		clear = Integer.parseInt(_d[8]);
		key = Integer.parseInt(_d[9]);
	}
	
	public void readData(byte[] b) {
		String d = new String(b, 0, b.length);
		readData(d);
	}
	
	public int getEXScore() {
		return pg*2+gr;
	}
	
	public int getTotalNote() {
		return pg+gr+gd+pr+bd;
	}
	
	public float getRate() {
		return (float)getEXScore()/(note*2);
	}
	
	public String GetRateString() {
		return GetRateString(getRate());
	}
	
	public static String GetRateString(float rate) {
		if (rate > 8.0f/9) {
			return "AAA";
		} else if (rate > 7.0f/9) {
			return "AA";
		} else if (rate > 6.0f/9) {
			return "A";
		} else if (rate > 5.0f/9) {
			return "B";
		} else if (rate > 4.0f/9) {
			return "C";
		} else if (rate > 3.0f/9) {
			return "D";
		} else if (rate > 2.0f/9) {
			return "E";
		} else {
			return "F";
		}
	}
}
