package com.shenko.stanbook;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {

	final int PAGE_HEIGHT = 512;
	final int PAGE_WIDTH = 1024;
	
	private Vector2 Anchor1, Anchor2, Anchor3, Anchor4, PageCurl;
	private float PageTurn;
	private boolean TurningPage = false;
	
	private OrthographicCamera Cam;
	private Texture PageTexture, GridTexture, ShadowTexture, CurlTexture;
	private TextureRegion ShadowRegion, UnderShadowRegion, CurlRegion;
	private float[] ShadowVerts, UnderShadowVerts, CurlVerts;
	private Pixmap PagePixmap;
	private SpriteBatch Batch;
	
	@Override
	public void render(float delta) {	
		if (TurningPage)
		{
			SetAnchors();
		}
		
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        GridTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        PageTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        
        Batch.begin();
        
        // Draw next page
        Batch.draw(GridTexture, 0, 0);
        
        if (PageTurn != 0)
        {
        	// Draw the under-shadow on the next page
        	Batch.draw(ShadowTexture, UnderShadowVerts, 0, UnderShadowVerts.length);
        }  
        
        // Draw the current page
        Batch.draw(PageTexture, 0, 0);
        
        if (PageTurn != 0)
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
		
		GridTexture = new Texture(Gdx.files.internal("data/grid.png"));
		
		Batch = new SpriteBatch();
		
		// Set initial anchor positions
		Anchor1 = new Vector2();
		Anchor1.x = PAGE_WIDTH;
		Anchor1.y = PAGE_HEIGHT;
		
		Anchor2 = new Vector2();
		Anchor1.x = PAGE_WIDTH;
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
		
		// Load textures
		ShadowTexture = new Texture(Gdx.files.internal("data/shadow.tga"));
		CurlTexture = new Texture(Gdx.files.internal("data/curlgradient.png"));
		// Set up texture regions
		ShadowRegion = new TextureRegion(ShadowTexture, 0, 0, 256, 256);
		UnderShadowRegion = new TextureRegion(ShadowTexture, 0, 0, 256, 256);	
		CurlRegion = new TextureRegion(ShadowTexture, 300, 0, 512, 512);	
		
		UpdatePage();
		
		Cam = new OrthographicCamera(6, 10);
		Cam.setToOrtho(false);
		Cam.update();

		Batch.setProjectionMatrix(Cam.combined);
		
		BookInput StanInput = new BookInput();
		Gdx.input.setInputProcessor(StanInput);
		
		render(0.1f);
	}
	
	public void SetAnchors()
	{
		Anchor3.x = PAGE_WIDTH;
		Anchor3.y = 0;
		
		Anchor4.y = PAGE_HEIGHT;
		Anchor4.x = PAGE_WIDTH + (PAGE_WIDTH / 2);
		
		Anchor1.y = 0;
		Anchor1.x = FLerp(0, PAGE_WIDTH, PageTurn);
		
		Anchor2.y = PAGE_HEIGHT;
		Anchor2.x = FLerp(0, Anchor4.x, PageTurn);

		PageCurl.x = FLerp(0, PAGE_WIDTH, PageTurn) - (PageTurn * 64);
		PageCurl.y = PAGE_HEIGHT - 400;
		
		ShadowVerts = GetShadowVerts(ShadowRegion, Color.WHITE);
		UnderShadowVerts = GetUnderShadowVerts(UnderShadowRegion, Color.WHITE);
		CurlVerts = GetCurlVerts(CurlRegion, Color.WHITE);
		
		UpdatePage();
	}
	
	public void UpdatePage()
	{
		PagePixmap = new Pixmap(Gdx.files.internal("data/page1.png"));
		
		if (TurningPage)
		{
			Blending blending = PagePixmap.getBlending();
			PagePixmap.setColor(0f,0f,0f,0f);
			PagePixmap.setBlending(blending.None);
			PagePixmap.fillTriangle((int)Anchor1.x, (int)(PAGE_HEIGHT - Anchor1.y), (int)Anchor2.x, (int)(PAGE_HEIGHT - Anchor2.y), (int)Anchor3.x, (int)(PAGE_HEIGHT - Anchor3.y));
			PagePixmap.fillTriangle((int)Anchor2.x, (int)(PAGE_HEIGHT - Anchor2.y), (int)Anchor3.x, (int)(PAGE_HEIGHT - Anchor3.y), (int)Anchor4.x, (int)(PAGE_HEIGHT - Anchor4.y));
			PagePixmap.setColor(0f,0f,0f,0.5f);
			PagePixmap.drawLine((int)Anchor1.x, (int)(PAGE_HEIGHT - Anchor1.y), (int)Anchor2.x, (int)(PAGE_HEIGHT - Anchor2.y));
			PagePixmap.setBlending(blending);
		}
		
		PageTexture = new Texture(PagePixmap);
		PagePixmap.dispose();		
	}
	
	public float FLerp(float v0, float v1, float alpha)
	{
		return v0 + (v1 - v0) * alpha;
	}
	
	public float[] GetShadowVerts(TextureRegion region, Color color) 
	{
		 float w = region.getRegionWidth();
		 float h = region.getRegionHeight();
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
		 float w = region.getRegionWidth();
		 float h = region.getRegionHeight();
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
		 float w = region.getRegionWidth();
		 float h = region.getRegionHeight();
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
	
	public class BookInput implements InputProcessor
	{

		@Override
		public boolean keyDown(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer,
				int button) {
			TurningPage = true;
			return true;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			if (PageTurn != 0 || PageTurn != 1)
				TurningPage = false;
			return true;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			PageTurn = (float)screenX / (float)PAGE_WIDTH;
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

}
