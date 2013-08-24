package com.kuna.rhythmus;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class BMSUtil {
	public static String GetHash(byte[] data) {
		MessageDigest md;
		String hash = null;
		try {
			md = MessageDigest.getInstance("MD5");
			hash = new BigInteger(1, md.digest( data )).toString(16);
		} catch (NoSuchAlgorithmException e) {
			Gdx.app.error("BMSParser", "Hashing error");
			e.printStackTrace();
		}
		return hash;
	}
	
	public static BMSKeyData getLastValidKey(BMSParser bp, int channel) {
		for (int i=0; i<bp.bmsdata.size(); i++) {
			if (bp.bmsdata.get(i).key == channel &&
					bp.bmsdata.get(i).attr == 0)
				return bp.bmsdata.get(i);
		}
		
		return null;
	}

	public static Sound PlayUntilLoad(final Sound s, final int trial) {
		new Thread(new Runnable() {
			int wtime = 0;
			
			@Override
			public void run() {
				try {
					while (wtime < trial) {
						if (s == null)
							break;
						if (s.play() >= 0)	// -1 is failure
							break;
							
						Thread.sleep(500);
						wtime += 1;
						Gdx.app.log("PlayUntilLoad", "Retry...");
					}
				} catch (InterruptedException e) {
					Gdx.app.error("Sound", "Error occured While Playing Sound");
				}
			}
		}).start();
		return s;
	}

	public static Sound LoopUntilLoad(final Sound s, final int trial) {
		new Thread(new Runnable() {
			int wtime = 0;
			
			@Override
			public void run() {
				try {
					while (wtime < trial) {
						if (s == null)
							break;
						if (s.loop() >= 0)
							break;
							
						Thread.sleep(500);
						wtime += 1;
						Gdx.app.log("LoopUntilLoad", "Retry...");
					}
				} catch (InterruptedException e) {
					Gdx.app.error("Sound", "Error occured While Playing Sound");
				}
			}
		}).start();
		return s;
	}
}
