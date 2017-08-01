package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Image {
	public final Sprite sprite;

	public Image(Sprite sprite) {
		super();
		this.sprite = sprite;
	}

	public float height() {
		return sprite.getHeight();
	}

	public float width() {
		return sprite.getWidth();
	}
}
