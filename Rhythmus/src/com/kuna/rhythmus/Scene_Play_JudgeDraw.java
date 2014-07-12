package com.kuna.rhythmus;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Scene_Play_JudgeDraw implements Scene_Interface {
	Sprite judgespr[] = new Sprite[7];
	Sprite judgenum[] = new Sprite[40];
	
	int nowTime, judgeTime;
	int x, y;
	int judge;
	int combo;
	float scale;
	
	public Scene_Play_JudgeDraw(Texture t_play, int x, int y, float scale) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		judgeTime = -10000;

		// load judge
		judgespr[0] = new Sprite(new TextureRegion(t_play, 64, 1, 75, 33));		// PGREAT
		judgespr[1] = new Sprite(new TextureRegion(t_play, 64, 36, 75, 33));	// PGREAT
		judgespr[2] = new Sprite(new TextureRegion(t_play, 64, 71, 75, 33));	// PGREAT
		judgespr[3] = new Sprite(new TextureRegion(t_play, 64, 107, 75, 33));	// GREAT
		judgespr[4] = new Sprite(new TextureRegion(t_play, 71, 178, 60, 33));	// GOOD
		judgespr[5] = new Sprite(new TextureRegion(t_play, 74, 214, 60, 33));	// POOR
		judgespr[6] = new Sprite(new TextureRegion(t_play, 81, 250, 45, 33));	// BAD
		
		for (int i=0; i<7; i++) {
			judgespr[i].scale(scale);
		}
		
		int _top[] = {1, 36, 71, 107};
		for (int i=0; i<4; i++) {
			for (int j=0; j<10; j++) {
				judgenum[i*10 + j] = new Sprite(new TextureRegion(t_play, 150+j*17, _top[i], 17, 33));
				judgenum[i*10 + j].scale(scale);
			}
		}
		
	}
	
	public void setCombo(int combo) {
		this.combo = combo;
	}
	
	public void setJudge(int judge) {
		this.judge = judge;
	}
	
	public void setJudgeTime() {
		judgeTime = nowTime;
	}

	private final static int SPACING = 10;
	private final static int NUM_WIDTH = 17;

	@Override
	public void draw(SpriteBatch batch) {
		nowTime = Scene_Play.eclipsedTime;
		if (!(nowTime - judgeTime > 0 && nowTime - judgeTime < 3000))
			return;
		
		// get sprite
		int judgesprNum=-1;
		if (judge == Scene_Play_Judge.JUDGE_PGREAT) {
			judgesprNum = nowTime%3;
		} else if (nowTime%3 != 0) { /* for blink effect */
			if (judge == Scene_Play_Judge.JUDGE_GREAT)
				judgesprNum = 3;
			if (judge == Scene_Play_Judge.JUDGE_GOOD)
				judgesprNum = 4;
			if (judge == Scene_Play_Judge.JUDGE_POOR)
				judgesprNum = 5;
			if (judge == Scene_Play_Judge.JUDGE_BAD)
				judgesprNum = 6;
		}
		if (judgesprNum < 0)
			return;	// TODO UNEXCEPTED
		Sprite judgespr_draw = judgespr[judgesprNum];
		
		// calculate pre-size for centering number
		int width;
		if (judge == Scene_Play_Judge.JUDGE_BAD || judge == Scene_Play_Judge.JUDGE_POOR) {
			width = (int) (judgespr_draw.getWidth()*(scale+1));
		} else {
			width = (int) ((judgespr_draw.getWidth() + SPACING + Integer.toString(combo).length()*NUM_WIDTH)*(scale+1));
		}
		int height = (int) judgespr_draw.getHeight();
		
		//width *= scale;
		//height *= scale;
		
		int px = x - width/2;
		int py = y - height/2;
		
		// draw judge
		judgespr_draw.setX(px);
		judgespr_draw.setY(py);
		judgespr_draw.draw(batch);
		px += (judgespr_draw.getWidth() + SPACING) * (scale+1);
		
		if (judgesprNum >= 0 && judgesprNum <= 4) {
			ArrayList<Integer> arr = new ArrayList<Integer>();
			for (int _combo=combo; _combo>0; _combo/=10) {
				arr.add(_combo%10);
			}
			for (int i=arr.size()-1; i>=0; i--) {
				int n = judgesprNum*10 + arr.get(i);
				if (n >= 40) n-=10;
				judgenum[n].setX(px);
				judgenum[n].setY(py);
				judgenum[n].draw(batch);
				px += NUM_WIDTH*(scale+1);
			}
		}
	}
}
