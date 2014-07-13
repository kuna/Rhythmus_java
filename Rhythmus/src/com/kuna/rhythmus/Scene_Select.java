package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kuna.rhythmus.bmsdata.BMSData;
import com.kuna.rhythmus.bmsdata.BMSUtil;
import com.kuna.rhythmus.data.Common;
import com.kuna.rhythmus.score.ScoreData;

// OPTIONS
// 1.HARD/GROOVE/EASY
// 2.SPEED Control
// 3.Mobile/PC
// 4. JUDGE ASSIST TIME
// 5. (Button) Play / Autoplay

public class Scene_Select implements Scene {
	private Texture select;
	private BitmapFont font;
	private SelectInputListener sl;
	
	private Sprite s_bg;
	private Sprite s_bg_spr;
	private Sprite s_arrow_right;
	private Sprite s_arrow_left;
	private Sprite s_btn_autoplay;
	private Sprite s_btn_keysetting;
	private Sprite s_black;
	
	private Sprite s_bar;
	private Sprite s_bar_pos;

	private int selIndex_fixed = 0;
	public double selIndex = 0, _selIndex = 0;
	private float bgRotation = 0;
	
	private BMSList bmsList;
	
	Scene_Select_List ssList;
	Scene_FadeInOut fade;
	int exitMode;		// exitmode 1: decide, 2: keysetting
	
	private boolean initalized = false;

	@Override
	public void init() {
		// init vars
		Settings.autoplay = false;
		
		// load textures
		select = new Texture(Gdx.files.internal("data/select.png"));
		TextureRegion region;
		
		region = new TextureRegion(select, 0, 0, 800, 480);
		s_bg = new Sprite(region);
		//s_bg.setSize(800, 480);
		s_bg.setOrigin(s_bg.getWidth()/2, s_bg.getHeight()/2);
		s_bg.setPosition(0, 0);

		region = new TextureRegion(select, 256, 480, 444, 444);	// 444
		s_bg_spr = new Sprite(region);
		s_bg_spr.setOrigin(222, 222);
		s_bg_spr.setPosition(178, 18);

		s_arrow_left = new Sprite(new TextureRegion(select, 808, 274, 56, 56));
		s_arrow_right = new Sprite(new TextureRegion(select, 808, 330, 56, 56));
		s_btn_autoplay = new Sprite(new TextureRegion(select, 0, 775, 94, 20));
		s_btn_autoplay.setSize(120, 25);
		s_btn_keysetting = new Sprite(new TextureRegion(select, 97, 775, 94, 20));
		s_btn_keysetting.setSize(120, 25);
		s_arrow_left.setSize(30, 30);
		s_arrow_right.setSize(30, 30);
		
		
		region = new TextureRegion(select, 980, 0, 44, 288);
		s_bar = new Sprite(region);
		s_bar.setPosition(40, 96);
		
		region = new TextureRegion(select, 956, 0, 24, 28);
		s_bar_pos = new Sprite(region);
		
		s_black = new Sprite(new TextureRegion(select, 1022, 1022, 2, 2));
		s_black.setPosition(0, 0);
		s_black.setSize(800, 480);
		
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		
		bmsList = Rhythmus.bmsList;
		ssList = new Scene_Select_List(select, Rhythmus.bmsList);
		fade = new Scene_FadeInOut(null, new Handler() {
			@Override
			public void InformEvent(Object arg) {
				// change scene
				if (exitMode == 1) {
					Rhythmus.selPath = bmsList.bmsArr.get((int)selIndex_fixed).path;
					Rhythmus.changeScene(Rhythmus.SCENE_DECIDE);
				} else if (exitMode == 2) {
					Rhythmus.changeScene(Rhythmus.SCENE_KEYSETTING);
				}
			}
		});
		fade.doFadeIn();
		
		// get bmsList
		this.bmsList = Rhythmus.bmsList;
		
		// set input listener
		sl = new SelectInputListener();
		Gdx.input.setInputProcessor(sl);
		
		// play BGM
		playSound();
		
		// find bmsKeyIndex for matching of key-settings
		// TODO refactor code
		for (int i=0; i<match.length; i++) {
			if (Settings.key == i) {
				bmsKeyIndex = i;
				break;
			}
		}
		
		initalized = true;
	}

	@Override
	public void draw(SpriteBatch batch, DecalBatch dbatch) {
		if (!initalized) return;

		batch.begin();
		
		// draw select background
		s_bg.draw(batch);
		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);
		s_bg_spr.setRotation((TimeUtils.millis()%(360*50)) / 50.0f);
		s_bg_spr.draw(batch);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		// if loading?
		if (!bmsList.load) {
			font.setColor(Color.WHITE);
			font.draw(batch, String.format("LOADING %d%%", bmsList.loading), 100, 80);
			batch.end();
			return;
		} else {
			if (bmsList.bmsArr.isEmpty()) {
				font.setColor(Color.WHITE);
				font.draw(batch, String.format("No BMS Folder at \'SD Card/BMS\' Folder (or cannot accessible)"), 100, 80);
				batch.end();
				return;
			}
		}
		
