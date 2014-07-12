package com.kuna.rhythmus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Scene_Play_Guage implements Scene_Interface {
	int x, y, wid, hei;
	int guage;
	int guageEffect = 0;
	BitmapFont font;
	Sprite s;
	
	public Scene_Play_Guage(Texture t_play, int x, int y, int wid, int hei) {	
		this.x = x;
		this.y = y;
		this.wid = wid;
		this.hei = hei;

		Sprite s_guage_normal = new Sprite(new TextureRegion(t_play, 63, 332, 15, 4));
		Sprite s_guage_hard = new Sprite(new TextureRegion(t_play, 63, 336, 15, 4));
		
		if (Settings.guagemode == Settings.GUAGE_HARD) {
			s = s_guage_hard;
		} else {
			s = s_guage_normal;
		}
		s.setSize(wid, s.getHeight());
		font = new BitmapFont();
	}
	
	// if you want blink effect, call every frame
	// TODO implementation necessary
	public void guageBlinkEffect() {
		guageEffect = (int) (Math.random()*4);
	}
	
	public void setGuage(int val) {
		this.guage = val;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		// TODO draw background
		
		
		// draw guage
		for (int i=y; i<y+hei; i+= 5)
		{
			s.setPosition(x, i);
			
			if (i > y && i > y+hei*guage/100)
				s.draw(batch, 0.5f);
			else
				s.draw(batch);
		}
		
		// draw number
		int g = (int)(guage/2)*2;
		font.draw(batch, String.format("%d%%", g), x, y - 20);
	}

}
