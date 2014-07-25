package com.masseranolabs.snappyfrog;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.masseranolabs.snappyfrog.Game.TimeOfDay;

public class LevelScreen extends StagedScreen {
	private final float WORLD_SPEED_LIMIT = ResHelper.LinearWidthValue(-320.0f);
	private float WORLD_SPEED = ResHelper.LinearWidthValue(-90.0f);
	private float CLOUDS_SPEED;
	private float TREES_SPEED;
	
	private float OBSTACLES_DISTANCE = ResHelper.LinearWidthValue(85.0f);
	private final float MIN_OPENING_HEIGHT = ResHelper.LinearHeightValue(60.0f);
	private final int NUM_CLOUDS = 6;
	private final int NUM_TREES = 7;
	
	private final int BASE_LEVEL = 10;
	private final int LEVEL_MULTIPLIER = 3; // 10, 30, 90, ...
	private final int MEDALS_COUNT = 5;

	private Array<Obstacle> obstacles = new Array<Obstacle>(20);
    private final Pool<Obstacle> obstaclesPool = new Pool<Obstacle>() {
	    @Override
	    protected Obstacle newObject() {
	        return new Obstacle();
	    }
    };
    
	private Frog frog;
	private PowerBar powerBar;
	private Image ground;
	private Image grass;
	private Image sunMoon;
	private boolean holdingTouch;
	private Group obstaclesGroup = new Group();
	private Group cloudsGroup = new Group();
	private Group treesGroup = new Group();
	private Group miniGameGroup = new Group();
	public Group lasersGroup = new Group();
	private float obstacleDistanceSalt;
	private int score;
	private Label scoreLabel;
	private Label tutorialLabel;
	private Label hintLabel;
	private boolean gameOver;
	private boolean secondJumpActivated;
	private boolean mainMenu;
	private boolean tutorial;
	private int tutorialStep;
	private boolean beatHighscore;
	private boolean miniGameStarted;
	private boolean miniGameDone;
	private boolean userInitiatedShare;
	private boolean topDialogDisplayed;
	
	// Mini game stuff
	private Image bigG;
	private Image apple;
	private Image appleBeam;
	private Image bigGBeam;
	private Label miniGameLabel;
	private boolean miniGameShooting;
    private boolean miniGameStartReleasingSalaries;
	private long lastShootTime;
	private long lastSalaryCreatedTime;	
	private Vector2 appleBeamLocation;
	private Vector2 bigGBeamLocation;
	
    private Pool<LaserBeam> lasersPool;
    private Pool<Salary> salariesPool;
    private int salariesHitCount;
    private int salariesCount;
    private int salariesAccounted;
    private final int MAX_SALARIES_COUNT = 200;
	private final int SECRET_COLUMN = Game.getPreferences().getInteger("secret_column");
    
	public static boolean paused;
	public static float groundHeightLine; // for faster checks
	public static Obstacle collisionObstacle;
	public static boolean miniGame;
	
