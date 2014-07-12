package com.kuna.rhythmus.bmsdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * TODO - REMOVE STOP and BPM (add to parser)
 * TODO - parse & store #BGA information.
 * TODO - store #IF~#ENDIF information successfully
 */

public class BMSData {
	public int player = 1;	// default is SP
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
	public boolean[] LNObj = new boolean[1322];
	
	public int key;										// custom; 5-7-10-14

	public int[] beat_numerator = new int[1600];		// MAXIMUM_BEAT
	public int[] beat_denominator = new int[1600];		// MAXIMUM_BEAT
	
	public List<BMSKeyData> bmsdata = new ArrayList<BMSKeyData>();	// MAXIMUM_OBJECT (Trans object+hit object+STOP+BPM)
	public List<BMSKeyData> bgadata = new ArrayList<BMSKeyData>();	// BGA
	public List<BMSKeyData> bgmdata = new ArrayList<BMSKeyData>();	// BGM
	public int notecnt;
	public double time;
	
	// bms file specific data
	public String hash;
	public String path;
	public String dir;
	public String preprocessCommand;
	
		
	// We dont store LNType
	// we always save LNTYPE 1 (with LNOBJ)

	public double getBeatFromTime(int millisec) {
		double bpm = BPM;
		double beat = 0;
		
		// for more precision set vals as Double
		double time = 0;
		double newtime = 0;
		
		for (int i=0; i<bmsdata.size(); i++) {
			BMSKeyData d = bmsdata.get(i);
			
			// Beat is effected by midi length ... check midi length
			while (d.beat > (int)beat+1) {
				newtime = time + ((int)beat+1-beat) * (1.0f/bpm*60*4) * 1000 * getBeatLength((int) beat);	// millisec
				if (newtime >= millisec) {
					return beat + (millisec-time)*(bpm/60000/4.0f)/getBeatLength((int) beat);
				}
				
				time = newtime;
				beat = (int)beat+1;
			}
			
			if (d.isSTOPChannel()) {	// STOP
				time += d.value * 1000;
				if (time >= millisec)
					return beat;
				continue;
			}
			
			if (d.isBPMChannel() || d.isBPMExtChannel()) {	// BPM
				newtime = time + (d.beat-beat) * (1.0f/bpm*60*4) * 1000 * getBeatLength((int) beat);	// millisec
				if (newtime >= millisec) {
					return beat + (millisec-time)*(bpm/60000/4.0f)/getBeatLength((int) beat);
				}
				
				beat = d.beat;
				bpm = d.value;
				time = newtime;
			}
		}
		
		// get beat from last beat
		beat += (millisec-time)*((double)bpm/60000/4.0f);
		
		// cannot be larger then last beat
		//double maxbeat = bmsdata.get(bmsdata.size()-1).beat;
		//if (beat > maxbeat)
		//	beat = maxbeat;
		
		return beat;
	}
	
