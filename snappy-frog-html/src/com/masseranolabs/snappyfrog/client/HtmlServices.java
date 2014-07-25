package com.masseranolabs.snappyfrog.client;

import com.masseranolabs.snappyfrog.PlatformServices;

public class HtmlServices implements PlatformServices {

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
		return false;
	}
	
	@Override
	public boolean isGamePadButtonPressed() {
		return false;
	}
	
	@Override
	public void initGamePadControllers() {}
}
