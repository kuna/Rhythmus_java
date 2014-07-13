package com.kuna.rhythmus;

import java.io.File;
import java.io.InputStream;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.kuna.rhythmus.bmsdata.BMSData;
import com.kuna.rhythmus.bmsdata.BMSKeyData;
import com.kuna.rhythmus.bmsdata.BMSParser;
import com.kuna.rhythmus.data.Common;
import com.kuna.rhythmus.score.ScoreManager;

public class Rhythmus implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	public static DecalBatch dbatch;
	
	// accessible common variables
	public static BMSList bmsList;
	public static BMSData bmsData;
	public static ScoreManager scoreManager;
	
	private static int scene = -1;
	
	public static final int SCENE_SELECT = 0;
	public static final int SCENE_DECIDE = 1;
	public static final int SCENE_PLAY = 2;
	public static final int SCENE_RESULT = 3;
	public static final int SCENE_KEYSETTING = 4;
	
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 480;
	
	// scenes
	public static Scene_Select sSelect;
	public static Scene_Decide sDecide;
	public static Scene_Play sPlay;
	public static Scene_Result sResult;
	public static Scene_KeySetting sKey;
	
	public int targetScene;
	public static String selPath;
	public static boolean exitOnEnd;
	
	// TODO decal test
	public static PerspectiveCamera cam;
	
	public Rhythmus() {
		if (Common.argPath == null) {
			targetScene = SCENE_SELECT;
			Settings.autoplay = false;
			exitOnEnd = false;
		} else {
			selPath = Common.argPath;
			targetScene = SCENE_DECIDE;
			Settings.autoplay = true;
			exitOnEnd = true;
		}
	}
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
		camera.position.set(SCREEN_WIDTH/2,SCREEN_HEIGHT/2,0);
		camera.update();
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		// TODO decal test
		cam = new PerspectiveCamera(45, SCREEN_WIDTH, SCREEN_HEIGHT);
		cam.near = 1;
		cam.far = 300;
		cam.position.set(0, 0, 5);
		dbatch = new DecalBatch(new CameraGroupStrategy(Rhythmus.cam));
		//d_note = Decal.newDecal(1, 1, new TextureRegion(new Texture(Gdx.files.internal("data/play.png")), 0, 305, 30, 6), true);
		//d_note.setScale(1.0f, 1.0f);
		
		// load sound resource
		Common.loadCommonSound();
        
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
		Settings.LoadSetting();
		Rhythmus.bmsData = new BMSData();
		
		// prepare scenes
		sSelect = new Scene_Select();
		sDecide = new Scene_Decide();
		sPlay = new Scene_Play();
		sResult = new Scene_Result();
		sKey = new Scene_KeySetting();
		
		// set scene
		changeScene(targetScene);
	}

	@Override
	public void dispose() {
		if (Common.argRemove) {
			new File(bmsData.path).delete();
		}
		
		// save setting
		Settings.SaveSetting();
		
		// save BMSList cache
		bmsList.SaveBMSCache();
		
		// memory release
		Common.disposeCommonSound();
		BMSResource.dispose();
		sSelect.dispose();
		sDecide.dispose();
		sPlay.dispose();
		sResult.dispose();
		sKey.dispose();
		batch.dispose();
		dbatch.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		// TODO decal test
		cam.update();
		
		switch (scene) {
		case SCENE_SELECT:
			sSelect.draw(batch, dbatch);
			break;
		case SCENE_DECIDE:
			sDecide.draw(batch, dbatch);
			break;
		case SCENE_PLAY:
			sPlay.draw(batch, dbatch);
			break;
		case SCENE_RESULT:
			sResult.draw(batch ,dbatch);
			break;
		case SCENE_KEYSETTING:
			sKey.draw(batch, dbatch);
			break;
		}
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
			if (!LoadBMS(selPath)) {
				Gdx.app.error("ERROR", "File not Found");
				if (exitOnEnd) {
					Gdx.app.exit();
				} else {
					changeScene(SCENE_SELECT);
				}
				return;
			}
			BMSParser.ExecutePreProcessor(Rhythmus.bmsData);
			bmsData.convertLNOBJ();
			bmsData.checkKey();
			
			if (exitOnEnd) {
				// to the beat, process the key
				for (BMSKeyData bkd: bmsData.bmsdata) {
					if (bkd.getBeat() > Common.argBeat)
						break;
					
					bkd.setAttr(1);
				}
				for (BMSKeyData bkd: bmsData.bgmdata) {
					if (bkd.getBeat() > Common.argBeat)
						break;
					
					bkd.setAttr(1);
				}
				
				// automatically key setting
				Settings.key = bmsData.checkKey()+1;
				if (Settings.key == 8 || Settings.key == 15)
					Settings.key++;
			}
			
			// remove channel
			// CAUTION! it's 16Hex
			if (Settings.key == 7) {
				bmsData.removeChannel(new int[] {0x16});
				bmsData.getTotal();
			} else if (Settings.key == 6) {
				bmsData.removeChannel(new int[] {0x18, 0x19});
				bmsData.getTotal();
			} else if (Settings.key == 5) {
				bmsData.removeChannel(new int[] {0x16, 0x18, 0x19});
				bmsData.getTotal();
			}
			
			BMSParser.setTimemark(Rhythmus.bmsData);
			Rhythmus.bmsData.fillNotePosition(Rhythmus.bmsData.bmsdata, 100, true);	// calculate note's position
			
			sDecide = new Scene_Decide();
			sDecide.init();
			break;
		case SCENE_PLAY:
			sDecide.dispose();
			
			sPlay = new Scene_Play();
			sPlay.init();
			break;
		case SCENE_RESULT:
			// all resource despose
			BMSResource.dispose();
			
			sResult = new Scene_Result();
			sResult.init();
			break;
		case SCENE_KEYSETTING:
			sKey.init();
			break;
		}
		
		scene = mode;
	}

	public static boolean LoadBMS(String path) {
		// this method also checks about archive
		bmsData = new BMSData();	// init data
		
		Gdx.app.log("TEST", path);
		if (BMSArchive.getArchiveFileName(path) == null) {
			if (!BMSParser.LoadBMSFile( path, Rhythmus.bmsData )) {
				return false;
			}
		} else {

			InputStream is = BMSArchive.getInputStream(path);
			byte[] b = BMSArchive.loadBytesFromInputStream(is);
			
			Rhythmus.bmsData.path = path;
			Rhythmus.bmsData.dir = BMSArchive.getArchiveName(path) + "|";
			if (!BMSParser.LoadBMSFile(b, Rhythmus.bmsData)) {
				return false;
			}
		}
		
		return true;
	}
}
