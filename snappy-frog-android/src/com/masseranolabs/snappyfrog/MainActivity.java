package com.masseranolabs.snappyfrog;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MainActivity extends AndroidApplication {
	public static AndroidApplication app; // Singleton
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.app = this;
        
        // Create the layout
        RelativeLayout layout = new RelativeLayout(this);

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Create the libgdx View
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        View gameView = initializeForView(new Game(new AndroidServices()), cfg);


        // Create and setup the AdMob view
        AdView adView = new AdView(this, AdSize.SMART_BANNER, "ca-app-pub-3144450577280402/6068522170"); // Put in your secret key here
        adView.loadAd(new AdRequest());
        
        // Add the libgdx view
        layout.addView(gameView);

        // Add the revmob view
        RelativeLayout.LayoutParams adParams = 
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        
        layout.addView(adView, adParams);

        // Hook it all up
        setContentView(layout);
        
    }
}