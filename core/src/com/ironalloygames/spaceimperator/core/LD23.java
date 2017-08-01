package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LD23 extends ApplicationAdapter {
	SpriteBatch batch;
	long doneMs = 0;

	SpaceImperatorGame game;

	Texture img;

	Surface surf;

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		surf = new Surface();
		game = new SpaceImperatorGame();
		game.init();

		doneMs = System.currentTimeMillis();
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}

	@Override
	public void render() {

		while (System.currentTimeMillis() > doneMs) {
			game.update(0.016f);
			doneMs += 16;
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		surf.begin();

		game.render(surf);

		surf.end();
	}
}
