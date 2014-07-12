package com.kuna.rhythmus.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Sound;

public class Common {
	public static String selectPath;
	public static int keymode;
	
	public static String argPath;
	public static double argBeat;
	public static boolean argRemove = false;
	
	/* common resources */	
	public static Sound snd_button;
	public static Sound snd_select, snd_decide, snd_scratch;
	
	public static void loadCommonSound() {
		snd_button = Gdx.audio.newSound(Gdx.files.internal("data/change.wav"));
		snd_select = Gdx.audio.newSound(Gdx.files.internal("data/select.ogg"));
		snd_decide = Gdx.audio.newSound(Gdx.files.internal("data/decide.ogg"));
		snd_scratch = Gdx.audio.newSound(Gdx.files.internal("data/scratch.wav"));
	}
	
	public static void disposeCommonSound() {
		snd_button.dispose();
		snd_select.dispose();
		snd_decide.dispose();
		snd_scratch.dispose();
	}
}
