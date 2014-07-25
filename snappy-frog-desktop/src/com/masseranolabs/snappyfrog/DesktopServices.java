package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.Gdx;

public class DesktopServices implements PlatformServices {
	
	public DesktopServices(){

	}
	
	@Override
	public boolean isSharingAvailable() {
		return false;
	}

	@Override
	public boolean shareCurrentScreen() {
		return false;
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
