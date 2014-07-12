package com.kuna.rhythmus.bmsdata;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * *** CAUTION ***
 * #BGA information won't be applied.
 * programmed condition(#IF~#ENDIF) won't save.
 * wait for next version of BMSWriter / BMSParser!
 * TODO save preprocessCommand
 */

public class BMSWriter {
	private static ArrayList<BMSKeyData> datas;
	private static ArrayList<Double> BPMs;
	private static ArrayList<Double> STOPs;
	private static BMSData bd;
	
	public static boolean SaveBMSFile(String path, BMSData _bd) {
		// init
		BMSWriter.bd = _bd;
		String data = "";
		BPMs = new ArrayList<Double>();
		STOPs = new ArrayList<Double>();
		List<BMSKeyData> tmp;
		int beat = 1;
		int i= 0;

		// mix & copy keydata and sort
		// copy them all
		BMSUtil.Log("BMSWriter", "writing started ... " + path);
		BMSUtil.Log("BMSWriter", "copying objects");
		datas = new ArrayList<BMSKeyData>();
		datas.addAll(BMSUtil.cloneKeyArray(bd.bmsdata));
		datas.addAll(BMSUtil.cloneKeyArray(bd.bgmdata));
		datas.addAll(BMSUtil.cloneKeyArray(bd.bgadata));
		Collections.sort(datas);

		// scan & convert BPM / STOP
		BMSUtil.Log("BMSWriter", "scanning BPM/STOP information...");
		tmp = BMSUtil.ExtractChannel(datas, 3);	// BPM
		for (BMSKeyData bkd: tmp) {
			bkd.value = getArrayIndex(BPMs, bkd.value);
		}
		tmp = BMSUtil.ExtractChannel(datas, 9);	// STOP
		for (BMSKeyData bkd: tmp) {
			if (bkd.getNumerator() == 0) continue;
			bkd.value = getArrayIndex(STOPs, bkd.value);
		}
		// scan & convert #LNOBJs -> (already did)
		
		// add metadata
		// ignores: #LNOBJ
		BMSUtil.Log("BMSWriter", "adding metadatas");
		data += "*---------------------- HEADER FIELD\n";
		data += MetaData("#TITLE", bd.title);
		data += MetaData("#SUBTITLE", bd.subtitle);
		data += MetaData("#PLAYER", bd.player);
		data += MetaData("#GENRE", bd.genre);
		data += MetaData("#ARTIST", bd.artist);
		data += MetaData("#BPM", bd.BPM);
		data += MetaData("#DIFFICULTY", bd.difficulty);
		data += MetaData("#PLAYLEVEL", bd.playlevel);
		data += MetaData("#RANK", bd.rank);
		data += MetaData("#TOTAL", bd.total);
		data += MetaData("#VOLWAV", bd.volwav);
		data += MetaData("#STAGEFILE", bd.title);
		data += MetaData("#LNTYPE", "1");
		// process STP - channel 9 with NO NUMERATOR
		tmp = BMSUtil.ExtractChannel(datas, 9);
		for (BMSKeyData bkd: tmp) {
			if (bkd.getNumerator() == 0) {
				data += MetaData("#STP"+String.format("%03.03f", bkd.getBeat()), (int)(bkd.value*1000));
			}
		}
		// #BMP
		for (i=0; i<bd.str_bg.length; i++) {
			if (bd.str_bg[i] != null) {
				data += MetaData("#BMP"+BMSUtil.IntToExtHex(i), bd.str_bg[i]);
			}
		}
		// #WAV
		for (i=0; i<bd.str_wav.length; i++) {
			if (bd.str_wav[i] != null) {
				data += MetaData("#WAV"+BMSUtil.IntToExtHex(i), bd.str_wav[i]);
			}
		}
		// #BPM
		i = 0;
		for (double v: BPMs) {
			data += MetaData("#BPM"+BMSUtil.IntToExtHex(i), v);
			i++;
		}
		// #STOP
		i = 0;
		for (double v: STOPs) {
			data += MetaData("#STOP"+BMSUtil.IntToExtHex(i), v);
			i++;
		}
		
		
		// add keydata (need to sort first by beat -> key)
		BMSUtil.Log("BMSWriter", "adding keydatas");
		BMSUtil.Log("BMSWriter", Integer.toString(datas.size()));
		data += "*---------------------- MAIN DATA FIELD\n";
		tmp.clear();
		for (i=0 ; i<datas.size(); i++) {
			while (datas.get(i).getBeat() >= beat) {
				// midi length first
				if (bd.getBeatLength(beat-1) != 1)
					data += String.format("#%03d02:", beat-1) + Double.toString(bd.getBeatLength(beat-1)) + "\r\n";
				data += ProcessChannels(tmp) + "\r\n";
				beat ++;
				tmp.clear();
			}
			
			tmp.add(datas.get(i));
		}
		
		// save string to file
		// TODO check charset ... ?? UTF-8?
		try {
			FileOutputStream outputStream = new FileOutputStream(path, false);
		    outputStream.write(data.getBytes());
		    outputStream.flush();
		    outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			// release all resources
			datas.clear();
			return false;
		}
		
		// release all resources
		datas.clear();

		BMSUtil.Log("BMSWriter", "successfully finished.");
		return true;
	}
	
	private static int getArrayIndex(ArrayList<Double> arr, double val) {
		for (int i=0; i<arr.size(); i++) {
			if (arr.get(i) == val) {
				return i;
			}
		}
		arr.add(val);
		return arr.size()-1;
	}
	
	private static String MetaData(String name, int val) {
		return MetaData(name, Integer.toString(val));
	}
	
	private static String MetaData(String name, double val) {
		return MetaData(name, Double.toString(val));
	}
	
	private static String MetaData(String name, String val) {
		if (val != null && !val.equals("")) {
			return name + " " + val + "\r\n";
		}
		return "";
	}
	
	// MUST IN SAME BEAT!
	private static String ProcessChannels(List<BMSKeyData> datas) {
		if (datas.size() <= 0)
			return "";
		
		String r = "";
		BMSKeyData bkd_sample = datas.get(0);
		
		// BGMs FIRST
		List<BMSKeyData> tmp;
		tmp = BMSUtil.ExtractChannel(datas, 1);
		int lcnt = 1;
		for (int i=1; i<=32; i++) {
			List<BMSKeyData> t;
			t = BMSUtil.ExtractLayer(tmp, i);
			if (t.size() > 0) {
				while (lcnt < i) {
					// fill empty layer
					r += String.format("#%03d01:00\n", (int)bkd_sample.beat);
					lcnt++;
				}
				
				String s = GetBeatString(t);
				r += s + "\r\n";
				lcnt++;
			}
		}
		
		// other layers (STOP / BPM must need to check whether ....)
		for (int i=2; i<=120; i++) {
			tmp = BMSUtil.ExtractChannel(datas, i);
			String s = GetBeatString(tmp);
			if (s != null)
				r += s + "\r\n";
		}
		
		return r;
	}
	
	
	// MUST IN SAME CHANNEL, SAME BEAT!
	private static String GetBeatString(List<BMSKeyData> datas) {
		if (datas == null || datas.size() <= 0)
			return null;
		
		String ret = "";
		BMSKeyData bkd_sample = datas.get(0);
		
		// get common GCD first
		int denominator = 192 * bd.getBeatNumerator((int) bkd_sample.beat) / bd.getBeatDenominator((int) bkd_sample.beat);
		int gcd = denominator;
		for (BMSKeyData bkd: datas) {
			if (bkd.getNumerator() == 0)
				continue;	// we think if numerator == 0, then pass (because of #STP)
			gcd = getGCD(gcd, bkd.getNumerator());
		}
		
		// fill ret first
		for (int i=0; i<denominator/gcd; i++)
			ret += "00";
		
		// get Beat string from BMSData
		for (BMSKeyData bkd: datas) {
			int p = bkd.getNumerator() / gcd;
			p *= 2;
			ret = ret.substring(0, p) + BMSUtil.IntToExtHex((int)bkd.value) + ret.substring(p+2);
		}
		
		// return beat string
		if (ret.equals(""))
			return "";
		ret = String.format("#%03d%02X:", (int)bkd_sample.beat, bkd_sample.getChannel()) + ret;
		return ret;
	}
	
	private static int getGCD(int a, int b) {
		if (b == 0)
			return a;
		if (b>a)
			getGCD(b, a);
		return getGCD(b, a % b);
	}
}
