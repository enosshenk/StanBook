package com.shenko.stanbook;

import com.badlogic.gdx.Game;

public class StanBook extends Game
{
	public Page[] Pages = new Page[4];
	public int CurrentPage = 0;
	
	@Override
	public void create() {
		Pages[0] = new Page1();
		Pages[1] = new Page2();
		Pages[2] = new Page3();
		Pages[3] = new Page4();
		
		int i;
		for (i=0; i < Pages.length; i++)
		{
			Pages[i].SetGame(this);
		}
		
		setScreen(Pages[CurrentPage]);
	}
	
	public void NextPage()
	{
		CurrentPage += 1;
		setScreen(Pages[CurrentPage]);
	}
	
	public void PreviousPage(float inSpeed)
	{
		CurrentPage -= 1;
		setScreen(Pages[CurrentPage]);
		Pages[CurrentPage].ClosePage(inSpeed);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
