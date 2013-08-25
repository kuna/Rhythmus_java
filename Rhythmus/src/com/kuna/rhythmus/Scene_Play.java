package com.kuna.rhythmus;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kuna.rhythmus.score.ScoreData;

public class Scene_Play {
	// const
	public final static int JUDGE_PGREAT = 1;
	public final static int JUDGE_GREAT = 2;
	public final static int JUDGE_GOOD = 3;
	public final static int JUDGE_BAD = 4;
	public final static int JUDGE_POOR = 5;
	
	
	// texture & sprite
	private Texture t_play;
	private Texture t_bom;	// 16 frame, 240, 200
	
	private Sprite s_lain;
	private Sprite s_note_scr;
	private Sprite s_note1;
	private Sprite s_note2;
	private Sprite[] s_bom = new Sprite[16];
	private Sprite s_effect1;
	private Sprite s_effect2;
	private Sprite s_effect3;
	private Sprite[] judgespr = new Sprite[10];
	private Sprite[] judgenum = new Sprite[40];
	private Sprite s_white;
	private Sprite s_prs_normal;
	private Sprite s_prs_press;
	private Sprite s_btn_up;
	private Sprite s_btn_down;
	private Sprite s_btn_exit;
	private Sprite s_guage_hard;
	private Sprite s_guage_normal;
	private Sprite s_black;
	
	private Sound sButton;
	
	private BitmapFont font;
	private ShapeRenderer r;
	
	private int[] noteX = new int[20];
	private int[] noteWidth = new int[8];
	private int bomOffsetX, bomOffsetY;
	private long[] bomTime = new long[10];	// when bom started?
	private long[] pressTime = new long[10];	// when last pressed?
	private long missTime;
	private int[] effectWidth = new int[3];
	private int effectHeight;
	private int leftPos, rightPos, lainWidth;
	private int[] notePress = new int[8];
	private int lainheight = 480;
	private int bottompos = 0;
	private int prs_left;
	private int[] prs_width = new int[8];
	private int prs_height;
	private Sprite bga_miss = null;
	private Sprite bga_now = null;
	private Sprite bga_overlay = null;
	
	PlayInputListener pl;
	
	// bms
	private double beat = 0;
	private double bpm = 0;
	public int pg=0;
	public int gr=0;
	public int gd=0;
	public int pr=0;
	public int bd=0;
	public int maxcombo=0;
	public int combo=0;
	public int score = 0;
	private int judge=0;
	public float guage;
	
	public float speed = 3;
	private int judgetime = Settings.JUDGE_EASY;
	public int autoplay = 0;
	public int exitmode = 0;	// 0 is normal, 1 is interrupted(cancel), 2 is failed
	
	// timer
	private long startTime;
	private long nowTime;
	private int eclipsedTime;
	private long judgeTime;
	private long forceexittime;
	
	// touch
	private boolean[] touch = new boolean [8];
	
