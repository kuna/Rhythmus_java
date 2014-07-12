package com.kuna.rhythmus.bmsdata;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

import com.badlogic.gdx.Gdx;

public class BMSParser {
	public static int BMS_LOCALE_NONE = 0;
	public static int BMS_LOCALE_JP = 1;
	public static int BMS_LOCALE_KR = 2;

	private static int LNType;
	private static int[] LNprevVal = new int[120];
	private static BMSKeyData[] LNKey = new BMSKeyData[120];
	private static int[] BGALayerCount;

	private static int BMSParseMode;
	private static int BMS_PARSER_HEADER = 1;
	private static int BMS_PARSER_MAINDATA = 2;
	private static int BMS_PARSER_BGA = 3;

	private static int[] BMSKeyCount = new int[14];
	private static int randomStackCnt;
	private static int[] randomVal = new int[256];		// Maximum stack: 256
	private static int[] condition = new int[256];		// 0: read line, 1: ignore line, 2: executing command, 3: command already executed
	
	public static boolean LoadBMSFile(String path, BMSData bd) {
		BMSUtil.Log("BMSParser", "Loading BMS File ... " + path);
		File f = new File(path);
		
		bd.path = path;
		bd.dir = path.substring(0, path.length() - f.getName().length());

		//Read text from file
		long Filesize = f.length();
	    byte[] bytes = new byte[(int) Filesize];
	    try {
	        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(new File(path)));
	        buf.read(bytes, 0, bytes.length);
	        buf.close();
	    } catch (FileNotFoundException e) {
	    	BMSUtil.Log("BMSParser", "File not found");
	    	return false;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	    
	    return LoadBMSFile(bytes, bd);
	}
	
	public static boolean LoadBMSFile(byte[] bytes, BMSData bd) {
		BMSUtil.Log("BMSParser", "checking locale...");
		
		// just process data
		bd.hash = BMSUtil.GetHash(bytes);
		
		// check locale
		String locale = BMSUtil.CheckEncoding(bytes);
	    String data;
	    
	    if (locale.compareTo("ANSI") == 0) {
		    try {
		    	// attempt SHIFT_JIS first
		    	// we also can use https://github.com/hnakamur/sjis-check,
		    	// but it may be too much slow in mobile device.
				data = new String(bytes, "SHIFT_JIS");
				byte[] b = data.getBytes();
				for (int i=0; i< ((data.length()>1000)?1000:data.length()) ; i++) {
					if (b[i] >= 44032 && b[i] <= 55203) {
						// EUC-KR encoding
						data = new String(bytes, "CP949");
						break;
					}
				}
		    } catch (UnsupportedEncodingException e) {
		    	BMSUtil.Log("BMSParser", "Unsupported Encoding Exception");
		    	return false;
		    }
	    } else {
	    	try {
				data = new String(bytes, locale);
			} catch (UnsupportedEncodingException e) {
		    	BMSUtil.Log("BMSParser", "Unsupported Encoding Exception");
		    	return false;
			}
	    }
		
		
		// init before parshing
		for (int i=0; i<14; i++) {
			BMSKeyCount[i] = 0;
		}
		
		return ParseBMSData(data, bd);
	}
	
