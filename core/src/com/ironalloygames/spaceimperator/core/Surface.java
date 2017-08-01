package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Surface {

	SpriteBatch batch;

	public Surface() {
		batch = new SpriteBatch();
	}

	public void drawImage(Image img, float x, float y) {
		drawImage(img, x, y, img.width(), img.height());
	}

	public void drawImage(Image img, float x, float y, float w, float h) {
		batch.draw(img.sprite, x, y, w, h);
	}

	public void drawText(String string, float x, float y) {
		// TODO Auto-generated method stub

	}

	public void restore() {
		// TODO Auto-generated method stub

	}

	public void rotate(float dustFacing) {
		// TODO Auto-generated method stub

	}

	public void save() {
		// TODO Auto-generated method stub

	}

	public void scale(int i, int j) {
		// TODO Auto-generated method stub

	}

	public void setTransform(int i, int j, int k, int l, int m, int n) {
		// TODO Auto-generated method stub

	}

	public void translate(float x, float y) {
		// TODO Auto-generated method stub

	}

	public void translate(int i, int j) {
		// TODO Auto-generated method stub

	}

}
