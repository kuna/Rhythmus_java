package com.kuna.rhythmus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import sun.misc.IOUtils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.kuna.rhythmus.bmsdata.BMSData;
import com.kuna.rhythmus.bmsdata.BMSParser;
import com.kuna.rhythmus.bmsdata.BMSUtil;
import com.kuna.rhythmus.score.ScoreData;

public class BMSList {
	public ArrayList<BMSData> bmsArr = new ArrayList<BMSData>();
	public int loading = 0;
	public boolean load = false;
	public final static String _FILENAME = "BMSCache.dat";
	
	public void LoadBMSCache() {
		bmsArr.clear();
		if (Gdx.files.external(_FILENAME).exists()) {
			try {
				String d = Gdx.files.external(_FILENAME).readString();
				String l[] = d.split("\n");
				for (String _d:l) {
					if (_d.length() == 0)
						continue;
					
					String args[] = _d.split("[|][|]");
					
					BMSData bd = new BMSData();
					
					bd.path = args[0];
					bd.dir = args[0].substring(0, args[0].length() - Gdx.files.absolute(args[0]).name().length());
					bd.hash = args[1];
					bd.title = args[2];
					bd.subtitle = args[3];
					bd.genre = args[4];
					bd.artist = args[5];
					bd.difficulty = Integer.parseInt(args[6]);
					bd.BPM = Integer.parseInt(args[7]);
					bd.player = Integer.parseInt(args[8]);
					bd.rank = Integer.parseInt(args[9]);
					bd.playlevel = Integer.parseInt(args[10]);
					bd.notecnt = Integer.parseInt(args[11]);
					bd.key = Integer.parseInt(args[12]);
					
					// ONLY add when file exists
					if (Gdx.files.absolute(bd.path).exists() ||
							Gdx.files.absolute(BMSArchive.getArchiveName(bd.path)).exists())
						bmsArr.add(bd);
				}
			} catch (Exception e) {
				Gdx.app.error("ERROR", "error while reading BMS cache. maybe different format?");
			}
		}
	}
	
	public void SaveBMSCache() {
		try {
			OutputStream o = Gdx.files.external(_FILENAME).write(false);
			for (int i=0; i<bmsArr.size(); i++) {
				BMSData bd = bmsArr.get(i);
				
				String dat = String.format("%s||%s||%s||%s||%s||%s"
						+ "||%d||%d||%d||%d||%d||%d||%d\n", bd.path, bd.hash, bd.title, bd.subtitle, bd.genre, bd.artist,
						bd.difficulty, bd.BPM, bd.player, bd.rank, bd.playlevel, bd.notecnt, bd.key);
				
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
				
				// if android then add .nomedia file
				// automatically
				if (Gdx.app.getType() == ApplicationType.Android) {
					Gdx.files.external(f.path() + "/" + ".nomedia").writeString("", false);
				}
			}
		}
		
		// if cache is already exist? then dont read
		for (int i=0; i<bmsFileList.size(); i++) {
			loading = (int) (((float)(i+1))/bmsFileList.size()*100);
			String absolutePath = Gdx.files.external(bmsFileList.get(i)).file().getAbsolutePath();
			
			File f = new File(absolutePath);
			long Filesize = f.length();
		    byte[] bytes = new byte[(int) Filesize];
		    try {
		        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(new File(absolutePath)));
		        buf.read(bytes, 0, bytes.length);
		        buf.close();
		    } catch (Exception e) {
		    	// ??
		    	e.printStackTrace();
		    }
		    
			String hash = BMSUtil.GetHash(bytes);
			if ( CheckBMSExistsByHash( hash ) != null) {
				continue;
			}
			
			BMSData bd = new BMSData();
			if (BMSParser.LoadBMSFile(absolutePath, bd)) {
				bd.checkKey();
				bmsArr.add(bd);
			}
		}
		
		// new method from 140712
		LoadBMSListFromArchive(path);
		
		load = true;
		return true;
	}
	
	public boolean LoadBMSListFromArchive(String path) {
		FileHandle handle = Gdx.files.external(path);
		List<String> ArchiveList = new ArrayList<String>();
		
		for (FileHandle bmshandle: handle.list()) {
			if (bmshandle.path().endsWith(".zip")) {
				ArchiveList.add(bmshandle.file().getAbsolutePath());
			}
		}
		
		Gdx.app.log("BMSArchive", String.format("%d archives Found", ArchiveList.size()));
		loading = 0;
		
		for (int i=0; i<ArchiveList.size(); i++) {
			// if this file already exists in bmsArr
			// then don't check it.
			String archivePath = ArchiveList.get(i);
			boolean archiveExists = false;
			
			Gdx.app.log("BMSArchive", archivePath);
			for (BMSData bd: bmsArr) {
				if (BMSArchive.getArchiveName(bd.path) == null)
					continue;
				
				if (BMSArchive.getArchiveName(bd.path).compareToIgnoreCase( BMSArchive.getArchiveName(archivePath) ) == 0) {
					archiveExists = true;
					break;
				}
			}
			if (archiveExists)
				continue;
			
			List<String> bmsList = BMSArchive.getBMSFileList(archivePath);
			if (bmsList == null) {
				Gdx.app.log("ERROR", "error occured during reading zip file");
				continue;
			}
			
			// find and load BMS file in archive
			for (String bmsPath: bmsList) {
				Gdx.app.log("BMSArchive", bmsPath);
				InputStream is = BMSArchive.getInputStream(bmsPath);
				byte[] b = BMSArchive.loadBytesFromInputStream(is);
				
				BMSData bd = new BMSData();
				bd.path = bmsPath;
				bd.dir = BMSArchive.getArchiveName(bmsPath) + "|";
				if (BMSParser.LoadBMSFile(b, bd)) {
					bd.checkKey();
					bmsArr.add(bd);
				}
			}
			
			loading = (i+1)*100/ArchiveList.size();
		}
		return true;
	}
	
	private BMSData CheckBMSExistsByHash(String hash) {
		for (int i=0; i<bmsArr.size(); i++) {
			if (bmsArr.get(i).hash.compareToIgnoreCase(hash) == 0)
				return bmsArr.get(i);
		}
		return null;
	}
}