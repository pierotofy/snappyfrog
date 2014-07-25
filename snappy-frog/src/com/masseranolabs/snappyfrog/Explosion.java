package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Explosion extends Actor {
	private static Array<Sprite> frames;
	private int currentFrame;
	private long lastUpdateFrameTime;
	private boolean exploding;
	
	public Explosion(){
		super();
		
		if (frames == null){
			frames = Game.getTextureAtlas().createSprites("gamescene/explosion");
		}
		
		reset();
	}
	
	public void reset() {
		currentFrame = 0;
		lastUpdateFrameTime = 0;
		exploding = false;
	}
	
	public void explode(){
		exploding = true;
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
		
		if (exploding){
			if (TimeUtils.nanoTime() - lastUpdateFrameTime > 25000000){
				if (++currentFrame == frames.size - 1){
					exploding = false;
				}
				
				lastUpdateFrameTime = TimeUtils.nanoTime();
			}
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		if (exploding){
			batch.draw(frames.get(currentFrame), getX(), getY(), getOriginX(), getOriginY(),
	                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
		}
	}
}
