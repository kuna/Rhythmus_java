package com.kuna.rhythmus;

import java.util.logging.LogRecord;

import javax.xml.ws.AsyncHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.TimeUtils;
import com.sun.org.glassfish.external.amx.MBeanListener.Callback;

/*
 * this method must be called after all screen are drawn
 */
public class Scene_FadeInOut implements Scene_Interface{
	private static final int MOTION_TIME = 1500;
	
	private int nowTime;
	private int fadeInTime = -1000;
	private int fadeOutTime = -1000;
	private boolean isfadeIn = false;
	private boolean isfadeOut = false;
	private float alpha = 0;
	
	private Handler hFadeIn, hFadeOut;
	private ShapeRenderer r;
	
	public Scene_FadeInOut(Handler hFadeIn, Handler hFadeOut) {
		this.hFadeIn = hFadeIn;
		this.hFadeOut = hFadeOut;
		r = new ShapeRenderer();
	}
	
	public void doFadeIn() {
		if (nowTime == 0)
			nowTime = (int) TimeUtils.millis();
		fadeInTime = nowTime;
		isfadeIn = true;
		isfadeOut = false;
	}
	
	public void doFadeOut() {
		if (nowTime == 0)
			nowTime = (int) TimeUtils.millis();
		fadeOutTime = nowTime;
		isfadeIn = false;
		isfadeOut = true;
	}

	@Override
	public void draw(SpriteBatch batch) {
		nowTime = (int) TimeUtils.millis();
		
		if (isfadeIn) {
			alpha = (float)(nowTime - fadeInTime) / MOTION_TIME;
			alpha = 1-alpha;
			if (alpha > 1) {
				alpha = 1;
			}
			if (alpha < 0) {
				if (hFadeIn != null)
					hFadeIn.InformEvent(null);
				isfadeIn = false;
				alpha = 0;
			}
		}
		
		if (isfadeOut) {
			alpha = (float)(nowTime - fadeOutTime) / MOTION_TIME;
			if (alpha > 1) {
				if (hFadeOut != null)
					hFadeOut.InformEvent(null);
				isfadeOut = false;
				alpha = 1;
			}
			if (alpha < 0) {
				alpha = 0;
			}
		}
		
		if (alpha != 0) {
			// draw
			Gdx.gl.glEnable(GL10.GL_BLEND);
		    Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		    r.setProjectionMatrix( batch.getProjectionMatrix() );
			r.begin(ShapeType.FilledRectangle);
			r.setColor(new Color(0, 0, 0, alpha));
			r.filledRect(0, 0, 800, 480);
			r.end();
			Gdx.gl.glDisable(GL10.GL_BLEND);
		}
	}

}
