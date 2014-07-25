package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LaserBeam extends Actor implements Poolable {
	private Sprite laser;
	private Vector2 velocity;
	private Vector2 startLocation;
	private boolean visible;
	LevelScreen parent;
	
	public LaserBeam(LevelScreen parent){
		super();
		this.parent = parent;
		
		laser = Game.getTextureAtlas().createSprite("gamescene/laser");
		laser.setScale(ResHelper.LinearHeightValue(1.0f));
		startLocation = new Vector2();
		
		reset();
	}
	
	@Override
	public void reset() {
		visible = false;
	}
	
	public void setStartLocation(float x, float y){
		startLocation.x = x;
		startLocation.y = y;
		setPosition(x, y);
	}
	
	public void shoot(Vector2 target){
		visible = true;
		
		velocity = target.cpy().sub(startLocation);
		velocity.nor();
		velocity.scl(ResHelper.LinearHeightValue(500.0f));

		setRotation(velocity.angle());
	}

	
	@Override
	public float getWidth() {
		return laser.getWidth();
	}
	
	@Override
	public float getHeight() {
		return laser.getHeight();
	}	
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		// Move
		moveBy(velocity.x * delta, velocity.y * delta);
		
		// Check for out of bounds
		if (getX() + getWidth() < 0 || getX() > Game.getWidth() ||
			getY() + getHeight() < 0 || getY() > Game.getHeight()){
			parent.removeLaserBeam(this);
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		if (visible){
			batch.draw(laser, getX(), getY(), getOriginX(), getOriginY(),
	                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
		}
	}
}
