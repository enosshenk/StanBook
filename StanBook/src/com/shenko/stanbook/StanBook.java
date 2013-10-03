package com.shenko.stanbook;

import com.badlogic.gdx.Game;

public class StanBook extends Game
{
	@Override
	public void create() {
		setScreen(new GameScreen());
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
