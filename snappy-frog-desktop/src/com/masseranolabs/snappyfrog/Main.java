package com.masseranolabs.snappyfrog;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "snappy-frog";
		cfg.width = 1024;
		cfg.height = 768;
/*
		cfg.width = 800;
		cfg.height = 500;*/
		new LwjglApplication(new Game(new DesktopServices()), cfg);
	}
}
