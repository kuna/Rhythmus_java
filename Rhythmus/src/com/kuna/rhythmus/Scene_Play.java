package com.kuna.rhythmus;

import java.util.ArrayList;
import java.util.List;

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
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kuna.rhythmus.bmsdata.BMSKeyData;
import com.kuna.rhythmus.bmsdata.BMSUtil;
import com.kuna.rhythmus.data.Common;
import com.kuna.rhythmus.score.ScoreData;

public class Scene_Play implements Scene {
	// texture & sprite
	private Texture t_play;
	private Texture t_bom;	// 16 frame, 240, 200

	// buttons
	private Sprite s_btn_up;
	private Sprite s_btn_down;
	
	// lain, guage, etc.
	private Sprite s_btn_exit;
	private Sprite s_progress;
	
	private static Sound sButton;
	
	private BitmapFont font;
	
	
	// for keyboard event / LN
	private static int[] notePress = new int[17];
	private static BMSKeyData[] longnotePressData = new BMSKeyData[17];
	public static int[] longnotePress = new int[17];
	private static int[] longnoteJudge = new int[17];
	private static float[] longnoteBeat = new float[17];
	
	private int screenHeight = 480;	// TODO need to process ...?
	
	PlayInputListener pl;
	
	// timer & beat
	public static long startTime;
	public static long nowTime;
	public static int eclipsedTime;
	public static double nowBeat;
	
	public static int exitmode = 0;	// 0 is normal, 1 is interrupted(cancel), 2 is failed
	
	// Scene_Play elements
	static Scene_Play_Note spNote;
	static Scene_Play_Judge spJudge;
	static Scene_Play_Guage spGuage;
	static Scene_Play_BGA spBGA;
	static Scene_Play_Lain spLain;
	static Scene_FadeInOut fade;
	static Scene_Play_FullCombo fullcombo;
	static Scene_Play_3DNote sp3DNote;
	static Scene_Play_3DLain sp3DLain;
	
	private boolean initalized = false;
	private List<BMSKeyData> bpms;
	
	public Scene_Play() {
		notePress = new int[17];
		longnotePressData = new BMSKeyData[17];
		longnotePress = new int[17];
		longnoteJudge = new int[17];
		longnoteBeat = new float[17];
	}

	@Override
	public void init() {
		eclipsedTime = 0;
		exitmode = 0;
		startTime = 0;

		bpms = BMSUtil.ExtractChannel(Rhythmus.bmsData.bmsdata, 3);	// bpm channel
		
		t_play = new Texture(Gdx.files.internal("data/play.png"));
		t_bom = new Texture(Gdx.files.internal("data/bom.png"));
		font = new BitmapFont();

		// do player setting
		Scene_Play_Setting.setKeyMode();

		// button resources
		s_btn_up = new Sprite(new TextureRegion(t_play, 453, 0, 55, 54));
		s_btn_up.setSize(20, 20);
		s_btn_up.setPosition(20, 440);
		s_btn_down = new Sprite(new TextureRegion(t_play, 398, 0, 55, 54));
		s_btn_down.setSize(20, 20);
		s_btn_down.setPosition(20, 400);
		s_btn_exit = new Sprite(new TextureRegion(t_play, 513, 0, 54, 54));
		s_btn_exit.setSize(20, 20);
		s_btn_exit.setPosition(760, 440);
		s_progress = new Sprite(new TextureRegion(t_play, 18, 444, 14, 24));
		
		// create Scene_Play objects and initalize
		fullcombo = new Scene_Play_FullCombo(t_play);
		spNote = new Scene_Play_Note(screenHeight, t_play, t_bom, bpms);
		spBGA = new Scene_Play_BGA(Scene_Play_Setting.BGADest[0], Scene_Play_Setting.BGADest[1], 
				Scene_Play_Setting.BGADest[2], Scene_Play_Setting.BGADest[3]);
		spJudge = new Scene_Play_Judge(t_play, spBGA, fullcombo, Scene_Play_Setting.JudgeDest[0], Scene_Play_Setting.JudgeDest[1], 
				Scene_Play_Setting.JudgeScale);
		spGuage = new Scene_Play_Guage(t_play, Scene_Play_Setting.GuageDest[0], Scene_Play_Setting.GuageDest[1], 
				Scene_Play_Setting.GuageDest[2], Scene_Play_Setting.GuageDest[3]);
		spLain = new Scene_Play_Lain(t_play, Scene_Play_Setting.drawLain);
		sp3DNote = new Scene_Play_3DNote(t_play, t_bom, bpms);
		sp3DLain = new Scene_Play_3DLain(t_play, Scene_Play_Setting.drawLain);
		
		// set default guage
		switch (Settings.guagemode) {
		case Settings.GUAGE_EASY:
		case Settings.GUAGE_GROOVE:
			spJudge.setGuage(20);
			break;
		case Settings.GUAGE_HARD:
			spJudge.setGuage(100);
			break;
		}
		
		// fade inout
		fade = new Scene_FadeInOut(new Handler() {
			@Override
			public void InformEvent(Object arg) {
				// check out start time
				if (Common.argPath != null)
					startTime = TimeUtils.millis() - (int)(Rhythmus.bmsData.getTimeFromBeat(bpms, Common.argBeat)*1000);
				else
					startTime = TimeUtils.millis();
			}
		}, new Handler() {
			@Override
			public void InformEvent(Object arg) {
				// exit play
				Rhythmus.bmsData.dispose();			// dispose data
				BMSResource.dispose();

				// when exitOnEnd
				if (Rhythmus.exitOnEnd) {
					Gdx.app.exit();
				}
				
				if (exitmode == 1) {		// cancel
					Rhythmus.changeScene(Rhythmus.SCENE_SELECT);
				} else if (exitmode >= 2) {	// failed or finished
					Rhythmus.changeScene(Rhythmus.SCENE_RESULT);
				}
			}
		});
		fade.doFadeIn();
		
		// load sound
		sButton = Gdx.audio.newSound(Gdx.files.internal("data/change.wav"));
		
		// set input listener
		pl = new PlayInputListener();
		Gdx.input.setInputProcessor(pl);
		
		initalized = true;
	}
	
