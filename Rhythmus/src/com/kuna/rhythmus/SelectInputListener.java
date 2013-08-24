package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class SelectInputListener implements InputProcessor {
	public static int x;
	public static int y;
	public static int moveY = 0;
	public int startX;
	public int startY;
	public boolean mouseDown = false;
	public boolean clicked = false;

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		startX = x = screenX;
		startY = y = screenY;
		
		mouseDown = true;
				
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		x = screenX;
		y = screenY;
		
		mouseDown = false;
		
		if (x == startX && y == startY)
			clicked = true;

		screenX = screenX*800/Gdx.graphics.getWidth();
		screenY = screenY*480/Gdx.graphics.getHeight();
		
		// check out button press
		if (screenX > 116 && screenX < 156) {
			if (screenY > 280 && screenY < 340) {
					Rhythmus.sSelect.buttonPress(7);
			} else if (screenY > 222) {
				Rhythmus.sSelect.buttonPress(5);
			} else if (screenY > 164) {
				Rhythmus.sSelect.buttonPress(3);
			} else if(screenY > 108) {
				Rhythmus.sSelect.buttonPress(1);
			}
		}
		if (screenX > 392 && screenX < 436) {
			if (screenY > 280 && screenY < 340) {
				Rhythmus.sSelect.buttonPress(8);
			} else if (screenY > 222) {
				Rhythmus.sSelect.buttonPress(6);
			} else if (screenY > 164) {
				Rhythmus.sSelect.buttonPress(4);
			} else if(screenY > 108) {
				Rhythmus.sSelect.buttonPress(2);
			}
		}
		
		if (screenY > 340 && screenY < 388 && screenX > 232 && screenX < 368)
			Rhythmus.sSelect.buttonPress(9);
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		x = screenX;
		y = screenY;
		
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
