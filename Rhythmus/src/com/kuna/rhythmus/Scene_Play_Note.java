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
import com.kuna.rhythmus.bmsdata.BMSKeyData;
import com.kuna.rhythmus.bmsdata.BMSUtil;

public class Scene_Play_Note implements Scene_Interface {
	
	private int screenHeight;
	private int pos_y = 0;		// scrolling pos
	private float speed = 1;	// 
	
	// for drawing lines
	private Sprite s_white;
	
	// effect
	private Sprite s_effect1, s_effect2, s_effect3;
	private int[] effectTime = new int[17];
	private boolean[] notePressed = new boolean[17];
	
	// pression
	private Sprite s_prs_press;
	private Sprite s_prs_base;
	private Sprite s_prs_scr;
	private Sprite s_prs_base2;
	private Sprite s_prs_scr2;

	// notes
	private Sprite[] noteSprite = new Sprite[17];
	private Sprite[] noteLNBody = new Sprite[17];
	private Sprite[] noteLNStart = new Sprite[17];
	private Sprite[] noteLNEnd = new Sprite[17];
	
	// BOMs
	private Scene_Play_Bom[] spBom = new Scene_Play_Bom[17];
	
	// time
	private int nowTime;
	
	// bpm chananel
	private List<BMSKeyData> bpms;
	
	/* TODO tmp font*/
	private BitmapFont font = new BitmapFont();
	
	public Scene_Play_Note(int scrHeight, Texture t_play, Texture t_bom, List<BMSKeyData> bpms) {
		screenHeight = scrHeight;
		this.speed = Settings.speed;
		this.bpms = bpms;
		//this.font = font;

		// set sprite
		s_white = new Sprite(new TextureRegion(t_play, 764, 267, 2, 2));

		TextureRegion region;
		Sprite s_note_scr, s_note1, s_note2;
		region = new TextureRegion(t_play, 0, 264, 53, 8);
		s_note_scr = new Sprite(region);
		region = new TextureRegion(t_play, 0, 273, 30, 8);
		s_note1 = new Sprite(region);
		region = new TextureRegion(t_play, 0, 282, 23, 8);
		s_note2 = new Sprite(region);
		noteSprite[1] = noteSprite[3] = noteSprite[5] = noteSprite[7] = 
				noteSprite[9] = noteSprite[11] = noteSprite[13] = noteSprite[15] = s_note1;
		noteSprite[2] = noteSprite[4] = noteSprite[6] = 
				noteSprite[10] = noteSprite[12] = noteSprite[14] = s_note2;
		noteSprite[8] = noteSprite[16] = s_note_scr;

		Sprite lnBody, lnStart, lnEnd;
		lnBody = new Sprite(new TextureRegion(t_play, 0, 305, 30, 6));
		lnStart = new Sprite(new TextureRegion(t_play, 0, 291, 30, 6));
		lnEnd = new Sprite(new TextureRegion(t_play, 0, 298, 30, 6));
		noteLNBody[8] = noteLNBody[16] = lnBody;
		noteLNStart[8] = noteLNStart[16] = lnStart;
		noteLNEnd[8] = noteLNEnd[16] = lnEnd;
		lnBody = new Sprite(new TextureRegion(t_play, 31, 305, 17, 6));
		lnStart = new Sprite(new TextureRegion(t_play, 31, 291, 17, 6));
		lnEnd = new Sprite(new TextureRegion(t_play, 31, 298, 17, 6));
		noteLNBody[1] = noteLNBody[3] = noteLNBody[5] = noteLNBody[7] = 
				noteLNBody[9] = noteLNBody[11] = noteLNBody[13] = noteLNBody[15] = lnBody;
		noteLNStart[1] = noteLNStart[3] = noteLNStart[5] = noteLNStart[7] = 
				noteLNStart[9] = noteLNStart[11] = noteLNStart[13] = noteLNStart[15] = lnStart;
		noteLNEnd[1] = noteLNEnd[3] = noteLNEnd[5] = noteLNEnd[7] = 
				noteLNEnd[9] = noteLNEnd[11] = noteLNEnd[13] = noteLNEnd[15] = lnEnd;
		lnBody = new Sprite(new TextureRegion(t_play, 49, 305, 13, 6));
		lnStart = new Sprite(new TextureRegion(t_play, 49, 291, 13, 6));
		lnEnd = new Sprite(new TextureRegion(t_play, 49, 298, 13, 6));
		noteLNBody[2] = noteLNBody[4] = noteLNBody[6] = 
				noteLNBody[10] = noteLNBody[12] = noteLNBody[14] = lnBody;
		noteLNStart[2] = noteLNStart[4] = noteLNStart[6] = 
				noteLNStart[10] = noteLNStart[12] = noteLNStart[14] = lnStart;
		noteLNEnd[2] = noteLNEnd[4] = noteLNEnd[6] = 
				noteLNEnd[10] = noteLNEnd[12] = noteLNEnd[14] = lnEnd;
		
		region = new TextureRegion(t_play, 0, 0, 29, 255);
		s_effect1 = new Sprite(region);
		region = new TextureRegion(t_play, 31, 0, 17, 255);
		s_effect2 = new Sprite(region);
		region = new TextureRegion(t_play, 49, 0, 13, 255);
		s_effect3 = new Sprite(region);
		
		// initalize
		for (int i=1; i<17; i++)
			effectTime[i] = -1000;
		
		// init BOM
		Sprite[] s_bom = new Sprite[16];
		for (int i=0; i<16; i++) {
			region = new TextureRegion(t_bom, 200*i, 0, 200, 240);
			s_bom[i] = new Sprite(region);
		}
		for (int i=1; i<17; i++) {
			int nidx = i;
			if (nidx > 8) {
				nidx = i - 9;
			}
			
			spBom[i] = new Scene_Play_Bom(Scene_Play_Setting.noteX[i] + Scene_Play_Setting.noteWidth[nidx]/2, 
					Scene_Play_Setting.getLainBottom(), s_bom);
		}
		
		s_prs_press = new Sprite(new TextureRegion(t_play, 33, 394, 29, 47));
		if (Settings.key <= 6) {
			// 5K
			s_prs_base = new Sprite(new TextureRegion(t_play, 63, 394, 85, 44));
		} else {
			// 7K
			s_prs_base = new Sprite(new TextureRegion(t_play, 63, 394, 117, 44));
		}
		s_prs_base.setSize(Scene_Play_Setting.lainWidth - Scene_Play_Setting.noteWidth[8], Scene_Play_Setting.lainPrsBaseHeight);
		s_prs_base.setPosition(Scene_Play_Setting.leftPos + Scene_Play_Setting.noteWidth[8], Scene_Play_Setting.getLainBottom() - Scene_Play_Setting.lainPrsBaseHeight);
		s_prs_scr = new Sprite(new TextureRegion(t_play, 1, 486, 38, 46));
		s_prs_scr.setSize(Scene_Play_Setting.noteWidth[8], Scene_Play_Setting.lainPrsBaseHeight);
		s_prs_scr.setPosition(Scene_Play_Setting.leftPos, Scene_Play_Setting.getLainBottom() - Scene_Play_Setting.lainPrsBaseHeight);
		
		s_prs_base2 = new Sprite(s_prs_base);
		s_prs_base2.setPosition(Scene_Play_Setting.leftPos + Scene_Play_Setting.lainWidth + Scene_Play_Setting.lainMargin, 
				Scene_Play_Setting.getLainBottom() - Scene_Play_Setting.lainPrsBaseHeight);
		s_prs_scr2 = new Sprite(s_prs_scr);
		s_prs_scr2.setPosition(Scene_Play_Setting.leftPos + Scene_Play_Setting.lainWidth*2 + Scene_Play_Setting.lainMargin - Scene_Play_Setting.noteWidth[8],
				Scene_Play_Setting.getLainBottom() - Scene_Play_Setting.lainPrsBaseHeight);
	}
	
