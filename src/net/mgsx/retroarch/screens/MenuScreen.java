package net.mgsx.retroarch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.mgsx.retroarch.ui.MenuUI;

public class MenuScreen extends ScreenAdapter
{
	protected Stage stage;
	protected Viewport viewport;
	
	public MenuScreen() 
	{
		this.viewport = new ScreenViewport();
		stage = new Stage(viewport);
		Skin skin = new Skin(Gdx.files.internal("assets/skins/game-skin.json"));
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		root.add(new MenuUI(skin)).expand().center();
	}
	
	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
}
