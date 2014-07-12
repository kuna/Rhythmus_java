package com.kuna.rhythmus;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;

public class Scene_Play_Bom implements Scene_Interface {
	private final static int BOMWIDTH = 200;
	private final static int BOMHEIGHT = 256;
	private final static double TIMEPERFRAME = 20.0;
	int x, y;
	Sprite[] s_bom;
	int nowTime, bomTime;
	boolean isLongEffect = false;
	
	public Scene_Play_Bom(int x, int y, Sprite[] s_bom) {
		this.x = x;
		this.y = y;
		this.s_bom = s_bom;
		bomTime = -1000;	// initalization
	}
	
	public void isLongEffecet(boolean b) {
		isLongEffect = b;
	}
	
	public void setBomTime() {
		bomTime = nowTime;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		nowTime = Scene_Play.eclipsedTime;
		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);
		// draw bom effect
		for (int i=0; i<8; i++) {
			int frame = (int) ((nowTime - bomTime)/TIMEPERFRAME);
			// if longnote...?
			if (isLongEffect)
				frame = (nowTime%2==0)?2:6;
			if (frame < 16 && frame >= 0) {
				s_bom[frame].setX(x - BOMWIDTH/2);
				s_bom[frame].setY(y - BOMHEIGHT/2);
				s_bom[frame].draw(batch);
			}
		}
		
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

}