	public void setSpeed(float speed2) {
		speed = speed2;
	}
	
	// should be called per every frame
	public void setBottomPos(int val) {
		pos_y = val;
	}
	
	private void drawLines(SpriteBatch batch) {
		double py = Scene_Play_Setting.getLainBottom() - pos_y;
		double bpm = Rhythmus.bmsData.BPM;
		List<BMSKeyData> arr = BMSUtil.ExtractChannel( Rhythmus.bmsData.bmsdata, 3 );	// get BPM channel
		
		int beat = 0;
		double beatDecimal = 0;
		s_white.setSize(Scene_Play_Setting.getLainWidth(), 1);
		
		for (BMSKeyData bkd: arr) {
			while (py < screenHeight && beat < (int)bkd.getBeat()) {
				// calculate next beat pos
				py += Scene_Play_Setting.getLainHeight() * Rhythmus.bmsData.getBeatLength(beat) * speed * (1-beatDecimal) * bpm / BMSData.GENERAL_BPM /* standard BPM */;
				beatDecimal = 0;
				beat++;
				
				// draw lines
				s_white.setPosition(Scene_Play_Setting.leftPos, (float) py);
				if (py > Scene_Play_Setting.getLainBottom())
					s_white.draw(batch, 0.5f);
				if (Settings.key == 16) {
					// if DP, then draw more
					s_white.setPosition(Scene_Play_Setting.leftPos+Scene_Play_Setting.lainMargin+Scene_Play_Setting.lainWidth, (float) py);
					if (py > Scene_Play_Setting.getLainBottom())
						s_white.draw(batch, 0.5f);
				}
			}

			// bpm change first applies
			if ((int)bkd.getBeat() == beat) {
				py += Scene_Play_Setting.getLainHeight() * Rhythmus.bmsData.getBeatLength(beat) * speed * (bkd.getBeat()%1 - beatDecimal) * bpm / BMSData.GENERAL_BPM /* standard BPM */;
				beatDecimal = bkd.getBeat()%1;
				bpm = bkd.getValue();
				//font.draw(batch, String.format("BPM %.2f", bpm), 30, (float)py);	// BPM test code
			}
			
			if (py >= screenHeight) break;
		}
		
		// calculate left beat
		while (py < screenHeight) {
			// calculate position
			py += Scene_Play_Setting.getLainHeight() * Rhythmus.bmsData.getBeatLength(beat) * speed * (1-beatDecimal) * bpm / BMSData.GENERAL_BPM;
			beat++;
			beatDecimal = 0;
			
			s_white.setPosition(Scene_Play_Setting.leftPos, (float) py);
			if (py > Scene_Play_Setting.getLainBottom())
				s_white.draw(batch, 0.5f);
			if (Settings.key == 16) {
				// if DP, then draw more
				s_white.setPosition(Scene_Play_Setting.leftPos+Scene_Play_Setting.lainMargin+Scene_Play_Setting.lainWidth, (float) py);
				if (py > Scene_Play_Setting.getLainBottom())
					s_white.draw(batch, 0.5f);
			}
		}
		
		/* test code 
		beat = 0;
		while (true) {
			py = (int) Rhythmus.bmsData.getNotePositionWithBPM(lainHeight, bpms, beat) - pos_y + bottompos;
			if (py > screenHeight) break;
			s_white.setPosition(leftPos, (float) py);
			s_white.draw(batch, 0.5f);
			beat++;
		}*/
		
		// bottom line
		s_white.setPosition(Scene_Play_Setting.leftPos, Scene_Play_Setting.getLainBottom());
		s_white.draw(batch, 0.5f);
		if (Settings.key == 16) {
			// if DP, then draw more
			s_white.setPosition(Scene_Play_Setting.leftPos+Scene_Play_Setting.lainMargin+Scene_Play_Setting.lainWidth, Scene_Play_Setting.getLainBottom());
			s_white.draw(batch, 0.5f);
		}
	}
	