	public void init() {
		score = 0;
		pg=gr=gd=bd=pr=0;
		eclipsedTime = 0;
		
		switch (Settings.guagemode) {
		case Settings.GUAGE_EASY:
		case Settings.GUAGE_GROOVE:
			guage = 20;
			break;
		case Settings.GUAGE_HARD:
			guage = 100;
			break;
		}
		
		switch (Rhythmus.bmsParser.rank) {
		case 0:	// VERY HARD
			judgetime = Settings.JUDGE_VERYHARD;
			break;
		case 1:
			judgetime = Settings.JUDGE_HARD;
			break;
		case 2:
			judgetime = Settings.JUDGE_NORMAL;
			break;
		case 3:
			judgetime = Settings.JUDGE_EASY;
			break;
		}
		
		t_play = new Texture(Gdx.files.internal("data/play.png"));
		t_bom = new Texture(Gdx.files.internal("data/bom.png"));
		TextureRegion region;
		font = new BitmapFont();
		
		// 
		region = new TextureRegion(t_play, 764, 267, 260, 2);
		s_lain = new Sprite(region);
		s_white = new Sprite(new TextureRegion(t_play, 764, 267, 2, 2));

		region = new TextureRegion(t_play, 0, 264, 53, 8);
		s_note_scr = new Sprite(region);
		region = new TextureRegion(t_play, 0, 273, 30, 8);
		s_note1 = new Sprite(region);
		region = new TextureRegion(t_play, 0, 282, 23, 8);
		s_note2 = new Sprite(region);

		region = new TextureRegion(t_play, 0, 0, 29, 255);
		s_effect1 = new Sprite(region);
		region = new TextureRegion(t_play, 31, 0, 17, 255);
		s_effect2 = new Sprite(region);
		region = new TextureRegion(t_play, 49, 0, 13, 255);
		s_effect3 = new Sprite(region);

		s_prs_press = new Sprite(new TextureRegion(t_play, 0, 340, 31, 40));
		s_prs_normal = new Sprite(new TextureRegion(t_play, 31, 340, 31, 40));
		s_btn_up = new Sprite(new TextureRegion(t_play, 453, 0, 55, 54));
		s_btn_down = new Sprite(new TextureRegion(t_play, 398, 0, 55, 54));
		s_btn_exit = new Sprite(new TextureRegion(t_play, 513, 0, 54, 54));
		s_guage_normal = new Sprite(new TextureRegion(t_play, 63, 332, 15, 4));
		s_guage_hard = new Sprite(new TextureRegion(t_play, 63, 336, 15, 4));
		
		s_black = new Sprite(new TextureRegion(t_play, 1022, 1022, 2, 2));
		s_black.setPosition(0, 0);
		s_black.setSize(800, 480);
		
		// load bom
		for (int i=0; i<16; i++) {
			region = new TextureRegion(t_bom, 200*i, 0, 200, 240);
			s_bom[i] = new Sprite(region);
		}
		for (int i=0; i<10; i++) {
			pressTime[i] = bomTime[i] = -1000;	// initalization
		}
		
		// load judge
		judgespr[0] = new Sprite(new TextureRegion(t_play, 64, 1, 80, 33));		// PGREAT
		judgespr[1] = new Sprite(new TextureRegion(t_play, 64, 36, 80, 33));	// PGREAT
		judgespr[2] = new Sprite(new TextureRegion(t_play, 64, 71, 80, 33));	// PGREAT
		judgespr[3] = new Sprite(new TextureRegion(t_play, 64, 107, 80, 33));	// GREAT
		judgespr[4] = new Sprite(new TextureRegion(t_play, 71, 178, 60, 33));	// GOOD
		judgespr[5] = new Sprite(new TextureRegion(t_play, 74, 214, 60, 33));	// POOR
		judgespr[6] = new Sprite(new TextureRegion(t_play, 81, 250, 40, 33));	// BAD
		int _top[] = {1, 36, 71, 107};
		for (int i=0; i<4; i++) {
			for (int j=0; j<10; j++) {
				judgenum[i*10 + j] = new Sprite(new TextureRegion(t_play, 150+j*17, _top[i], 17, 33));
			}
		}
		
		switch (Settings.bmsmode) {
		case Settings.MODE_MOBILE:
			init_Mobile();
			break;
		case Settings.MODE_PAD:
			init_Pad();
			break;
		case Settings.MODE_PC:
			init_PC();
			break;
		}
		
		sButton = Gdx.audio.newSound(Gdx.files.internal("data/change.wav"));

		//ShapeRenderer
		r = new ShapeRenderer();
		
		// check out start time
		startTime = TimeUtils.millis();
		
		// set input listener
		pl = new PlayInputListener();
		Gdx.input.setInputProcessor(pl);
	}
	
