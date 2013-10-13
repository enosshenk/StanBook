package com.shenko.stanbook;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class Page implements Screen {

	final int PAGE_HEIGHT = 512;
	final int PAGE_WIDTH = 1024;
	
	public StanBook Game;

	public FileHandle PageTextureFile, NextPageTextureFile;	
	public boolean HasMusic;
	public FileHandle PageMusicFile;	
	public boolean HasEntrySound;
	public FileHandle PageEntrySoundFile;	
	public boolean HasExitSound;
	public FileHandle PageExitSoundFile;
	
	private Vector2 Anchor1, Anchor2, Anchor3, Anchor4, PageCurl;		// Point anchors for masking and drawing the effects
	private float PageTurn;												// Float value for turn progression
	private float TurnSpeed;											// Speed for auto-turn
	private boolean TurningPage = false;
	private boolean OpeningPage = false;
	private boolean ClosingPage = false;
	
	private OrthographicCamera Cam;
	public Texture PageTexture, NextPageTexture, ShadowTexture, CurlTexture;
	private TextureRegion ShadowRegion, UnderShadowRegion, CurlRegion;
	private float[] ShadowVerts, UnderShadowVerts, CurlVerts;
	private Pixmap PagePixmap;
	private SpriteBatch Batch;
	
	@Override
	public void render(float delta) {
		// Update logic
		if (OpeningPage)
		{
			PageTurn += TurnSpeed;
			if (PageTurn >= 1)
			{
				PageTurn = 1;
				OpeningPage = false;
				TurningPage = false;
				Game.NextPage();
			}
		}
		if (ClosingPage)
		{
			PageTurn -= TurnSpeed;
			if (PageTurn <= 0)
			{
				PageTurn = 0;
				ClosingPage = false;
				TurningPage = false;
			}
		}	
		// Update point anchors
		SetAnchors();

		
		// Rendering
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        NextPageTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        PageTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        
        Batch.begin();
        
        // Draw next page
        if (PageTurn != 0)
        {
        	Batch.setColor(1,1,1,1);
        	Batch.draw(NextPageTexture, 0, 0);
        }
        
        if (PageTurn != 0 && PageTurn != 1)
        {
        	// Draw the under-shadow on the next page
        	Batch.draw(ShadowTexture, UnderShadowVerts, 0, UnderShadowVerts.length);
        }  
        
        // Draw the current page
        if (PageTurn != 1)
        {
        	Batch.draw(PageTexture, 0, 0);
        }
        
        if (PageTurn != 0 && PageTurn != 1)
        {
        	// Draw the under-curl shadow and page curl
        	Batch.draw(ShadowTexture, ShadowVerts, 0, ShadowVerts.length);
        	Batch.draw(CurlTexture, CurlVerts, 0, CurlVerts.length);
        }
        
        Batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		
		NextPageTexture = new Texture(NextPageTextureFile);
		
		Batch = new SpriteBatch();
		
		// Set initial anchor positions
		Anchor1 = new Vector2();
		Anchor1.x = PAGE_WIDTH;
		Anchor1.y = PAGE_HEIGHT;
		
		Anchor2 = new Vector2();
		Anchor2.x = PAGE_WIDTH;
		Anchor2.y = 0;
		
		Anchor3 = new Vector2();
		Anchor3.x = PAGE_WIDTH;
		Anchor3.y = PAGE_HEIGHT;
		
		Anchor4 = new Vector2();
		Anchor4.x = PAGE_WIDTH;
		Anchor4.y = 0;
		
		PageCurl = new Vector2();
		
		// Page is initially not turned at all
		PageTurn = 0f;
		TurnSpeed = 0f;
		
		// Load textures
		ShadowTexture = new Texture(Gdx.files.internal("data/shadow.tga"));
		CurlTexture = new Texture(Gdx.files.internal("data/curlgradient.png"));
		// Set up texture regions
		ShadowRegion = new TextureRegion(ShadowTexture, 0, 0, 256, 256);
		UnderShadowRegion = new TextureRegion(ShadowTexture, 0, 0, 256, 256);	
		CurlRegion = new TextureRegion(ShadowTexture, 300, 0, 512, 512);	
		
		// Get the initial page pixmap
		UpdatePage();
		
		Cam = new OrthographicCamera(6, 10);
		Cam.setToOrtho(false);
		Cam.update();

		Batch.setProjectionMatrix(Cam.combined);
		
		Gdx.input.setInputProcessor(new GestureDetector(new BookGesture()));
		
		render(0.1f);
	}
	
	public void SetAnchors()
	{
		if (PageTurn == 1)
		{
			Anchor1.x = PAGE_WIDTH;
			Anchor1.y = PAGE_HEIGHT;
			
			Anchor2.x = PAGE_WIDTH;
			Anchor2.y = 0;
			
			Anchor3.x = PAGE_WIDTH;
			Anchor3.y = PAGE_HEIGHT;
			
			Anchor4.x = PAGE_WIDTH;
			Anchor4.y = 0;			
		}
		else if (PageTurn == 0)
		{
			Anchor1.x = 0;
			Anchor1.y = PAGE_HEIGHT;
			
			Anchor2.x = 0;
			Anchor2.y = 0;
			
			Anchor3.x = PAGE_WIDTH / 2;
			Anchor3.y = PAGE_HEIGHT;
			
			Anchor4.x = PAGE_WIDTH;
			Anchor4.y = 0;				
		}
		else
		{
			Anchor3.x = PAGE_WIDTH;
			Anchor3.y = 0;
			
			Anchor4.y = PAGE_HEIGHT;
			Anchor4.x = PAGE_WIDTH + (PAGE_WIDTH / 2);
			
			Anchor1.y = 0;
			Anchor1.x = FLerp(PAGE_WIDTH, 0f, PageTurn);
			
			Anchor2.y = PAGE_HEIGHT;
			Anchor2.x = FLerp(Anchor4.x, 0f, PageTurn);
	
			PageCurl.x = FLerp(PAGE_WIDTH, 0f, PageTurn) - (PageTurn * 64);
			PageCurl.y = PAGE_HEIGHT - 400;
			
			ShadowVerts = GetShadowVerts(ShadowRegion, Color.WHITE);
			UnderShadowVerts = GetUnderShadowVerts(UnderShadowRegion, Color.WHITE);
			CurlVerts = GetCurlVerts(CurlRegion, Color.WHITE);
		}
		UpdatePage();
	}
	
	public void UpdatePage()
	{
		PagePixmap = new Pixmap(PageTextureFile);
		
		if (PageTurn != 1 && PageTurn != 0)
		{
			Blending blending = PagePixmap.getBlending();
			PagePixmap.setColor(0f,0f,0f,0f);
			PagePixmap.setBlending(blending.None);
			PagePixmap.fillTriangle(
					(int)Anchor1.x,
					(int)(PAGE_HEIGHT - Anchor1.y),
					(int)Anchor2.x,
					(int)(PAGE_HEIGHT - Anchor2.y),
					(int)Anchor3.x,
					(int)(PAGE_HEIGHT - Anchor3.y) );
			PagePixmap.fillTriangle(
					(int)Anchor2.x,
					(int)(PAGE_HEIGHT - Anchor2.y),
					(int)Anchor3.x,
					(int)(PAGE_HEIGHT - Anchor3.y),
					(int)Anchor4.x,
					(int)(PAGE_HEIGHT - Anchor4.y) );
			PagePixmap.setColor(0f,0f,0f,0.5f);
			PagePixmap.drawLine(
					(int)Anchor1.x,
					(int)(PAGE_HEIGHT - Anchor1.y),
					(int)Anchor2.x,
					(int)(PAGE_HEIGHT - Anchor2.y) );
			PagePixmap.setBlending(blending);
		}
		
		PageTexture = new Texture(PagePixmap);
		PagePixmap.dispose();		
	}
	
	public void InitPage()
	{
		PageTurn = 0f;
		SetAnchors();
		UpdatePage();
	}
	
	public void SetGame(StanBook inGame)
	{
		Game = inGame;
	}
	
	public float FLerp(float v0, float v1, float alpha)
	{
		return v0 + (v1 - v0) * alpha;
	}
	
	public void OpenPage(float Speed)
	{
		TurningPage = true;
		OpeningPage = true;
		ClosingPage = false;
		TurnSpeed = Speed;
	}
	
	public void ClosePage(float Speed)
	{
		PageTurn = 1;
		TurningPage = true;
		OpeningPage = false;
		ClosingPage = true;
		TurnSpeed = Speed;
	}	
	
	public float[] GetShadowVerts(TextureRegion region, Color color) 
	{
		 float c = color.toFloatBits();
		 float u = region.getU();
		 float v = region.getV();
		 float u2 = region.getU2();
		 float v2 = region.getV2();
		 return new float[] {
			 PageCurl.x - 10, PageCurl.y - 40, c, u, v,
			 Anchor1.x, Anchor1.y, c, u, v2,
			 Anchor2.x, Anchor2.y, c, u2, v,
			 PageCurl.x - 10, PageCurl.y - 40, c, u, v
		 	};
	}
	
	public float[] GetUnderShadowVerts(TextureRegion region, Color color) 
	{
		 float c = color.toFloatBits();
		 float u = region.getU();
		 float v = region.getV();
		 float u2 = region.getU2();
		 float v2 = region.getV2();
		 return new float[] {
			 Anchor2.x - 20, Anchor2.y, c, u, v2,
			 Anchor2.x, Anchor2.y, c, u, v,
			 Anchor1.x + 20, Anchor1.y, c, u2, v,
			 Anchor1.x - 20, Anchor1.y, c, u2, v2
		 	};
	}	

	public float[] GetCurlVerts(TextureRegion region, Color color) 
	{
		 float c = color.toFloatBits();
		 float u = region.getU();
		 float v = region.getV();
		 float u2 = region.getU2();
		 float v2 = region.getV2();
		 return new float[] {
			 PageCurl.x, PageCurl.y, c, u2, v2,
			 Anchor1.x + 1, Anchor1.y, c, u, v2,
			 Anchor2.x + 1, Anchor2.y, c, u, v,
			 PageCurl.x, PageCurl.y, c, u2, v2
		 	};
	}
	
	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
	}
	
	public class BookGesture implements GestureListener
	{

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (x < 128)
			{
				Game.PreviousPage(0.05f);
			}
			else if (x > 896)
			{
				OpenPage(0.05f);
			}
			return true;
		}

		@Override
		public boolean longPress(float x, float y) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			System.out.print(velocityX + "\n");
			if (Math.abs(velocityX) > Math.abs(velocityY))
			{
				if (velocityX < -1500)
				{
					OpenPage(velocityX / 20000);
				}
				else if (velocityX > 1500)
				{
					Game.PreviousPage(velocityX / 20000);
				}
			}
			return true;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			TurningPage = true;
			PageTurn = 1 - (x / (float)PAGE_WIDTH);
			return true;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			if (x < 256)
			{
				OpenPage(0.05f);
			}
			else if (x > 768)
			{
				Game.PreviousPage(0.05f);
			}
			else
			{
				TurningPage = false;
			}
			return true;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
				Vector2 pointer1, Vector2 pointer2) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

}