	private void drawNotes(SpriteBatch batch) {
		for (BMSKeyData d: Rhythmus.bmsData.bmsdata) {
			if (d.is1PChannel() || d.is2PChannel()) {
				int Offset = (d.is2PChannel())?8:0;
				int kn = d.getKeyNum()+Offset;
				
				if (!Scene_Play_Setting.isKeyEnabled[kn])
					continue;
				int pos = d.getPosY(Scene_Play_Setting.getLainHeight() / 100.0 * speed)
						+ Scene_Play_Setting.getLainBottom() - pos_y;
				if (pos > screenHeight) return;
				if (pos < 0) continue;
				if (d.getAttr() != 0) continue;		// only 0 is drawable note
				
				// check type - only note
				if (pos < Scene_Play_Setting.getLainBottom())
					pos = Scene_Play_Setting.getLainBottom();
				Sprite s = noteSprite[ kn ];
				s.setSize(Scene_Play_Setting.noteWidth[ kn ], Scene_Play_Setting.noteHeight);
				s.setX(Scene_Play_Setting.noteX[ kn ]);
				s.setY(pos);
				s.draw(batch);
				
				//font.draw(batch, String.format("%.0f", d.getTime()), noteX[ d.getKeyNum() ], pos);
			}
		}
	}
	