	private void init_Mobile() {
		// default args for setting
		// bottom pos (lain start)
		bottompos = 80;
		prs_height = bottompos;
		lainheight = Rhythmus.SCREEN_HEIGHT - bottompos;
		// note sizes (width)
		int wid_scr = 130;
		int wid_note1 = 80;
		int wid_note2 = 62;
		lainWidth = wid_scr+wid_note1*4+wid_note2*3;	// lain width
		int wid_start = leftPos = (800-lainWidth)/2;	// lain X pos
		rightPos = leftPos + lainWidth;					// lain end pos (X)
		prs_left = 0;
		for (int i=0; i<8; i++)							// press(bottom) X pos
			prs_width[i] = 100;
		
		// set notes size
		s_note_scr.setSize(wid_scr, 20);
		s_note1.setSize(wid_note1, 20);
		s_note2.setSize(wid_note2, 20);

		// set press effect size
		effectWidth[0] = (int) wid_scr;
		effectWidth[1] = (int) wid_note1;
		effectWidth[2] = (int) wid_note2;
		effectHeight = 400;
		
		// set size of prs
		s_prs_normal.setSize(100, bottompos);
		s_prs_press.setSize(100, bottompos);
		
		// set size & pos of up, down, exit
		s_btn_up.setSize(40, 40);
		s_btn_down.setSize(40, 40);
		s_btn_exit.setSize(40, 40);
		s_btn_up.setPosition(20, 400);
		s_btn_down.setPosition(20, 320);
		s_btn_exit.setPosition(740, 400);
		
		// set bom effect size
		for (int i=0; i<16; i++) {
			s_bom[i].scale(2);
		}
		bomOffsetX = -66;
		bomOffsetY = -112;
		
		// set judge size
		for (int i=0; i<=6; i++)
			judgespr[i].scale(1);
		for (int i=0; i<4; i++) {
			for (int j=0; j<10; j++) {
				judgenum[i*10 + j].scale(1);
			}
		}
		
		noteWidth[0] = wid_scr;
		noteWidth[1] = noteWidth[3] = noteWidth[5] = noteWidth[7] = wid_note1;
		noteWidth[2] = noteWidth[4] = noteWidth[6] = wid_note2;
		
		// set note's x pos
		noteX[10] = noteX[6] = wid_start;
		noteX[11] = noteX[1] = noteX[6] + wid_scr;
		noteX[12] = noteX[2] = noteX[1] + wid_note1;
		noteX[13] = noteX[3] = noteX[2] + wid_note2;
		noteX[14] = noteX[4] = noteX[3] + wid_note1;
		noteX[15] = noteX[5] = noteX[4] + wid_note2;
		noteX[16] = noteX[8] = noteX[5] + wid_note1;
		noteX[17] = noteX[9] = noteX[8] + wid_note2;

		// set lain size
		s_lain.setSize(lainWidth, lainheight);
		s_lain.setPosition(wid_start, bottompos);
	}
	
	private void init_Pad() {
		// WILL NOT BE IMPLEMENTED ...
		// mobile type 2
		init_Mobile();
		
		prs_left = noteX[10];
		for (int i=0; i<8; i++)
			prs_width[i] = noteWidth[i];
	}
	