	public static boolean ParseBMSData(String data, BMSData bd) {
		// init
		BGALayerCount = new int[1322];
		
		bd.notecnt = 0;
		bd.total = 0;
		bd.rank = 3;	// EASY is default;
		bd.title = "";
		bd.subtitle = "";
		bd.genre = "";
		bd.artist = "";
		bd.stagefile = "";
		LNType = 1;	// 1 is default
		for (int i=0; i<1322; i++)
			bd.LNObj[i] = false;
		bd.bmsdata.clear();
		bd.bgmdata.clear();
		bd.bgadata.clear();

		data = data.replace("\r\n", "\n");
		String[] lines = data.split("\n");

		for (int i=0; i<lines.length; i++) {
			PreProcessBMSLine(lines[i].trim(), bd);
		}
		
		for (int i=0; i<lines.length; i++) {
			ProcessBMSLine(lines[i].trim(), bd);
		}
		
		// sort data
		Collections.sort(bd.bmsdata);
		Collections.sort(bd.bgadata);
		Collections.sort(bd.bgmdata);
		
		// when difficulty is not setted,
		// process automatic difficulty set
		if (bd.difficulty == 0) {
			// basically it is 5
			bd.difficulty = 5;
			
			String _title = bd.title.toUpperCase();
			String _path = bd.path.toUpperCase();
			if (_title.indexOf("BEGINNER")>0 || _path.indexOf("BEGINNER")>0 ||
					_title.indexOf("LIGHT")>0 || _path.indexOf("LIGHT")>0 ||
					_title.indexOf("EASY")>0 || _path.indexOf("EASY")>0) {
				bd.difficulty = 1;
			}
			if (_title.indexOf("NORMAL")>0 || _path.indexOf("NORMAL")>0 ||
					_title.indexOf("STANDARD")>0 || _path.indexOf("STANDARD")>0) {
				bd.difficulty = 2;
			}
			if (_title.indexOf("HARD")>0 || _path.indexOf("HARD")>0 ||
					_title.indexOf("HYPER")>0 || _path.indexOf("HYPER")>0) {
				bd.difficulty = 3;
			}
			if (_title.indexOf("ANOTHER")>0 || _path.indexOf("ANOTHER")>0 ||
					_title.indexOf("EX")>0 || _path.indexOf("EX")>0) {
				bd.difficulty = 4;
			}
			if (_title.indexOf("BLACK")>0 || _path.indexOf("BLACK")>0 ||
					_title.indexOf("KUSO")>0 || _path.indexOf("KUSO")>0 ||
					_title.indexOf("INSANE")>0 || _path.indexOf("INSANE")>0) {
				bd.difficulty = 5;
			}
		}
		
		// total : 160+(note)*0.16
		if (bd.total == 0) {
			bd.getTotal();
		}

		BMSUtil.Log("BMSParser", "Parse finished");
		return true;
	}
	
	public static void ExecutePreProcessor(BMSData bd) {
		// this will modify BMSData
		// TODO check this thing may executed layer
		
		String data = bd.preprocessCommand;
		if (data == null)
			return;
		for (String line: data.split("\n")) {
			// preprocessor
			if (line.toUpperCase().startsWith("#RANDOM") || line.toUpperCase().startsWith("#SETRANDOM")) {
				String args[] = line.split(" ");
				int val = Integer.parseInt(args[1]);
				randomVal[randomStackCnt++] = (int)(Math.random()*val);
				return;
			} else if (line.toUpperCase().startsWith("#IF")) {
				String args[] = line.split(" ");
				int val = Integer.parseInt(args[1]);
				if (val == randomVal[randomStackCnt-1])
					condition[randomStackCnt-1] = 2;
				else
					condition[randomStackCnt-1] = 0;
				return;
			} else if (line.toUpperCase().startsWith("#ELSEIF")) {
				if (condition[randomStackCnt-1] == 2) {
					condition[randomStackCnt-1] = 3;
					return;
				}
				
				String args[] = line.split(" ");
				int val = Integer.parseInt(args[1]);
				if (val == randomVal[randomStackCnt-1])
					condition[randomStackCnt-1] = 2;
				else
					condition[randomStackCnt-1] = 0;
				return;
			} else if (line.compareToIgnoreCase("#ENDIF") == 0) {
				condition[--randomStackCnt] = 0;
				return;
			}
			if (randomStackCnt > 0) {
				if (condition[randomStackCnt-1] == 1 || condition[randomStackCnt-1] == 3)
					return;
			}
			// preprocessor end
		}
	}
	
