package com.kuna.rhythmus;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

public interface Scene {
	public void init();
	public void draw(SpriteBatch batch, DecalBatch dbatch);
	public void dispose();
}