	private void init_PC() {
		// default args for setting
		// bottom pos (lain start)
		bottompos = 80;
		lainheight = Rhythmus.SCREEN_HEIGHT - bottompos;
		// note sizes (width)
		int wid_scr = 72;
		int wid_note1 = 42;
		int wid_note2 = 34;
		lainWidth = wid_scr+wid_note1*4+wid_note2*3;	// lain width
		int wid_start = leftPos = 400;	// lain X pos
		rightPos = leftPos + lainWidth;					// lain end pos (X)
		
		// set notes size
		s_note_scr.setSize(wid_scr, 12);
		s_note1.setSize(wid_note1, 12);
		s_note2.setSize(wid_note2, 12);

		// set press effect size
		effectWidth[0] = (int) wid_scr;
		effectWidth[1] = (int) wid_note1;
		effectWidth[2] = (int) wid_note2;
		effectHeight = 400;
		
		// set size of prs
		prs_height = 80;
		s_prs_normal.setSize(50, prs_height);
		s_prs_press.setSize(50, prs_height);
		
		// set size & pos of up, down, exit
		s_btn_up.setSize(20, 20);
		s_btn_down.setSize(20, 20);
		s_btn_exit.setSize(20, 20);
		s_btn_up.setPosition(20, 420);
		s_btn_down.setPosition(20, 340);
		s_btn_exit.setPosition(760, 440);
		
		// set bom effect size
		for (int i=0; i<16; i++) {
			s_bom[i].scale(1);
		}
		bomOffsetX = -76;
		bomOffsetY = -120;
		
		// set judge size
		for (int i=0; i<=6; i++)
			judgespr[i].scale(0.8f);
		for (int i=0; i<4; i++) {
			for (int j=0; j<10; j++) {
				judgenum[i*10 + j].scale(0.8f);
			}
		}
		
		noteWidth[0] = wid_scr;
		noteWidth[1] = noteWidth[3] = noteWidth[5] = noteWidth[7] = wid_note1;
		noteWidth[2] = noteWidth[4] = noteWidth[6] = wid_note2;
		
		// set note's x pos
		noteX[10] = noteX[6] = wid_start;
		noteX[11] = noteX[1] = noteX[6] + wid_scr;
		noteX[12] = noteX[2] = noteX[1] + wid_note1;
		noteX[13] = noteX[3] = noteX[2] + wid_note2;
		noteX[14] = noteX[4] = noteX[3] + wid_note1;
		noteX[15] = noteX[5] = noteX[4] + wid_note2;
		noteX[16] = noteX[8] = noteX[5] + wid_note1;
		noteX[17] = noteX[9] = noteX[8] + wid_note2;
		
		prs_left = wid_start;
		for (int i=0; i<8; i++)
			prs_width[i] = noteWidth[i];

		// set lain size
		s_lain.setSize(lainWidth, lainheight);
		s_lain.setPosition(wid_start, bottompos);
		
	}
	
	public void draw(SpriteBatch batch) {
		// get touch input
		getTouchInput();
		
		// update time
		nowTime = TimeUtils.millis();
		eclipsedTime = (int) (nowTime - startTime);
		
		// draw ui
		s_lain.draw(batch);
		
		switch (Settings.bmsmode) {
		case Settings.MODE_MOBILE:
			drawInterface_Mobile(batch);
			break;
		case Settings.MODE_PAD:
			drawInterface_Pad(batch);
			break;
		case Settings.MODE_PC:
			drawInterface_PC(batch);
			break;
		}
		
		// play BGM
		double bgmBeat = Rhythmus.bmsParser.getBeatFromTime(eclipsedTime + Settings.judgetime);
		for (int i=0; i<Rhythmus.bmsParser.bgmdata.size(); i++) {
			BMSKeyData d = Rhythmus.bmsParser.bgmdata.get(i);
			if (d.beat > bgmBeat)
				break;
			if (d.key == 1) {
				if (d.attr == 0) {
					BMSData.playSound((int) d.value);
					d.attr = 1;
				}
			}
		}
		
		// fadeout on Force exit
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		if (exitmode > 0) {
			float a =  ((float)(nowTime - forceexittime) / 1000);
			if (a>1) {
				a=1;
				
				if (exitmode == 1) {		// cancel
					Rhythmus.changeScene(Rhythmus.SCENE_SELECT);
				} else if (exitmode == 2) {	// failed
					Rhythmus.changeScene(Rhythmus.SCENE_RESULT);
				}
			}

			s_black.draw(batch, a);
			return;	/*** DONT GO ON! IMPORTANT ***/
		}
		
		// screen fadeout
		if (eclipsedTime > Rhythmus.bmsParser.time*1000 +1000) {
			float a = (float) ((eclipsedTime - Rhythmus.bmsParser.time*1000 -1000) / 1000);
			if (a>1) {
				a=1;
				Rhythmus.changeScene(Rhythmus.SCENE_RESULT);
			}
			
			s_black.draw(batch, a);
		}
		Gdx.gl.glDisable(GL10.GL_BLEND);
	}

