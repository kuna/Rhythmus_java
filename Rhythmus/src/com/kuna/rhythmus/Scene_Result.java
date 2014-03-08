package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.kuna.rhythmus.score.ScoreData;

public class Scene_Result {
	private ScoreData s;
	private BitmapFont font;
	private ResultInputListener rl;
	private ShapeRenderer r;
	
	private Texture t_res;
	private Sprite[] s_rank = new Sprite[10];
	private Sprite s_clear;
	private Sprite s_fail;
	private Sprite s_black;
	
	private long time_now;
	private long time_init;
	private long time_end;
	
	private Sound sClear;
	private Sound sFail;
	
	private boolean initalized = false;
	
	public void init() {
		// update score
		s = new ScoreData();
		s.hash = Rhythmus.bmsParser.hash;
		s.note = Rhythmus.bmsParser.notecnt;
		s.pg = Rhythmus.sPlay.pg;
		s.gr = Rhythmus.sPlay.gr;
		s.gd = Rhythmus.sPlay.gd;
		s.pr = Rhythmus.sPlay.pr;
		s.bd = Rhythmus.sPlay.bd;
		s.combo = Rhythmus.sPlay.maxcombo;
		s.clear = ScoreData.CLEAR_FAILED;
		switch (Settings.guagemode) {
		case Settings.GUAGE_GROOVE:
			if (Rhythmus.sPlay.guage > 80)
				s.clear = ScoreData.CLEAR_GROOVE;
			break;
		case Settings.GUAGE_EASY:
			if (Rhythmus.sPlay.guage > 80)
				s.clear = ScoreData.CLEAR_EASY;
			break;
		case Settings.GUAGE_HARD:
			if (Rhythmus.sPlay.guage > 0) {
				s.clear = ScoreData.CLEAR_HARD;
			}
			break;
		}
		if (s.note == s.combo)
			s.clear = ScoreData.CLEAR_FULLCOMBO;
		
		// save score when not autoplay
		if (Settings.autoplay == 0)
			Rhythmus.scoreManager.addScore(s);
		
		// spr & fonts
		t_res = new Texture(Gdx.files.internal("data/result.png"));
		for (int i=0; i<8; i++) {
			s_rank[i] = new Sprite(new TextureRegion(t_res, 0, 80+80*i, 172, 80));
		}
		s_clear = new Sprite(new TextureRegion( t_res, 0, 0, 640, 20 ));
		s_fail = new Sprite(new TextureRegion( t_res, 0, 22, 640, 20 ));
		s_black = new Sprite(new TextureRegion(t_res, 1022, 1022, 2, 2));
		s_black.setPosition(0, 0);
		s_black.setSize(800, 480);
		
		font = new BitmapFont();
		
		rl = new ResultInputListener();
		Gdx.input.setInputProcessor(rl);
		
		r = new ShapeRenderer();
		
		// load BGM
		sClear = Gdx.audio.newSound(Gdx.files.internal("data/clear.ogg"));
		sFail = Gdx.audio.newSound(Gdx.files.internal("data/fail.ogg"));
		if (s.clear > ScoreData.CLEAR_FAILED)
			BMSUtil.PlayUntilLoad(sClear, 5);
		else
			BMSUtil.PlayUntilLoad(sFail, 5);
		
		time_end = 0;
		time_init = TimeUtils.millis();
		
		initalized = true;
	}
	
	public void draw(SpriteBatch batch) {
		if (!initalized) return;
		
		time_now = TimeUtils.millis();
		float rate = s.getRate();
		
		// failed or clear
		if (s.clear > ScoreData.CLEAR_FAILED) {
			s_clear.setPosition(80, 0);
			s_clear.draw(batch);
		} else {
			s_fail.setPosition(80, 0);
			s_fail.draw(batch);
		}
		
		// show score
		font.setScale(1.0f);
		font.setColor(Color.WHITE);
		font.draw(batch, String.format("Notes: %d", s.getTotalNote()), 100, 400);
		font.draw(batch, String.format("EXScore: %d/%d", s.getEXScore(), s.getTotalNote()*2), 100, 360);
		font.draw(batch, String.format("Rate: %.2f", s.getRate()), 100, 320);
		font.draw(batch, String.format("PGREAT: %d", s.pg), 100, 280);
		font.draw(batch, String.format("GREAT: %d", s.gr), 100, 240);
		font.draw(batch, String.format("GOOD: %d", s.gd), 100, 200);
		font.draw(batch, String.format("POOR: %d", s.pr), 100, 160);
		font.draw(batch, String.format("BAD: %d", s.bd), 100, 120);
		font.draw(batch, String.format("COMBO: %d", s.combo), 100, 80);
		
		font.draw(batch, "Rank", 400, 400);
		font.setScale(5.0f);
		String rank="";
		Sprite s;
		if (rate > 8.0f/9) {
			s = s_rank[0];
		} else if (rate > 7.0f/9) {
			s = s_rank[1];
		} else if (rate > 6.0f/9) {
			s = s_rank[2];
		} else if (rate > 5.0f/9) {
			s = s_rank[3];
		} else if (rate > 4.0f/9) {
			s = s_rank[4];
		} else if (rate > 3.0f/9) {
			s = s_rank[5];
		} else if (rate > 2.0f/9) {
			s = s_rank[6];
		} else {
			s = s_rank[7];
		}
		s.setPosition(400, 160);
		s.setSize(344, 160);
		s.draw(batch);
		
		// fade in and fade out
		if (time_now-time_init < 1000) {
			float a = ((float)(time_now-time_init))/1000;
			a = 1-a;
			s_black.draw(batch, a);
		}
		if (time_end>0) {
			if (time_now-time_end < 1000) {
				float a = ((float)(time_now-time_end))/1000;
				if (a>1) a=1;
				s_black.draw(batch, a);
			} else {
				// save score when not autoplay
				Rhythmus.scoreManager.SaveScore();
				
				// move scene
				Rhythmus.changeScene(Rhythmus.SCENE_SELECT);
			}
		}
	}
	
	public void exitResult() {
		if (time_now-time_init<1000) return;
		if (time_end > 0) return;
		time_end = TimeUtils.millis();
	}
	
	public void dispose() {
		if (t_res != null) t_res.dispose();
		if (sClear != null) sClear.dispose();
		if (sFail!=null) sFail.dispose();
	}
}
