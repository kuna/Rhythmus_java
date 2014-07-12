package com.kuna.rhythmus;

import java.io.OutputStream;

import com.badlogic.gdx.Gdx;
import com.kuna.rhythmus.score.ScoreData;

public class Settings {
	/*
	 * JUDGE patch: more easiler judgement for mobile
	 */
	public static final int JUDGE_EASY = 21*2;	// it multiplys to next judge
	public static final int JUDGE_NORMAL = 18*2;
	public static final int JUDGE_HARD = 15*2;
	public static final int JUDGE_VERYHARD = 8*2;
	
	public final static int GUAGE_HARD = 3;
	public final static int GUAGE_GROOVE = 2;
	public final static int GUAGE_EASY = 1;

	public final static int MODE_MOBILE = 1;
	public final static int MODE_PAD = 2;
	public final static int MODE_PC = 3;
	
	public static float speed = 2.0f;
	public static int guagemode = Settings.GUAGE_GROOVE;
	public static int judgetime;
	public static boolean autoplay = false;
	public static int key=5;
	
	public static int[] keycode = new int[]{0, 
		29, 47, 32, 62, 38, 39, 40, 59,
		0, 0, 0, 0, 0, 0, 0, 0};
	
	public static final String _FILENAME = "rhythmus_settings.dat";
	
	public static void LoadSetting() {
		try {
			if (Gdx.files.external(_FILENAME).exists()) {
				String d = Gdx.files.external(_FILENAME).readString();
				String l[] = d.split("\n");
				speed = Float.parseFloat(l[0]);
				guagemode = Integer.parseInt(l[1]);
				judgetime = Integer.parseInt(l[2]);
				key = Integer.parseInt(l[3]);
				
				String keys[] = l[4].split(" ");
				for (int i=1; i<=16; i++) {
					keycode[i] = Integer.parseInt(keys[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void SaveSetting() {
		try {
			OutputStream o = Gdx.files.external(_FILENAME).write(false);
			
			String data = "";
			data += Float.toString(speed) + "\n";
			data += Integer.toString(guagemode) + "\n";
			data += Integer.toString(judgetime) + "\n";
			data += Integer.toString(key) + "\n";
			
			String keycodeData = "";
			for (int i: keycode) {
				keycodeData += Integer.toString(i) + " ";
			}
			data += keycodeData;
			
			o.write(data.getBytes());
			o.flush();
			o.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
