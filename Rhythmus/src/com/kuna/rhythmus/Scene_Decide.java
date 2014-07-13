package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
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
		String stagefilePath = Rhythmus.bmsData.dir + Rhythmus.bmsData.stagefile;
		if (Gdx.files.absolute(stagefilePath).exists() && !Gdx.files.absolute(stagefilePath).isDirectory()) {
			//Texture.setEnforcePotImages(false);
			t_bg = new Texture(Gdx.files.absolute(stagefilePath));
			s_bg = new Sprite(t_bg);
			s_bg.setSize(800, 480);
		}
		
		font = new BitmapFont();
		r = new ShapeRenderer();

		// Thread inside so no deadlock
		BMSResource.LoadData(Rhythmus.bmsData);
		
		initalized = true;
	}

	@Override
	public void draw(SpriteBatch batch, DecalBatch dbatch) {
		if (!initalized) return;
		
		batch.begin();
		
		// draw BMS title and background
		if (s_bg != null) {
			s_bg.draw(batch);
		} else {
			font.setScale(2.8f);
			font.setColor(Color.WHITE);
			font.draw(batch, Rhythmus.bmsData.title, 120, 360);
			
			font.setScale(1.2f);
			font.setColor(Color.LIGHT_GRAY);
			font.draw(batch, Rhythmus.bmsData.artist, 120, 420);
			
			font.setScale(1.2f);
			font.setColor(Color.LIGHT_GRAY);
			font.draw(batch, Rhythmus.bmsData.subtitle, 120, 300);
		}
		
		font.setScale(1.0f);
		font.draw(batch, String.format("Loading %d", BMSResource.progress), 120, 100);
		
		batch.end();
		
		// show loading status
		r.begin(ShapeType.Line);
	    r.setProjectionMatrix( batch.getProjectionMatrix() );
		r.setColor(Color.WHITE);
		r.rect(30, 80, 740, 30);
		r.end();

		r.begin(ShapeType.Filled);
	    r.setProjectionMatrix( batch.getProjectionMatrix() );
		r.setColor(Color.WHITE);
		r.rect(30, 80, (float)740*BMSResource.progress/100, 30);
		r.end();
		
		if (BMSResource.isLoaded) {
			Rhythmus.changeScene( Rhythmus.SCENE_PLAY );
		}
	}

	@Override
	public void dispose() {
		if (t_bg != null)
			t_bg.dispose();
	}
}