	private void drawLongNotes(SpriteBatch batch) {
		float[] longnotePos = new float[17];
		BMSKeyData[] longnoteObj = new BMSKeyData[17];
		
		for (BMSKeyData d: Rhythmus.bmsData.bmsdata) {
			if (d.is1PLNChannel() || d.is2PLNChannel()) {		// LONGNOTE -------------------
				int Offset = (d.is2PLNChannel())?8:0;
				int kn = d.getKeyNum()+Offset;
				
				if (!Scene_Play_Setting.isKeyEnabled[kn])
					continue;
				
				int pos = d.getPosY(Scene_Play_Setting.getLainHeight() / 100.0 * speed)
						+ Scene_Play_Setting.getLainBottom() - pos_y;
				
				if (longnoteObj[kn] == null) {
					// LONGNOTE START (store)
					// if out of screen, exit route.
					
					longnotePos[kn] = pos;
					longnoteObj[kn] = d;
				} else {
					// LONGNOTE END (draw)
					longnoteObj[kn] = null;

					if (longnotePos[kn] > screenHeight)
						continue;
					if (pos < 0)
						continue;
					if (d.getAttr() == 1)
						continue;
					
					float a = 1;
					if (d.getAttr() == 2)
						a = 0.5f;

					if (longnotePos[kn] < Scene_Play_Setting.getLainBottom())
						longnotePos[kn] = Scene_Play_Setting.getLainBottom();
					if (pos < Scene_Play_Setting.getLainBottom())
						pos = Scene_Play_Setting.getLainBottom();
					
					Sprite s;
					s = noteLNBody[ kn ];
					s.setSize(Scene_Play_Setting.noteWidth[ kn ], pos-longnotePos[kn]);
					s.setX(Scene_Play_Setting.noteX[kn]);
					s.setY(longnotePos[kn]);
					s.draw(batch, a);
					
					s = noteLNEnd[ kn ];
					s.setSize(Scene_Play_Setting.noteWidth[ kn ], s.getHeight());
					s.setX(Scene_Play_Setting.noteX[kn]);
					s.setY(pos);
					s.draw(batch, a);
					
					s = noteLNStart[ kn ];
					s.setSize(Scene_Play_Setting.noteWidth[ kn ], s.getHeight());
					s.setX(Scene_Play_Setting.noteX[kn]);
					s.setY(longnotePos[kn]);
					s.draw(batch, a);
				}
			}
		}
	}
	
	public void drawPressEffect(SpriteBatch batch) {
		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);
		for (int i=1; i<17; i++) {
			if (!Scene_Play_Setting.isKeyEnabled[i])
				continue;
			
			int t = (int) (nowTime - effectTime[i]);
			if (notePressed[i]) t=0;
			float a = (float)t/160;
			
			if (a<1 && a>=0) {
				// effect width is same as noteWidth
				int width = (int) (Scene_Play_Setting.noteWidth[i]*(1-a));
				int height = (int) (Scene_Play_Setting.getLainHeight() *(1 - 0.4f*a)); // effectHeight = lainHeight
				int x = Scene_Play_Setting.noteX[i] + (Scene_Play_Setting.noteWidth[i] - width)/2;
				int y = Scene_Play_Setting.getLainBottom();
				
				Sprite s;
				if (i==8 || i==16) s = s_effect1;	// Scratch
				else if (i%2 == 1) s = s_effect2;
				else s = s_effect3;
				s.setPosition(x, y);
				s.setSize(width, height);
				s.draw(batch, 1-a);
			}
		}
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void drawPressStatus(SpriteBatch batch) {
		// draw base
		s_prs_base.draw(batch);
		s_prs_scr.draw(batch);
		if (Settings.key == 16) {
			s_prs_base2.draw(batch);
			s_prs_scr2.draw(batch);
		}
		
		// draw pression under bottom line
		int p = Scene_Play_Setting.leftPos;
		for (int i=1; i<=16; i++) {
			if (i == 8 || i == 16) continue;
			
			if (notePressed[i]) {
				batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);
				s_prs_press.setPosition(Scene_Play_Setting.noteX[i], Scene_Play_Setting.getLainBottom()-Scene_Play_Setting.lainPrsBaseHeight);
				s_prs_press.setSize(Scene_Play_Setting.noteWidth[i], Scene_Play_Setting.lainPrsBaseHeight);
				s_prs_press.draw(batch);
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			}
		}
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		// draw note from eclipsedTime
		nowTime = Scene_Play.eclipsedTime;
		double beat = Scene_Play.nowBeat;
		// Time->Beat->pos method necessary.
		pos_y = (int) (Rhythmus.bmsData.getNotePositionWithBPM(Scene_Play_Setting.getLainHeight(), bpms, beat) * speed);
		
		// draw Lines
		drawLines(batch);
		
		// draw notes
		drawNotes(batch);
		
		// draw LN
		drawLongNotes(batch);
		
		// draw press effect
		drawPressEffect(batch);
		
		// draw status
		drawPressStatus(batch);
		
		// draw boms
		for (int i=1; i<17; i++) {
			spBom[i].isLongEffect = (Rhythmus.sPlay.longnotePress[i] != 0);
			spBom[i].draw(batch);
		}
		
		//font.setColor(Color.WHITE);
		//font.draw(batch, String.format("%f, %d", beat, pos_y), 200, 200);
	}
	
	public void pressNote(int key) {
		notePressed[key] = true;
	}
	
	public void releaseNote(int key) {
		notePressed[key] = false;
		effectTime[key] = nowTime;
	}
	
	public void setBom(int key) {
		spBom[key].setBomTime();
	}
}