	private static void PreProcessBMSLine(String line, BMSData bd) {
		// in this function, Metadata & midi length will parsed
		// and preprocessor will be parsed
		
		if (line.toUpperCase().startsWith("#RANDOM")
				|| line.toUpperCase().startsWith("#SETRANDOM")
				|| line.toUpperCase().startsWith("#IF")
				|| line.toUpperCase().startsWith("#ELSEIF")
				|| line.toUpperCase().startsWith("#ENDIF")) {
			bd.preprocessCommand += line + "\n";
		} else {
			String[] args;
			args = line.split(" ", 2);
			if (args.length > 1) {
				if (args[0].compareToIgnoreCase("#TITLE") == 0) {
					bd.title = args[1];
				} else
				if (args[0].compareToIgnoreCase("#SUBTITLE") == 0) {
					bd.subtitle = args[1];
				} else
				if (args[0].compareToIgnoreCase("#PLAYER") == 0) {
					bd.player = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#GENRE") == 0) {
					bd.genre = args[1];
				} else
				if (args[0].compareToIgnoreCase("#ARTIST") == 0) {
					bd.artist = args[1];
				} else
				if (args[0].compareToIgnoreCase("#BPM") == 0) {
					bd.BPM = (int) Double.parseDouble(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#DIFFICULTY") == 0) {
					bd.difficulty = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#PLAYLEVEL") == 0) {
					bd.playlevel = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#RANK") == 0) {
					bd.rank = Integer.parseInt(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#TOTAL") == 0) {
					bd.total = (int) Double.parseDouble(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#VOLWAV") == 0) {
					bd.volwav = (int) Double.parseDouble(args[1]);
				} else
				if (args[0].compareToIgnoreCase("#STAGEFILE") == 0) {
					bd.stagefile = args[1];
				} else
				if (args[0].compareToIgnoreCase("#LNTYPE") == 0) {
					LNType = Integer.parseInt(args[1]);
				} else
				if (args[0].toUpperCase().startsWith("#STP")) {
					String[] pt = args[0].substring(4).split("[.]");
					
					BMSKeyData nData = new BMSKeyData();
					nData.value = Double.parseDouble(args[1])/1000;
					nData.key = 9;		// STOP Channel
					nData.beat = Integer.parseInt(pt[0]) + (double)Integer.parseInt(pt[1])/1000;
					bd.bmsdata.add(nData);
				} else
				if (args[0].toUpperCase().startsWith("#LNOBJ")) {
					bd.LNObj[BMSUtil.ExtHexToInt(args[1])] = true;
				} else
				if (args[0].toUpperCase().startsWith("#BMP")) {
					int index = BMSUtil.ExtHexToInt(args[0].substring(4, 6));
					bd.str_bg[index] = args[1];
				} else
				if (args[0].toUpperCase().startsWith("#WAV")) {
					int index = BMSUtil.ExtHexToInt(args[0].substring(4, 6));
					bd.str_wav[index] = args[1];
				} else
				if (args[0].toUpperCase().startsWith("#BPM")) {
					int index = BMSUtil.ExtHexToInt(args[0].substring(4, 6));
					bd.str_bpm[index] = Double.parseDouble(args[1]);
				} else
				if (args[0].toUpperCase().startsWith("#STOP")) {
					int index = BMSUtil.ExtHexToInt(args[0].substring(4, 6));
					bd.str_stop[index] = Double.parseDouble(args[1]);
				}
			}

			args = line.split(":", 2);
			if (args.length > 1) {
				if (!BMSUtil.IsInteger(args[0].substring(1, 6))) return;
				int beat = Integer.parseInt(args[0].substring(1, 4));
				int channel = BMSUtil.HexToInt(args[0].substring(4, 6));	// channel is heximedical
				
				if (channel == 2) {
					double length_beat = Double.parseDouble(args[1]);
					if (length_beat == 0) {
						BMSUtil.Log("BMSParser", "length_beat cannot be Zero, ignored.");
					}
					// TODO fix!
					if (length_beat * 4 % 1 == 0) {
						bd.beat_numerator[beat] = (int)(length_beat * 4);
						bd.beat_denominator[beat] = 4;
					} else if (length_beat * 8 % 1 == 0) {
						bd.beat_numerator[beat] = (int)(length_beat * 8);
						bd.beat_denominator[beat] = 8;
					} else if (length_beat * 16 % 1 == 0) {
						bd.beat_numerator[beat] = (int)(length_beat * 16);
						bd.beat_denominator[beat] = 16;
					} else if (length_beat * 32 % 1 == 0) {
						bd.beat_numerator[beat] = (int)(length_beat * 32);
						bd.beat_denominator[beat] = 32;
					} else if (length_beat * 64 % 1 == 0) {
						bd.beat_numerator[beat] = (int)(length_beat * 64);
						bd.beat_denominator[beat] = 64;
					}
				}
			}
		}
	}
	
	private static void ProcessBMSLine(String line, BMSData bd) {
		/*
		 * many BMS has not follow this rule,
		 * so we're going to ignore it.
		 * 
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
		*/

		// in this function, we'll only parse note data.

		String[] args;
		args = line.split(":", 2);
		if (args.length > 1) {
			if (!BMSUtil.IsInteger(args[0].substring(1, 6))) return;
			int beat = Integer.parseInt(args[0].substring(1, 4));
			int channel = BMSUtil.HexToInt(args[0].substring(4, 6));	// channel is heximedical
			
			if (channel == 2) {
				// ignore! we already did it in PreProcessBMSLine
			} else {
				if (channel == 1) {
					// BGM
					BGALayerCount[beat]++;
				}
				
				int ncb = args[1].length();
				for (int i=0; i<ncb/2; i++) {
					String val_str = args[1].substring(i*2, i*2+2);
					int val = BMSUtil.ExtHexToInt(val_str);
					if (val == 0) {
						// ignore data 00
						LNprevVal[channel] = 0;
						continue;		
					}

					double nb = beat + (double)i/(double)ncb*2;
					BMSKeyData nData = new BMSKeyData();
					nData.value = val;
					nData.key = channel;
					nData.beat = nb;
					nData.numerator = i*((192*bd.getBeatNumerator(beat)/bd.getBeatDenominator(beat))/(ncb/2));
					// beat numerator must proceed first!!
					
					if (nData.is1PChannel() || nData.is2PChannel() || nData.is1PLNChannel() || nData.is2PLNChannel())
						bd.notecnt ++;
					
					
					if (nData.isBGMChannel()) {
						// BGM
						nData.layernum = BGALayerCount[beat];
						bd.bgmdata.add(nData);
					} else if (nData.isBPMExtChannel()) {
						nData.value = bd.getBPM(val);
						nData.setBPMChannel();  // for ease
						bd.bmsdata.add(nData);
					} else if (nData.isSTOPChannel()) {
						nData.value = bd.getSTOP(val);
						nData.value = val;
						bd.bmsdata.add(nData);
					} else if (nData.isBPMChannel()) {
						nData.value = Integer.parseInt(val_str, 16);
						bd.bmsdata.add(nData);
					} else if (nData.isBGAChannel() || nData.isBGALayerChannel() || nData.isPoorChannel()) {
						// BGA
						bd.bgadata.add(nData);
					} else if (nData.is1PChannel() || nData.is2PChannel()) {
						// BMS key
						// if you need data for playing, use convertLNOBJ() in BMSData class.
						bd.bmsdata.add(nData);
					} else if (nData.is1PTransChannel() || nData.is2PTransChannel()) {
						// transparent key sound
						bd.bmsdata.add(nData);
					} else if (nData.is1PLNChannel() || nData.is2PLNChannel()) {
						// long note (LNTYPE)
						// find previous LN obj and set etime.
						// if no previous LN obj found, then insert new one.
						boolean foundObj = false;
						for (int _i=bd.bmsdata.size()-1; _i>=0 ;_i--)
						{
							if (LNType == 2 && nData.key != LNprevVal[channel])
								break;	// LNTYPE 2: create new keydata when not continuous
							
							BMSKeyData oldData = bd.bmsdata.get(_i);
							if (nData.key == oldData.key) {
								if (LNType == 1) {
									// LNTYPE 1: only uses clean one
									//oldData.ebeat = nData.ebeat;
									//oldData.evalue = nData.value;
									bd.bmsdata.add(nData);
									foundObj = true;
									break;
								} else if (LNType == 2) {
									// LNTYPE 2: able to use dirty one when continuous.
									// TODO it may not have same key. have to be fixed.
									//oldData.ebeat = nData.ebeat;
									//oldData.evalue = nData.value;
									if (LNKey[channel] != oldData)
										bd.bmsdata.remove(_i);
									bd.bmsdata.add(nData);
									foundObj = true;
									break;
								}
							}
						}
						
						if (!foundObj) {
							nData.isLNfirst = true;
							bd.bmsdata.add(nData);
							LNKey[channel] = nData;
						} else {
							bd.notecnt--;	// LN needs 2 key data, so 1 discount to correct note number.
						}
					}
					
					
					// save prev val for LNTYPE 2
					if (nData.is1PLNChannel() || nData.is2PLNChannel()) {
						LNprevVal[channel] = val;
					} else {
						LNprevVal[channel] = 0;
					}
				}
			}
		}
	}
	
	// MUST USE AFTER PARSING & SORTING!
	public static void setTimemark(BMSData bd) {
		double _bpm = bd.BPM;		// BPM for parsing
		double _time = 0;		// time for parsing
		double _beat = 0;		// beat for parsing
		
		for (int i=0; i<bd.bmsdata.size(); i++) {
			BMSKeyData d = bd.bmsdata.get(i);
			
			// check midi length
			while (d.beat >= (int)_beat+1) {
				_time += ((int)_beat+1-_beat) * (1.0f/_bpm*60*4) * bd.getBeatLength((int)_beat);
				_beat = (int)_beat+1;
			}
			
			_time += (d.beat - _beat) * (1.0f / _bpm * 60 * 4) * bd.getBeatLength((int)_beat);
			d.time = _time*1000;	// millisecond
			
			if (d.key == 3 || d.key == 8 )	// BPM
				_bpm = d.value;
			if (d.key == 9)
				_time += d.value;
			
			_beat = d.beat;
		}
		
		bd.time = _time;
		
		
		// LN note time
	}
	
	/*
	 * Useless now!
	 * 
	// this command MUST be called after sorting!
	// only proc LONGNOTE TYPE 2
	private void ProcessLongnote(BMSData bd) {
		for (int i=0; i<bd.bmsdata.size(); i++) {
			BMSKeyData d = bd.bmsdata.get(i);
			if (d.key > 50 && d.key<60) {
				if (d.attr == 1) {
					// LNTYPE 1
					for (int j=i+1; j<bd.bmsdata.size(); j++) {
						if (bd.bmsdata.get(j).key == d.key && bd.bmsdata.get(j).attr == 1) {
							bd.bmsdata.get(j).attr = 4;	// longnote end attr = 4
							bd.notecnt++;
							break;
						}
					}
					d.attr = 0;
				} else if (d.attr == 2) {
					// LNTYPE 2
					int prevIndex=-1;
					for (int j=i+1; j<bd.bmsdata.size(); j++) {
						if (bd.bmsdata.get(j).key == d.key && bd.bmsdata.get(j).attr == 2) {
							if (d.value != bd.bmsdata.get(j).key)
								break;
							if (prevIndex > 0) {
								bd.bmsdata.remove(prevIndex);
								j--;
							}
							bd.bmsdata.get(j).attr = 4;	// longnote end attr = 4
							prevIndex = j;
							bd.notecnt++;
							break;
						}
					}
					d.attr = 0;
				}
			}
		}
	}*/
}
