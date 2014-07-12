package com.kuna.rhythmus;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.kuna.rhythmus.bmsdata.BMSData;
		
public class BMSResource {
	public static Sound[] wav = new Sound[1322];
	public static Texture[] bg = new Texture[1322];

	public static int progress = 0;
	public static int loadedCnt = 0;
	private static boolean isBitmapLoaded = false;
	private static boolean isSoundLoaded = false;
	public static boolean isLoaded = false;
	
	private static String tempFolder = System.getProperty("java.io.tmpdir") + "Rhythmus" + File.separator;
	
	public static void LoadData(final BMSData bd) {
		isLoaded = false;
		isBitmapLoaded = false;
		isSoundLoaded =false;
		loadedCnt = 0;

		Texture.setEnforcePotImages(false);
		
		// create temp directory
		if (!new File(tempFolder).exists()) {
			new File(tempFolder).mkdir();
		}
		
		// bitmap loading multithread
		new Thread( new Runnable() {
			@Override
			public void run() {
				for (int textureIdx=0; textureIdx<1322; textureIdx++) {
					if (bd.str_bg[textureIdx] == null || bd.str_bg[textureIdx].length() == 0) {
						loadedCnt++;
						continue;
					}
					final String path = bd.dir + bd.str_bg[textureIdx];
					final int idx = textureIdx;
					// check is file from archive
					if (bd.dir.endsWith("|")) {
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								try {
									File f = new File(tempFolder + bd.str_bg[idx]);
									if (!BMSArchive.UnzipFile(path, f)) {
										Gdx.app.log("BMSResource", String.format("Failed to unzipping %s", path));
										f.delete();
										return;
									}

									bg[idx] = new Texture(Gdx.files.absolute(f.getAbsolutePath()));
									Gdx.app.log("BMSResource", String.format("Loading bitmap %s", path));
								} catch (Exception e) {
									Gdx.app.error("BMSResource", String.format("Cannot load bitmap %s", path));
								}
								loadedCnt++;
							}
						});
					} else {
						if (Gdx.files.absolute(path).exists()) 
						{
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									try {
										bg[idx] = new Texture(Gdx.files.absolute(path));
										Gdx.app.log("BMSResource", String.format("Loading bitmap %s", path));
									} catch (Exception e) {
										Gdx.app.error("BMSResource", String.format("Cannot load bitmap %s", path));
									}
									loadedCnt++;
								}
							});
						}
					}
				}
				
				isBitmapLoaded = true;
			}
		}).start();

		new Thread( new Runnable() {
			@Override
			public void run() {
				String path;
				for (int i=0; i<1322; i++) {
					loadedCnt++;
					if (bd.str_wav[i] == null || bd.str_wav[i].length() == 0)
						continue;
					path = bd.dir + bd.str_wav[i];
					
					boolean loaded = false;
					// check is file from archive
					if (bd.dir.endsWith("|")) {
						try {
							File f = new File(tempFolder + bd.str_wav[i]);
							if (!BMSArchive.UnzipFile(path, f.getAbsolutePath())) {
								Gdx.app.log("BMSResource", String.format("Failed to unzipping %s", path));
								f.delete();
							} else {
								wav[i] = Gdx.audio.newSound(Gdx.files.absolute(f.getAbsolutePath()));
								Gdx.app.log("BMSResource", String.format("Loading Audio %s", path));
								loaded = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
							Gdx.app.error("BMSResource", String.format("Cannot load Audio %s", path));
						}
					} else {
						if (Gdx.files.absolute(path).exists())
						{
							try {
								Gdx.app.log("BMSResource", String.format("Loading Audio %s", path));
								wav[i] = Gdx.audio.newSound(Gdx.files.absolute(path));
								loaded = true;
							} catch (Exception e) {
								Gdx.app.error("BMSResource", String.format("Cannot load Audio %s", path));
							}
						}
					}

					
					if (!loaded && bd.str_wav[i].endsWith(".wav")) {
						bd.str_wav[i] = bd.str_wav[i].substring(0, bd.str_wav[i].length()-3) + "ogg";
						i--;
						continue;
					}
				}

				isSoundLoaded = true;
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!isBitmapLoaded || !isSoundLoaded) {
					try {
						Thread.sleep(100);
						progress = (int) ((double)loadedCnt/2644*100);
					} catch (InterruptedException e) {
					}
				}
				Gdx.app.log("BMSResource", "Loading Complete");
				isLoaded =true;
			}
		}).start();
	}

	public static void dispose() {
		// memory release
		for (int i=0; i<1322; i++)
			if (wav[i] != null)
				wav[i].dispose();
		for (int i=0; i<1322; i++)
			if (bg[i]!=null)
				bg[i].dispose();
		
		// release all temp files
		if (tempFolder != null) {
			Gdx.files.absolute(tempFolder).deleteDirectory();
		}
		
		isLoaded = false;
	}

	public static void playSound(int sndIndex) {
		if (wav[sndIndex]!= null) {
			wav[sndIndex].play();
		} else {
			Gdx.app.error("BMSResource", String.format("cannot play %d sound", sndIndex));
		}
	}
}
