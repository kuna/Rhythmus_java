package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.sun.xml.internal.bind.v2.model.impl.ModelBuilder;

public class Scene_Play_Lain implements Scene_Interface{
	public Sprite s_lain_left8K;
	public Sprite s_lain_right8K;
	public Sprite s_lain_7K;
	public Sprite s_lain_left6K;
	public Sprite s_lain_right6K;
	public Sprite s_lain_5K;
	
	public Sprite lainBeatEffect;
	
	private boolean[] drawLain = new boolean[6];

	public Decal d_lain;
	
	public Scene_Play_Lain(Texture t_play, boolean[] drawLain) {
		this.drawLain = drawLain;
		
		s_lain_left8K = new Sprite(new TextureRegion(t_play, 764, 267, 260, 2));
		s_lain_left8K.setPosition(Scene_Play_Setting.lainDest[0], Scene_Play_Setting.lainDest[1]);
		s_lain_left8K.setSize(Scene_Play_Setting.lainDest[2], Scene_Play_Setting.lainDest[3]);
		
		s_lain_right8K = new Sprite(new TextureRegion(t_play, 764, 270, 260, 2));
		s_lain_right8K.setPosition(Scene_Play_Setting.lainDest[0]+Scene_Play_Setting.lainWidth+Scene_Play_Setting.lainMargin,
				Scene_Play_Setting.lainDest[1]);
		s_lain_right8K.setSize(Scene_Play_Setting.lainDest[2], Scene_Play_Setting.lainDest[3]);
		
		s_lain_left6K = new Sprite(new TextureRegion(t_play, 821, 273, 203, 2));
		s_lain_left6K.setPosition(Scene_Play_Setting.lainDest[0], Scene_Play_Setting.lainDest[1]);
		s_lain_left6K.setSize(Scene_Play_Setting.lainDest[2], Scene_Play_Setting.lainDest[3]);
		
		s_lain_right6K = new Sprite(new TextureRegion(t_play, 821, 276, 203, 2));
		s_lain_right6K.setPosition(Scene_Play_Setting.lainDest[0], Scene_Play_Setting.lainDest[1]);
		s_lain_right6K.setSize(Scene_Play_Setting.lainDest[2], Scene_Play_Setting.lainDest[3]);
		
		s_lain_7K = new Sprite(new TextureRegion(t_play, 819, 279, 205, 2));
		s_lain_7K.setPosition(Scene_Play_Setting.lainDest[0], Scene_Play_Setting.lainDest[1]);
		s_lain_7K.setSize(Scene_Play_Setting.lainDest[2], Scene_Play_Setting.lainDest[3]);
		
		s_lain_5K = new Sprite(new TextureRegion(t_play, 876, 282, 148, 2));
		s_lain_5K.setPosition(Scene_Play_Setting.lainDest[0], Scene_Play_Setting.lainDest[1]);
		s_lain_5K.setSize(Scene_Play_Setting.lainDest[2], Scene_Play_Setting.lainDest[3]);
		
		lainBeatEffect = new Sprite(new TextureRegion(t_play, 63, 287, 144, 18));
		lainBeatEffect.setSize(Scene_Play_Setting.lainWidth, 50);
	}
	
	public void set8KLeft(boolean val) {
		drawLain[0] = val;
	}
	
	public void set8KRight(boolean val) {
		drawLain[1] = val;
	}
	
	public void set7K(boolean val) {
		drawLain[2] = val;
	}
	
	public void set6KLeft(boolean val) {
		drawLain[3] = val;
	}
	
	public void set6KRight(boolean val) {
		drawLain[4] = val;
	}
	
	public void set5K(boolean val) {
		drawLain[5] = val;
	}

	@Override
	public void draw(SpriteBatch batch) {
		if (drawLain[0])
			s_lain_left8K.draw(batch, Scene_Play_Setting.lainAlpha);
		if (drawLain[1]) {
			s_lain_right8K.draw(batch, Scene_Play_Setting.lainAlpha);
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			lainBeatEffect.setPosition(Scene_Play_Setting.lainDest[0]+Scene_Play_Setting.lainWidth+Scene_Play_Setting.lainMargin, Scene_Play_Setting.lainDest[1]);
			lainBeatEffect.draw(batch, (float)(Scene_Play.nowBeat*4%1)/4);
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
		if (drawLain[2])
			s_lain_7K.draw(batch, Scene_Play_Setting.lainAlpha);
		if (drawLain[3])
			s_lain_left6K.draw(batch, Scene_Play_Setting.lainAlpha);
		if (drawLain[4])
			s_lain_right6K.draw(batch, Scene_Play_Setting.lainAlpha);
		if (drawLain[5])
			s_lain_5K.draw(batch, Scene_Play_Setting.lainAlpha);
		
		// common
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		lainBeatEffect.setPosition(Scene_Play_Setting.lainDest[0], Scene_Play_Setting.lainDest[1]);
		lainBeatEffect.draw(batch, (float)(Scene_Play.nowBeat*4%1)/4);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
}
