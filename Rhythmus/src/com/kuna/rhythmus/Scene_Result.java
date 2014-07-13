package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.kuna.rhythmus.bmsdata.BMSUtil;
import com.kuna.rhythmus.data.Common;
import com.kuna.rhythmus.score.ScoreData;

public class Scene_Result implements Scene {
	private ScoreData s;
	private BitmapFont font;
	private ResultInputListener rl;
	
	private Texture t_res;
	private Sprite[] s_rank = new Sprite[10];
	private Sprite s_clear;
	private Sprite s_fail;
	private Sprite s_black;
	
	private boolean initalized = false;
	
	Scene_FadeInOut fade;
	
	@Override
	public void init() {
		// update score
		s = Rhythmus.sPlay.spJudge.getScoreData();
		
		if (s.save)
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
		
		fade = new Scene_FadeInOut(null, new Handler() {
			@Override
			public void InformEvent(Object arg) {
				// exit result window
				Common.m_clear.stop();
				Common.m_fail.stop();
				exitResult();
			}
		});
		// basic: do fade in
		fade.doFadeIn();
		
		// load BGM
		if (s.clear > ScoreData.CLEAR_FAILED)
			Common.m_clear.play();
		else
			Common.m_fail.play();
		
		initalized = true;
	}
	
	@Override
	public void draw(SpriteBatch batch, DecalBatch dbatch) {
		if (!initalized) return;
		
		batch.begin();
		
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
		
		batch.end();
		
		// fade in and fade out
		fade.draw(batch);
	}
	
	public void exitResult() {
		
		// save score when not autoplay
		Rhythmus.scoreManager.SaveScore();
		
		// move scene
		Rhythmus.changeScene(Rhythmus.SCENE_SELECT);
	}
	
	@Override
	public void dispose() {
		if (t_res != null) t_res.dispose();
	}
}