	@Override
	public void draw(SpriteBatch batch, DecalBatch dbatch) {
		if (!initalized) return;

		batch.begin();
		
		// get touch input
		PlayInputListener.getTouchInput();
		
		// update time & beat
		nowTime = TimeUtils.millis();
		eclipsedTime = (int) (nowTime - startTime);
		if (startTime == 0)
			eclipsedTime = 0;
		nowBeat = Rhythmus.bmsData.getBeatFromTime(eclipsedTime);
		
		// draw interfaces
		drawInterface(batch);
		
		// draw how much played (song progress)
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		int s_progress_height = Scene_Play_Setting.getLainHeight() - 120;
		int s_progress_bottom = Scene_Play_Setting.getLainBottom();
		s_progress.setPosition(20, (float) (s_progress_bottom + s_progress_height*(1 - eclipsedTime/1000.0/Rhythmus.bmsData.time)));
		s_progress.draw(batch);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		// if autoplay then release key
		if (Settings.autoplay) {
			for (int i=1; i<=16; i++)
				if (notePress[i] > 0 && longnotePress[i] == 0)
					releaseNote(i);
		}
		
		batch.end();
		
		// fade
		fade.draw(batch);

		/* process part */
		// is guage bad? then go to result screen
		if (spJudge.getGuage() == 0.0 && Settings.guagemode == Settings.GUAGE_HARD && exitmode == 0) {
			exitGame(2);
		}
		
		// check is song finished
		if (eclipsedTime > Rhythmus.bmsData.time*1000 + 3000 && exitmode == 0) {
			exitGame(3);
		}
		
		// process miss note
		checkBadNote();
		
		// play BGM
		playBGM();
	}

	private void drawInterface(SpriteBatch batch) {
		spBGA.draw(batch);

		spLain.draw(batch);
		spNote.draw(batch);
		
		//sp3DLain.draw(batch);
		//sp3DNote.draw(batch);
		
		fullcombo.draw(batch);
		
		spJudge.draw(batch);
		
		spGuage.setGuage((int) spJudge.getGuage());
		spGuage.draw(batch);
		
		
		// place buttons
		s_btn_up.draw(batch);
		s_btn_down.draw(batch);
		s_btn_exit.draw(batch);
		
		// infomation
		ScoreData s = spJudge.getScoreData();
		float rate;
		if (s.getTotalNote() == 0) rate = 0;
		else rate = (float)s.getEXScore()/(s.getTotalNote()*2);
		String rate_str=ScoreData.GetRateString(rate);
		
		font.setColor(Color.WHITE);
		font.draw(batch, String.format("BPM %.1f", Rhythmus.bmsData.getBPMFromBeat(nowBeat)), 20, 100);
		font.draw(batch, String.format("PG %d", s.pg), 20, 220);
		font.draw(batch, String.format("GR %d", s.gr), 20, 200);
		font.draw(batch, String.format("GD %d", s.gd), 20, 180);
		font.draw(batch, String.format("PR %d", s.pr), 20, 160);
		font.draw(batch, String.format("BD %d", s.bd), 20, 140);
		font.draw(batch, String.format("%s (%.0f)", rate_str, rate*100), 20, 260);
		
		/* debugging */
		//font.draw(batch, String.format("%d, %f, %d", eclipsedTime, nowBeat, (int)(1000*Rhythmus.bmsData.getTimeFromBeat(bpms, nowBeat))), 50, 50);
	}
	
