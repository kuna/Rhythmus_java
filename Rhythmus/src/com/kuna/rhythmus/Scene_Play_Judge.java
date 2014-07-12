package com.kuna.rhythmus;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;
import com.kuna.rhythmus.score.ScoreData;


/*
 * not only judge,
 * also contains about score information
 */
public class Scene_Play_Judge implements Scene_Interface {
	public final static int JUDGE_PGREAT = 1;
	public final static int JUDGE_GREAT = 2;
	public final static int JUDGE_GOOD = 3;
	public final static int JUDGE_BAD = 4;
	public final static int JUDGE_POOR = 5;

	public int judgeStandardtime = Settings.JUDGE_EASY;

	public int pg=0;
	public int gr=0;
	public int gd=0;
	public int pr=0;
	public int bd=0;
	public int maxcombo=0;
	public int score = 0;
	public int combo;
	private float guage;

	private Scene_Play_BGA spBGA;
	private Scene_Play_FullCombo full;
	
	private Scene_Play_JudgeDraw Judge1P, Judge2P;
	
	public Scene_Play_Judge(Texture t_play, Scene_Play_BGA spBGA, Scene_Play_FullCombo full, int x, int y, float scale) {
		this.spBGA = spBGA;
		this.full = full;

		Judge1P = new Scene_Play_JudgeDraw(t_play, x, y, scale);
		if (Settings.key == 16)
			Judge2P = new Scene_Play_JudgeDraw(t_play, x+Scene_Play_Setting.lainMargin + Scene_Play_Setting.lainWidth, y, scale);
		
		// set judge time
		switch (Rhythmus.bmsData.rank) {
		case 0:	// VERY HARD
			judgeStandardtime = Settings.JUDGE_VERYHARD;
			break;
		case 1:
			judgeStandardtime = Settings.JUDGE_HARD;
			break;
		case 2:
			judgeStandardtime = Settings.JUDGE_NORMAL;
			break;
		case 3:
			judgeStandardtime = Settings.JUDGE_EASY;
			break;
		}
	}
	
	public int getJudgePGREATTime() {
		return judgeStandardtime;
	}
	
	public int getJudgeGREATTime() {
		return judgeStandardtime*2;
	}
	
	public int getJudgeGOODTime() {
		return judgeStandardtime*4;
	}
	
	public int getJudgeBADTime() {
		return judgeStandardtime*6;
	}
	
	public ScoreData getScoreData() {
		ScoreData s = new ScoreData();
		
		s.hash = Rhythmus.bmsData.hash;
		s.note = Rhythmus.bmsData.notecnt;
		s.pg = pg;
		s.gr = gr;
		s.gd = gd;
		s.pr = pr;
		s.bd = bd;
		s.combo = maxcombo;
		s.clear = ScoreData.CLEAR_FAILED;
		s.key = Settings.key;
		switch (Settings.guagemode) {
		case Settings.GUAGE_GROOVE:
			if (guage > 80)
				s.clear = ScoreData.CLEAR_GROOVE;
			break;
		case Settings.GUAGE_EASY:
			if (guage > 80)
				s.clear = ScoreData.CLEAR_EASY;
			break;
		case Settings.GUAGE_HARD:
			if (guage > 0) {
				s.clear = ScoreData.CLEAR_HARD;
			}
			break;
		}
		if (s.note == s.combo)
			s.clear = ScoreData.CLEAR_FULLCOMBO;
		
		// save score when not autoplay
		if (!Settings.autoplay)
			s.save = true;
		
		return s;
	}

	public void judge(int num, int player) {
		switch(num){
		case JUDGE_PGREAT:
			switch (Settings.guagemode) {
			case Settings.GUAGE_HARD:
				guage += 0.1f;
				break;
			case Settings.GUAGE_GROOVE:
				guage += 1.0f * ((float)Rhythmus.bmsData.total/Rhythmus.bmsData.notecnt);
				break;
			case Settings.GUAGE_EASY:
				guage += 1.2f * ((float)Rhythmus.bmsData.total/Rhythmus.bmsData.notecnt);
				break;
			}
			combo++;
			pg++;
			break;
		case JUDGE_GREAT:
			switch (Settings.guagemode) {
			case Settings.GUAGE_HARD:
				guage += 0.1f;
				break;
			case Settings.GUAGE_GROOVE:
				guage += 1.0f * ((float)Rhythmus.bmsData.total/Rhythmus.bmsData.notecnt);
				break;
			case Settings.GUAGE_EASY:
				guage += 1.2f * ((float)Rhythmus.bmsData.total/Rhythmus.bmsData.notecnt);
				break;
			}
			combo++;
			gr++;
			break;
		case JUDGE_GOOD:
			switch (Settings.guagemode) {
			case Settings.GUAGE_HARD:
				guage += 0.05f;
				break;
			case Settings.GUAGE_GROOVE:
				guage += 0.5f * ((float)Rhythmus.bmsData.total/Rhythmus.bmsData.notecnt);
				break;
			case Settings.GUAGE_EASY:
				guage += 0.6f * ((float)Rhythmus.bmsData.total/Rhythmus.bmsData.notecnt);
				break;
			}
			combo++;
			gd++;
			break;
		case JUDGE_BAD:
			switch (Settings.guagemode) {
			case Settings.GUAGE_HARD:
				guage -= 6;
				break;
			case Settings.GUAGE_GROOVE:
				guage -= 4;
				break;
			case Settings.GUAGE_EASY:
				guage -= 3.2f;
				break;
			}
			combo=0;
			bd++;
			// call miss
			if (spBGA != null) spBGA.miss();
			break;
		case JUDGE_POOR:
			switch (Settings.guagemode) {
			case Settings.GUAGE_HARD:
				guage -= 10;
				break;
			case Settings.GUAGE_GROOVE:
				guage -= 6;
				break;
			case Settings.GUAGE_EASY:
				guage -= 4.8f;
				break;
			}
			combo=0;
			pr++;
			// call miss
			if (spBGA != null) spBGA.miss();
			break;
		}
		if (guage > 100) guage = 100;
		if (guage <= 0) {
			if (Settings.guagemode == Settings.GUAGE_HARD)
				Scene_Play.exitGame(2);	// static method
			guage = 0;
		}
		
		if (maxcombo < combo) maxcombo = combo;
		
		// set judge num (judge = num)
		if (player == 1) {
			Judge1P.setCombo(combo);
			Judge1P.setJudge(num);
			Judge1P.setJudgeTime();
		} else if (Judge2P != null) {
			Judge2P.setCombo(combo);
			Judge2P.setJudge(num);
			Judge2P.setJudgeTime();
		}
		
		// is full combo?
		if (combo == Rhythmus.bmsData.notecnt) {
			full.doFullCombo();
		}
	}
	
	public void setGuage(int val) {
		guage = val;
	}
	
	public float getGuage() {
		return guage;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		Judge1P.draw(batch);
		if (Judge2P != null)
			Judge2P.draw(batch);
	}
}
