package net.mgsx.retroarch.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import net.mgsx.retroarch.service.RetroArchRun;
import net.mgsx.retroarch.service.RetroArchService;

public class MenuUI extends Table
{
	private Image cover;
	private SelectBox<RetroArchRun> gameList;
	private Texture texture;
	public MenuUI(Skin skin) {
		super(skin);
		
		gameList = new SelectBox<RetroArchRun>(skin);
		
		add(gameList).row();
		
		gameList.setItems(RetroArchService.i().getHistory());
		
		TextButton btRun;
		add(btRun = new TextButton("Run", skin)).row();
		
		cover = new Image();
		cover.setScaling(Scaling.fit);
		add(cover);
		
		gameList.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
			}
		});
		
		btRun.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RetroArchService.i().run(gameList.getSelected());
			}
		});
		
		updateCover();
	}
	
	private void updateCover(){
		FileHandle picFile = RetroArchService.i().getPicture(gameList.getSelected());
		if(picFile != null){
			if(texture != null) texture.dispose();
			texture = new Texture(picFile);
			cover.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
		}
	}
	
}
