package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class PlayInputListener implements InputProcessor {
	@Override
	public boolean keyDown(int keycode) {
		// default key type: LS A S D SPC J K L ;
		switch (keycode) {
		case 29:	// A
			Rhythmus.sPlay.pressNote(1);
			break;
		case 47:	// S
			Rhythmus.sPlay.pressNote(2);
			break;
		case 32:	// D
			Rhythmus.sPlay.pressNote(3);
			break;
		case 62:	// SPC
			Rhythmus.sPlay.pressNote(4);
			break;
		case 38:	// J
			Rhythmus.sPlay.pressNote(5);
			break;
		case 39:	// K
			Rhythmus.sPlay.pressNote(6);
			break;
		case 40:	// L
			Rhythmus.sPlay.pressNote(7);
			break;
		case 59:	// SHIFT
			Rhythmus.sPlay.pressNote(0);
			break;
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case 29:	// A
			Rhythmus.sPlay.releaseNote(1);
			break;
		case 47:	// S
			Rhythmus.sPlay.releaseNote(2);
			break;
		case 32:	// D
			Rhythmus.sPlay.releaseNote(3);
			break;
		case 62:	// SPC
			Rhythmus.sPlay.releaseNote(4);
			break;
		case 38:	// J
			Rhythmus.sPlay.releaseNote(5);
			break;
		case 39:	// K
			Rhythmus.sPlay.releaseNote(6);
			break;
		case 40:	// L
			Rhythmus.sPlay.releaseNote(7);
			break;
		case 59:	// SHIFT
			Rhythmus.sPlay.releaseNote(0);
			break;
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
		if (screenX < 80 && screenY <80) {
			Rhythmus.sPlay.changeSpeed(Rhythmus.sPlay.speed + 0.5f);
		}
		if (screenX < 80 && screenY <160 && screenY >=80) {
			Rhythmus.sPlay.changeSpeed(Rhythmus.sPlay.speed - 0.5f);
		}
		
		// exit
		if (screenX > 720 && screenY < 80) {
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
}