	// should be called every frame
	public void playBGM() {
		double bgmBeat = Rhythmus.bmsData.getBeatFromTime(eclipsedTime + Settings.judgetime);
		for (int i=0; i<Rhythmus.bmsData.bgmdata.size(); i++) {
			BMSKeyData d = Rhythmus.bmsData.bgmdata.get(i);
			if (d.getBeat() > bgmBeat)
				break;
			// bgm is always 1 so we dont check channel
			if (d.getAttr() == 0) {
				BMSResource.playSound((int) d.getValue());
				d.setAttr(1);
			}
		}
	}
	
	// should be called every frame
	// + autoplay part
	public void checkBadNote() {
		for (int i=1; i<=16; i++) {
			while (true) {
				BMSKeyData bkd = getLastValidKey(i);
				if (bkd == null)
					break;

				if (bkd.getTime() - eclipsedTime < -spJudge.getJudgeBADTime()) {
					if (bkd.is1PLNChannel() || bkd.is2PLNChannel()) {
						BMSKeyData bkd2 = Rhythmus.bmsData.getPairLN(bkd);
						if (bkd2 == null) {
							Gdx.app.log("ERROR", "UNEXPECTED");
						}
						bkd2.setAttr(2);	// failed
					}
					
					if (bkd.is1PChannel() || bkd.is1PLNChannel())
						spJudge.judge(Scene_Play_Judge.JUDGE_BAD, 1);
					else if (bkd.is2PChannel() || bkd.is2PLNChannel())
						spJudge.judge(Scene_Play_Judge.JUDGE_BAD, 2);
					bkd.setAttr(1);
					
				} else {
					if (Settings.autoplay && bkd.getTime() - eclipsedTime < 0)
						pressNote(i);
					break;
				}
			}
		}
		
		// find Last LN up key (in autoplay, releaseKey)
		for (int i=1; i<=16; i++) {
			while (true) {
				BMSKeyData bkd = getLastValidReleaseKey(i);
				if (bkd == null)
					break;
				if (bkd.getTime() - eclipsedTime < 0) {
					// automatical release
					bkd.setAttr(1);
					
					if (Settings.autoplay)
						releaseNote(i);
				} else {
					// no more releaseable note
					break;
				}
			}
		}
	}
	
	// attr 0 - nothing processed
	// attr 1 - processed, not showing
	// attr 2 - failed
	// in case LN, first one means pressing, second one means drawing.
	public static void pressNote(int n) {
		if (n < 0) return;
		if (notePress[n] > 0) return;
		
		notePress[n] = 1;
		spNote.pressNote(n);
		
		BMSKeyData d = getLastValidKey(n);
		
		if (d != null) {
			BMSResource.playSound( (int) d.getValue() );
			
			if (d.is1PLNChannel() || d.is2PLNChannel()) {
				// only check bad timing on longnote press...
				if (Math.abs(d.getTime() - eclipsedTime + Settings.judgetime) < spJudge.getJudgeGOODTime()) {
					// when its inside GOOD timing
					// Long note pressed
					longnotePress[n] = 1;
					longnotePressData[n] = d;
					longnoteBeat[n] = (float) d.getBeat();

					if ( Math.abs(d.getTime() - eclipsedTime + Settings.judgetime) < spJudge.getJudgePGREATTime() ) {
						longnoteJudge[n] = Scene_Play_Judge.JUDGE_PGREAT;
					} else if ( Math.abs(d.getTime() - eclipsedTime + Settings.judgetime) < spJudge.getJudgeGREATTime() ) {
						longnoteJudge[n] = Scene_Play_Judge.JUDGE_GREAT;
					} else if ( Math.abs(d.getTime() - eclipsedTime + Settings.judgetime) < spJudge.getJudgeGOODTime() ) {
						longnoteJudge[n] = Scene_Play_Judge.JUDGE_GOOD;
					}

					Gdx.app.log("LN", "OK");
					d.setAttr(1);	// this note is already pressed so no more press available.
				} else if ( Math.abs(d.getTime() - eclipsedTime + Settings.judgetime) < spJudge.getJudgeBADTime() ) {
					spJudge.judge(Scene_Play_Judge.JUDGE_BAD, ((n>8)?2:1));
					BMSKeyData d_ln = Rhythmus.bmsData.getPairLN(d);
					if (d_ln != null) {
						// failed
						d_ln.setAttr(2);
					}
					d.setAttr(1);
				}
			} else {
				// normal note pressed
				if ( Math.abs(d.getTime() - eclipsedTime + Settings.judgetime) < spJudge.getJudgePGREATTime() ) {
					spJudge.judge(Scene_Play_Judge.JUDGE_PGREAT, ((n>8)?2:1));
					d.setAttr(1);
					spNote.setBom(n);
				} else if ( Math.abs(d.getTime() - eclipsedTime + Settings.judgetime) < spJudge.getJudgeGREATTime() ) {
					spJudge.judge(Scene_Play_Judge.JUDGE_GREAT, ((n>8)?2:1));
					d.setAttr(1);
					spNote.setBom(n);
				} else if ( Math.abs(d.getTime() - eclipsedTime + Settings.judgetime) < spJudge.getJudgeGOODTime() ) {
					spJudge.judge(Scene_Play_Judge.JUDGE_GOOD, ((n>8)?2:1));
					d.setAttr(1);
				} else if ( Math.abs(d.getTime() - eclipsedTime + Settings.judgetime) < spJudge.getJudgeBADTime() ) {
					spJudge.judge(Scene_Play_Judge.JUDGE_BAD, ((n>8)?2:1));
					d.setAttr(1);
				}
			}
		}
	}
	
