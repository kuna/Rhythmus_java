package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Scene_Decide implements Scene {
	private Texture t_bg;
	private Sprite s_bg;
	
	private BitmapFont font;

	private ShapeRenderer r;
	
	private boolean initalized = false;
	
	@Override
	public void init()
	{
		// load bitmap
		String stagefilePath = Rhythmus.bmsParser.dir + Rhythmus.bmsParser.stagefile;
		if (Gdx.files.external(stagefilePath).exists() && !Gdx.files.external(stagefilePath).isDirectory()) {
			Texture.setEnforcePotImages(false);
			t_bg = new Texture(Gdx.files.external(stagefilePath));
			s_bg = new Sprite(t_bg);
			s_bg.setSize(800, 480);
		}
		
		font = new BitmapFont();
		r = new ShapeRenderer();

		// Thread inside so no deadlock
		BMSData.LoadData(Rhythmus.bmsParser);
		
		initalized = true;
	}

	@Override
	public void draw(SpriteBatch batch) {
		if (!initalized) return;
		
		// draw BMS title and background
		if (s_bg != null) {
			s_bg.draw(batch);
		} else {
			font.setScale(2.8f);
			font.setColor(Color.WHITE);
			font.draw(batch, Rhythmus.bmsParser.title, 120, 360);
			
			font.setScale(1.2f);
			font.setColor(Color.LIGHT_GRAY);
			font.draw(batch, Rhythmus.bmsParser.artist, 120, 420);
			
			font.setScale(1.2f);
			font.setColor(Color.LIGHT_GRAY);
			font.draw(batch, Rhythmus.bmsParser.subtitle, 120, 300);
		}
		
		// show loading status
		r.begin(ShapeType.Rectangle);
		r.setColor(Color.WHITE);
		r.rect(30, 80, 740, 30);
		r.end();

		r.begin(ShapeType.FilledRectangle);
		r.setColor(Color.WHITE);
		r.filledRect(30, 80, (float)740*BMSData.progress/100, 30);
		r.end();
		
		font.setScale(1.0f);
		font.draw(batch, String.format("Loading %d", BMSData.progress), 120, 100);
		
		if (BMSData.isLoaded) {
			Rhythmus.changeScene( Rhythmus.SCENE_PLAY );
		}
	}

	@Override
	public void dispose() {
		if (t_bg != null)
			t_bg.dispose();
	}
}
