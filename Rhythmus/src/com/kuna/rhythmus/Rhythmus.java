package com.kuna.rhythmus;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;
import com.kuna.rhythmus.score.ScoreManager;

public class Rhythmus implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	// accessible common variables
	public static BMSList bmsList;
	public static BMSParser bmsParser;
	public static ScoreManager scoreManager;
	
	private static int scene = -1;
	
	public static final int SCENE_SELECT = 0;
	public static final int SCENE_DECIDE = 1;
	public static final int SCENE_PLAY = 2;
	public static final int SCENE_RESULT = 3;
	public static final int SCENE_EDIT = 4;
	
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 480;
	
	// scenes
	public Scene_Select sSelect;
	public Scene_Decide sDecide;
	public Scene_Play sPlay;
	public Scene_Result sResult;
	public Scene_sdf
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
		camera.position.set(SCREEN_WIDTH/2,SCREEN_HEIGHT/2,0);
		camera.update();
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
		
		// Load BMS List
		bmsList = new BMSList();
		Thread t = new Thread() {
			public void run() {
				bmsList.LoadBMSList("BMS/");
			};
		};
		t.start();
		
		// init values
		scoreManager = new ScoreManager();
		scoreManager.LoadScore();
		Settings.speed = 3;
		Settings.bmsmode = Settings.MODE_MOBILE;
		Settings.guagemode = Settings.GUAGE_GROOVE;
		
		// prepare scenes
		sSelect = new Scene_Select();
		sDecide = new Scene_Decide();
		sPlay = new Scene_Play();
		sResult = new Scene_Result();
		
		// set scene
		changeScene(SCENE_SELECT);
	}

	@Override
	public void dispose() {
		// save BMSList cache
		bmsList.SaveBMSCache();
		
		// memory release
		sSelect.dispose();
		sDecide.dispose();
		sPlay.dispose();
		sResult.dispose();
		batch.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		switch (scene) {
		case SCENE_SELECT:
			sSelect.draw(batch);
			break;
		case SCENE_DECIDE:
			sDecide.draw(batch);
			break;
		case SCENE_PLAY:
			sPlay.draw(batch);
			break;
		case SCENE_RESULT:
			sResult.draw(batch);
			break;
		case SCENE_EDIT:
			break;
		}
		batch.end();
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	public static void changeScene(int mode) {
		if (mode == scene) return;
		
		switch (mode) {
		case SCENE_SELECT:
			// song objects are should be removed
			BMSData.dispose();
			sPlay.dispose();
			sResult.dispose();
			
			sSelect = new Scene_Select();
			sSelect.init();
			break;
		case SCENE_DECIDE:
			// dispose and clear input device
			sSelect.dispose();
			Gdx.input.setInputProcessor(null);
			
			// must bmsload first
			Rhythmus.bmsParser = new BMSParser();
			Rhythmus.bmsParser.LoadBMSFile( sSelect.selPath );
			Rhythmus.bmsParser.setTimemark();
			
			sDecide = new Scene_Decide();
			sDecide.init();
			break;
		case SCENE_PLAY:
			sDecide.dispose();
			
			// autoplay?
			sPlay = new Scene_Play();
			sPlay.autoplay = Settings.autoplay;
			sPlay.init();
			break;
		case SCENE_RESULT:
			sResult = new Scene_Result();
			sResult.init();
			break;
		}
		
		scene = mode;
	}
}
