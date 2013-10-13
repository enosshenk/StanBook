package com.shenko.stanbook;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Page2 extends Page {
	
	public void show()
	{
		this.PageTextureFile = Gdx.files.internal("data/default2.png");
		this.NextPageTextureFile = Gdx.files.internal("data/default3.png");
		
		this.HasMusic = true;
		this.PageMusicFile = Gdx.files.internal("data/music1.ogg");
		
		this.HasEntrySound = false;
		this.HasExitSound = false;
		
		super.show();
	}
}
