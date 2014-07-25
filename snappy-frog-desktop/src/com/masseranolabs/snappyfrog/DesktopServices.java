package com.masseranolabs.snappyfrog;

public class DesktopServices implements PlatformServices {

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
}
