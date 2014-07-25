package com.masseranolabs.snappyfrog;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.RevMobTestingMode;
import com.revmob.ads.banner.RevMobBanner;

public class MainActivity extends AndroidApplication {
	public static AndroidApplication app; // Singleton
	RevMobBanner banner;
	
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
        RevMob revmob = RevMob.start(this);
        //revmob.setTestingMode(RevMobTestingMode.WITH_ADS); // with this line, RevMob will always deliver a sample ad
        //revmob.setTestingMode(RevMobTestingMode.WITHOUT_ADS); // with this line, RevMob will not delivery ads
        
        banner = revmob.createBanner(this);
       

        // Add the libgdx view
        layout.addView(gameView);

        // Add the revmob view
        RelativeLayout.LayoutParams adParams = 
            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
            		80);
        
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        
        /*
        LinearLayout bannerLayout = new LinearLayout(this);
        bannerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        bannerLayout.setVisibility(View.GONE);
        bannerLayout.addView(banner);*/
        
        
        layout.addView(banner, adParams);

        // Hook it all up
        setContentView(layout);
        
    }
}