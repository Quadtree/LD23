package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;

public class Explosion extends Actor {
	static Image graphic;
	Vector2 pos;

	float size;

	public Explosion(Vector2 pos, float size) {
		this.pos = new Vector2(pos);
		this.size = size;
	}

	@Override
	public boolean keep() {
		return size > 0.01f;
	}

	@Override
	public void render(Surface target) {
		target.save();
		target.translate(pos.x, pos.y);
		target.rotate(SpaceImperatorGame.s.rand.nextFloat() * (float) Math.PI * 2);

		if (graphic == null)
			graphic = PlayN.assets().getImage("images/shot_hit.png");

		target.drawImage(graphic, -size / 2, -size / 2, size, size);

		target.restore();
		super.render(target);
	}

	@Override
	public void update() {
		size *= 0.8f;
		super.update();
	}
}
