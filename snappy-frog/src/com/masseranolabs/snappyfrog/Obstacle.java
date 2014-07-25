package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Obstacle extends Actor implements Poolable {
	private static NinePatch bottomPatch;
	private static NinePatch topPatch;
	private static Sprite plusOneTemplate;
	private Sprite plusOne;
	private float plusOneYTarget;
	private float openingHeight;
	private float bottomY;
	private float bottomHeight;
	private float topHeight;
	private float width;
	private float topY;
	private boolean scored;
	public static float WIDTH;
	public static float OPENINGHEIGHT;
	
	public static void loadTimeBasedResources(){
		bottomPatch = Game.getTextureAtlas().createPatch(Game.getTimeBasedTextureName("gamescene/bottom-obstacle"));
		topPatch = Game.getTextureAtlas().createPatch(Game.getTimeBasedTextureName("gamescene/top-obstacle"));
	}

	public Obstacle(){
		super();
		if (bottomPatch == null){
			loadTimeBasedResources();
		}
		if (plusOneTemplate == null){
			plusOneTemplate = Game.getTextureAtlas().createSprite("gamescene/+1");
		}
		
		reset();
	}
	
	@Override
	public void reset() {
		scored = false;
		
		width = Obstacle.WIDTH;
		openingHeight = Obstacle.OPENINGHEIGHT;

		bottomY =  LevelScreen.groundHeightLine - ResHelper.LinearHeightValue(2.0f);
		bottomHeight = ResHelper.LinearHeightValue(20.0f) +
				Game.getRandomGenerator().nextInt((int)(Game.getHeight() * 0.75f - ResHelper.LinearHeightValue(20.0f) - openingHeight));
		
		topY = (LevelScreen.groundHeightLine - ResHelper.LinearHeightValue(1)) + bottomHeight + openingHeight;
		topHeight = Game.getHeight() - topY + ResHelper.LinearHeightValue(4);
		
		setX(Game.getWidth());
		
		if (plusOne == null) plusOne = new Sprite(plusOneTemplate);
		plusOne.setY(bottomY + bottomHeight);
		plusOne.setScale(ResHelper.LinearHeightValue(1.0f));
		plusOneYTarget = bottomY + bottomHeight + openingHeight / 2.0f - plusOne.getHeight() / 2.0f;
	}
	
	public void markScored(){
		scored = true;
	}
	
	public boolean isScored(){
		return scored;
	}
	
	public float getLowerYBound(){
		return bottomY + bottomHeight - (int)ResHelper.LinearHeightValue(1);
	}
	
	public float getUpperYBound(){
		return topY;
	}
	
	@Override
	public float getWidth() {
		return width;
	}
	
	@Override
	public float getHeight() {
		return Game.getHeight();
	}	
	
	@Override
	public void act(float delta) {
		super.act(delta);

	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		bottomPatch.draw(batch, getX(), bottomY, width, bottomHeight);
		topPatch.draw(batch, getX(), topY, width, topHeight);	
		
		if (scored){
			plusOne.setX(getX() + width / 2.0f - plusOne.getWidth() / 2.0f);
			
			if (plusOne.getY() < plusOneYTarget){
				plusOne.setY(plusOne.getY() + 4.0f);
			}
			plusOne.draw(batch, parentAlpha);
		}
	}

	public void dispose() {

	}
}
