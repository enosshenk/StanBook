package com.shenko.stanbook;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;

public class GameScreen implements Screen {

	final float PAGE_HEIGHT = 1000;
	final float PAGE_WIDTH = 580;
	
	OrthographicCamera Cam;
	Mesh Page;
	Texture PageTexture;
	MeshBuilder Builder;
	
	@Override
	public void render(float delta) {		
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
        
        PageTexture.bind();
        Page.render(GL10.GL_TRIANGLE_STRIP);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {		
		Builder = new MeshBuilder();
		Builder.begin(new VertexAttributes(new VertexAttribute(Usage.Position, 3, "a_pos"), new VertexAttribute(Usage.TextureCoordinates, 2, "a_tex")), GL10.GL_TRIANGLES);
		Builder.patch(
				0, 0, 0,
				600, 0, 0,
				600, 900, 0,
				0, 900, 0,
				300, 500, 1,
				6, 10 );
		Page = Builder.end();
		
		PageTexture = new Texture(Gdx.files.internal("data/page2.png"));
		
		Cam = new OrthographicCamera(6, 10);
		Cam.setToOrtho(true);
		Cam.update();
		Cam.apply(Gdx.graphics.getGL10());
		
		render(0.1f);
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

}
