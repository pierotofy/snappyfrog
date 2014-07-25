package com.masseranolabs.snappyfrog;

import java.util.Calendar;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.TimeUtils;

public class Game extends com.badlogic.gdx.Game implements ApplicationListener {
    public enum TimeOfDay { Morning, Day, Night };
    
	// Static members
	private static int width, height;
	private static TextureAtlas textureAtlas;
	private static Skin skin;
	private static BitmapFont buttonFont;
	private static BitmapFont scoreFont;
	private static BitmapFont hintFont;
	private static Game singleton;
	private static Random randomGenerator;
	
	private static Sound plusOneSound;
	private static Sound crashSound;
	private static Sound bigJumpSound;
	private static Sound smallJumpSound;
	private static Sound newRecordSound;
	private static Sound cheerSound;
	private static Sound gaspSound;
	private static Sound laserSound;
	
	
	private static Sprite goldMedal;
	private static Sprite missMedal;
	
	private static Preferences preferences;
	private static PlatformServices platformServices;
	
	private static Color backgroundColor = new Color(0.059f, 0.663f, 0.875f, 1.0f);
	
	// Constants	
	public final static int VIRTUAL_WIDTH = 480;
	public final static int VIRTUAL_HEIGHT = 320;
	public static float GRAVITY;
	
	// Accessors
	public static int getWidth(){ return width; }
	public static int getHeight(){ return height; }
	public static TextureAtlas getTextureAtlas(){ return textureAtlas; }
	public static Skin getSkin(){ return skin; }
	public static BitmapFont getButtonFont(){ return buttonFont; }
	public static BitmapFont getScoreFont(){ return scoreFont; }
	public static BitmapFont getHintFont(){ return hintFont; }	
	public static Game getSingleton() { return singleton; }
	public static Random getRandomGenerator(){ return randomGenerator; } 
	
	public static Sound getPlusOneSound(){ return plusOneSound; }
	public static Sound getCrashSound(){ return crashSound; }
	public static Sound getBigJumpSound(){ return bigJumpSound; }
	public static Sound getSmallJumpSound(){ return smallJumpSound; }
	public static Sound getNewRecordSound(){ return newRecordSound; }
	public static Sound getCheerSound(){ return cheerSound; }
	public static Sound getGaspSound(){ return gaspSound; }
	public static Sound getLaserSound(){ return laserSound; }
	
	
	
	public static Sprite getGoldMedal(){ return goldMedal; }
	public static Sprite getMissMedal(){ return missMedal; }	
		
	public static Preferences getPreferences(){ return preferences; }
	public static PlatformServices getPlatformServices(){ return platformServices; }
	
	// Setters
	public static void SetBackgroundColor(Color c){ Game.backgroundColor = c; }

	public Game(PlatformServices platformServices){
		Game.platformServices = platformServices;
	}
	
	public static void StartNewLevelScreen(){  
		LevelScreen level = new LevelScreen(false);
		singleton.setScreen(level);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void create() {
		singleton = this;
		
		platformServices.initGamePadControllers();
		
		// Get width, height
		Game.width = Gdx.graphics.getWidth();
		Game.height = Gdx.graphics.getHeight();
		
		GRAVITY = ResHelper.LinearHeightValue(1000.0f);
		
		randomGenerator = new Random(TimeUtils.millis());
		
		// Init objects
		textureAtlas = new TextureAtlas(Gdx.files.internal("textures/pack.atlas"));
		skin = new Skin();
		skin.addRegions(textureAtlas);
		
		if (platformServices.supportsFreetype()){
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("slkscr.ttf"));
			buttonFont = generator.generateFont((int)ResHelper.LinearHeightValue(18));
			scoreFont = generator.generateFont((int)ResHelper.LinearHeightValue(14));
			hintFont = generator.generateFont((int)ResHelper.LinearHeightValue(12));	
			generator.dispose();
		}else{
			buttonFont = new BitmapFont(Gdx.files.internal("prerenderedFonts/buttonFont.fnt"));
			scoreFont = new BitmapFont(Gdx.files.internal("prerenderedFonts/scoreFont.fnt"));
			hintFont = new BitmapFont(Gdx.files.internal("prerenderedFonts/hintFont.fnt"));			
		}
		
		plusOneSound = Gdx.audio.newSound(Gdx.files.internal("sounds/plusOne.wav"));
		crashSound = Gdx.audio.newSound(Gdx.files.internal("sounds/crash.wav"));
		bigJumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bigJump.wav"));
		smallJumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/smallJump.wav"));
		newRecordSound = Gdx.audio.newSound(Gdx.files.internal("sounds/newRecord.wav"));
		cheerSound = Gdx.audio.newSound(Gdx.files.internal("sounds/cheer.wav"));
		gaspSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gasp.wav"));
		laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
		
		
		goldMedal = textureAtlas.createSprite("misc/medal-gold");
		missMedal = textureAtlas.createSprite("misc/medal-miss");
				
		
		preferences = Gdx.app.getPreferences("Globals");
		if (!preferences.contains("highscore")) preferences.putInteger("highscore", 0);
		if (!preferences.contains("tutorial")) preferences.putBoolean("tutorial", true);
		if (!preferences.contains("secret_column")) preferences.putInteger("secret_column", 5 + randomGenerator.nextInt(25));
		if (!preferences.contains("user_shared")) preferences.putBoolean("user_shared", false);
		preferences.flush();
		
		setScreen(new LevelScreen(true));
	}
	
	public static TimeOfDay getTimeOfDay(){
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hour >= 8 && hour <= 20){
			return TimeOfDay.Day;
		}else if (hour >= 21 || hour <= 4){
			return TimeOfDay.Night;
		}else{
			return TimeOfDay.Morning;
		}
		//return TimeOfDay.Day;
	}
	
	public static String getTimeBasedTextureName(String name){
		TimeOfDay tod = getTimeOfDay();
		
		if (tod == TimeOfDay.Night) return name + "-night";
		else if (tod == TimeOfDay.Morning) return name + "-morning";
		else return name;
	}

	@Override
	public void dispose() {
		buttonFont.dispose();
		textureAtlas.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		Game.width = width;
		Game.height = height;
	}

	@Override
	public void pause() {
		getScreen().pause();
	}

	@Override
	public void resume() {
		getScreen().resume();
	}
}