	private void drawInterface_Mobile(SpriteBatch batch) {
		// draw note from eclipsedTime
		beat = Rhythmus.bmsParser.getBeatFromTime(eclipsedTime);
		bpm = Rhythmus.bmsParser.getBPMFromBeat(beat);
		
		float pos = bottompos;
		double _beat = beat;
		double _bpm = bpm;
		for (int i=0; i<Rhythmus.bmsParser.bmsdata.size(); i++) {
			BMSKeyData d = Rhythmus.bmsParser.bmsdata.get(i);
			if (d.beat >= _beat) {
				// check beat (line drawing)
				while (d.beat > (int)_beat+1) {
					pos += ((int)(_beat+1) - _beat) * speed * _bpm * Rhythmus.bmsParser.length_beat[(int)_beat]
							* lainheight/200;
					_beat = (int)_beat+1;

					s_white.setPosition(leftPos, pos);
					s_white.draw(batch, 0.5f);
				}
				
				// calc pos
				pos += (d.beat - _beat) * speed * _bpm * Rhythmus.bmsParser.length_beat[(int)d.beat]
						* lainheight/200;
				_beat = d.beat;
				
				// draw line(2) -- when beat%1 == 0
				if (d.beat%1==0)
				{
					s_white.setPosition(leftPos, pos);
					s_white.draw(batch, 0.5f);
				}
				
				// check type
				if (d.key == 9)					// STOP
				{
					continue;
				}
				if (d.key == 3 || d.key == 8) {	// BPM
					_bpm = d.value;
				} else if (d.key > 10 && d.key<20) {		// key
					int x;
					Sprite s;
					if (d.key%10 == 6) {			// SCR
						s = s_note_scr;
					} else if ((d.key%10)%2 == 0) {	// 2, 4, 8 (2,4,6)
						s = s_note2;
					} else {						// 1,3,5,9 (1,3,5,7) 
						s = s_note1;
					}
					s.setX(noteX[d.key%10]);
					s.setY(pos);
					
					if (d.attr == 0) {			// attr==0 is drawable object
						s.draw(batch);
					}
				}
			} else {
				// when autoplay
				if (autoplay > 0 && d.attr == 0) {
					pressNote( getKeyFromChannel(d.key) );
					releaseNote( getKeyFromChannel(d.key) );
					/*BMSData.playSound((int) d.value);
					judge(JUDGE_PGREAT);*/
				} else {
					// its a dropped note - check out missed note
					if (d.attr == 0) {
						if (eclipsedTime - d.time + Settings.judgetime > judgetime*8) {
							d.attr = 1;
							judge(JUDGE_POOR);
						} else {
							// draw
							Sprite s;
							int spriteIndex = getKeyFromChannel(d.key);

							if (spriteIndex%10 == 0) {			// SCR
								s = s_note_scr;
							} else if ((spriteIndex%10)%2 == 0) {	// (2,4,6)
								s = s_note2;
							} else {						// (1,3,5,7) 
								s = s_note1;
							}
							s.setX(noteX[spriteIndex%10+10]);
							s.setY(bottompos);
							s.draw(batch);
						}
					}
				}
			}
			if (pos > Rhythmus.SCREEN_HEIGHT)	// Out of screen
				break;
		}

		
		/*** START ALPHA(COLOR) BLENDING ***/
		// draw press effect
		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);
		for (int i=0; i<8; i++) {
			int t = (int) (nowTime - pressTime[i]);	// ** OVERFLOW CAN CAUSED **
			if (notePress[i] > 0) t=0;
			float a = (float)t/160;
			
			if (a<1 && a>=0) {
				int org_w;
				if (i==0) org_w = effectWidth[0];
				else if (i%2 == 1) org_w = effectWidth[1];
				else org_w = effectWidth[2];
				int h = (int) ((float)effectHeight*(1 - 0.4f*a));
				int w = (int) ((float)org_w*(1-a));
				
				int x = noteX[10+i] + (org_w-w)/2;
				int y = bottompos;
				
				Sprite s;
				if (i==0) s = s_effect1;
				else if (i%2 == 1) s = s_effect2;
				else s = s_effect3;
				s.setPosition(x, y);
				s.setSize(w, h);
				s.draw(batch, 1-a);
			}
		}
		
