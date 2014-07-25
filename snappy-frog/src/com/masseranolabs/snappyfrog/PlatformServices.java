package com.masseranolabs.snappyfrog;

public interface PlatformServices {
	boolean isSharingAvailable();
	boolean shareCurrentScreen();
	boolean delayHint();
	boolean willResumeAfterShare();
	boolean supportsFreetype();
	boolean isGamePadButtonPressed();
	void initGamePadControllers();
}
