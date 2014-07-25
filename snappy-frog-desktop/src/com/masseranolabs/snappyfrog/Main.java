package com.masseranolabs.snappyfrog;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Snappy Frog";
		cfg.fullscreen = true;
		cfg.width = (int)width;
		cfg.height = (int)height;
		
		
		cfg.addIcon("icon32.png", FileType.Internal);
/*
		cfg.width = 960;
		cfg.height = 640;*/
		new LwjglApplication(new Game(new DesktopServices()), cfg);
	}
}
