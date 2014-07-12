package com.kuna.rhythmus;

import java.awt.event.KeyEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.TimeUtils;

public class Scene_KeySetting implements Scene {
	
	private int startTime;
	private int nowTime;
	private int eclipsedTime;
	
	private BitmapFont font;
	private ShapeRenderer r;
	
	public int[][] keyShapes = new int[17][];
	
	private Texture select;
	private Sprite s_btn_exit;
	
	private KeySettingInputListener pListener;
	
	public Scene_FadeInOut fade;
	
	@Override
	public void init() {
		startTime = (int) TimeUtils.millis();
		font = new BitmapFont();
		r = new ShapeRenderer();
		
		keyShapes[1] = new int[] {160, 180, 40, 60};
		keyShapes[2] = new int[] {185, 250, 40, 60};
		keyShapes[3] = new int[] {210, 180, 40, 60};
		keyShapes[4] = new int[] {235, 250, 40, 60};
		keyShapes[5] = new int[] {260, 180, 40, 60};
		keyShapes[6] = new int[] {285, 250, 40, 60};
		keyShapes[7] = new int[] {310, 180, 40, 60};
		keyShapes[8] = new int[] {60, 210, 60, 60};

		keyShapes[9] = new int[] {450, 180, 40, 60};
		keyShapes[10] = new int[] {475, 250, 40, 60};
		keyShapes[11] = new int[] {500, 180, 40, 60};
		keyShapes[12] = new int[] {525, 250, 40, 60};
		keyShapes[13] = new int[] {550, 180, 40, 60};
		keyShapes[14] = new int[] {575, 250, 40, 60};
		keyShapes[15] = new int[] {600, 180, 40, 60};
		keyShapes[16] = new int[] {680, 210, 60, 60};
		
		fade = new Scene_FadeInOut(null, new Handler() {
			@Override
			public void InformEvent(Object arg) {
				// change scene
				Rhythmus.changeScene(Rhythmus.SCENE_SELECT);
			}
		});
		
		pListener = new KeySettingInputListener();
		Gdx.input.setInputProcessor(pListener);
	}

	@Override
	public void draw(SpriteBatch batch, DecalBatch dbatch) {
		nowTime = (int) TimeUtils.millis();
		eclipsedTime = nowTime - startTime;
		
		batch.begin();
		
		// btn
		if (s_btn_exit != null)
			s_btn_exit.draw(batch);
		
		// draw key status
		for (int i=1; i<=16; i++) {
			if (i == pListener.selectedIndex)
				font.setColor(Color.RED);
			else
				font.setColor(Color.WHITE);
			
			font.draw(batch, Integer.toString(Settings.keycode[i]), keyShapes[i][0]+10, keyShapes[i][1]+15);
		}
		
		batch.end();

		select = new Texture(Gdx.files.internal("data/select.png"));
		s_btn_exit = new Sprite(select, 195, 776, 19, 19);
		s_btn_exit.setPosition(760, 440);
		
		// draw key shape
		for (int i=1; i<=16; i++) {
			int s[] = keyShapes[i];
			if (s == null)
				continue;

			r.setColor(Color.WHITE);
			if (i == 8 || i == 16) {
				r.begin(ShapeType.Circle);
			    r.setProjectionMatrix( batch.getProjectionMatrix() );
				r.circle(s[0], s[1], s[2]/2);
			} else {
				r.begin(ShapeType.Rectangle);
			    r.setProjectionMatrix( batch.getProjectionMatrix() );
				r.rect(s[0], s[1], s[2], s[3]);
			}
			r.end();
		}
		
		
		// fade
		fade.draw(batch);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
