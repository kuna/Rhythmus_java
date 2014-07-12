package com.kuna.rhythmus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
 * return positions and settings for playing
 * 
 */
public class Scene_Play_Setting {
	public static boolean[] isKeyEnabled = new boolean[17];
	public static int leftPos;
	public static int[] noteX = new int[17];
	public static int[] noteWidth = new int[17];
	public static int noteHeight = 10;
	public static int lainWidth, lainBottom, lainMargin;
	public static int[] lainDest;
	public static int lainPrsBaseHeight;
	public static float lainAlpha;
	public static boolean[] drawLain = new boolean[6];
	public static int[] BGADest;
	public static int[] GuageDest;
	public static int[] JudgeDest;
	public static float JudgeScale;
	
	public static void set5KMode() {
		// 5K with SCR_AUTO
		int noteWidthA = 140;
		int noteWidthB = 105;
		
		// enable key
		isKeyEnabled = new boolean[] {false,
				true, true, true, true, true, false, false, false,
				false, false, false, false, false, false, false, false};
		
		lainWidth = noteWidthA*3 + noteWidthB*2;
		leftPos = (int)(800-lainWidth)/2;
		lainBottom = 100;
		lainDest = new int[] { leftPos, lainBottom, lainWidth, 480-lainBottom };
		lainAlpha = 0.8f;
		lainPrsBaseHeight = 100;
		drawLain = new boolean[] {false, false, false, false, false, true};
		
		noteWidth = new int[] {0,
				noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0};
		
		noteX = new int[17];
		noteX[1] = leftPos;
		for (int i=2; i<=5; i++) {
			noteX[i] = noteX[i-1] + ((i%2==0)?noteWidthA:noteWidthB);
		}
		
		BGADest = new int[] {0, 0, 800, 480};
		GuageDest = new int[] {740, 100, 30, 320};
		JudgeDest = new int[] {lainWidth/2+leftPos, 240};
		JudgeScale = 0.5f;
	}

	public static void set6KMode() {
		// 5K
		int noteWidthScr = 162;
		int noteWidthA = 96;
		int noteWidthB = 75;
		
		// enable key
		isKeyEnabled = new boolean[] {false,
				true, true, true, true, true, false, false, true,
				false, false, false, false, false, false, false, false};
		
		lainWidth = noteWidthScr + noteWidthA*3 + noteWidthB*2;
		leftPos = (int)(800-lainWidth)/2;
		lainBottom = 100;
		lainDest = new int[] { leftPos, lainBottom, lainWidth, 480-lainBottom };
		lainAlpha = 0.8f;
		lainPrsBaseHeight = 100;
		drawLain = new boolean[] {false, false, false, true, false, false};
		
		noteWidth = new int[] {0,
				noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, 0, 0, noteWidthScr,
				0, 0, 0, 0, 0, 0, 0, 0};
		
		noteX = new int[17];
		noteX[8] = leftPos;
		noteX[1] = noteX[8] + noteWidthScr;
		for (int i=2; i<=5; i++) {
			noteX[i] = noteX[i-1] + ((i%2==0)?noteWidthA:noteWidthB);
		}
		
		BGADest = new int[] {0, 0, 800, 480};
		GuageDest = new int[] {740, 100, 30, 320};
		JudgeDest = new int[] {lainWidth/2+leftPos, 240};
		JudgeScale = 0.5f;
	}
	
	public static void set7KMode() {
		// 7K with SCR_AUTO
		int noteWidthA = 100;
		int noteWidthB = 75;
		
		// enable key
		isKeyEnabled = new boolean[] {false,
				true, true, true, true, true, true, true, false,
				false, false, false, false, false, false, false, false};
		
		lainWidth = noteWidthA*4 + noteWidthB*3;
		leftPos = (int)(800-lainWidth)/2;
		lainBottom = 100;
		lainDest = new int[] { leftPos, lainBottom, lainWidth, 480-lainBottom };
		lainAlpha = 0.8f;
		lainPrsBaseHeight = 100;
		drawLain = new boolean[] {false, false, true, false, false, false};
		
		noteWidth = new int[] {0,
				noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, 0,
				0, 0, 0, 0, 0, 0, 0, 0};
		
		noteX = new int[17];
		noteX[1] = leftPos;
		for (int i=2; i<=7; i++) {
			noteX[i] = noteX[i-1] + ((i%2==0)?noteWidthA:noteWidthB);
		}
		
		BGADest = new int[] {0, 0, 800, 480};
		GuageDest = new int[] {740, 100, 30, 320};
		JudgeDest = new int[] {lainWidth/2+leftPos, 240};
		JudgeScale = 0.5f;
	}
	
