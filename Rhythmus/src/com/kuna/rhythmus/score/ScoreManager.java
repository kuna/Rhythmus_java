package com.kuna.rhythmus.score;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

public class ScoreManager {
	public static final String _FILENAME = "rhythmus_score.dat";
	public static List<ScoreData> scoreData = new ArrayList<ScoreData>();
	
	public void LoadScore() {
		scoreData.clear();
		if (Gdx.files.external(_FILENAME).exists()) {
			String d = Gdx.files.external(_FILENAME).readString();
			String l[] = d.split("\n");
			for (String _d:l) {
				if (_d.length() == 0)
					continue;
				
				ScoreData s = new ScoreData();
				s.readData(_d);
				scoreData.add(s);
			}
		}
	}
	
	public void SaveScore() {
		try {
			OutputStream o = Gdx.files.external(_FILENAME).write(false);
			for (int i=0; i<scoreData.size(); i++) {
				ScoreData s = scoreData.get(i);
				o.write(s.createData().getBytes());
			}
			o.flush();
			o.close();
		} catch (IOException e) {
			Gdx.app.error("ScoreManager", "Error Occured While Saving Score");
		}
	}
	
	public ScoreData getScore(String hash) {
		for (int i=0; i<scoreData.size(); i++)
		{
			if (scoreData.get(i).hash.equals(hash))
				return scoreData.get(i);
		}
		return null;
	}
	
	public void addScore(ScoreData s) {
		// update available with this method
		for (int i=0; i<scoreData.size(); i++)
		{
			if (scoreData.get(i).hash.equals(s.hash)) {
				ScoreData old = scoreData.get(i);
				if (old.clear < s.clear) old.clear = s.clear;
				if (old.combo < s.combo) old.combo = s.combo;
				if (old.getRate() < s.getRate()) {
					old.pg = s.pg;
					old.gr = s.gr;
					old.gd = s.gd;
					old.pr = s.pr;
					old.bd = s.bd;
				}
				scoreData.set(i, old);
				return;
			}
		}
		
		scoreData.add(s);
	}
	
	public void addScore(String hash, int note, int pg, int gr, int gd, int pr, int bd, int combo, int clear) {
		ScoreData s = new ScoreData();
		s.hash = hash;
		s.note = note;
		s.pg = pg;
		s.gr = gr;
		s.gd = gd;
		s.pr = pr;
		s.bd = bd;
		s.combo = combo;
		s.clear = clear;
		scoreData.add(s);
	}
}
