package com.kuna.rhythmus;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.kuna.rhythmus.bmsdata.BMSKeyData;
/*
 * TODO implementing
 * not stable
 */
public class Scene_Play_3DNote implements Scene_Interface {
	private Decal[] noteSprite = new Decal[17];
	private Decal[] noteLNBody = new Decal[17];
	private Decal[] noteLNStart = new Decal[17];
	private Decal[] noteLNEnd = new Decal[17];
	
	float speed;
	int pos_y;
	int nowTime;
	List<BMSKeyData> bpms;
	
	float[] notePosition = new float[9];
	
	public Scene_Play_3DNote(Texture t_play, Texture t_bom, List<BMSKeyData> bpms) {
		this.bpms = bpms;
		speed = Settings.speed;
		
		float noteWidthScr=1, noteWidthA=1, noteWidthB=1;
		
		// default settings
		noteWidthScr = 0.6f;
		noteWidthA = 0.4f;
		noteWidthB = 0.3f;
		
		notePosition[0] = notePosition[8] = -(noteWidthScr + noteWidthA*4 + noteWidthB*3)/2 + 0.4f;
		notePosition[1] = notePosition[0] + noteWidthScr;
		for (int i=2; i<=7; i++) {
			notePosition[i] = notePosition[i-1] + ((i%2==1)?noteWidthA:noteWidthB);
		}
		
		Decal s_note_scr, s_note1, s_note2;
		s_note_scr = Decal.newDecal(noteWidthScr, 0.1f, new TextureRegion(t_play, 0, 264, 53, 8), true);
		s_note1 = Decal.newDecal(noteWidthA, 0.1f, new TextureRegion(t_play, 0, 273, 30, 8), true);
		s_note2 = Decal.newDecal(noteWidthB, 0.1f, new TextureRegion(t_play, 0, 282, 23, 8), true);
		noteSprite[1] = noteSprite[3] = noteSprite[5] = noteSprite[7] = 
				noteSprite[9] = noteSprite[11] = noteSprite[13] = noteSprite[15] = s_note1;
		noteSprite[2] = noteSprite[4] = noteSprite[6] = 
				noteSprite[10] = noteSprite[12] = noteSprite[14] = s_note2;
		noteSprite[8] = noteSprite[16] = s_note_scr;

		Decal lnBody, lnStart, lnEnd;
		lnBody = Decal.newDecal(1, 1, new TextureRegion(t_play, 0, 305, 30, 6));
		lnStart = Decal.newDecal(1, 1, new TextureRegion(t_play, 0, 291, 30, 6));
		lnEnd = Decal.newDecal(1, 1, new TextureRegion(t_play, 0, 298, 30, 6));
		noteLNBody[8] = noteLNBody[16] = lnBody;
		noteLNStart[8] = noteLNStart[16] = lnStart;
		noteLNEnd[8] = noteLNEnd[16] = lnEnd;
		lnBody = Decal.newDecal(1, 1, new TextureRegion(t_play, 31, 305, 17, 6));
		lnStart = Decal.newDecal(1, 1, new TextureRegion(t_play, 31, 291, 17, 6));
		lnEnd = Decal.newDecal(1, 1, new TextureRegion(t_play, 31, 298, 17, 6));
		noteLNBody[1] = noteLNBody[3] = noteLNBody[5] = noteLNBody[7] = 
				noteLNBody[9] = noteLNBody[11] = noteLNBody[13] = noteLNBody[15] = lnBody;
		noteLNStart[1] = noteLNStart[3] = noteLNStart[5] = noteLNStart[7] = 
				noteLNStart[9] = noteLNStart[11] = noteLNStart[13] = noteLNStart[15] = lnStart;
		noteLNEnd[1] = noteLNEnd[3] = noteLNEnd[5] = noteLNEnd[7] = 
				noteLNEnd[9] = noteLNEnd[11] = noteLNEnd[13] = noteLNEnd[15] = lnEnd;
		lnBody = Decal.newDecal(1, 1, new TextureRegion(t_play, 49, 305, 13, 6));
		lnStart = Decal.newDecal(1, 1, new TextureRegion(t_play, 49, 291, 13, 6));
		lnEnd = Decal.newDecal(1, 1, new TextureRegion(t_play, 49, 298, 13, 6));
		noteLNBody[2] = noteLNBody[4] = noteLNBody[6] = 
				noteLNBody[10] = noteLNBody[12] = noteLNBody[14] = lnBody;
		noteLNStart[2] = noteLNStart[4] = noteLNStart[6] = 
				noteLNStart[10] = noteLNStart[12] = noteLNStart[14] = lnStart;
		noteLNEnd[2] = noteLNEnd[4] = noteLNEnd[6] = 
				noteLNEnd[10] = noteLNEnd[12] = noteLNEnd[14] = lnEnd;
		
	}
	
	private void draw3DNotes(SpriteBatch batch) {
		batch.end();
		
		// 3D note only supports 1P channel
		for (BMSKeyData d: Rhythmus.bmsData.bmsdata) {
			if (d.is1PChannel()) {
				int kn = d.getKeyNum();
				
				if (!Scene_Play_Setting.isKeyEnabled[kn])
					continue;
				int pos = d.getPosY(Scene_Play_Setting.getLainHeight() / 100.0 * speed)
						+ Scene_Play_Setting.getLainBottom() - pos_y;
				if (pos < 0) continue;				// under screen
				if (pos > 480+2000) continue;		// over screen (screenHeight) (cuz its 3D, it can go long)
				if (d.getAttr() != 0) continue;		// only 0 is drawable note
				
				// SAPZIL based Programming
				float d_pos = (pos / 480.0f * 4 - 1.92f) * speed;
				if (d_pos < -1.3f)
					d_pos = -1.3f;

				Decal d_note = noteSprite[ kn ];
				d_note.setPosition(notePosition[ kn ], 
						(float)(d_pos*Math.sin(30.0*Math.PI/180)),
						1.01f - (float)(d_pos*Math.cos(30.0/180*Math.PI)));
				d_note.setRotationX(-60f);
				Rhythmus.dbatch.add(d_note);
				Rhythmus.dbatch.flush();
			}
		}
		
		batch.begin();
	}
	

	@Override
	public void draw(SpriteBatch batch) {
		// draw note from eclipsedTime
		nowTime = Scene_Play.eclipsedTime;
		double beat = Scene_Play.nowBeat;
		// Time->Beat->pos method necessary.
		pos_y = (int) (Rhythmus.bmsData.getNotePositionWithBPM(Scene_Play_Setting.getLainHeight(), bpms, beat) * speed);
		
		draw3DNotes(batch);
	}
}
