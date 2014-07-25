package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ButtonFactory {
	private static boolean initialized = false;
	
	// Always call this on each method
	private static void Initialize(){
		if (!initialized){
			
			// Make sure we add padding to the textures for the buttons.
			// This needs to be done only once!
			final int BUTTON_PADDING = ResHelper.LinearHeightValue(10);
			
			Drawable up = Game.getSkin().getDrawable("misc/basic-button");
			
			up.setLeftWidth(up.getLeftWidth() + BUTTON_PADDING);
			up.setRightWidth(up.getRightWidth() + BUTTON_PADDING);
			up.setTopHeight(up.getTopHeight() + BUTTON_PADDING);
			up.setBottomHeight(up.getBottomHeight() + BUTTON_PADDING);
		
			initialized = true;
		}
	}
	
	public static TextButton MakeStandardButton(String caption){
		Initialize();	

		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.up = Game.getSkin().getDrawable("misc/basic-button");
		buttonStyle.down = Game.getSkin().getDrawable("misc/basic-button-down");
		buttonStyle.font = Game.getButtonFont();
		buttonStyle.fontColor = Color.BLACK;
		return new TextButton(caption, buttonStyle);		
	}
}