	public static void set8KMode() {
		// 7K
		int noteWidthScr = 131;
		int noteWidthA = 80;
		int noteWidthB = 59;
		
		// enable key
		isKeyEnabled = new boolean[] {false,
				true, true, true, true, true, true, true, true,
				false, false, false, false, false, false, false, false};
		
		lainWidth = noteWidthScr + noteWidthA*4 + noteWidthB*3;
		leftPos = (int)(800-lainWidth)/2;
		lainBottom = 100;
		lainDest = new int[] { leftPos, lainBottom, lainWidth, 480-lainBottom };
		lainAlpha = 0.8f;
		lainPrsBaseHeight = 100;
		drawLain = new boolean[] {true, false, false, false, false, false};
		
		noteWidth = new int[] {0,
				noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, noteWidthScr,
				0, 0, 0, 0, 0, 0, 0, 0};
		
		noteX = new int[17];
		noteX[8] = leftPos;
		noteX[1] = noteX[8] + noteWidthScr;
		for (int i=2; i<=7; i++) {
			noteX[i] = noteX[i-1] + ((i%2==0)?noteWidthA:noteWidthB);
		}
		
		BGADest = new int[] {0, 0, 800, 480};
		GuageDest = new int[] {740, 100, 30, 320};
		JudgeDest = new int[] {lainWidth/2+leftPos, 240};
		JudgeScale = 0.5f;
	}
	
	public static void set8KPCMode() {
		// 7K PC
		int noteWidthScr = 56;
		int noteWidthA = 34;
		int noteWidthB = 26;
		
		isKeyEnabled = new boolean[] {false,
				true, true, true, true, true, true, true, true,
				false, false, false, false, false, false, false, false};
		
		leftPos = 100;
		
		lainWidth = noteWidthScr + noteWidthA*4 + noteWidthB*3;
		lainBottom = 100;
		lainDest = new int[] { leftPos, lainBottom, lainWidth, 480-lainBottom };
		lainAlpha = 1.0f;
		lainPrsBaseHeight = 70;
		drawLain = new boolean[] {true, false, false, false, false, false};
		
		noteWidth = new int[] {0,
				noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, noteWidthScr,
				0, 0, 0, 0, 0, 0, 0, 0};
		noteX = new int[17];
		noteX[8] = leftPos;
		noteX[1] = noteX[8] + noteWidthScr;
		for (int i=2; i<=7; i++) {
			noteX[i] = noteX[i-1] + ((i%2==0)?noteWidthA:noteWidthB);
		}
		
		BGADest = new int[] {420, 100, 320, 320};
		GuageDest = new int[] {380, 100, 25, 320};
		JudgeDest = new int[] {lainWidth/2+leftPos, 240};
		JudgeScale = 0.2f;
	}
	
	public static void set16KMode() {
		// 14K
		int noteWidthScr = 56;
		int noteWidthA = 34;
		int noteWidthB = 26;
		lainMargin = 50;
		
		// enable key
		isKeyEnabled = new boolean[] {false,
				true, true, true, true, true, true, true, true,
				true, true, true, true, true, true, true, true};
		
		lainWidth = noteWidthScr + noteWidthA*4 + noteWidthB*3;
		leftPos = 100;
		lainBottom = 100;
		lainDest = new int[] { leftPos, lainBottom, lainWidth, 480-lainBottom };
		lainAlpha = 1.0f;
		lainPrsBaseHeight = 70;
		drawLain = new boolean[] {true, true, false, false, false, false};
		
		noteWidth = new int[] {0,
				noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, noteWidthScr,
				noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, noteWidthB, noteWidthA, noteWidthScr};
		
		noteX = new int[17];
		noteX[8] = leftPos;
		noteX[1] = noteX[8] + noteWidthScr;
		for (int i=2; i<=7; i++) {
			noteX[i] = noteX[i-1] + ((i%2==0)?noteWidthA:noteWidthB);
		}
		noteX[9] = leftPos + lainWidth + lainMargin;
		for (int i=10; i<=16; i++) {
			noteX[i] = noteX[i-1] + ((i%2==0)?noteWidthA:noteWidthB);
		}
		
		BGADest = new int[] {0, 0, 0, 0};
		GuageDest = new int[] {760, 100, 20, 320};
		JudgeDest = new int[] {lainWidth/2+leftPos, 240};
		JudgeScale = 0.2f;
	}
	
	public static void setKeyMode() {
		switch (Settings.key) {
		case 5:
			set5KMode();
			break;
		case 6:
			set6KMode();
			break;
		case 7:
			set7KMode();
			break;
		case 8:
			set8KMode();
			break;
		case 9:
			set8KPCMode();
			break;
		case 16:
			set16KMode();
			break;
		}
	}
	
	public static int getLainWidth() {
		return lainDest[2];
	}
	
	public static int getLainHeight() {
		return lainDest[3];
	}
	
	public static int getLainBottom() {
		return lainDest[1];
	}
}
