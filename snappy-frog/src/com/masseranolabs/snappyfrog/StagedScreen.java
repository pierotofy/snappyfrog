package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class StagedScreen implements Screen{
	protected Stage stage;
	
	public StagedScreen(){
		// Setup the stage
		stage = new Stage();
	}

	@Override
	public void render(float delta) {
	    stage.act(delta);	    
	    stage.draw();		
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {
		stage.dispose();	
	}

}
