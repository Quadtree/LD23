package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;

public class PointDefense extends Gun {

	int cooldown;

	Ship owner;

	public PointDefense(Vector2 offset, Ship owner) {
		super(offset);
		this.owner = owner;
	}

	@Override
	public void update() {
		if (cooldown <= 0) {
			Vector2 pos = getWorldPosition(owner);

			float bestDist = Float.MAX_VALUE;
			Actor target = null;

			for (Actor a : SpaceImperatorGame.s.actors) {
				if ((a instanceof Ship || a instanceof Bolt || a instanceof Missile) && a != owner && a.keep() && a.colGroup != owner.colGroup) {
					float dist = a.body.getPosition().sub(pos).lengthSquared();

					if (dist < 10 * 10) {
						if (!(a instanceof Missile))
							dist += 1000000;

						if (dist < bestDist) {
							bestDist = dist;
							target = a;
						}
					}
				}
			}

			if (target != null) {
				SoundPlayer.play("sfx/point_defense");
				SpaceImperatorGame.s.actors.add(new Beam(pos, target.body.getPosition()));
				target.takeDamage(1);
				cooldown = 25;
			}
		} else {
			cooldown--;
		}

		super.update();
	}

}
