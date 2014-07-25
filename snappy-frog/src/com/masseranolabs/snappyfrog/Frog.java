package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Frog extends Actor {
	private Array<Sprite> frames;
	private int currentFrame;
	private boolean squatting;
	private boolean jumping;
	private boolean dying;
	private boolean shocked;
	private float jumpingVelocity;
	private float dyingVelocity;
	private long lastUpdateFrameTime;
	private LevelScreen parent;
	private float width;
	private float height;
	private float actualWidth;
	private float actualHeight;
	
	public Frog(LevelScreen parent){
		super();
		this.parent = parent;
		
		dying = squatting = jumping = shocked = false;
		
		frames = Game.getTextureAtlas().createSprites("gamescene/frog");
		
		currentFrame = 0;
		lastUpdateFrameTime = 0;
		
		width = frames.get(0).getWidth();
		height = frames.get(0).getHeight();
		updateCachedValues();
	}
	
	public void updateCachedValues(){
		// Here you can decide the bounding box for the frog
		// to make the game less picky/frustrating, even if the frog partially hits
		// something it will still carry forward.
		final int MARGIN = 5;
		actualWidth = (width - MARGIN) * getScaleX();
		actualHeight = (height - MARGIN) * getScaleY();
	}
	
	public void jump(float initialVelocity){
		jumping = true;
		squatting = false;
		jumpingVelocity = initialVelocity;
	}

	public void squat() {
		squatting = true;
	}
	
	public void walk(){
		squatting = jumping = false;
		jumpingVelocity = 0;
	}
	
	public void shock(){
		shocked = true;
		squatting = jumping = false;
		
		// Shock frame has different dimensions
		width = frames.get(5).getWidth();
		height = frames.get(5).getHeight();
	}
	
	public void die(){
		// Dying frame has different dimensions
		width = frames.get(5).getWidth();
		height = frames.get(5).getHeight();
		
		// Move to the right slightly to improve dying animation
		moveBy(-ResHelper.LinearWidthValue(frames.get(5).getWidth() - frames.get(0).getWidth()), 0.0f);
		
		squatting = jumping = false;
		dying = true;
		dyingVelocity = ResHelper.LinearHeightValue(500.0f);
	}
	
	public float getJumpingVelocity(){
		return jumpingVelocity;
	}
	
	@Override
	public float getWidth() {
		return width;
	}
	
	@Override
	public float getHeight() {
		return height;
	}
	
	// @cached
	public float getActualWidth(){
		return actualWidth;
	}
	
	// @cached
	public float getActualHeight(){
		return actualHeight;
	}
	
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (!LevelScreen.paused){		
			if (jumping){
				float newVelocity = jumpingVelocity - Game.GRAVITY * delta;
				float distanceToMoveY = newVelocity * delta;
				
				// Check for collissions
				boolean collision = false;
	
				// Ground?
				if (getY() + distanceToMoveY <= LevelScreen.groundHeightLine){
					setY(LevelScreen.groundHeightLine);
					walk();
					collision = true;
				}
				
				// Obstacle ground?
				if (!LevelScreen.miniGame &&
						LevelScreen.collisionObstacle.getX() <= (getX() + getActualWidth()) && 
						getY() + distanceToMoveY <= LevelScreen.collisionObstacle.getLowerYBound()){
					setY(LevelScreen.collisionObstacle.getLowerYBound());
					walk();
					
					// +1
					if (!LevelScreen.collisionObstacle.isScored()){
						parent.incrementScore();
					}
					
					collision = true;
				}
				
				if (!collision){		
					this.moveBy(0.0f, distanceToMoveY);
					jumpingVelocity = newVelocity;
				}
			}
			
			if (dying){
				float newVelocity = dyingVelocity - Game.GRAVITY * delta;
				float distanceToMoveY = newVelocity * delta;
				
				if (getY() + getActualHeight() >= 0){
					this.moveBy(0.0f, distanceToMoveY);
					dyingVelocity = newVelocity;
				}else{
					this.remove();
					parent.frogDied();
				}
			}
			
			// Limit animation frames update to every 1/10th of second
			if (TimeUtils.nanoTime() - lastUpdateFrameTime > 100000000){
				if (jumping){
					currentFrame = 4;			
				}else if (squatting){
					if (currentFrame == 0 || currentFrame == 2){
						currentFrame = 3;
					}else{
						currentFrame = 2;
					}
				}else if (dying || shocked){
					currentFrame = 5;
				}else{
					if (currentFrame == 0){
						currentFrame = 1;
					}else{
						currentFrame = 0;
					}				
				}
				
				lastUpdateFrameTime = TimeUtils.nanoTime();
			}
		}
	}
	
	public boolean isJumping(){
		return jumping;
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
