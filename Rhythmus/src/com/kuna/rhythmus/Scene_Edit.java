package com.kuna.rhythmus;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.kuna.rhythmus.data.Common;


// https://github.com/mattdesl/lwjgl-basics/wiki/Batching-Rectangles-and-Lines
public class Scene_Edit implements Scene {
	// default consts
	private static final int BEATLENGTH = 400; //px
	private static final int CHANNELSIZE = 100; //px
	private LinkedList<String[]> columnString = new LinkedList<String[]>();
	
	private float offsetX, offsetY, Zoom;
	private int keymode;
	private BMSParser bmsParser;
	private int selectBeat;
	private LinkedList<History> arrHistory = new LinkedList<History>();
	private LinkedList<NoteObj> arrCopy = new LinkedList<NoteObj>();
	private LinkedList<NoteObj> arrSelection = new LinkedList<NoteObj>();
	private int gridBeat;
	
	// drawing
	private BitmapFont font;
	
	/**
	 * inner class - history
	 */
	private class History {
		private static final int WORK_NOTEBEAT = 1;
		private static final int WORK_NOTELENGTH = 2;
		private static final int WORK_NOTEKEY = 3;
		
		private 
		
		public void addStatus(int index, int type) {
			bmsParser.
		}
		
		public void applyHistory() {
			wew
		}
	}
	
	private class NoteObj {
		private static final int SEL_NORMAL = 1;
		private static final int SEL_LN = 2;
		public int objType;
		public int objIndex;
		
		public NoteObj(int objType, int objIndex) {
			this.objType = objType;
			this.objIndex = objIndex;
		}
	}
	
	private class Cursor {
		public int channel;
		public double beat;
	}
	
	public int getNowBeatIndex() {
		double nowHeight = 0;
		int beatIndex = 0;
		while (nowHeight <= offsetY) {
			nowHeight += BEATLENGTH * bmsParser.length_beat[beatIndex];
			beatIndex ++;
		}
		return beatIndex;
	}
	
	public Cursor getCursorFromPos(float x, float y) {
		double nowHeight = 0;
		int beatIndex = 0;
		while (nowHeight <= offsetY) {
			nowHeight += BEATLENGTH * bmsParser.length_beat[beatIndex];
			beatIndex ++;
		}
		nowHeight -= BEATLENGTH * bmsParser.length_beat[beatIndex-1];	// rollback
		
		// find closest beat
		double heightInBeat = y - nowHeight;
		int beatInnerIndex = (int) Math.round(heightInBeat / (BEATLENGTH / gridBeat));
		float cursorBeat = beatIndex-1 + (float)beatInnerIndex / gridBeat;
		
		// find channel
		float totalX = offsetX + x;
		int nowChannel = Math.round((totalX - CHANNELSIZE/2) / CHANNELSIZE);
		if (nowChannel<0) nowChannel = 0;
	}
	
	public void selectBetweenPos(float x1, float y1, float x2, float y2) {
		Cursor c1 = getCursorFromPos(x1, y1);
		Cursor c2 = getCursorFromPos(x2, y2);
		double startBeat = c1.beat;
		int startChannel = c1.channel;
		double endBeat = c2.beat;
		int endChannel = c2.channel;
		if (startBeat > endBeat) {
			double _bt;
			int _ct;
			_bt = startBeat;
			startBeat = endBeat;
			endBeat = _bt;
			_ct = startChannel;
			startChannel = endChannel;
			endChannel = _ct;
		}
		
		// scan all bms data
		for (int i=0; i<bmsParser.bmsdata.size(); i++) {
			
		}
	}
	
	public void add2Select(int index) {
		// not duplicated
		if (!arrSelection.contains(index))
			arrSelection.add(index);
	}
	
	public void copy() {
		// if no selection? then select all notes from nowBeat
		if (arrSelection.size() == 0) {
			int i = 0;
			for (BMSKeyData keyData: bmsParser.bmsdata) {
				if ((int)keyData.beat == selectBeat)
					arrSelection.add(new NoteObj(NoteObj.SEL_NORMAL, i));
				else if (keyData.beat > selectBeat)
					break;
				i++;
			}
		}
		
		// copy all note in selection
		arrCopy = (LinkedList<NoteObj>) arrSelection.clone();
	}
	
	public void paste() {
		// paste it
		// when beat duplicated? then change key value
		for (NoteObj n: arrCopy) {
			switch(n.objType) {
			case NoteObj.SEL_NORMAL:
				BMSKeyData keyData = bmsParser.bmsdata.get(n.objIndex);
				keyData.beat = selectBeat + (keyData.beat - (int)keyData.beat);
				int i = 0, findbeat = 0;;
				for (BMSKeyData _keyData: bmsParser.bmsdata) {
					if (_keyData.beat == keyData.beat && _keyData.key == keyData.key) {
						_keyData.value = keyData.value;
						findbeat = 1;
						break;
					} else if (_keyData.beat < keyData.beat) {
						break;
					}
					i++;
				}
				if (findbeat == 0) {
					bmsParser.bmsdata.add(i, keyData);
				}
				break;
			case NoteObj.SEL_LN:
				break;
			}
		}
	}

	@Override
	public void init() {
		// copy values from common
		keymode = Common.keymode;
		
		// set default value
		offsetX = offsetY = 0;
		Zoom = 1;
		selectBeat = 0;
		gridBeat = 8;
		
		// init vars
		font = new BitmapFont();
		columnString.add(new String[] {"BPM", "STOP", "SC", "1", "2", "3", "4", "5", "6", "7", "BGA", "LAYER", "POOR",
				"B01", "B02", "B03", "B04", });
		
		// load & analyze bms file
		bmsParser.LoadBMSFile(Common.selectPath);
	}

	@Override
	public void draw(SpriteBatch batch) {
		double nowHeight = 0;
		int beatIndex = 0;
		while (nowHeight <= offsetY) {
			nowHeight += BEATLENGTH * bmsParser.length_beat[beatIndex];
			beatIndex ++;
		}
		nowHeight -= BEATLENGTH * bmsParser.length_beat[beatIndex-1];	// rollback
		double nowHeightSave = nowHeight;
		
		// draw column
		double drawX = offsetX;
		font.draw(batch, "BPM", (float) drawX, 0);	drawX+=CHANNELSIZE;
		font.draw(batch, "STOP", (float) drawX, 0);	drawX+=CHANNELSIZE;
		font.draw(batch, "SC", (float) drawX, 0);	drawX+=CHANNELSIZE;
		for (int i=0; i<Common.keymode; i++) {
			font.draw(batch, Integer.toString(i+1), (float) drawX, 0);
			drawX+=CHANNELSIZE;
		}
		font.draw(batch, "BGA", (float) drawX, 0);	drawX+=CHANNELSIZE;
		font.draw(batch, "LAYER", (float) drawX, 0);	drawX+=CHANNELSIZE;
		font.draw(batch, "POOR", (float) drawX, 0);	drawX+=CHANNELSIZE;
		
		// channel line - draw from bottom
		while (nowHeight < 480) {
			
		}
		
		// note line - draw from bottom
		nowHeight = nowHeightSave;
		while (nowHeight < 480) {
			bmsParser.bmsdata
		}
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
}