		// draw bom effect
		for (int i=0; i<8; i++) {
			int frame = (int) ((nowTime - bomTime[i])/20.0f);
			if (frame < 16 && frame >= 0) {
				s_bom[frame].setX(noteX[10+i] + bomOffsetX);
				s_bom[frame].setY(bottompos + bomOffsetY);
				s_bom[frame].draw(batch);
			}
		}
		
		// draw judge
		if (nowTime - judgeTime > 0 && nowTime - judgeTime < 3000) {
			int t = (int) (nowTime - judgeTime);
			
			float judgeSize = 1.5f;
			int w=0;
			int y=(int) ((480+bottompos-33*judgeSize)/2);
			if (judge == JUDGE_PGREAT || judge == JUDGE_GREAT || judge == JUDGE_GOOD) {
				w = (int) judgespr[0].getWidth();
				if (judge == JUDGE_GOOD)
					w = (int) judgespr[4].getWidth();
				w += 11;
				w += 17 * Integer.toString(combo).length();
			} else {
				if (judge == JUDGE_POOR)
					w = (int) judgespr[5].getWidth();
				if (judge == JUDGE_BAD)
					w = (int) judgespr[6].getWidth();
			}
			
			//w *= judgeSize;
			int x = leftPos + (lainWidth-w)/2;
			int judgesprNum=-1;
			if (judge == JUDGE_PGREAT) {
				judgesprNum = t%3;
			} else if (t%3 != 0) {
				if (judge == JUDGE_GREAT)
					judgesprNum = 3;
				if (judge == JUDGE_GOOD)
					judgesprNum = 4;
				if (judge == JUDGE_POOR)
					judgesprNum = 5;
				if (judge == JUDGE_BAD)
					judgesprNum = 6;
			}
			
			if (judgesprNum >= 0 && judgesprNum <= 4) {
				judgespr[judgesprNum].setX(x);
				judgespr[judgesprNum].setY(y);
				judgespr[judgesprNum].draw(batch);
				x += judgespr[judgesprNum].getWidth()*judgeSize;
				x += 11*judgeSize;
				
				ArrayList<Integer> arr = new ArrayList<Integer>();
				for (int _combo=combo; _combo>0; _combo/=10) {
					arr.add(_combo%10);
				}
				for (int i=arr.size()-1; i>=0; i--) {
					int n = judgesprNum*10 + arr.get(i);
					if (n >= 40) n-=10;
					judgenum[n].setX(x);
					judgenum[n].setY(y);
					judgenum[n].draw(batch);
					x += 17*judgeSize;
				}
			} else if (judgesprNum > 4) {
				judgespr[judgesprNum].setX(x);
				judgespr[judgesprNum].setY(y);
				judgespr[judgesprNum].draw(batch);
			}
		}
		
		// draw pression under bottom line
		int p = prs_left;
		for (int i=0; i<8; i++) {
			Sprite s = s_prs_normal;
			if (notePress[i] > 0) {
				s = s_prs_press;
			}
			s.setPosition(p, bottompos-prs_height);
			s.setSize(prs_width[i], prs_height); 
			s.draw(batch);
			p += prs_width[i];
		}
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		/*** END ALPHA(COLOR) BLENDING ***/

		// draw guage
		for (int i=bottompos+10; i<bottompos+10+300; i+= 5)
		{
			Sprite s;
			if (Settings.guagemode == Settings.GUAGE_HARD)
				s = s_guage_hard;
			else
				s = s_guage_normal;
			
			s.setPosition(760, i);
			
			if (i > bottompos+10+300*guage/100)
				s.draw(batch, 0.5f);
			else
				s.draw(batch);
		}
		
		// draw bottom border line
		s_white.setPosition(leftPos, bottompos);
		s_white.setSize(lainWidth, 4);
		s_white.draw(batch);
		
		// place buttons
		s_btn_up.draw(batch);
		s_btn_down.draw(batch);
		s_btn_exit.draw(batch);
		
