package com.masseranolabs.snappyfrog;

// Resolution helper
public class ResHelper {
	public static int LinearWidthValue(int value){ 
		return (int)((float)value/(float)Game.VIRTUAL_WIDTH * (float)Game.getWidth()); 
	}
	public static int LinearHeightValue(int value){ 
		return (int)((float)value/(float)Game.VIRTUAL_HEIGHT * (float)Game.getHeight()); 
	}
	
	public static float LinearWidthValue(float value){
		return (float)value/(float)Game.VIRTUAL_WIDTH * (float)Game.getWidth(); 
	}
	public static float LinearHeightValue(float value){
		return (float)value/(float)Game.VIRTUAL_HEIGHT * (float)Game.getHeight(); 
	}
	
	public static float StretchScaleMultipleOfTwoWidth(float scale, float itemSize){
		int v = (int) Math.ceil(scale * (float)Game.getWidth() / (float)Game.VIRTUAL_WIDTH);
		// http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
		v--;
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		v++;
		
		while(v * itemSize > Game.getWidth() && v > 1) v--;
		
		return (float)v;
	}
	
}
