package com.shenko.stanbook;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Page3 extends Page {

	public void show()
	{
		this.PageTextureFile = Gdx.files.internal("data/default3.png");
		this.NextPageTextureFile = Gdx.files.internal("data/default4.png");
		
		this.HasMusic = false;
		this.HasEntrySound = false;
		this.HasExitSound = false;
		
		super.show();
	}
}