	public static void releaseNote(int n) {
		if (n < 0) return;
		if (notePress[n] == 0) return;
		
		notePress[n] = 0;
		spNote.releaseNote(n);
		
		// check long note
		if (longnotePress[n] > 0) {
			// get release key data
			BMSKeyData d = longnotePressData[n];
			
			// find end of the longnote
			BMSKeyData end = Rhythmus.bmsData.getPairLN(d);
			if (end == null) {
				Gdx.app.log("LN", "ERROR rel");
				return;
			}
			
			// you should not release it over GOOD timing
			if ( Math.abs(end.getTime() - eclipsedTime + Settings.judgetime) < spJudge.getJudgeGOODTime() ) {
				spJudge.judge(longnoteJudge[n], ((n>8)?2:1));
				end.setAttr(1);
			} else {
				spJudge.judge(Scene_Play_Judge.JUDGE_BAD, ((n>8)?2:1));
				end.setAttr(2);
			}
			
			longnotePress[n] = 0;
		}
	}
	
	
	
	public static void exitGame(int v) {
		if (exitmode != 0) return;
		fade.doFadeOut();
		exitmode = v;
		Gdx.input.setInputProcessor(null);
		if (v == 1) sButton.play();
	}
	
	private static BMSKeyData getLastValidKey(int key) {
		for (BMSKeyData bkd: Rhythmus.bmsData.bmsdata) {
			if (bkd.getAttr() == 0) {
				if (key > 8) {
					if (bkd.getKeyNum() == key-8) {
						if (bkd.is2PChannel()) {
							return bkd;
						} else if (bkd.is2PLNChannel() || bkd.isLNFirst()) {
							return bkd;
						}
					}
				} else {
					if (bkd.getKeyNum() == key) {
						if (bkd.is1PChannel()) {
							return bkd;
						} else if (bkd.is1PLNChannel() || bkd.isLNFirst()) {
							return bkd;
						}
					}
				}
			}
		}
		return null;
	}
	
	private static BMSKeyData getLastValidReleaseKey(int key) {
		for (BMSKeyData bkd: Rhythmus.bmsData.bmsdata) {
			if (key > 8) {
				if (bkd.is2PLNChannel()) {
					if (bkd.getKeyNum() == key-8) {
						if (bkd.getAttr() == 0 && !bkd.isLNFirst())
							return bkd;
					}
				}
			} else {
				if (bkd.is1PLNChannel()) {
					if (bkd.getKeyNum() == key) {
						if (bkd.getAttr() == 0 && !bkd.isLNFirst())
							return bkd;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void dispose() {
		if (t_bom != null) t_bom.dispose();
		if (t_play != null) t_play.dispose();
		if (sButton != null) sButton.dispose();
	}
	
	public void changeSpeed(float newSpeed) {
		if (newSpeed <= 0) return;
		Settings.speed = newSpeed;
		sButton.play();
		spNote.setSpeed(Settings.speed);
	}
}
