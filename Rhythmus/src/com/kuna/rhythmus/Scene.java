package com.kuna.rhythmus;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Scene {
	public void init();
	public void draw(SpriteBatch batch);
	public void dispose();
}
