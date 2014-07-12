package com.kuna.rhythmus;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
/*
 * TODO implementing
 * not stable!
 */
public class Scene_Play_3DLain implements Scene_Interface {
	public Decal s_lain_left8K;
	public Decal s_lain_right8K;
	public Decal s_lain_7K;
	public Decal s_lain_left6K;
	public Decal s_lain_right6K;
	public Decal s_lain_5K;
	
	boolean[] drawLain;
	
	public Scene_Play_3DLain(Texture t_play, boolean[] drawLain) {
		this.drawLain = drawLain;
		
		s_lain_left8K = Decal.newDecal(1, 1, new TextureRegion(t_play, 764, 267, 260, 2));
		s_lain_left8K.setScale(3.0f, 200.0f);
		
		s_lain_right8K = Decal.newDecal(1, 1, new TextureRegion(t_play, 764, 270, 260, 2));
		s_lain_right8K.setScale(3.0f, 200.0f);
		
		s_lain_left6K = Decal.newDecal(1, 1, new TextureRegion(t_play, 821, 273, 203, 2));
		s_lain_left8K.setScale(3.0f, 200.0f);
		
		s_lain_right6K = Decal.newDecal(1, 1, new TextureRegion(t_play, 821, 276, 203, 2));
		s_lain_right6K.setScale(3.0f, 200.0f);
		
		s_lain_7K = Decal.newDecal(1, 1, new TextureRegion(t_play, 819, 279, 205, 2));
		s_lain_7K.setScale(3.0f, 200.0f);
		
		s_lain_5K = Decal.newDecal(1, 1, new TextureRegion(t_play, 876, 282, 148, 2));
		s_lain_5K.setScale(3.0f, 200.0f);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.end();

		Decal d = null;
		if (drawLain[0])
			d = s_lain_left8K;
		if (drawLain[1])
			d = s_lain_right8K;
		if (drawLain[2])
			d = s_lain_7K;
		if (drawLain[3])
			d = s_lain_left6K;
		if (drawLain[4])
			d = s_lain_right6K;
		if (drawLain[5])
			d = s_lain_5K;
		
		if (d == null) {
			// this never should be happen
			batch.begin();
			return;
		}

		d.setColor(1, 1, 1, Scene_Play_Setting.lainAlpha);
		d.setPosition(0, 0, 1);
		d.setRotationX(-60f);
		Rhythmus.dbatch.add(d);
		Rhythmus.dbatch.flush();
		batch.begin();
	}
}
