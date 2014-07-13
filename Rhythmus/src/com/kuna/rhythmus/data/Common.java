package com.kuna.rhythmus.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class Common {
	public static String selectPath;
	public static int keymode;
	
	public static String argPath;
	public static double argBeat;
	public static boolean argRemove = false;
	
	/* common resources */	
	public static Sound snd_button;
	public static Sound snd_decide, snd_scratch;
	public static Music m_select, m_clear, m_fail;
	
	public static void loadCommonSound() {
		m_select = Gdx.audio.newMusic(Gdx.files.internal("data/select.ogg"));
		m_clear =  Gdx.audio.newMusic(Gdx.files.internal("data/clear.ogg"));
		m_fail =  Gdx.audio.newMusic(Gdx.files.internal("data/fail.ogg"));
		
		snd_button = Gdx.audio.newSound(Gdx.files.internal("data/change.wav"));
		snd_decide = Gdx.audio.newSound(Gdx.files.internal("data/decide.ogg"));
		snd_scratch = Gdx.audio.newSound(Gdx.files.internal("data/scratch.wav"));
		
	}
	
	public static void disposeCommonSound() {
		m_select.dispose();
		m_clear.dispose();
		m_fail.dispose();
		snd_button.dispose();
		snd_decide.dispose();
		snd_scratch.dispose();
	}
}
