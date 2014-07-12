package com.kuna.rhythmus;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kuna.rhythmus.bmsdata.BMSData;
import com.kuna.rhythmus.score.ScoreData;

public class Scene_Select_List implements Scene_Interface {
	List<BMSData> selectArr;
	double selIndex;
	BitmapFont font;
	
	private Sprite s_sel_effect;
	
	private Sprite s_sel_noplay;
	private Sprite s_sel_easy;
	private Sprite s_sel_groove;
	private Sprite s_sel_hard;
	private Sprite s_sel_fc;
	private Sprite s_sel_fail;
	
	private BMSList bmsList;
	
	public Scene_Select_List(Texture select, BMSList b) {
		font = new BitmapFont();
		bmsList = b;
		
		s_sel_effect = new Sprite(new TextureRegion(select, 0, 988, 262, 36));
		s_sel_effect.setOrigin(s_sel_effect.getWidth()/2, s_sel_effect.getHeight()/2);
		s_sel_effect.setPosition(0, 0);
		
		s_sel_noplay = new Sprite(new TextureRegion(select, 0, 486, 256, 25));
		s_sel_easy = new Sprite(new TextureRegion(select, 0, 518, 256, 25));
		s_sel_groove = new Sprite(new TextureRegion(select, 0, 550, 256, 25));
		s_sel_hard = new Sprite(new TextureRegion(select, 0, 582, 256, 25));
		s_sel_fc = new Sprite(new TextureRegion(select, 0, 614, 256, 25));
		s_sel_fail = new Sprite(new TextureRegion(select, 0, 742, 256, 25));
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		// draw songs
		// if same folder then sort as level
		if (bmsList.bmsArr.size() <= 0)
			return;	// dont draw
		
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
		BMSData bd = bmsList.bmsArr.get(selectIndex);
		font.setScale(0.9f);
		font.draw(batch, String.format("BPM %d", bd.BPM), 100, 40);
		font.draw(batch, String.format("NOTES %d", bd.notecnt), 100, 60);
		font.draw(batch, "JUDGE", 100, 80);
		String rank_str="";
		switch (bd.rank) {
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
		ScoreData sd = Rhythmus.scoreManager.getScore(bd.hash, Settings.key);
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
		
		font.setColor(Color.WHITE);
		font.draw(batch, String.format("KEY %d", bd.key), 100, 140);
	}

	private void drawTitle(int x, int y, BMSData bd, SpriteBatch batch) {
		Sprite s = null;
		
		ScoreData sd = Rhythmus.scoreManager.getScore(bd.hash, Settings.key);
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

		int diff = bd.difficulty;
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
		font.draw(batch, Integer.toString(bd.playlevel), 500, y);
		
		font.setColor(Color.WHITE);
		font.draw(batch, bd.title, 530, y);
	}
	
	public int getSelectedIndex() {
		return (int)selIndex;
	}
}
