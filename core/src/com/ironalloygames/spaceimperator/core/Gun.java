package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;

public abstract class Gun {
	Vector2 offset;

	public Gun(Vector2 offset) {
		this.offset = new Vector2(offset);
	}

	public void fire(Ship firer) {
	}

	protected Vector2 getWorldPosition(Ship on) {
		Vector2 pos = new Vector2(on.body.getPosition());

		float mat11 = (float) Math.cos(on.body.getAngle());
		float mat12 = (float) Math.sin(on.body.getAngle());
		float mat21 = -(float) Math.sin(on.body.getAngle());
		float mat22 = (float) Math.cos(on.body.getAngle());

		Vector2 temp = new Vector2();

		temp.x = (offset.x * mat11) + (offset.y * mat21);
		temp.y = (offset.x * mat12) + (offset.y * mat22);

		pos.add(temp);

		return pos;
	}

	public void update() {
	}
}
