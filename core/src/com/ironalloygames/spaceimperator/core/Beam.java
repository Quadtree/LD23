package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;

public class Beam extends Actor {
	static Image graphic;
	float angle;
	float length;
	Vector2 pos;

	float width;

	public Beam(Vector2 source, Vector2 target) {
		Vector2 delta = target.sub(source);

		angle = (float) Math.atan2(delta.y, delta.x);
		length = delta.length();
		pos = target.add(source).mul(0.5f);

		width = 0.5f;
	}

	@Override
	public boolean keep() {
		return width > 0.01f;
	}

	@Override
	public void render(Surface target) {
		target.save();
		target.translate(pos.x, pos.y);
		target.rotate(angle);

		if (graphic == null)
			graphic = PlayN.assets().getImage("images/beam.png");

		target.drawImage(graphic, -length / 2, -width / 2, length, width);

		target.restore();

		width *= 0.8f;

		super.render(target);
	}
}
