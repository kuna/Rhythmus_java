package com.kuna.rhythmus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import sun.misc.IOUtils;

import com.badlogic.gdx.Gdx;

public class BMSArchive {
	public static String currentPath = "";
	public static ZipFile currentZip;
	
	public static boolean OpenArchive(String path) {
		// this method automatically calls when you use any methods.
		CloseArchive();
		
		try {
			currentZip = new ZipFile(new File(getArchiveName(path)));
			currentPath = getArchiveName(path);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static boolean isWorkingCurrentArchive(String path) {
		String[] s= path.split("[|]");
		if (s.length != 2)
			return false;
		if (s[0].compareToIgnoreCase(currentPath) != 0)
			return false;
		return true;
	}
	
	public static String getArchiveName(String path) {
		String s = path.split("[|]")[0];
		if (s.endsWith(".zip"))
			return s;
		return null;
		//return path.split("[|]")[0];
	}
	
	public static String getArchiveFileName(String path) {
		String[] s= path.split("[|]");
		if (s.length != 2)
			return null;
		return s[1];
	}
	
	public static List<String> getBMSFileList(String path) {
		if (!isWorkingCurrentArchive(path))
			OpenArchive(path);

		if (!isArchiveOpened())
			return null;
		
		List<String> paths = new ArrayList<String>();
        for (Enumeration e = currentZip.entries(); e.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) e.nextElement();

            String currentEntry = entry.getName();

            if (!entry.isDirectory() && 
            		(currentEntry.endsWith(".bme") || currentEntry.endsWith(".bms") || currentEntry.endsWith(".bml"))) {
            	paths.add(getArchiveName(path) + "|" + currentEntry);
            }
        }
        
        return paths;
	}
	
	public static InputStream getInputStream(String path) {
		if (!isWorkingCurrentArchive(path))
			OpenArchive(path);

		if (!isArchiveOpened())
			return null;
		
		String fname = getArchiveFileName(path);
		if (fname == null)
			return null;

		ZipEntry entry = currentZip.getEntry(fname);
		if (entry == null)
			return null;
        
		try {
			return currentZip.getInputStream(entry);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean UnzipFile(String path, String dest) {
		return UnzipFile(path, new File(dest));
	}
	
	public static boolean UnzipFile(String path, File f) {
		if (!isWorkingCurrentArchive(path))
			OpenArchive(path);

		if (!isArchiveOpened())
			return false;
		
		InputStream is = getInputStream(path);
		if (is == null)
			return false;
		
		byte[] data = loadBytesFromInputStream(is);
		
		try {
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			bos.write(data);
			bos.flush();
			bos.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String UnzipAllFiles(String dir) {
		// not implemented
		return null;
	}
	
	public static byte[] loadBytesFromInputStream(InputStream is) {
		try {
			byte[] bytes = IOUtils.readFully(is, -1, true);
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void CloseArchive() {
		if (currentZip != null) {
			try {
				currentZip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			currentZip = null;
		}
	}
	
	public static boolean isArchiveOpened() {
		return (currentZip != null);
	}
}
