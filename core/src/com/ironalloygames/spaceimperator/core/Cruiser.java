package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;

public class Cruiser extends Ship {

	static Image mainGraphic;
	static Image thrustGraphic;

	public Cruiser(Vector2 pos) {
		super(pos);

		guns.add(new BoltCannon(new Vector2(-1, -1)));
		guns.add(new BoltCannon(new Vector2(1, -1)));

		guns.add(new BoltCannon(new Vector2(-1, 1)));
		guns.add(new BoltCannon(new Vector2(1, 1)));

		guns.add(new PointDefense(new Vector2(0.5f, 1.7f), this));
		guns.add(new PointDefense(new Vector2(-0.5f, 1.7f), this));

		guns.add(new PointDefense(new Vector2(0.5f, -1.7f), this));
		guns.add(new PointDefense(new Vector2(-0.5f, -1.7f), this));
	}

	@Override
	public Image getForwardThrustGraphic() {
		return thrustGraphic;
	}

	@Override
	public Image getGraphic() {
		graphicCheck();
		return mainGraphic;
	}

	@Override
	public Image getLeftThrustGraphic() {
		return null;
	}

	@Override
	public float getMaxHP() {
		return 100;
	}

	@Override
	public Image getRightThrustGraphic() {
		return null;
	}

	@Override
	Vector2 getSize() {
		return new Vector2(90.f / 16.f, 95.f / 16.f);
	}

	@Override
	public float getThrustPower() {
		return 140;
	}

	@Override
	public float getTurnPower() {
		return 11.0f;
	}

	@Override
	public float getUpgradeCost() {
		return 5000;
	}

	@Override
	public String getUpgradeText() {
		return "A fleet mainstay, the battleship has eight gun batteries,\n" +
				"plus six point defenses, giving it unrivaled firepower.\n" +
				"It does suffer from low maneuverability, but this is usually\n" +
				"compensated for by its huge amount of armor.";
	}

	void graphicCheck() {
		if (mainGraphic == null) {
			mainGraphic = PlayN.assets().getImage("images/cruiser.png");
			thrustGraphic = PlayN.assets().getImage("images/cruiser_fwd.png");
		}
	}

	@Override
	public boolean hasTurrets() {
		return true;
	}

	@Override
	public void upgrade() {
		replaced = true;

		Ship replacement = new Battleship(body.getPosition());

		SpaceImperatorGame.s.actors.add(replacement);
		SpaceImperatorGame.s.pc = replacement;

		mouse().setListener(replacement);
		keyboard().setListener(replacement);

		super.upgrade();
	}
}
