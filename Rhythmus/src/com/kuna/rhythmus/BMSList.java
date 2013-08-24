package com.kuna.rhythmus;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.kuna.rhythmus.score.ScoreData;

public class BMSList {
	public ArrayList<BMSParser> bmsArr = new ArrayList<BMSParser>();
	public int loading = 0;
	public boolean load = false;
	public final static String _FILENAME = "BMSCache.dat";
	
	public void LoadBMSCache() {
		bmsArr.clear();
		if (Gdx.files.external(_FILENAME).exists()) {
			String d = Gdx.files.external(_FILENAME).readString();
			String l[] = d.split("\n");
			for (String _d:l) {
				if (_d.length() == 0)
					continue;
				
				String args[] = _d.split("[|][|]");
				
				BMSParser bp = new BMSParser();
				
				bp.readHeaderOnly = true;
				bp.path = args[0];
				bp.dir = args[0].substring(0, args[0].length() - Gdx.files.absolute(args[0]).name().length());
				bp.hash = args[1];
				bp.title = args[2];
				bp.subtitle = args[3];
				bp.genre = args[4];
				bp.artist = args[5];
				bp.difficulty = Integer.parseInt(args[6]);
				bp.BPM = Integer.parseInt(args[7]);
				bp.player = Integer.parseInt(args[8]);
				bp.rank = Integer.parseInt(args[9]);
				bp.playlevel = Integer.parseInt(args[10]);
				bp.notecnt = Integer.parseInt(args[11]);
				
				bmsArr.add(bp);
			}
		}
	}
	
	public void SaveBMSCache() {
		try {
			OutputStream o = Gdx.files.external(_FILENAME).write(false);
			for (int i=0; i<bmsArr.size(); i++) {
				BMSParser bp = bmsArr.get(i);
				
				String dat = String.format("%s||%s||%s||%s||%s||%s"
						+ "||%d||%d||%d||%d||%d||%d\n", bp.path, bp.hash, bp.title, bp.subtitle, bp.genre, bp.artist,
						bp.difficulty, bp.BPM, bp.player, bp.rank, bp.playlevel, bp.notecnt);
				
				o.write(dat.getBytes());
			}
			o.flush();
			o.close();
		} catch (IOException e) {
			Gdx.app.error("ScoreManager", "Error Occured While Saving BMSCache");
		}
	}
	
	public boolean LoadBMSList(String path) {
		// clean
		bmsArr.clear();
		load = false;
		loading = 0;
		
		// load cache
		LoadBMSCache();
		
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
		
		// if cache is already exist? then dont read
		for (int i=0; i<bmsFileList.size(); i++) {
			loading = (int) (((float)(i+1))/bmsFileList.size()*100);
			
			String hash = BMSUtil.GetHash(Gdx.files.external(bmsFileList.get(i)).readString().getBytes());
			if ( CheckBMSExistsByHash( hash ) != null) {
				continue;
			}
			
			BMSParser b = new BMSParser();
			b.readHeaderOnly = true;
			b.LoadBMSFile(bmsFileList.get(i));
			
			// ONLY SINGLE PLAYER
			if (b.player != 1)
				continue;
			
			bmsArr.add(b);
		}
		
		load = true;
		return true;
	}
	
	private BMSParser CheckBMSExistsByHash(String hash) {
		for (int i=0; i<bmsArr.size(); i++) {
			if (bmsArr.get(i).hash.compareToIgnoreCase(hash) == 0)
				return bmsArr.get(i);
		}
		return null;
	}
}
