package com.kuna.rhythmus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.kuna.rhythmus.bmsdata.BMSKeyData;

public class Scene_Play_BGA implements Scene_Interface {
	private final static int MISS_TIME = 500;
	
	private Sprite bga_miss = null;
	private Sprite bga_now = null;
	private Sprite bga_overlay = null;
	private int missTime;
	private int nowTime;
	private double nowBeat;
	
	private int x, y, width, height;
	
	public Scene_Play_BGA(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		missTime = -MISS_TIME;
	}
	
	public void miss() {
		missTime = (int) TimeUtils.millis();
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		nowTime = Scene_Play.eclipsedTime;
		nowBeat = Scene_Play.nowBeat;
		
		// draw BG (layer 7,6,4)
		for (int i=0; i<Rhythmus.bmsData.bgadata.size(); i++) {
			BMSKeyData d = Rhythmus.bmsData.bgadata.get(i);
			if (d.getBeat() > nowBeat)
				break;
			if (d.getAttr() == 0) {
				Texture t;
				if (d.isPoorChannel()) {
					t = BMSResource.bg[(int) d.getValue()];
					if (t != null) {
						bga_miss = new Sprite(t);
						bga_miss.setPosition(x, y);
						bga_miss.setSize(width, height);
					}
				}
				else if (d.isBGALayerChannel()) {
					t = BMSResource.bg[(int) d.getValue()];
					if (t != null) {
						bga_overlay = new Sprite(t);
						bga_overlay.setPosition(x, y);
						bga_overlay.setSize(width, height);
					}
				}
				else if (d.isBGAChannel()) {
					t = BMSResource.bg[(int) d.getValue()];
					if (t != null) {
						bga_now = new Sprite(t);
						bga_now.setPosition(x, y);
						bga_now.setSize(width, height);
					}
				}
				d.setAttr(1);
			}
		}
		
		if (nowTime - missTime < MISS_TIME && bga_miss != null) {
			bga_miss.draw(batch);
		} else {
			if (bga_now != null)
				bga_now.draw(batch);
		}
	}
}
