package com.kuna.rhythmus;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class BMSList {
	public ArrayList<BMSParser> bmsArr = new ArrayList<BMSParser>();
	public int loading = 0;
	public boolean load = false;
	
	public boolean LoadBMSList(String path) {
		// clean
		bmsArr.clear();
		load = false;
		loading = 0;
		
		FileHandle handle = Gdx.files.external(path);
		FileHandle[] fldList = handle.list();
		Gdx.app.log("BMSList", String.format("%d Folders Found", fldList.length));
		
		
		ArrayList<String> bmsFileList = new ArrayList<String>();
		for (FileHandle f: fldList) {
			if (f.isDirectory()) {
				FileHandle[] bmshandles = f.list();
				
				for (FileHandle bmshandle: bmshandles) {
					if (bmshandle.path().endsWith(".bms") ||
							bmshandle.path().endsWith(".bme") || 
							bmshandle.path().endsWith(".bml")) {
						bmsFileList.add(bmshandle.path());
					}
				}
			}
		}
		
		for (int i=0; i<bmsFileList.size(); i++) {
			BMSParser b = new BMSParser();
			b.readHeaderOnly = true;
			b.LoadBMSFile(bmsFileList.get(i));
			bmsArr.add(b);
			loading = (int) (((float)(i+1))/bmsFileList.size()*100);
		}
		
		load = true;
		return true;
	}
}
