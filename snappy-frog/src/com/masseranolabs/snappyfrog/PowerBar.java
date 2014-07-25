package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class PowerBar extends Actor {
	private Array<Sprite> frames;
	private int currentFrame;
	private long lastUpdateFrameTime;
	private boolean activated;
	private int holdCount;
	
	public PowerBar(){
		super();
		
		activated = false;
		frames = Game.getTextureAtlas().createSprites("gamescene/powerbar");
		
		currentFrame = 0;
		holdCount = 0;
		lastUpdateFrameTime = 0;
	}
	
	public void activate(){
		activated = true;
	}
	
	public int deactivate(){
		activated = false;
		return currentFrame;
	}
	
	private void updateFrame(){
		// Limit frame update to every 1/20th of second
		if (TimeUtils.nanoTime() - lastUpdateFrameTime > 25000000){
			if (activated){
				if (++currentFrame == frames.size){
					if (holdCount++ != 4){
						currentFrame = frames.size - 1;
					}else{
						currentFrame = 0;
						holdCount = 0;
					}
				}				
			}else{
				currentFrame = 0;
				holdCount = 0;
			}
			
			lastUpdateFrameTime = TimeUtils.nanoTime();
		}
	}
	
	@Override
	public float getWidth() {
		return frames.get(0).getWidth();
	}
	
	@Override
	public float getHeight() {
		return frames.get(0).getHeight();
	}	
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		updateFrame();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		batch.draw(frames.get(currentFrame), getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}
}
