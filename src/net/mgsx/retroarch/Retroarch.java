package net.mgsx.retroarch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.retroarch.screens.MenuScreen;

public class Retroarch extends Game 
{
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 480;
		new LwjglApplication(new Retroarch(), config);
	}

	@Override
	public void create () {
		setScreen(new MenuScreen());
	}

}