		// infomation
		ScoreData s = new ScoreData();
		s.pg=pg; s.gr=gr; s.gd=gd; s.pr=pr; s.bd=bd;
		float rate;
		if (s.getTotalNote() == 0) rate = 0;
		else rate = (float)s.getEXScore()/(s.getTotalNote()*2);
		String rate_str=ScoreData.GetRateString(rate);
		font.setColor(Color.WHITE);
		font.draw(batch, String.format("BPM %.0f", bpm), 20, 100);
		font.draw(batch, String.format("PG %d", pg), 20, 220);
		font.draw(batch, String.format("GR %d", gr), 20, 200);
		font.draw(batch, String.format("GD %d", gd), 20, 180);
		font.draw(batch, String.format("PR %d", pr), 20, 160);
		font.draw(batch, String.format("BD %d", bd), 20, 140);
		font.draw(batch, String.format("%s (%.0f)", rate_str, rate*100), 20, 260);
	}
	private void drawInterface_Pad(SpriteBatch batch) {
		// NEVER BE IMPLEMENTED ... NEVER...
		drawInterface_Mobile(batch);
	}
	private void drawInterface_PC(SpriteBatch batch) {
		// ADD: draw BG (7,6,4)
		for (int i=0; i<Rhythmus.bmsParser.bgadata.size(); i++) {
			BMSKeyData d = Rhythmus.bmsParser.bgadata.get(i);
			if (d.beat > beat)
				break;
			if (d.attr == 0) {
				Texture t;
				if (d.key == 6) {
					t = BMSData.bg[(int) d.value];
					if (t != null) {
						bga_miss = new Sprite(t);
						bga_miss.setPosition(20, 60);
						bga_miss.setSize(360, 360);
					}
				}
				if (d.key == 7) {
					t = BMSData.bg[(int) d.value];
					if (t != null) {
						bga_overlay = new Sprite(t);
						bga_overlay.setPosition(20, 60);
						bga_overlay.setSize(360, 360);
					}
				}
				if (d.key == 4) {
					t = BMSData.bg[(int) d.value];
					if (t != null) {
						bga_now = new Sprite(t);
						bga_now.setPosition(20, 60);
						bga_now.setSize(360, 360);
					}
				}
				d.attr = 1;
			}
		}
		
		if (nowTime - missTime < 500 && bga_miss != null) {
			bga_miss.draw(batch);
		} else {
			if (bga_now != null)
				bga_now.draw(batch);
		}
		
		drawInterface_Mobile(batch);
	}
	
	public void pressNote(int n) {
		if (n < 0) return;
		if (notePress[n] > 0) return;
		
		notePress[n] = 1;
		pressTime[n] = (int) TimeUtils.millis();
		
		BMSKeyData d = null;
		switch (n) {
		case 0:	// SCR
			d = BMSUtil.getLastValidKey(Rhythmus.bmsParser, 16);
			break;
		case 1:
			d = BMSUtil.getLastValidKey(Rhythmus.bmsParser, 11);
			break;
		case 2:
			d = BMSUtil.getLastValidKey(Rhythmus.bmsParser, 12);
			break;
		case 3:
			d = BMSUtil.getLastValidKey(Rhythmus.bmsParser, 13);
			break;
		case 4:
			d = BMSUtil.getLastValidKey(Rhythmus.bmsParser, 14);
			break;
		case 5:
			d = BMSUtil.getLastValidKey(Rhythmus.bmsParser, 15);
			break;
		case 6:
			d = BMSUtil.getLastValidKey(Rhythmus.bmsParser, 18);
			break;
		case 7:
			d = BMSUtil.getLastValidKey(Rhythmus.bmsParser, 19);
			break;
		}
		
		if (d != null) {
			BMSData.playSound( (int) d.value );
			
			if ( Math.abs(d.time - eclipsedTime + Settings.judgetime) < judgetime ) {
				judge(JUDGE_PGREAT);
				d.attr = 1;
				bomTime[n] = TimeUtils.millis();
			} else if ( Math.abs(d.time - eclipsedTime + Settings.judgetime) < judgetime*2 ) {
				judge(JUDGE_GREAT);
				d.attr = 1;
				bomTime[n] = TimeUtils.millis();
			} else if ( Math.abs(d.time - eclipsedTime + Settings.judgetime) < judgetime*4 ) {
				judge(JUDGE_GOOD);
				d.attr = 1;
			} else if ( Math.abs(d.time - eclipsedTime + Settings.judgetime) < judgetime*6 ) {
				judge(JUDGE_BAD);
				d.attr = 1;
			}
		}
	}
	
	public void judge(int num) {
		switch(num){
		case JUDGE_PGREAT:
			switch (Settings.guagemode) {
			case Settings.GUAGE_HARD:
				guage += 0.1f;
				break;
			case Settings.GUAGE_GROOVE:
				guage += 1.0f * ((float)Rhythmus.bmsParser.total/Rhythmus.bmsParser.notecnt);
				break;
			case Settings.GUAGE_EASY:
				guage += 1.2f * ((float)Rhythmus.bmsParser.total/Rhythmus.bmsParser.notecnt);
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
				guage += 1.0f * ((float)Rhythmus.bmsParser.total/Rhythmus.bmsParser.notecnt);
				break;
			case Settings.GUAGE_EASY:
				guage += 1.2f * ((float)Rhythmus.bmsParser.total/Rhythmus.bmsParser.notecnt);
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
				guage += 0.5f * ((float)Rhythmus.bmsParser.total/Rhythmus.bmsParser.notecnt);
				break;
			case Settings.GUAGE_EASY:
				guage += 0.6f * ((float)Rhythmus.bmsParser.total/Rhythmus.bmsParser.notecnt);
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
			missTime = TimeUtils.millis();
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
			missTime = TimeUtils.millis();
			break;
		}
		if (guage > 100) guage = 100;
		if (guage <= 0) {
			if (Settings.guagemode == Settings.GUAGE_HARD)
				exitGame(2);
			guage = 0;
		}
		
		if (maxcombo < combo) maxcombo = combo;
		judge = num;
		judgeTime = TimeUtils.millis();
	}
	
	public void releaseNote(int n) {
		if (n < 0) return;
		notePress[n] = 0;
		pressTime[n] = TimeUtils.millis();
	}
	
	public void exitGame(int v) {
		// force exit
		if (exitmode > 0) return;
		forceexittime = TimeUtils.millis();
		exitmode = v;
		Gdx.input.setInputProcessor(null);
		if (v == 1) sButton.play();
	}
	
	public void dispose() {
		if (t_bom != null) t_bom.dispose();
		if (t_play != null) t_play.dispose();
		if (sButton != null) sButton.dispose();
	}
	
	public void changeSpeed(float newSpeed) {
		if (newSpeed <= 0) return;
		speed = newSpeed;
		sButton.play();
	}
	
	private int getKeyFromChannel(int channel) {
		if (channel == 16)
			return 0;
		else if (channel == 11)
			return 1;
		else if (channel == 12)
			return 2;
		else if (channel == 13)
			return 3;
		else if (channel == 14)
			return 4;
		else if (channel == 15)
			return 5;
		else if (channel == 18)
			return 6;
		else if (channel == 19)
			return 7;
		else 
			return -1;
	}
	
	private boolean[] getTouchStatus() {
		// check touch status and put it in the boolean array
		// to check key press
		boolean [] touched = new boolean[8];
		
		for (int i=0; i<20; i++)
		{
			if (Gdx.input.isTouched(i)) {
				int x = Gdx.input.getX(i);
				int y = Gdx.input.getY(i);
				
				// change num to 800x480
				x = x*800/Gdx.graphics.getWidth();
				y = y*480/Gdx.graphics.getHeight();
				
				if (y>240) {
					int p = prs_left;
					for (int a=0; a<8; a++)
					{
						if (x >= p && x < p+prs_width[a])
							touched[a] = true;
						p += prs_width[a];
					}
				}
			}
		}
		
		return touched;
	}
	
	private void getTouchInput() {
		// get touch input
		// this method should be called at rendering
		// to catch user input
		
		boolean [] _touch = getTouchStatus();
		
		for (int i=0; i<8; i++) {
			if (!touch[i] && _touch[i])
				pressNote(i);
			else if (touch[i] && !_touch[i])
				releaseNote(i);
		}
		
		touch = _touch;
	}
}
