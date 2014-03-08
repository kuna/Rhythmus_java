package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
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
	private Sprite s_sel_noplay;
	private Sprite s_sel_easy;
	private Sprite s_sel_groove;
	private Sprite s_sel_hard;
	private Sprite s_sel_fc;
	private Sprite s_sel_fail;
	private Sprite s_sel_effect;
	private Sprite s_arrow_right;
	private Sprite s_arrow_left;
	private Sprite s_btn_autoplay;
	private Sprite s_black;
	private ShapeRenderer fadeoutRenderer;
	
	private Sprite s_bar;
	private Sprite s_bar_pos;

	private int selIndex_fixed = 0;
	private double selIndex = 0, _selIndex = 0;
	public String selPath = "";
	private float bgRotation = 0;
	private float fadeOutAlpha = 0;
	
	private BMSList bmsList;
	private Timer timer;
	
	private Sound snd_select, snd_decide, snd_scratch;
	private Sound snd_btn;
	
	private boolean initalized = false;

	@Override
	public void init() {
		// init vars
		Settings.autoplay = 0;
		fadeOutAlpha = 0;
		
		// load sound
		snd_select = Gdx.audio.newSound(Gdx.files.internal("data/select.ogg"));
		snd_decide = Gdx.audio.newSound(Gdx.files.internal("data/decide.ogg"));
		snd_scratch = Gdx.audio.newSound(Gdx.files.internal("data/scratch.wav"));
		snd_btn = Gdx.audio.newSound(Gdx.files.internal("data/change.wav"));
		
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
		
		s_sel_noplay = new Sprite(new TextureRegion(select, 0, 486, 256, 25));
		s_sel_easy = new Sprite(new TextureRegion(select, 0, 518, 256, 25));
		s_sel_groove = new Sprite(new TextureRegion(select, 0, 550, 256, 25));
		s_sel_hard = new Sprite(new TextureRegion(select, 0, 582, 256, 25));
		s_sel_fc = new Sprite(new TextureRegion(select, 0, 614, 256, 25));
		s_sel_fail = new Sprite(new TextureRegion(select, 0, 742, 256, 25));

		s_arrow_left = new Sprite(new TextureRegion(select, 808, 274, 56, 56));
		s_arrow_right = new Sprite(new TextureRegion(select, 808, 330, 56, 56));
		s_btn_autoplay = new Sprite(new TextureRegion(select, 0, 775, 94, 20));
		s_btn_autoplay.setSize(120, 25);
		s_arrow_left.setSize(30, 30);
		s_arrow_right.setSize(30, 30);
		
		region = new TextureRegion(select, 0, 988, 262, 36);
		s_sel_effect = new Sprite(region);
		s_sel_effect.setOrigin(s_sel_effect.getWidth()/2, s_sel_effect.getHeight()/2);
		s_sel_effect.setPosition(0, 0);
		
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
		
		fadeoutRenderer = new ShapeRenderer();
		
		// set Timer
		timer = new Timer();
		timer.scheduleTask(new Task() {
			@Override
			public void run() {
				// rotate image
				bgRotation += 0.5f;
				s_bg_spr.setRotation(bgRotation);
			}
		}, 0, 0.02f);
		timer.start();
		
		// get bmsList
		this.bmsList = Rhythmus.bmsList;
		
		// set input listener
		sl = new SelectInputListener();
		Gdx.input.setInputProcessor(sl);
		
		// play BGM
		playSound();
		
		initalized = true;
	}

	@Override
	public void draw(SpriteBatch batch) {
		if (!initalized) return;
		
		// draw select background
		s_bg.draw(batch);
		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);
		s_bg_spr.draw(batch);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		// if loading?
		if (!bmsList.load) {
			font.setColor(Color.WHITE);
			font.draw(batch, String.format("LOADING %d%%", bmsList.loading), 100, 80);
			return;
		} else {
			if (bmsList.bmsArr.isEmpty()) {
				font.setColor(Color.WHITE);
				font.draw(batch, String.format("No BMS Folder at \'SD Card/BMS\' Folder (or cannot accessible)"), 100, 80);
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
		
		// draw songs
		// if same folder then sort as level
		int selectIndex=(int)selIndex;
		int sel = selectIndex;
		int itemHeight = 36;
		boolean first = true;
		font.setScale(1.0f);
		for (int y=Rhythmus.SCREEN_HEIGHT/2 + (int)(itemHeight*(selIndex % 1)); y<Rhythmus.SCREEN_HEIGHT; y += itemHeight) {
			drawTitle(480, y-itemHeight/2, bmsList.bmsArr.get(sel), batch);

			if (first) {
				batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);
				s_sel_effect.setX(476);
				s_sel_effect.setY(y - 25);
				s_sel_effect.draw(batch);
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				first = false;
			}
			
			sel--;
			if (sel < 0)
				sel = bmsList.bmsArr.size()-1;
		}
		
		sel = selectIndex+1;	// next item
		if (sel >= bmsList.bmsArr.size())
			sel = 0;
		
		for (int y=Rhythmus.SCREEN_HEIGHT/2-itemHeight + (int)(itemHeight*(selIndex % 1)); y>-itemHeight; y -= itemHeight) {
			drawTitle(480, y-itemHeight/2, bmsList.bmsArr.get(sel), batch);

			sel++;
			if (sel >= bmsList.bmsArr.size())
				sel = 0;
		}
		
		// draw information of the selected song
		BMSParser bp = bmsList.bmsArr.get(selectIndex);
		font.setScale(0.9f);
		font.draw(batch, String.format("BPM %d", bp.BPM), 100, 40);
		font.draw(batch, String.format("NOTES %d", bp.notecnt), 100, 60);
		font.draw(batch, "JUDGE", 100, 80);
		String rank_str="";
		switch (bp.rank) {
		case 0:	// VERY HARD
			rank_str = "VERY HARD";
			font.setColor(1, 0, 0, 1);
		case 1:	// HARD
			rank_str = "HARD";
			font.setColor(0.8f, 0.5f, 0.5f, 1);
		case 2:	// NORMAL
			rank_str = "GROOVE";
			font.setColor(0.5f, 0.5f, 0.8f, 1);
		case 3:	// EASY
			rank_str = "EASY";
			font.setColor(0.5f, 0.8f, 0.5f, 1);
		}
		font.draw(batch, rank_str, 150, 80);
		ScoreData sd = Rhythmus.scoreManager.getScore(bp.hash);
		if (sd != null) {
			String clr_str = "";
			switch (sd.clear) {
			case ScoreData.CLEAR_NONE:
				clr_str = "NO PLAY";
				font.setColor(Color.WHITE);
				break;
			case ScoreData.CLEAR_FAILED:
				clr_str = "FAILED";
				font.setColor(0.5f, 0.3f, 0.3f, 1);
				break;
			case ScoreData.CLEAR_EASY:
				clr_str = "EASY CLEAR";
				font.setColor(0.5f, 0.8f, 0.5f, 1);
				break;
			case ScoreData.CLEAR_GROOVE:
				clr_str = "GROOVE CLEAR";
				font.setColor(0.8f, 0.5f, 0.8f, 1);
				break;
			case ScoreData.CLEAR_HARD:
				clr_str = "HARD CLEAR";
				font.setColor(0.8f, 0.5f, 0.5f, 1);
				break;
			case ScoreData.CLEAR_FULLCOMBO:
				clr_str = "FULLCOMBO";
				font.setColor(1, 1, 0.5f, 1);
				break;
			}
			font.draw(batch, clr_str, 100, 100);
			
			font.setColor(Color.WHITE);
			font.draw(batch, String.format("RANK %s (%.0f)", ScoreData.GetRateString(sd.getRate()), sd.getRate()*100), 100, 120);
		} else {
			font.setColor(Color.WHITE);
			font.draw(batch, "NO RANK", 100, 120);
			font.draw(batch, "NO PLAY", 100, 100);
		}
		
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
		switch (Settings.bmsmode) {
		case Settings.MODE_MOBILE:
			str = "Mobile Mode";
			break;
		case Settings.MODE_PAD:
			str = "Mobile Mode 2";
			break;
		case Settings.MODE_PC:
			str = "PC Mode";
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
		s_btn_autoplay.draw(batch, 0.8f);
		
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
				snd_scratch.play();
			}
			_selIndex = selIndex;
		} else {
			selIndex_fixed = selectIndex;
		}
		
		if (sl.clicked) {
			sl.clicked = false;
			if (sl.x > 480 && sl.y > Rhythmus.SCREEN_HEIGHT/2-30 && sl.y < Rhythmus.SCREEN_HEIGHT/2+30) {
				selectMusic();
			}
		}
		
		if (fadeOutAlpha > 0) {
			if (fadeOutAlpha > 1) fadeOutAlpha=1;

			s_black.draw(batch, fadeOutAlpha);
		}
	}
	
	private void drawTitle(int x, int y, BMSParser bp, SpriteBatch batch) {
		Sprite s = null;
		ScoreData sd = Rhythmus.scoreManager.getScore(bp.hash);
		if (sd == null || sd.clear == ScoreData.CLEAR_NONE) {
			s = s_sel_noplay;
		} else if (sd.clear == ScoreData.CLEAR_EASY) {
			s = s_sel_easy;
		} else if (sd.clear == ScoreData.CLEAR_GROOVE) {
			s = s_sel_groove;
		} else if (sd.clear == ScoreData.CLEAR_HARD) {
			s = s_sel_hard;
		} else if (sd.clear == ScoreData.CLEAR_FULLCOMBO) {
			s = s_sel_fc;
		} else if (sd.clear == ScoreData.CLEAR_FAILED) {
			s = s_sel_fail;
		}
		
		s.setX(x);
		s.setY(y);
		s.draw(batch);

		int diff = bp.difficulty;
		switch (diff) {
		case 1:
			font.setColor(Color.GREEN);
			break;
		case 2:
			font.setColor(Color.BLUE);
			break;
		case 3:
			font.setColor(Color.YELLOW);
			break;
		case 4:
			font.setColor(Color.RED);
			break;
		case 5:
			font.setColor(Color.toFloatBits(180, 60, 180, 255));
			break;
		}
		y += 18;
		font.draw(batch, Integer.toString(bp.playlevel), 500, y);
		
		font.setColor(Color.WHITE);
		font.draw(batch, bp.title, 530, y);
	}
	
	public void playSound() {
		BMSUtil.LoopUntilLoad(snd_select, 20);
	}
	
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
			 Settings.speed-=0.5f;
			if (Settings.speed < 0) Settings.speed+=0.5f;
			break;
		case 4:
			 Settings.speed+=0.5f;
			break;
		case 5:
			Settings.bmsmode--;
			if (Settings.bmsmode < Settings.MODE_MOBILE)
				Settings.bmsmode = Settings.MODE_PC;
			break;
		case 6:
			Settings.bmsmode++;
			if (Settings.bmsmode > Settings.MODE_PC)
				Settings.bmsmode = Settings.MODE_MOBILE;
			break;
		case 7:
			Settings.judgetime -= 2;
			break;
		case 8:
			Settings.judgetime += 2;
			break;
		case 9:
			Settings.autoplay = 1;
			selectMusic();
			break;
		}
		
		snd_btn.play();
	}
	
	private void selectMusic() {
		// stop bgm
		snd_select.stop();
		snd_decide.play();
		
		// go to next scene
		timer.scheduleTask(new Task() {
			@Override
			public void run() {
				fadeOutAlpha += 0.02f;
				if (fadeOutAlpha > 1) {
					selPath = bmsList.bmsArr.get((int)selIndex_fixed).path;
					Rhythmus.changeScene(Rhythmus.SCENE_DECIDE);
					this.cancel();
				}
			}
		}, 0, 0.02f);
	}

	@Override
	public void dispose() {
		if (timer != null) timer.stop();
		if (select != null) select.dispose();
		if (fadeoutRenderer != null) fadeoutRenderer.dispose();
		if (snd_select != null) snd_select.dispose();
		if (snd_decide != null) snd_decide.dispose();
	}
}
