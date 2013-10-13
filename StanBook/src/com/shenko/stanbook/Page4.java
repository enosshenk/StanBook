package com.shenko.stanbook;

import com.badlogic.gdx.Gdx;

public class Page4 extends Page {

	public void show()
	{
		this.PageTextureFile = Gdx.files.internal("data/default4.png");
		
		this.HasMusic = true;
		this.PageMusicFile = Gdx.files.internal("data/music2.ogg");
		
		this.HasEntrySound = false;
		this.HasExitSound = false;
		
		super.show();
	}
}
