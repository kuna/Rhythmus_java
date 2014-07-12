package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;

public class KeySettingInputListener implements InputProcessor {

	private Sound snd_btn;
	public int selectedIndex = 1;
	
	public KeySettingInputListener() {
		snd_btn = Gdx.audio.newSound(Gdx.files.internal("data/change.wav"));
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		snd_btn.play();
		
		if (keycode == Input.Keys.ESCAPE) {
			// exit
			Rhythmus.sKey.fade.doFadeOut();
			return false;
		}
		
		Settings.keycode[ selectedIndex ] = keycode;
		selectedIndex++;
		if (selectedIndex > 16)
			selectedIndex = 1;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenX = screenX*800/Gdx.graphics.getWidth();
		screenY = screenY*480/Gdx.graphics.getHeight();
		
		screenY = 480 - screenY;
		
		if (screenY > 440 && screenX > 760) {
			// exit
			Rhythmus.sKey.fade.doFadeOut();
			return false;
		}
		
		for (int i=1; i<=16; i++) {
			int s[] = Rhythmus.sKey.keyShapes[i];
			if (s[0] < screenX && screenX < s[0]+s[2] &&
					s[1] < screenY && screenY < s[1]+s[3]) {
				selectedIndex = i;
				break;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
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
}
