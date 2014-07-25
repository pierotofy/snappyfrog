package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.SnapshotArray;

public class Salary extends Actor implements Poolable {
	private float SPEED = ResHelper.LinearWidthValue(100.0f);
	private Sprite salary;
	private boolean hit;
	private boolean leftToRight;
	LevelScreen parent;
	private Explosion explosion;
	
	public Salary(LevelScreen parent){
		super();
		this.parent = parent;
		
		explosion = new Explosion();
		explosion.setScale(ResHelper.LinearHeightValue(1.0f));
		
		salary = Game.getTextureAtlas().createSprite("gamescene/salary");
		salary.setScale(ResHelper.LinearHeightValue(1.0f));
		
		reset();
	}
	
	@Override
	public void reset() {
		hit = false;
		leftToRight = Game.getRandomGenerator().nextBoolean();
		explosion.reset();
	}
	
	public void setStartLocation(float topLineHeight){
		setPosition(leftToRight ? -getWidth() : Game.getWidth(), 
				topLineHeight - Game.getRandomGenerator().nextInt((int)ResHelper.LinearHeightValue(40.0f)));
	}
	
	@Override
	public float getWidth() {
		return salary.getWidth();
	}
	
	@Override
	public float getHeight() {
		return salary.getHeight();
	}	
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		// Move
		if (leftToRight) moveBy(SPEED * delta, 0);
		else moveBy(-SPEED * delta, 0);
		
		// Check for out of bounds
		if (getX() + getWidth() < -1 || getX() > Game.getWidth() + 1){
			parent.removeSalary(this);
		}else if (!hit){
			SnapshotArray<Actor> lasers = parent.lasersGroup.getChildren();
			Actor laser;
			for (int i = 0; i < lasers.size; i++){
				laser = lasers.get(i);
				if (laser.getX() > getX() && laser.getX() < getX() + getWidth() &&
					laser.getY() < getY() + getHeight() && laser.getY() > getY()
					){
					hit = true;
					
					Game.getCrashSound().play();
					explosion.setPosition(getX(), getY());
					explosion.explode();			
					
					// Bounce down
					addAction(Actions.moveBy(0, ResHelper.LinearHeightValue(-100.0f), 1.0f, Interpolation.circleOut));
					
					// remove laser
					parent.removeLaserBeam((LaserBeam)laser);
					
					parent.increaseSalaryHitCount();
					break;
				}
			}
		}else{
			explosion.act(delta);
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		batch.draw(salary, getX(), getY(), getOriginX(), getOriginY(),
	                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
		
		explosion.draw(batch, parentAlpha);
	}
}
