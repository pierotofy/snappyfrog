package com.masseranolabs.snappyfrog.client;

import com.masseranolabs.snappyfrog.Game;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(600, 400);
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener () {
		return new Game(new HtmlServices());
	}
}