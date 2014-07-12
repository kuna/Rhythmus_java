package com.kuna.rhythmus;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
 * plays fullcombo effect
 */
public class Scene_Play_FullCombo implements Scene_Interface {
	Sprite s;
	Sprite[] text = new Sprite[2];
	int nowTime;
	int effectTime = -500000;
	
	public Scene_Play_FullCombo(Texture t_play) {
		s = new Sprite(new TextureRegion(t_play, 830, 0, 194, 267));
		s.setSize(Scene_Play_Setting.lainWidth, Scene_Play_Setting.getLainHeight());
		s.setPosition(Scene_Play_Setting.leftPos, Scene_Play_Setting.getLainBottom());
		
		text[0] = new Sprite(new TextureRegion(t_play, 695, 0, 134, 17));
		text[1] = new Sprite(new TextureRegion(t_play, 695, 17, 134, 17));
	}
	
	public void doFullCombo() {
		effectTime = nowTime;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		nowTime = Scene_Play.eclipsedTime;
		int t = nowTime - effectTime;
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		
		// fullcombo bg first
		float a=0;
		float h=Scene_Play_Setting.getLainHeight();
		
		a = (float)t / 500;
		a = 1-a;
		if (a < 0) a = 0;
		h *= (1-a)*2;
		s.setSize(Scene_Play_Setting.lainWidth, h);
		if (a > 0)
			s.draw(batch, a);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		// draw fullcombo char
		if (t > 1000) {
			a = (float)(t-100) / 500;
			a = 1-a;
			if (a < 0) a = 0;
		} else {
			a = 1;
		}
		Sprite textspr = text[t%2];
		textspr.setPosition(Scene_Play_Setting.leftPos + Scene_Play_Setting.lainWidth / 2 - textspr.getWidth()/2, 
				Scene_Play_Setting.getLainBottom() + Scene_Play_Setting.getLainHeight()/2);
		if (a > 0)
			textspr.draw(batch, a);
		
	}
}
