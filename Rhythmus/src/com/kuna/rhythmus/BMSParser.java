package com.kuna.rhythmus;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import sun.text.resources.CollationData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class BMSParser {
	public static int BMS_LOCALE_NONE = 0;
	public static int BMS_LOCALE_JP = 1;
	public static int BMS_LOCALE_KR = 2;
	
	public String hash;
	public int locale;
	public int player;
	public String path;
	public String dir;
	public String title;
	public String subtitle;
	public String genre;
	public String artist;
	public int BPM;
	public int playlevel;
	public int difficulty;
	public int rank;
	public int total;
	public int volwav;
	public String stagefile;
	public String[] str_wav = new String[1322];
	public String[] str_bg = new String[1322];
	public double[] str_bpm = new double[1322];
	public double[] str_stop = new double[1322];
	public double[] length_beat = new double[65536];		// MAXIMUM_BEAT
	public List<BMSKeyData> bmsdata = new ArrayList<BMSKeyData>();	// MAXIMUM_OBJECT (Trans object+hit object+STOP+BPM)
	public List<BMSKeyData> bgadata = new ArrayList<BMSKeyData>();	// BGA
	public List<BMSKeyData> bgmdata = new ArrayList<BMSKeyData>();	// BGM
	public int notecnt;
	public double time;
	
	private static int BMS_PARSER_HEADER = 1;
	private static int BMS_PARSER_MAINDATA = 2;
	private static int BMS_PARSER_BGA = 3;
	
	public boolean readHeaderOnly = false;
	
	private int BMSParseMode;
	
	public boolean LoadBMSFile(String path) {
		Gdx.app.log("BMSFile", String.format( (readHeaderOnly?"Loading BMS File: %s (Only Header)":"Loading BMS File: %s"), path) );
		this.path = path;
		this.dir = path.substring(0, path.length() - Gdx.files.absolute(path).name().length());
		FileHandle handle = Gdx.files.external(path);
		
		// check locale
		locale = BMS_LOCALE_JP;
		String data = handle.readString("SHIFT_JIS");
		byte[] b = data.getBytes();
		for (int i=0; i< ((data.length()>1000)?1000:data.length()) ; i++) {
			if (b[i] >= 44032 && b[i] <= 55203) {
				locale = BMS_LOCALE_KR;
			}
		}
		if (locale == BMS_LOCALE_KR) {
			data = handle.readString("CP949");
		}
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] by = data.getBytes();
			hash = new BigInteger(1, md.digest( data.getBytes() )).toString(16);
		} catch (NoSuchAlgorithmException e) {
			Gdx.app.error("BMSParser", "Hashing error");
			e.printStackTrace();
		}
		return ParseBMSData(data);
	}
	
	public boolean ParseBMSData(String data) {
		// init
		for (int i=0; i<length_beat.length; i++)
			length_beat[i] = 1;
		notecnt = 0;
		total = 0;
		rank = 3;	// EASY is default;
		title = "";
		subtitle = "";
		genre = "";
		artist = "";
		stagefile = "";
		bmsdata.clear();
		bgmdata.clear();
		bgadata.clear();
		
		String[] lines = data.split("\r\n");
		
		for (int i=0; i<lines.length; i++) {
			ProcessBMSLine(lines[i].trim());
		}
		
		// sort data
		if (!readHeaderOnly) {
			Collections.sort(bmsdata);
			Collections.sort(bgadata);
			Collections.sort(bgmdata);
		}
		
		// when difficulty is not setted,
		// process automatic difficulty set
		if (difficulty == 0) {
			// basically it is 5
			difficulty = 5;
			
			String _title = title.toUpperCase();
			String _path = path.toUpperCase();
			if (_title.indexOf("BEGINNER")>0 || _path.indexOf("BEGINNER")>0 ||
					_title.indexOf("LIGHT")>0 || _path.indexOf("LIGHT")>0 ||
					_title.indexOf("EASY")>0 || _path.indexOf("EASY")>0) {
				difficulty = 1;
			}
			if (_title.indexOf("NORMAL")>0 || _path.indexOf("NORMAL")>0 ||
					_title.indexOf("STANDARD")>0 || _path.indexOf("STANDARD")>0) {
				difficulty = 2;
			}
			if (_title.indexOf("HARD")>0 || _path.indexOf("HARD")>0 ||
					_title.indexOf("HYPER")>0 || _path.indexOf("HYPER")>0) {
				difficulty = 3;
			}
			if (_title.indexOf("ANOTHER")>0 || _path.indexOf("ANOTHER")>0 ||
					_title.indexOf("EX")>0 || _path.indexOf("EX")>0) {
				difficulty = 4;
			}
			if (_title.indexOf("BLACK")>0 || _path.indexOf("BLACK")>0 ||
					_title.indexOf("KUSO")>0 || _path.indexOf("KUSO")>0 ||
					_title.indexOf("INSANE")>0 || _path.indexOf("INSANE")>0) {
				difficulty = 5;
			}
		}
		
		// total : 160+(note)*0.16
		if (total == 0) {
			total = (int) (notecnt*0.16f + 160);
		}
		
		return true;
	}
	
	public void ProcessBMSLine(String line) {
		if (line.compareTo("*---------------------- HEADER FIELD") == 0) {
			BMSParseMode = BMS_PARSER_HEADER;
			return;
		}
		if (line.compareTo("*---------------------- MAIN DATA FIELD") == 0) {
			BMSParseMode = BMS_PARSER_MAINDATA;
			return;
		}
		if (line.compareTo("*---------------------- BGA FIELD") == 0) {
			BMSParseMode = BMS_PARSER_BGA;
			return;
		}
		
		if (BMSParseMode == BMS_PARSER_HEADER || BMSParseMode == BMS_PARSER_BGA) {
			String[] args = line.split(" ", 2);
			if (args.length > 1) {
				if (args[0].compareToIgnoreCase("#TITLE") == 0) {
					title = args[1];
					Gdx.app.log("BMSFile_Title", title);
				} else
				if (args[0].compareToIgnoreCase("#SUBTITLE") == 0) {
					subtitle = args[1];
				} else
				if (args[0].compareToIgnoreCase("#PLAYER") == 0) {
					player = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#GENRE") == 0) {
					genre = args[1];
				} else
				if (args[0].compareToIgnoreCase("#ARTIST") == 0) {
					artist = args[1];
				} else
				if (args[0].compareToIgnoreCase("#BPM") == 0) {
					BPM = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#DIFFICULTY") == 0) {
					difficulty = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#PLAYLEVEL") == 0) {
					playlevel = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#RANK") == 0) {
					rank = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#TOTAL") == 0) {
					total = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#VOLWAV") == 0) {
					volwav = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#STAGEFILE") == 0) {
					stagefile = args[1];
				} else
				if (args[0].startsWith("#BMP")) {
					int index = HexToInt(args[0].substring(4, 6));
					str_bg[index] = args[1];
				} else
				if (args[0].startsWith("#WAV")) {
					int index = HexToInt(args[0].substring(4, 6));
					str_wav[index] = args[1];
				} else
				if (args[0].startsWith("#BPM")) {
					int index = HexToInt(args[0].substring(4, 6));
					str_bpm[index] = Double.parseDouble(args[1]);
				} else
				if (args[0].startsWith("#STOP")) {
					int index = HexToInt(args[0].substring(4, 6));
					str_stop[index] = Double.parseDouble(args[1]);
				}
			}
		}
		
		if (BMSParseMode == BMS_PARSER_MAINDATA || BMSParseMode == BMS_PARSER_BGA) {
			String[] args = line.split(":", 2);
			if (args.length > 1) {
				if (!IsInteger(args[0].substring(1, 6))) return;
				int beat = Integer.parseInt(args[0].substring(1, 4));
				int channel = Integer.parseInt(args[0].substring(4, 6));
				
				if (channel == 2) {
					if (!readHeaderOnly)
						length_beat[beat] = Double.parseDouble(args[1]);
				} else {
					int ncb = args[1].length();
					for (int i=0; i<ncb/2; i++) {
						String val_str = args[1].substring(i*2, i*2+2);
						int val = HexToInt(val_str);
						if (val == 0) continue;		// ignore data 00
						
						if (channel > 10 && channel < 20) notecnt++;			// 1 Player's notecnt
						double nb = beat + (double)i/(double)ncb*2;
	
						if (!readHeaderOnly) {
							BMSKeyData nData = new BMSKeyData();
							nData.value = val;
							nData.key = channel;
							nData.beat = nb;
							
							switch (channel) {
							case 1:		// BGM
								bgmdata.add(nData);
								break;
							case 8:		// Extended BPM
								nData.value = getBPM(val);
								bmsdata.add(nData);
								break;
							case 9:		// STOP
								nData.value = getSTOP(val);
								bmsdata.add(nData);
								break;
							case 3:		// BPM
								nData.value = Integer.parseInt(val_str, 16);
								bmsdata.add(nData);
								break;
							case 7:
							case 6:
							case 4:		// BGA
								bgadata.add(nData);
								break;
							case 11:	// 1 Player data
							case 12:
							case 13:
							case 14:
							case 15:
							case 16:
							case 18:
							case 19:
								bmsdata.add(nData);
								break;
							case 31:
							case 32:
							case 33:
							case 34:
							case 35:
							case 36:
							case 38:
							case 39:
								bmsdata.add(nData);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	private boolean IsInteger(String str) {
		return Pattern.compile("-?[0-9]+").matcher(str).matches();
	}

	private double getBPM(int val) {
		return str_bpm[val];
	}
	private double getSTOP(int val) {
		return str_stop[val];
	}
	private String getBGA(int val) {
		return str_bg[val];
	}
	private String getWAV(int val) {
		return str_wav[val];
	}
	
	private int HexToInt(String hex) {
		String sample = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int r = 0;
		for (int i=0; i<hex.length(); i++) {
			r *= 36;
			for (int j=0; j<sample.length(); j++) {
				if (hex.substring(i, i+1).compareTo( sample.substring(j, j+1) )==0) {
					r += j;
					continue;
				}
			}
		}
		
		return r;
	}
	
	// MUST USE AFTER PARSING & SORTING!
	public void setTimemark() {
		double _bpm = BPM;		// BPM for parsing
		double _time = 0;		// time for parsing
		double _beat = 0;		// beat for parsing
		
		for (int i=0; i<bmsdata.size(); i++) {
			_time += (bmsdata.get(i).beat - _beat) * (1.0f / _bpm * 60 * 4);
			bmsdata.get(i).time = _time*1000;	// millisecond
			
			if (bmsdata.get(i).key == 3 || bmsdata.get(i).key == 8 )	// BPM
				_bpm = bmsdata.get(i).value;
			if (bmsdata.get(i).key == 9)
				_time += bmsdata.get(i).value;
			
			_beat = bmsdata.get(i).beat;
		}
		
		time = _time;
	}
	
	public double getBeatFromTime(int millisec) {
		double bpm = BPM;
		double beat = 0;
		int time = 0;
		
		for (int i=0; i<bmsdata.size(); i++) {
			if (bmsdata.get(i).key == 9) {	// STOP
				time += bmsdata.get(i).value * 1000;
				if (time >= millisec)
					return beat;
				beat = bmsdata.get(i).beat;
				continue;
			}
			
			if (bmsdata.get(i).key == 3 || bmsdata.get(i).key == 8) {	// BPM
				int newtime = time + (int) ((bmsdata.get(i).beat-beat) * (1.0f/bpm*60*4) * 1000);	// millisec
				if (newtime >= millisec) {
					return beat + (double)(millisec-time)*((double)bpm/60000/4.0f);
				}
				
				beat = bmsdata.get(i).beat;
				bpm = bmsdata.get(i).value;
				time = newtime;
			}
		}
		
		// get beat from last beat
		beat += (millisec-time)*((double)bpm/60000/4.0f);
		
		// cannot be larger then last beat
		double maxbeat = bmsdata.get(bmsdata.size()-1).beat;
		if (beat > maxbeat)
			beat = maxbeat;
		
		return beat;
	}
	
	public double getBPMFromBeat(double beat) {
		double bpm = BPM;
		for (int i=0; i<bmsdata.size(); i++) {
			if (bmsdata.get(i).beat > beat)
				break;
			if (bmsdata.get(i).key == 3 || bmsdata.get(i).key == 8)
				bpm = bmsdata.get(i).value;
		}
		return bpm;
	}
}