	public double getTimeFromBeat(List<BMSKeyData> bpmarr, double beat) {
		double bpm = BPM;
		int nbeat = 0;
		double decimal = 0;
		double time = 0;
		
		for (BMSKeyData b: bpmarr) {
			while (nbeat < (int)b.getBeat() && nbeat < (int)beat) {
				time += (1.0 / bpm * 60) * getBeatLength(nbeat) * (1-decimal);
				decimal = 0;
				nbeat++;
			}
			
			// new BPM applies first
			if (nbeat == (int)b.getBeat()) {
				decimal = b.getBeat()%1;
				time += (1.0 / bpm * 60) * getBeatLength(nbeat) * decimal;
				bpm  = b.getValue();
			}
			
			if (nbeat == (int)beat)
				break;
		}
		
		// calculate left one
		while (nbeat < (int)beat) {
			time += (1.0 / bpm * 60) * getBeatLength(nbeat) * (1-decimal);
			decimal = 0;
			nbeat++;
		}

		time += (1.0 / bpm * 60) * getBeatLength(nbeat) * (beat%1);
		
		return time*4;
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
	
	public double getBPM(int val) {
		return str_bpm[val];
	}
	public double getSTOP(int val) {
		return str_stop[val];
	}
	public String getBGA(int val) {
		if (str_bg[val] == null)
			return "";
		return str_bg[val];
	}
	public String getWAV(int val) {
		if (str_wav[val] == null)
			return "";
		return str_wav[val];
	}
	public void setBGA(int val, String s) {
		str_bg[val] = s;
	}
	public void setWAV(int val, String s) {
		str_wav[val] = s;
	}

	public int getBeatNumerator(int beat) {
		if (beat_denominator[beat] == 0)
			return 4;
		else
			return beat_numerator[beat];
	}
	public int getBeatDenominator(int beat) {
		if (beat_denominator[beat] == 0)
			return 4;
		else
			return beat_denominator[beat];
	}

	public void setNumeratorFit(BMSKeyData bkd, int fit) {
		if (fit == 0)
			return;
		int divnum = 192 / fit;
		bkd.numerator = bkd.numerator - (bkd.numerator % divnum);
		bkd.beat = (int)(bkd.beat) + (double)bkd.numerator / (192 * getBeatNumerator((int) (bkd.beat)) / getBeatDenominator((int) (bkd.beat))); 
	}
	
	public double getBeatLength(int beat) {
		if (beat_denominator[beat] == 0)
			return 1;	// default
		return (double)beat_numerator[beat] / beat_denominator[beat];
	}
	
	public double getNotePosition(int beatHeight, int beat, int numerator) {
		int beatNum = 0;
		double r = 0;
		while (beatNum < beat) {
			// calculate new sbeatNum
			r += beatHeight * getBeatLength(beatNum);
			beatNum++;
		}
		
		r += beatHeight * getBeatLength(beatNum)
				* numerator / (192 * getBeatNumerator(beatNum) / getBeatDenominator(beatNum));
		return r;
	}
	
	public double getNotePosition(int beatHeight, int beat, double decimal) {
		int beatNum = 0;
		double r = 0;
		while (beatNum < beat) {
			// calculate new sbeatNum
			r += beatHeight * getBeatLength(beatNum);
			beatNum++;
		}
		
		r += beatHeight * getBeatLength(beatNum)
				* decimal;
		return r;
	}
	
	public static final double GENERAL_BPM = 130.0;
	public double getNotePositionWithBPM(int beatHeight, List<BMSKeyData> bpmarr, double b) {
		// may need lots of calculation
		int beat = (int)b;
		double decimal = b%1;
		double nbpm = BPM;
		
		int beatNum = 0;
		double beatDecimal = 0;
		double r = 0;
		for (BMSKeyData bpm: bpmarr) {
			while (beatNum < (int)bpm.getBeat() && beatNum < beat) {	// TODO remove (int) to rollback
				r += beatHeight * getBeatLength(beatNum) * (1-beatDecimal) * nbpm / GENERAL_BPM;
				beatNum++;
				beatDecimal = 0;
			}
			
			// new BPM applies first (in case of xx.0)
			if (beatNum == (int)bpm.getBeat()) {
				r += beatHeight * getBeatLength(beatNum) * (bpm.getBeat()%1 - beatDecimal) * nbpm / GENERAL_BPM;
				beatDecimal = bpm.getBeat() % 1;
				nbpm = bpm.getValue();
			}
			
			if (beatNum == beat)
				break;
		}
		
		// calculate left beat
		while (beatNum < beat) {
			r += beatHeight * getBeatLength(beatNum) * (1-beatDecimal) * nbpm / GENERAL_BPM;
			beatNum++;
			beatDecimal = 0;
		}
		r += beatHeight * getBeatLength(beatNum) * nbpm / GENERAL_BPM * decimal;
		return r;
	}
	
	public void fillNotePosition(List<BMSKeyData> arr, int beatHeight, boolean considerBPM) {
		if (!considerBPM) {
			for (BMSKeyData bkd: arr) {
				bkd.setPosY( getNotePosition(beatHeight, (int)bkd.getBeat(), bkd.getBeat()%1) );
			}
		} else {
			List<BMSKeyData> bpmarr = BMSUtil.ExtractChannel(bmsdata, 3);	// BPM channel
			for (BMSKeyData bkd: arr) {
				bkd.setPosY( getNotePositionWithBPM(beatHeight, bpmarr, bkd.getBeat()) );
			}
		}
	}
	
	public BMSKeyData getBeatFromPosition(int beatHeight, int sy) {
		BMSKeyData bk = new BMSKeyData();
		
		int beatNum = 0;
		int y = 0, by = 0;
		while (y < sy) {
			// calculate new sbeatNum
			by = y;
			y += (int) (beatHeight * getBeatLength(beatNum));
			beatNum++;
		}
		
		y = by;
		beatNum--;
		
		double beat = beatNum;
		beat += (double)(sy-y) / beatHeight / getBeatLength(beatNum);
		
		bk.beat = beat;
		bk.numerator = (int)(
				(beat % 1) * 192 / getBeatDenominator(beatNum) * getBeatNumerator(beatNum)
				);
		
		return bk;
	}
	
	public BMSKeyData getPairLN(BMSKeyData lnData) {
		// get another LN pair
		BMSKeyData LNPair = null;
		boolean returnAtNext = false;
		
		for (BMSKeyData bkd: bmsdata) {
			if (bkd.getChannel() != lnData.getChannel())
				continue;
			
			if (returnAtNext)
				return bkd;
			
			if (bkd == lnData) {
				if (LNPair != null)
					return LNPair;
				else
					returnAtNext = true;
			}
			
			if (LNPair != null)
				LNPair = null;
			else
				LNPair = bkd;
		}
		
		return null;	// No matching LN pair found.
	}
	
	public boolean isNoteAlreadyExists(int beat, int numerator, int channel, int layer) {
		return (getNote(beat, numerator, channel, layer) != null);
	}
	
	public BMSKeyData getNote(int beat, int numerator, int channel, int layer) {
		if (channel == 1 /*BGM*/) {
			for (BMSKeyData bkd: bgmdata) {
				if ((int)bkd.beat == beat && bkd.numerator == numerator && bkd.getLayerNum() == layer)
					return bkd;
			}
			
			return null;
		} else if (channel == 4 || channel == 6 || channel == 7) {
			for (BMSKeyData bkd: bgadata) {
				if ((int)bkd.beat == beat && bkd.numerator == numerator && bkd.getChannel() == channel)
					return bkd;
			}
			
			return null;
		} else {
			for (BMSKeyData bkd: bmsdata) {
				if ((int)bkd.beat == beat && bkd.numerator == numerator && bkd.getChannel() == channel)
					return bkd;
			}
			
			return null;
		}
	}
	
	public boolean removeNote(int beat, int numerator, int channel, int layer) {
		if (channel == 1 /*BGM*/) {
			for (int i=0; i<bgmdata.size(); i++) {
				BMSKeyData bkd = bgmdata.get(i);
				if ((int)bkd.beat == beat && bkd.numerator == numerator && bkd.getLayerNum() == layer) {
					bgmdata.remove(i);
					return true;
				}
			}
			return false;
		} else if (channel == 4 || channel == 6 || channel == 7) {
			for (int i=0; i<bgadata.size(); i++) {
				BMSKeyData bkd = bgadata.get(i);
				if ((int)bkd.beat == beat && bkd.numerator == numerator && bkd.getChannel() == channel) {
					bgadata.remove(i);
					return true;
				}
			}
			return false;
		} else {
			for (int i=0; i<bmsdata.size(); i++) {
				BMSKeyData bkd = bmsdata.get(i);
				if ((int)bkd.beat == beat && bkd.numerator == numerator && bkd.getChannel() == channel) {
					bmsdata.remove(i);
					return true;
				}
			}
			return false;
		}
	}
	
	public boolean removeNote(BMSKeyData bkd) {
		for (int i=0; i<bmsdata.size(); i++) {
			if (bmsdata.get(i) == bkd) {
				bmsdata.remove(i);
				return true;
			}
		}
		for (int i=0; i<bgadata.size(); i++) {
			if (bgadata.get(i) == bkd) {
				bgadata.remove(i);
				return true;
			}
		}
		for (int i=0; i<bgmdata.size(); i++) {
			if (bgmdata.get(i) == bkd) {
				bgmdata.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public void removeChannel(int[] channels) {
		/* in real, it doesn't removed but inserted into BGM channel! */
		for (int i=0; i<bmsdata.size(); i++) {
			int nc = bmsdata.get(i).getChannel();
			boolean b = false;
			for (int c: channels) {
				if (nc == c) {
					b = true;
					break;
				}
			}
			if (b) {
				BMSKeyData bkd = bmsdata.remove(i);
				bkd.setChannel(1);	//BGM
				bgmdata.add(bkd);
				i--;
				notecnt--;
			}
		}
		Collections.sort(bgmdata);
	}
	
	public boolean is5Key() {
		// check channel
		for (BMSKeyData bkd: bmsdata) {
			if (bkd.getKeyNum() == 6 || bkd.getKeyNum() == 7) {
				if (bkd.is1PChannel() || bkd.is1PLNChannel())
					return false;
			}
		}
		
		return true;
	}
	
	public boolean is10Key() {
		// check channel
		for (BMSKeyData bkd: bmsdata) {
			if (bkd.getKeyNum() == 6 || bkd.getKeyNum() == 7) {
				return false;
			}
		}
		
		return true;
	}
	
	public int checkKey() {
		if (player == 1) {
			if (is5Key())
				key = 5;
			else
				key = 7;
		} else {
			if (is10Key())
				key = 10;
			else
				key = 14;
		}
		
		return key;
	}
	
	public int getTotal() {
		total = (int) (notecnt*0.16f + 160);
		return total;
	}
	
	public void addNote(BMSKeyData bkd) {
		bmsdata.add(bkd);
	}
	
	public void dispose() {
		bmsdata.clear();
		bgadata.clear();
		bgmdata.clear();
	}
	
	public void convertLNOBJ() {
		// for playing, this command is necessary
		// before sort this command, you must sort bmsdata array. (default status)
		// check LNOBJ command
		
		BMSKeyData lnPrevObj[] = new BMSKeyData[50];
		
		for (BMSKeyData b: bmsdata) {
			if (b.isSTOPChannel() || b.isBPMChannel() || b.isBPMExtChannel())
				continue;
			
			int o = b.getKeyNum() + (b.is1PChannel()?0:8);
			
			if (LNObj[(int)b.getValue()]) {
				if (lnPrevObj[o] == null)
					continue;	// ignores
				
				if (b.is1PChannel()) {
					b.set1PLNKey(b.getKeyNum());
					lnPrevObj[o].set1PLNKey(b.getKeyNum());
				} else if (b.is2PChannel()) {
					b.set2PLNKey(b.getKeyNum());
					lnPrevObj[o].set2PLNKey(b.getKeyNum());
				}
				lnPrevObj[o].isLNfirst = true;
				notecnt--;	// LN needs 2 key data, so 1 discount to correct note number.
				
				lnPrevObj[o] = null;
			} else {
				lnPrevObj[o] = b;
			}
		}
	}
}
