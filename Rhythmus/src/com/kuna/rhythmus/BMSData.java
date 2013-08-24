package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

// this class stores BMSData
// that will need at game playing

public class BMSData {
	public static Sound[] wav = new Sound[1322];
	public static Texture[] bg = new Texture[1322];
	
	public static int progress = 0;
	public static boolean isLoaded = false;
	
	public static void LoadData(final BMSParser bp) {
		isLoaded = false;
		String path = "";

		Texture.setEnforcePotImages(false);
		for (int i=0; i<1322; i++) {
			if (bp.str_bg[i] == null || bp.str_bg[i].length() == 0)
				continue;
			path = bp.dir + bp.str_bg[i];
			if (Gdx.files.external(path).exists()) 
			{
				try {
					Gdx.app.log("BMSData", String.format("Loading bitmap %s", path));
					bg[i] = new Texture(Gdx.files.external(path));
				} catch (Exception e) {
					Gdx.app.error("BMSData", String.format("Cannot load bitmap %s", path));
				}
			}
			progress = (int) ((double)i/2644*100);
		}

		new Thread( new Runnable() {
			@Override
			public void run() {
				String path;
				for (int i=0; i<1322; i++) {
					if (bp.str_wav[i] == null || bp.str_wav[i].length() == 0)
						continue;
					path = bp.dir + bp.str_wav[i];
					if (Gdx.files.external(path).exists())
					{
						try {
							Gdx.app.log("BMSData", String.format("Loading Audio %s", path));
							wav[i] = Gdx.audio.newSound(Gdx.files.external(path));
						} catch (Exception e) {
							Gdx.app.error("BMSData", String.format("Cannot load Audio %s", path));
						}
					} else if (bp.str_wav[i].endsWith(".wav")) {
						bp.str_wav[i] = bp.str_wav[i].substring(0, bp.str_wav[i].length()-3) + "ogg";
						i--;
						continue;
					}
					progress = (int) ((double)i/2644*100) + 50;
				}

				Gdx.app.log("BMSData", "Loading Complete");
				isLoaded =true;
			}
		}).start();
	}
	
	public static void dispose() {
		for (int i=0; i<1322; i++)
			if (wav[i] != null)
				wav[i].dispose();
		for (int i=0; i<1322; i++)
			if (bg[i]!=null)
				bg[i].dispose();
		isLoaded = false;
	}
	
	public static void playSound(int sndIndex) {
		if (wav[sndIndex]!= null) wav[sndIndex].play();
	}
}