	public LevelScreen(boolean mainMenu){
		super();
		this.mainMenu = mainMenu;

		// Init vars
		score = 0;
		holdingTouch = false;
		userInitiatedShare = false;
		gameOver = false;
		miniGame = false;
		topDialogDisplayed = false;
		miniGameStarted = miniGameDone = miniGameStartReleasingSalaries = false;
		lastShootTime = 0;
		lastSalaryCreatedTime = 0;
		salariesHitCount = salariesCount = salariesAccounted = 0;
		secondJumpActivated = false;
		tutorial = Game.getPreferences().getBoolean("tutorial");
		tutorialStep = 0;
		beatHighscore = false;
		paused = false;
		updateScenaryItemsSpeeds();
		Obstacle.WIDTH = OBSTACLES_DISTANCE;
		Obstacle.OPENINGHEIGHT = ResHelper.LinearHeightValue(80.0f);
		
		// Setup world objects
		
		ground = new Image(Game.getTextureAtlas().createSprite(Game.getTimeBasedTextureName("gamescene/ground")));
		ground.setWidth(Game.getWidth());
		ground.setScale(ResHelper.LinearHeightValue(1.0f));
		groundHeightLine = (ground.getHeight() - 1) * ground.getScaleY();
		
		grass = new Image(Game.getTextureAtlas().createSprite(Game.getTimeBasedTextureName("gamescene/grass")));
		grass.setWidth(Game.getWidth() * 2);
		grass.setScale(ResHelper.LinearHeightValue(1.0f));
		grass.setPosition(0, groundHeightLine);
		
		// Moon/sun switch over
		
		TimeOfDay timeOfDay = Game.getTimeOfDay();
		String sunMoonTexture = "";
		
		if (timeOfDay == TimeOfDay.Morning){
			sunMoonTexture = "gamescene/sun-early";
			Game.SetBackgroundColor(new Color(0.859f, 0.663f, 0.412f, 1.0f));
		}else if (timeOfDay == TimeOfDay.Day){
			sunMoonTexture = "gamescene/sun";
			Game.SetBackgroundColor(new Color(0.059f, 0.663f, 0.875f, 1.0f));
		}else{ // night
			sunMoonTexture = "gamescene/moon";
			Game.SetBackgroundColor(new Color(0.02f, 0.227f, 0.298f, 1.0f));
		}
		
		sunMoon = new Image(Game.getTextureAtlas().createSprite(sunMoonTexture));
		sunMoon.setScale(ResHelper.LinearHeightValue(1.0f));

		float sunMoonPosY = Game.getHeight() - sunMoon.getHeight() * sunMoon.getScaleY() - ResHelper.LinearHeightValue(10.0f);
		if (timeOfDay == TimeOfDay.Morning){
			sunMoonPosY = groundHeightLine - sunMoon.getHeight() * sunMoon.getScaleY() * 0.33f;
		}
		sunMoon.setPosition(Game.getRandomGenerator().nextInt(Game.getWidth()) - sunMoon.getWidth() * sunMoon.getScaleX(), sunMoonPosY);

		
		// Clouds
		
		Array<Sprite> cloudSprites = Game.getTextureAtlas().createSprites("gamescene/cloud");
		cloudSprites.shuffle();
		float cloudXIncrement = Game.getWidth() / NUM_CLOUDS;
		float cloudX = cloudXIncrement;
		for (int i = 0; i < NUM_CLOUDS; i++){
			Image cloud = new Image(cloudSprites.get(i % cloudSprites.size));
			cloud.setScale(ResHelper.LinearWidthValue(1.0f));
			cloud.setPosition(cloudX, groundHeightLine + Game.getRandomGenerator().nextInt((int)(Game.getHeight() - groundHeightLine)));
			cloudX += cloudXIncrement;
			cloudsGroup.addActor(cloud);
		}
		
		Array<Sprite> treeSprites = Game.getTextureAtlas().createSprites("gamescene/tree");
		treeSprites.shuffle();
		float treeXIncrement = Game.getWidth() / NUM_TREES;
		float treeX = treeXIncrement / NUM_TREES;
		for (int i = 0; i < NUM_TREES; i++){
			Image tree = new Image(treeSprites.get(i % treeSprites.size));
			tree.setScale(ResHelper.LinearWidthValue(1.0f));
			tree.setPosition(treeX + Game.getRandomGenerator().nextFloat() * ResHelper.LinearWidthValue(100.0f), groundHeightLine);
			treeX += treeXIncrement;
			treesGroup.addActor(tree);
		}
		
		
		// Main menu layout
		Table mainMenuLayout = new Table();
		
		if (mainMenu){	
			mainMenuLayout.setFillParent(true);
			
			// Logo
			Image logo = new Image(Game.getTextureAtlas().createSprite("misc/logo"));
			logo.setOrigin(logo.getWidth() / 2, logo.getHeight() / 2);
			logo.setScale(ResHelper.StretchScaleMultipleOfTwoWidth(1, logo.getWidth()));
			
			// New game button
			TextButton newGame = ButtonFactory.MakeStandardButton("New Game");
			newGame.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					Game.getSingleton().setScreen(new LevelScreen(false));		
				}
			});

			
			// High score
			LabelStyle labelStyle = new LabelStyle();
			labelStyle.font = Game.getScoreFont();
			Label highscore = new Label("High score: " + Game.getPreferences().getInteger("highscore"), labelStyle);
			
			// Setup layout
			mainMenuLayout.add(logo).spaceBottom(ResHelper.LinearHeightValue(30));
			mainMenuLayout.row();
			mainMenuLayout.add(newGame).spaceBottom(ResHelper.LinearHeightValue(20));
			mainMenuLayout.row();
			mainMenuLayout.add(highscore);
			
			/*
			LabelStyle creditStyle = new LabelStyle();
			creditStyle.font = Game.getScoreFont();
			Label credit = new Label("\nMusic composition by teknoaxe.com", creditStyle);
			
			mainMenuLayout.row();
			mainMenuLayout.add(credit);*/
			
		}else{
			frog = new Frog(this);
			frog.setPosition(ResHelper.LinearWidthValue(100), groundHeightLine);
			frog.setScale(ResHelper.LinearHeightValue(1.0f));
			frog.updateCachedValues();
			
			powerBar = new PowerBar();
			powerBar.setScale(ResHelper.LinearHeightValue(1.0f));
			powerBar.setPosition(ResHelper.LinearWidthValue(10), Game.getHeight() - powerBar.getHeight() * powerBar.getScaleY() - ResHelper.LinearHeightValue(10));
			// OUYA: powerBar.setPosition(ResHelper.LinearWidthValue(28), Game.getHeight() - powerBar.getHeight() * powerBar.getScaleY() - ResHelper.LinearHeightValue(20));
			
			LabelStyle labelStyle = new LabelStyle();
			labelStyle.font = Game.getScoreFont();
			scoreLabel = new Label("", labelStyle);
			scoreLabel.setPosition(powerBar.getX() + powerBar.getWidth() * powerBar.getScaleX() + ResHelper.LinearWidthValue(10), 
					Game.getHeight() - ResHelper.LinearHeightValue(15.0f)); // 25.0f OUYA
			updateScoreLabel();
			
			if (tutorial){
				LabelStyle tutorialLabelStyle = new LabelStyle();
				tutorialLabelStyle.font = Game.getButtonFont();
				tutorialLabelStyle.fontColor = Color.WHITE;
				tutorialLabel = new Label("\nTouch and hold\nanywhere on the screen", tutorialLabelStyle);
				tutorialLabel.setPosition(Game.getWidth() / 2.0f - tutorialLabel.getWidth() / 2.0f, 
						Game.getHeight() / 2.0f);
			}
		}
		
		stage.addActor(sunMoon);
		if (!tutorial || mainMenu){
			stage.addActor(cloudsGroup);
		}
		stage.addActor(treesGroup);
		stage.addActor(miniGameGroup);
		stage.addActor(lasersGroup);
		stage.addActor(ground);
		stage.addActor(grass);
		stage.addActor(obstaclesGroup);
		
		if (mainMenu){
			stage.addActor(mainMenuLayout);
		}else{
			stage.addActor(powerBar);
			stage.addActor(scoreLabel);
			if (tutorial){
				stage.addActor(tutorialLabel);
			}
			stage.addActor(frog);
			
		}
		
		// Update obstacle textures
		Obstacle.loadTimeBasedResources();
		
		/* uncomment to set last level
		while (WORLD_SPEED > WORLD_SPEED_LIMIT){
			 WORLD_SPEED += ResHelper.LinearWidthValue(-2.22f);
			 Obstacle.WIDTH += ResHelper.LinearWidthValue(2.0987f);
			 OBSTACLES_DISTANCE = Obstacle.WIDTH;
			 
			 if (Obstacle.OPENINGHEIGHT > MIN_OPENING_HEIGHT){
				 Obstacle.OPENINGHEIGHT -= ResHelper.LinearHeightValue(1.0f);
			 }
			 
			 updateScenaryItemsSpeeds();
		}*/
		
		// Add first obstacle
		Obstacle o = obstaclesPool.obtain();
		obstacles.add(o);
		obstaclesGroup.addActor(o);
		collisionObstacle = o;
		obstacleDistanceSalt = 0.0f;
	}
	
	private void updateScenaryItemsSpeeds(){
		CLOUDS_SPEED = WORLD_SPEED / 7.5f;
		TREES_SPEED = WORLD_SPEED / 3.0f;
	}
	
	private void updateScoreLabel(){
		scoreLabel.setText("Score: " + score);
	}
	
	public void incrementScore(){
		score++;
		updateScoreLabel();
		collisionObstacle.markScored();
		
		Game.getPlusOneSound().play();
		if (score > Game.getPreferences().getInteger("highscore") && !beatHighscore){
			beatHighscore = true;
			Game.getCheerSound().play();
		}else if (score > 1 && 
				!secondJumpActivated && 
				frog.getX() + frog.getActualWidth() - collisionObstacle.getX() < ResHelper.LinearWidthValue(5.0f)){
			Game.getGaspSound().play();
		}
		
		// Increase speeds and obstacles width
		if (score % 10 == 0 && WORLD_SPEED >= WORLD_SPEED_LIMIT){
			 WORLD_SPEED += ResHelper.LinearWidthValue(-2.22f);
			 Obstacle.WIDTH += ResHelper.LinearWidthValue(2.0987f);
			 OBSTACLES_DISTANCE = Obstacle.WIDTH;
			 
			 if (Obstacle.OPENINGHEIGHT > MIN_OPENING_HEIGHT){
				 Obstacle.OPENINGHEIGHT -= ResHelper.LinearHeightValue(1.0f);
			 }
			 
			 updateScenaryItemsSpeeds();
		}
	}
	
	private void onTouchRelease(){
		if (!paused){
			if (!miniGame){
				
				if (tutorial && tutorialStep == 2){
					tutorialLabel.setText("\nNow jump, then tap again\nas you are falling.");
					tutorialStep++;
				}else if (tutorial && tutorialStep == 4){
					tutorialLabel.setText("\nThat's it!\nTap to begin play.");
					tutorialStep++;
				}else if (tutorial && tutorialStep == 5){
					Game.getPreferences().putBoolean("tutorial", false);
					Game.getPreferences().flush();
					Game.StartNewLevelScreen();
				}
				
				else if (!frog.isJumping()){
					int powerBarValue = powerBar.deactivate();
					frog.jump(powerBarValue * ResHelper.LinearHeightValue(44.0f));
					secondJumpActivated = false;
					Game.getBigJumpSound().play();
					
					if (tutorial && tutorialStep == 1 && powerBarValue > 6){
						tutorialLabel.setText("\nNice work!");
						Game.getPlusOneSound().play();
						tutorialStep++;
					}
				}
			}else{
				// Mini game
				miniGameShooting = false;
			}
		}else{
			// Was paused, now resume
			paused = false;
		}
	
		powerBar.deactivate();
	}
	
	private void onTouchHold(){
		if (!paused){
			if (!miniGame){
				
				// Skip the frog action in the tutorial
				if (!(tutorial && (tutorialStep == 2 || tutorialStep == 4))){
					if (!secondJumpActivated && frog.getJumpingVelocity() < 0.0f){
						frog.jump(ResHelper.LinearHeightValue(264.0f));
						secondJumpActivated = true;
						Game.getSmallJumpSound().play();
						
						if (tutorial && tutorialStep == 3){
							Game.getNewRecordSound().play();
							tutorialStep++;
						}
					}
	
					powerBar.activate();
					frog.squat();
				}
				
				if (tutorial && tutorialStep == 0){
					tutorialLabel.setText("\nNotice the power bar.\nRelease to jump.");
					tutorialStep++;
				}
				
			}else{
				// Mini game
				miniGameShooting = true;
			}
		}

	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
//		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
//			ScreenshotFactory.saveScreenshot("c:\\users\\piero\\desktop\\screens\\", true);
//		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
			Gdx.app.exit();
		}

		
		if (!gameOver){
			if (!mainMenu){
				// Handle input
				if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE) || Game.getPlatformServices().isGamePadButtonPressed()){
					// Touch and hold
					if (!holdingTouch){
						onTouchHold();
						holdingTouch = true;
					}
					
				}else{
					// Release
					if (holdingTouch){
						onTouchRelease();
						holdingTouch = false;
					}
				}
			}else{
				// Main menu, check new game start with game pads
				gamePadNewGameCheck();
			}
			
			
			if (!paused){
				if (!miniGameStarted){
					// Handle obstacles generation
					Obstacle lastObstacle = obstacles.get(obstacles.size - 1);
					if (Game.getWidth() - lastObstacle.getX() - lastObstacle.getWidth() >= OBSTACLES_DISTANCE + obstacleDistanceSalt){
						Obstacle o = obstaclesPool.obtain();
						obstacles.add(o);
						obstaclesGroup.addActor(o);
						
						//obstacleDistanceSalt = Game.getRandomGenerator().nextFloat() * 50.0f;
					}
					
					Obstacle firstObstacle = obstacles.get(0);
					if (firstObstacle.getX() + firstObstacle.getWidth() <= 0){
						obstaclesPool.free(firstObstacle);
						obstacles.removeIndex(0);
					}
					
					// Move grass
					if (-grass.getX() >= grass.getWidth() * grass.getScaleX() / 2.0f){
						grass.setX(0);
					}else{				
						grass.moveBy(WORLD_SPEED * delta, 0);
					}
					
					// Move obstacles
					if (!tutorial){
						for (int i = 0; i < obstacles.size; i++){
							obstacles.get(i).moveBy(WORLD_SPEED * delta, 0);
						}
					}
					
					// Move clouds
					for (int i = 0; i < NUM_CLOUDS; i++){
						Actor cloud = cloudsGroup.getChildren().get(i);
						cloud.moveBy(CLOUDS_SPEED * delta, 0);
						if (cloud.getX() + cloud.getWidth() * cloud.getScaleY() < 0){
							cloud.setPosition(Game.getWidth(),
											groundHeightLine + Game.getRandomGenerator().nextInt((int)(Game.getHeight() - groundHeightLine)));
						}
					}
					
					// Move trees
					for (int i = 0; i < NUM_TREES; i++){
						Actor tree = treesGroup.getChildren().get(i);
						tree.moveBy(TREES_SPEED * delta, 0);
						if (tree.getX() + tree.getWidth() * tree.getScaleY() < 0){
							tree.setX(Game.getWidth());
						}
					}
				}else{
					// Mini game logic
					if (!miniGameDone){
						
						// Handle shooting
						if (miniGameShooting){
							if (TimeUtils.nanoTime() - lastShootTime > 100000000){
								Vector2 target = new Vector2(Gdx.input.getX(), Game.getHeight() - Gdx.input.getY());
								Game.getLaserSound().play();
								
								LaserBeam lb1 = lasersPool.obtain();
								lb1.setStartLocation(bigGBeamLocation.x, bigGBeamLocation.y);
								lb1.shoot(target);
								
								LaserBeam lb2 = lasersPool.obtain();
								lb2.setStartLocation(appleBeamLocation.x, appleBeamLocation.y);
								lb2.shoot(target);
								
								appleBeam.setVisible(true);
								bigGBeam.setVisible(true);
								
								lasersGroup.addActor(lb1);
								lasersGroup.addActor(lb2);
								
								lastShootTime = TimeUtils.nanoTime();
							}else if (TimeUtils.nanoTime() - lastShootTime < 750000000){
								appleBeam.setVisible(false);
								bigGBeam.setVisible(false);
							}
						}else{
							appleBeam.setVisible(false);
							bigGBeam.setVisible(false);
						}
						
						// Generate salaries
						if (miniGameStartReleasingSalaries && TimeUtils.nanoTime() - lastSalaryCreatedTime > 100000000 && salariesCount < MAX_SALARIES_COUNT){
							Salary s = salariesPool.obtain();
							s.setStartLocation(bigG.getY());
							
							miniGameGroup.addActor(s);
							salariesCount++;
							
							lastSalaryCreatedTime = TimeUtils.nanoTime();
						}
						
						if (salariesAccounted >= MAX_SALARIES_COUNT){		
							miniGameDone = true;
							if (salariesHitCount >= MAX_SALARIES_COUNT){
								// Good job!
								Game.getCheerSound().play();
							}
							
							Timer t = new Timer();
							t.scheduleTask(new Task(){
								@Override
								public void run() {
									gameOver = true;
									frog.die();
									
									miniGame = false;
									miniGameStarted = false;
								}
							}, 2.0f);
						}
					}
				}
				
				if (!mainMenu && !tutorial && !miniGame){
					// Check collision route horizontally
					if (collisionObstacle.getX() <= (frog.getX() + frog.getActualWidth())){
						// Possible collision, check Y
						
						if (frog.getY() < collisionObstacle.getLowerYBound() ||
							frog.getY() + frog.getActualHeight() > collisionObstacle.getUpperYBound()){
							// Dead
							gameOver();
						}else{
											
							// Check for collisions with the ceiling
							if (frog.getY() + frog.getActualHeight() >= collisionObstacle.getUpperYBound()){
								// Dead
								gameOver();
							}
						
							// Check for end of obstacle
							if (frog.getX() > collisionObstacle.getX() + collisionObstacle.getWidth()){
								// Just in case we never touched the obstacle...
								if (!collisionObstacle.isScored()){
									incrementScore();
								}
								
								// Set new obstacle
								for (int i = 0; i < obstacles.size; i++){
									if (obstacles.get(i) == collisionObstacle && i != obstacles.size - 1){
										collisionObstacle = obstacles.get(i + 1);
										break;
									}
								}
								
								// Fall down if it's not jumping already
								if (!frog.isJumping()){
									if (!holdingTouch){
										frog.jump(0);
										secondJumpActivated = false;
									}else{
										// Player is holding down, meaning he probably wanted to jump
										onTouchRelease();
										holdingTouch = false;
									}
								}
							}
						}
					}
				}
			}
		}else{
			// Game over, see if gamepad is touched for new game
			if (topDialogDisplayed){
				gamePadNewGameCheck();
			}
		}
	}
	
	public void gamePadNewGameCheck(){
		if (Game.getPlatformServices().isGamePadButtonPressed()){
			// Touch and hold
			if (!holdingTouch){
				holdingTouch = true;
			}
		}else{
			// Release
			if (holdingTouch){
				// Start new game
				Game.getSingleton().setScreen(new LevelScreen(false));
				holdingTouch = false;
			}
		}
	}
	
	public void increaseSalaryHitCount(){
		if (++salariesHitCount > MAX_SALARIES_COUNT) salariesHitCount = MAX_SALARIES_COUNT;
		
		if (miniGameLabel != null) miniGameLabel.setText(salariesHitCount + "/" + MAX_SALARIES_COUNT + " salaries down!");
	}
	
	public void removeLaserBeam(LaserBeam l){
		l.remove();
		lasersPool.free(l);
	}

	public void removeSalary(Salary s){
		salariesAccounted++;
		s.remove();
		salariesPool.free(s);
	}
	
	private void gameOver(){
		powerBar.deactivate();
		if (score > Game.getPreferences().getInteger("highscore")){
			Game.getPreferences().putInteger("highscore", score);
			Game.getPreferences().flush();
		}	
		
		if (score != SECRET_COLUMN - 1 || Game.getPlatformServices().delayHint()){
			// Actual game over
			
			gameOver = true;
			Game.getCrashSound().play();
			frog.die();
		}else{
			// Init mini game
			final LevelScreen me = this;
			Game.getGaspSound().play();
			
			lasersPool = new Pool<LaserBeam>() {
				    @Override
				    protected LaserBeam newObject() {
				        return new LaserBeam(me);
				    }
		    };
			salariesPool = new Pool<Salary>() {
			    @Override
			    protected Salary newObject() {
			        return new Salary(me);
			    }
			};
			    
			miniGame = true;
			final float FADE_TIME = 2.0f;
			
			obstaclesGroup.remove();
			treesGroup.addAction(alpha(0.0f, FADE_TIME));
			cloudsGroup.addAction(alpha(0.0f, FADE_TIME));
			powerBar.addAction(alpha(0.0f, FADE_TIME));
			sunMoon.addAction(alpha(0.0f, FADE_TIME));
			scoreLabel.addAction(alpha(0.0f, FADE_TIME));
			
			Timer t = new Timer();
			t.scheduleTask(new Timer.Task(){ 
				@Override
				public void run() {
					frog.shock();
					miniGameStarted = true;
					
					// Add mini game items
					bigG = new Image(Game.getTextureAtlas().createSprite("gamescene/big-g"));
					bigG.setScale(ResHelper.LinearWidthValue(1.0f));
					bigG.setPosition(ResHelper.LinearWidthValue(10), Game.getHeight() - bigG.getHeight() * bigG.getScaleY() - ResHelper.LinearHeightValue(10));
					bigG.addAction(sequence(alpha(0), alpha(1.0f, FADE_TIME)));
					
					bigGBeam = new Image(Game.getTextureAtlas().createSprite("gamescene/shoot-glow"));
					bigGBeam.setScale(ResHelper.LinearWidthValue(1.0f));
					bigGBeam.setPosition(ResHelper.LinearWidthValue(20), Game.getHeight() - bigG.getHeight() * bigG.getScaleY() + ResHelper.LinearHeightValue(10));
					bigGBeam.setVisible(false);
					
					bigGBeamLocation = new Vector2(bigGBeam.getX() + bigGBeam.getWidth() * bigGBeam.getScaleX() / 2.0f,
							bigGBeam.getY() + bigGBeam.getHeight() * bigGBeam.getScaleY() / 2.0f);
					
					apple = new Image(Game.getTextureAtlas().createSprite("gamescene/apple"));
					apple.setScale(ResHelper.LinearWidthValue(1.0f));
					apple.setPosition(Game.getWidth() - apple.getWidth() * apple.getScaleX() - ResHelper.LinearWidthValue(10), 
							Game.getHeight() - apple.getHeight() * apple.getScaleY() - ResHelper.LinearHeightValue(10));
					apple.addAction(sequence(alpha(0), alpha(1.0f, FADE_TIME)));
					
					
					appleBeam = new Image(Game.getTextureAtlas().createSprite("gamescene/shoot-glow"));
					appleBeam.setScale(ResHelper.LinearWidthValue(1.0f));
					appleBeam.setPosition(Game.getWidth() - apple.getWidth() * apple.getScaleX() - ResHelper.LinearWidthValue(25), 
							Game.getHeight() - apple.getHeight() * apple.getScaleY() - ResHelper.LinearHeightValue(5));
					appleBeam.setVisible(false);
					
					appleBeamLocation = new Vector2(appleBeam.getX() + appleBeam.getWidth() * appleBeam.getScaleX() / 2.0f,
													appleBeam.getY() + appleBeam.getHeight() * appleBeam.getScaleY() / 2.0f);
					
					
					miniGameGroup.addActor(bigG);
					miniGameGroup.addActor(bigGBeam);
					miniGameGroup.addActor(apple);
					miniGameGroup.addActor(appleBeam);
					
					
					Timer t = new Timer();
					t.scheduleTask(new Timer.Task(){ 
						@Override
						public void run() {
							LabelStyle labelStyle = new LabelStyle();
							labelStyle.font = Game.getScoreFont();
							miniGameLabel = new Label("Shoot down the salaries!", labelStyle);
							miniGameLabel.setAlignment(Align.center);
							miniGameLabel.setPosition(Game.getWidth() / 2.0f - miniGameLabel.getWidth() / 2.0f, Game.getHeight() / 2.0f - ResHelper.LinearHeightValue(10.0f));
							miniGameGroup.addActor(miniGameLabel);
							
							miniGameStartReleasingSalaries = true;
						}
					}, 1.5f);					
				}
			}, FADE_TIME);
		}
	}

	// Called at the end of the animation
	public void frogDied(){
		if (score > 0){
			// Create score window
			
			// Root
			Table topDialog = new Table();
			Table medalsDialog = new Table();
			Table container = new Table();
			
			topDialog.setFillParent(true);
			topDialog.add(medalsDialog);
			medalsDialog.setBackground(Game.getSkin().getDrawable("misc/medal-window"));
			medalsDialog.add(container).pad(ResHelper.LinearWidthValue(8.0f));
			
			// Create score labels
			LabelStyle labelStyle = new LabelStyle();
			labelStyle.font = Game.getButtonFont();
			labelStyle.fontColor = new Color(0xecd814ff);
			
			LabelStyle scoreStyle = new LabelStyle();
			scoreStyle.font = Game.getButtonFont();
			scoreStyle.fontColor = Color.WHITE;
			
			Label scoreLabel = new Label("Score", labelStyle);
			Label highScoreLabel = new Label("High Score", labelStyle);
			
			Label scoreValue = new Label(Integer.toString(score), scoreStyle);
			Label highScoreValue = new Label(Integer.toString(Game.getPreferences().getInteger("highscore")), scoreStyle);
			
			// Add score labels
			container.add(scoreLabel).spaceRight(ResHelper.LinearWidthValue(50.0f)).left();
			container.add(highScoreLabel).left();
			container.row();
			container.add(scoreValue).left().spaceRight(ResHelper.LinearWidthValue(50.0f)).spaceBottom(ResHelper.LinearHeightValue(4.0f));
			container.add(highScoreValue).left().spaceBottom(ResHelper.LinearHeightValue(4.0f));
			
			container.row().colspan(2);
			
			// Create medals and container for them
			Table medalRow = new Table();			
			int medalCount = getMedalCount(score);
			int missCount = MEDALS_COUNT - medalCount;
			for (int i = 0; i < medalCount; i++){
				Image goldMedal = new Image(Game.getGoldMedal());
				goldMedal.setScale(ResHelper.LinearWidthValue(1.0f));
				medalRow.add(goldMedal).pad(ResHelper.LinearWidthValue(5.0f) +
													(goldMedal.getWidth() * goldMedal.getScaleX() - goldMedal.getWidth()) / 2.0f);
				goldMedal.setOrigin(goldMedal.getWidth() / 2.0f, goldMedal.getHeight() / 2.0f);
				goldMedal.addAction(sequence(rotateBy(360.0f * (i + 1), 0.30f * (i + 1)), new Action(){
					@Override
					public boolean act(float delta) {
						Game.getPlusOneSound().play();
						return true;
					}
				}));
				
			}
			for (int i = 0; i < missCount; i++){
				Image missMedal = new Image(Game.getMissMedal());
				missMedal.setScale(ResHelper.LinearWidthValue(1.0f));
				medalRow.add(missMedal).pad(ResHelper.LinearWidthValue(5.0f) + 
													(missMedal.getWidth() * missMedal.getScaleX() - missMedal.getWidth()) / 2.0f);
				missMedal.setOrigin(missMedal.getWidth() / 2.0f, missMedal.getHeight() / 2.0f);
			}
			
			// Add to root
			container.add(medalRow).center().spaceBottom(ResHelper.LinearHeightValue(8.0f));
			container.row().colspan(Game.getPlatformServices().isSharingAvailable() ? 1 : 2);
			
			// Create buttons
			TextButton newGame = ButtonFactory.MakeStandardButton("Again");
			newGame.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					Game.getSingleton().setScreen(new LevelScreen(false));		
				}
			});
			
			if (Game.getPlatformServices().isSharingAvailable()){
				TextButton shareButton = ButtonFactory.MakeStandardButton("Share");
				shareButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						Game.getPlatformServices().shareCurrentScreen();
						userInitiatedShare = true;
						
						if (!Game.getPlatformServices().willResumeAfterShare()){
							resume();
						}
					}
				});
				
				container.add(shareButton).left();
				container.add(newGame).right();
			}else{
				container.add(newGame).expandX().fillX();
			}
				
			if (Game.getPlatformServices().isSharingAvailable() && !Game.getPlatformServices().delayHint() && !Game.getPreferences().getBoolean("user_shared")){
				LabelStyle hintStyle = new LabelStyle();
				hintStyle.font = Game.getHintFont();
				hintStyle.fontColor = Color.WHITE;
				hintLabel = new Label("Psst! If you share\nI will tell you a secret...", hintStyle);
				hintLabel.setAlignment(Align.center);
				container.row().colspan(2);
				container.add(hintLabel).center().spaceTop(ResHelper.LinearHeightValue(8.0f));
			}
			
			// Add root to stage
			topDialog.moveBy(0, Game.getHeight());			
			stage.addActor(topDialog);
			
			topDialog.addAction(sequence(
									moveBy(0, -Game.getHeight(), 0.25f, Interpolation.circleOut),
									Actions.after(new Action(){
										@Override
										public boolean act(float delta) {
											topDialogDisplayed = true;
											return true;
										}
									})
								));
			
			
			// Beated high score?
			if (beatHighscore){
				Game.getNewRecordSound().play();
				
				// Flash around
				final float DELAY = 0.05f;
				final int REPEAT = 10;
				RepeatAction flashingA = repeat(REPEAT, sequence(alpha(0.1f), delay(DELAY), alpha(1.0f)));
				RepeatAction flashingB = repeat(REPEAT, sequence(alpha(0.1f), delay(DELAY), alpha(1.0f)));
				
				highScoreValue.addAction(flashingA);
				highScoreLabel.addAction(flashingB);
				
			}
		}else{
			Game.StartNewLevelScreen();
		}
	}
	
	private int getMedalCount(int score){
		int compare = BASE_LEVEL;
		for (int i = 0; i < MEDALS_COUNT; i++){
			if (score < compare) return i;
			compare *= LEVEL_MULTIPLIER;
		}
		
		return MEDALS_COUNT;
	}
	
	@Override
	public void pause() {
		super.pause();
		
		if (!mainMenu && !gameOver){
			paused = true;
		}
	}
	
	@Override
	public void resume() {
		super.resume();
		
		if (userInitiatedShare){
			// Assume the user completed
			
			// First time sharing?
			if (!Game.getPreferences().getBoolean("user_shared") && !Game.getPlatformServices().delayHint()){
				// Show hint
				if (hintLabel != null) hintLabel.remove();
				
				// Root
				final Table topDialog = new Table();
				Table hintDialog = new Table();
				Table container = new Table();
				
				topDialog.setFillParent(true);
				topDialog.add(hintDialog);
				hintDialog.setBackground(Game.getSkin().getDrawable("misc/medal-window"));
				hintDialog.add(container).pad(ResHelper.LinearWidthValue(8.0f));
				
				// Create score labels
				LabelStyle hintStyle = new LabelStyle();
				hintStyle.font = Game.getHintFont();
				hintStyle.fontColor = Color.WHITE;
				
				
				Label hintValue = new Label("This arrogance of theirs is nothing new,\n"+
											"For once they showed it at a less secret gate\n" + 
											"Which still is standing, in full view, unlocked.\n\n" + 
											"Above that gate " + Game.getPreferences().getInteger("secret_column") + " you read the deadly writing,\n" +
											"And already, from this side and down the slope,\n" + 
											"Passing through the circles without escort,\n\n" +
											"Comes one by whom the city will be opened.", hintStyle);
				hintValue.setAlignment(Align.center);
				
				// Add score labels
				container.add(hintValue).center();
				
				TextButton dismissButton = ButtonFactory.MakeStandardButton("Dismiss");
				dismissButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						topDialog.remove();
					}
				});
				
				container.row();
				container.add(dismissButton).spaceTop(ResHelper.LinearHeightValue(8.0f)).center();
				
				// Add root to stage
				stage.addActor(topDialog);
				
				Game.getPreferences().putBoolean("user_shared", true);
				Game.getPreferences().flush();
			}
		}
	}
	
	@Override
	public void hide() {
		super.hide();
		stage.dispose();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		for (int i = 0; i < obstacles.size; i++){
			obstacles.get(i).dispose();
		}
		
	}
}
