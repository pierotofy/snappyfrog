package com.masseranolabs.snappyfrog;

import java.io.File;
import java.util.Calendar;

import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIImage;

import com.badlogic.gdx.Gdx;
import com.masseranolabs.bridge.SLComposeViewController;

public class iOSServices implements PlatformServices {

	@Override
	public boolean isSharingAvailable() {
		return RobovmLauncher.isSharingAvailable();
	}

	@Override
	public boolean shareCurrentScreen() {
    	SLComposeViewController slc = RobovmLauncher.getSLComposeViewController();  
    	
		String screenshotPath = ScreenshotFactory.saveScreenshot(Gdx.files.getExternalStoragePath() + "/screenshots/", true);
		
		// If we were able to take a screenshot
		if (!screenshotPath.equals("")){
			
	    	slc.addImage(new UIImage(new File(screenshotPath)));
	    	UIViewController view = UIApplication.getSharedApplication().getKeyWindow().getRootViewController();
	    	view.presentViewController(slc, false, null);
			
	    	
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public boolean willResumeAfterShare() {
		return false;
	}
	
	@Override
	public boolean delayHint() {
		return false;
	}
	
	@Override
	public boolean supportsFreetype() {
		return true;
	}
	
	@Override
	public boolean isGamePadButtonPressed() {
		return false;
	}
	
	@Override
	public void initGamePadControllers() {}
}

