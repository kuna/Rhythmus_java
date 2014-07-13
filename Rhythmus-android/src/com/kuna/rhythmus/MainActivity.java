package com.kuna.rhythmus;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.kuna.rhythmus.data.Common;

public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Dont let screen off
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGLSurfaceView20API18 = true;

		// check intent whether if executed from other application
		Intent i = getIntent();
		
		if (i.getExtras() != null) {
			Log.i("Rhythmus", "Execute Arguments Found.");
			Common.argBeat = i.getExtras().getDouble("Beat", 0);
			Common.argPath = i.getExtras().getString("File");
			Common.argRemove = i.getExtras().getBoolean("RemoveAfterPlay", false);
		}
		
		// hide soft menu bar
		/*
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
			getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LOW_PROFILE);*/
		
        initialize(new Rhythmus(), cfg);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
}