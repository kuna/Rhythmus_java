package com.kuna.rhythmus;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kuna.rhythmus.data.Common;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Rhythmus";
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 480;
		
		/* arg test */
		//Common.argBeat = 4;
		//Common.argPath = "C:\\Users\\kuna\\BMS\\F\\F-LN-.bms";
		
		new LwjglApplication(new Rhythmus(), cfg);
	}
}
