package com.masseranolabs.snappyfrog;

import android.content.Intent;
import android.net.Uri;

import com.badlogic.gdx.Gdx;

public class AndroidServices implements PlatformServices {

	@Override
	public boolean isSharingAvailable() {
		return Gdx.files.isExternalStorageAvailable();
	}

	@Override
	public boolean shareCurrentScreen() {
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		String screenshotPath = ScreenshotFactory.saveScreenshot(Gdx.files.getExternalStoragePath() + "/screenshots/", true);
		
		// If we were able to take a screenshot
		if (!screenshotPath.equals("")){
			Uri screenshotUri = Uri.parse("file://" + screenshotPath);
	
			sharingIntent.setType("image/png");
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "You MUST try this game!");
			sharingIntent.putExtra(Intent.EXTRA_TEXT, "This was my score! http://www.playsnappyfrog.com");
			sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
			MainActivity.app.startActivity(Intent.createChooser(sharingIntent, "Share via"));
			
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public boolean willResumeAfterShare() {
		return true;
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
