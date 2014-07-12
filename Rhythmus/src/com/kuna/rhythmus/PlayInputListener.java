package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;

public class PlayInputListener implements InputProcessor {
	
	public PlayInputListener() {
		touch = new boolean[17];
	}
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.UP) {
			Settings.speed += 0.2f;
			Rhythmus.sPlay.spNote.setSpeed(Settings.speed);
		} else if (keycode == Input.Keys.DOWN) {
			Settings.speed -= 0.2f;
			if (Settings.speed<=0)
				Settings.speed = 0.2f;
			Rhythmus.sPlay.spNote.setSpeed(Settings.speed);
		}
		
		// default key type: LS A S D SPC J K L ;
		for (int i=1; i<=16; i++) {
			if (keycode == Settings.keycode[i]) {
				Rhythmus.sPlay.pressNote(i);
				break;
			}
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		for (int i=1; i<=16; i++) {
			if (keycode == Settings.keycode[i]) {
				Rhythmus.sPlay.releaseNote(i);
				break;
			}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenX = screenX*800/Gdx.graphics.getWidth();
		screenY = screenY*480/Gdx.graphics.getHeight();
		
		// speed up/down
		if (screenX < 40 && screenY <40) {
			Rhythmus.sPlay.changeSpeed(Settings.speed + 0.2f);
		}
		if (screenX < 40 && screenY <80 && screenY >=40) {
			Rhythmus.sPlay.changeSpeed(Settings.speed - 0.2f);
		}
		
		// exit
		if (screenX > 760 && screenY < 40) {
			Rhythmus.sPlay.exitGame(1);
		}
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * customs
	 */
	
	private static boolean[] touch = new boolean [17];
	public static boolean[] getTouchStatus() {
		// check touch status and put it in the boolean array
		// to check key press
		boolean [] touched = new boolean[17];
		
		for (int i=0; i<=10; i++)	/* maximum pointer to 10 */
		{
			if (Gdx.input.isTouched(i)) {
				int x = Gdx.input.getX(i);
				int y = Gdx.input.getY(i);
				
				// change num to 800x480
				x = x*800/Gdx.graphics.getWidth();
				y = y*480/Gdx.graphics.getHeight();
				
				for (int a=1; a<=16; a++)
				{
					if (x >= Scene_Play_Setting.noteX[a] && x < Scene_Play_Setting.noteX[a] + Scene_Play_Setting.noteWidth[a])
						touched[a] = true;
				}
			}
		}
		
		return touched;
	}
	
	public static void getTouchInput() {
		// get touch input
		// this method should be called at rendering
		// to catch user input
		
		boolean [] _touch = getTouchStatus();
		
		for (int i=1; i<17; i++) {
			if (!touch[i] && _touch[i])
				Scene_Play.pressNote(i);
			else if (touch[i] && !_touch[i])
				Scene_Play.releaseNote(i);
		}
		
		touch = _touch;
	}
}
