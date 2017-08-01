package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;

public class BoltCannon extends Gun {

	int recharge = 0;

	public BoltCannon(Vector2 offset) {
		super(offset);
	}

	@Override
	public void fire(Ship firer) {
		if (recharge <= 0) {
			recharge = 10;

			SpaceImperatorGame.s.actors.add(new Bolt(getWorldPosition(firer), firer.firePoint, firer.body.getLinearVelocity(), firer.colGroup, 60));
		}

		super.fire(firer);
	}

	@Override
	public void update() {
		if (recharge > 0)
			recharge--;
		super.update();
	}

}
