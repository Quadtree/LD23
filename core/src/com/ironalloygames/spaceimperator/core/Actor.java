package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Actor {
	static boolean[] claimedColGroups = new boolean[32768];
	Body body;
	short colGroup;

	void claimColGroup() {
		for (int i = 1; i < 32768; ++i) {
			if (!claimedColGroups[i]) {
				colGroup = (short) i;
				claimedColGroups[colGroup] = true;
				break;
			}
		}
	}

	void collidedWith(Actor other) {
	}

	public void created() {
	}

	public void destroyed() {
	}

	public boolean keep() {
		return true;
	}

	public void render(Surface target) {
	}

	void renderToMinimap(Vector2 upperLeft, Surface target) {
	}

	void takeDamage(float amount) {
	}

	public void update() {
	}
}
