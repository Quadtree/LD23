package com.ironalloygames.spaceimperator.core;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Surface {

	SpriteBatch batch;

	BitmapFont mainFont;

	boolean needToRestartBatch = false;

	List<Matrix4> transformStack = new ArrayList<Matrix4>();

	public Surface() {
		batch = new SpriteBatch();
		mainFont = new BitmapFont();
	}

	public void begin() {
		setIdentityTransform();
		batch.begin();
	}

	private void beginBatchIfStopped() {
		if (needToRestartBatch) {
			batch.begin();
			needToRestartBatch = false;
		}
	}

	public void drawImage(Image img, float x, float y) {
		drawImage(img, x, y, img.width(), img.height());
	}

	public void drawImage(Image img, float x, float y, float w, float h) {
		batch.draw(img.sprite, x, y, w, h);
	}

	public void drawText(String string, float x, float y) {
		mainFont.draw(batch, string, x, y);
	}

	public void end() {
		batch.end();
	}

	private void endBatchIfStarted() {
		if (batch.isDrawing()) {
			batch.end();
			needToRestartBatch = true;
		}
	}

	public void restore() {
		endBatchIfStarted();
		batch.setTransformMatrix(transformStack.get(transformStack.size() - 1));
		transformStack.remove(transformStack.size() - 1);
		beginBatchIfStopped();
	}

	public void rotate(float rot) {
		endBatchIfStarted();
		batch.getTransformMatrix().rotate(new Vector3(0, 0, 1), rot * MathUtils.radiansToDegrees);
		beginBatchIfStopped();
	}

	public void save() {
		transformStack.add(batch.getTransformMatrix().cpy());
	}

	public void scale(float x, float y) {
		endBatchIfStarted();
		batch.getTransformMatrix().scale(x, y, 1);
		beginBatchIfStopped();
	}

	public void setIdentityTransform() {
		endBatchIfStarted();
		batch.getTransformMatrix().idt();
		beginBatchIfStopped();
	}

	public void translate(float x, float y) {
		endBatchIfStarted();
		batch.getTransformMatrix().translate(x, y, 0);
		beginBatchIfStopped();
	}

}