		// draw bar and position
		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);
		s_bar.draw(batch);
		s_bar_pos.setX(50);
		s_bar_pos.setY((float) (100 + 250*selIndex/bmsList.bmsArr.size()));
		s_bar_pos.draw(batch);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		// draw song list
		ssList.selIndex = selIndex;
		ssList.draw(batch);
		
		// draw options
		// 1.guage
		String str="";
		font.setScale(1);
		s_arrow_left.setPosition(120, 340);
		s_arrow_right.setPosition(400, 340);
		switch (Settings.guagemode) {
		case Settings.GUAGE_EASY:
			str = "EASY GUAGE";
			break;
		case Settings.GUAGE_GROOVE:
			str="GROOVE GUAGE";
			break;
		case Settings.GUAGE_HARD:
			str = "HARD GUAGE";
			break;
		}
		s_arrow_left.draw(batch, 0.5f);
		s_arrow_right.draw(batch, 0.5f);
		font.drawWrapped(batch, str, 180, 360, 200, HAlignment.CENTER);
		
		// 2. speed
		s_arrow_left.setPosition(120, 280);
		s_arrow_right.setPosition(400, 280);
		s_arrow_left.draw(batch, 0.5f);
		s_arrow_right.draw(batch, 0.5f);
		font.drawWrapped(batch, String.format("x%.1f", Settings.speed), 180, 300, 200, HAlignment.CENTER);
		
		// 3. mobile/pc
		s_arrow_left.setPosition(120, 220);
		s_arrow_right.setPosition(400, 220);
		switch (Settings.key) {
		case 5:
			str = "5K";
			break;
		case 6:
			str = "5K+SC";
			break;
		case 7:
			str = "7K";
			break;
		case 8:
			str = "7K+SC";
			break;
		case 9:
			str = "7K+SC (PC Mode)";
			break;
		case 16:
			str = "14K+SC (PC Mode)";
			break;
		}
		s_arrow_left.draw(batch, 0.5f);
		s_arrow_right.draw(batch, 0.5f);
		font.drawWrapped(batch, str, 180, 240, 200, HAlignment.CENTER);
		
		// 4. judge assist
		s_arrow_left.setPosition(120, 160);
		s_arrow_right.setPosition(400, 160);
		s_arrow_left.draw(batch, 0.5f);
		s_arrow_right.draw(batch, 0.5f);
		font.drawWrapped(batch, String.format("ASSIST %dms", Settings.judgetime), 180, 180, 200, HAlignment.CENTER);
		
		// 5. button (Autoplay / play)
		s_btn_autoplay.setPosition(240, 100);
		s_btn_autoplay.draw(batch, 0.6f);
		s_btn_keysetting.setPosition(240, 65);
		s_btn_keysetting.draw(batch, 0.6f);
		
		/**********************************************************************************************/
		
		// check mouse event
		if (sl.mouseDown) {
			selIndex = selIndex_fixed + (sl.startY - sl.y) / (double)36;
			selIndex %= bmsList.bmsArr.size();
			if (selIndex>=bmsList.bmsArr.size())
				selIndex -= bmsList.bmsArr.size();
			if (selIndex<0)
				selIndex += bmsList.bmsArr.size();
			
			if ((int)_selIndex != (int)selIndex) {
				Common.snd_scratch.play();
			}
			_selIndex = selIndex;
		} else {
			selIndex_fixed = ssList.getSelectedIndex();
		}
		
		if (sl.clicked) {
			sl.clicked = false;
			if (sl.x > 480 && sl.y > Rhythmus.SCREEN_HEIGHT/2-30 && sl.y < Rhythmus.SCREEN_HEIGHT/2+30) {
				selectMusic();
			}
		}
		
		batch.end();
		
		// draw fade & out
		fade.draw(batch);
	}
	
	public void playSound() {
		Common.m_select.setLooping(true);
		Common.m_select.play();
	}
	
	private int bmsKeyIndex;
	private int[] match = new int[] {5, 6, 7, 8, 9, 16};
	public void buttonPress(int n) {
		
		switch (n){
		case 1:
			Settings.guagemode++;
			if (Settings.guagemode > Settings.GUAGE_HARD)
				Settings.guagemode = Settings.GUAGE_EASY;
			break;
		case 2:
			Settings.guagemode--;
			if (Settings.guagemode < Settings.GUAGE_EASY)
				Settings.guagemode = Settings.GUAGE_HARD;
			break;
		case 3:
			 Settings.speed-=0.2f;
			if (Settings.speed < 0) Settings.speed = 0.2f;
			break;
		case 4:
			 Settings.speed+=0.2f;
			break;
		case 5:
			bmsKeyIndex = (--bmsKeyIndex) % 6;
			if (bmsKeyIndex < 0) bmsKeyIndex+=6;
			Settings.key = match[bmsKeyIndex];
			break;
		case 6:
			bmsKeyIndex = (++bmsKeyIndex) % 6;
			if (bmsKeyIndex < 0) bmsKeyIndex+=6;
			Settings.key = match[bmsKeyIndex];
			break;
		case 7:
			Settings.judgetime -= 2;
			break;
		case 8:
			Settings.judgetime += 2;
			break;
		case 9:
			Settings.autoplay = true;
			selectMusic();
			break;
		case 10:
			selectKeysetting();
			break;
		}
		
		Common.snd_button.play();
	}
	
	public void selectMusic() {
		// stop bgm
		Common.m_select.stop();
		Common.snd_decide.play();
		
		// go to next scene (call fade)
		exitMode = 1;
		fade.doFadeOut();
	}
	
	public void selectKeysetting() {
		// stop bgm
		Common.m_select.stop();
		Common.snd_button.play();
		
		// call fade
		exitMode = 2;
		fade.doFadeOut();
	}

	@Override
	public void dispose() {
		if (select != null) select.dispose();
	}
}
