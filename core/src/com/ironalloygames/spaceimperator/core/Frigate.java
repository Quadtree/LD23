package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;

public class Frigate extends Ship {

	static Image mainGraphic;
	static Image thrustGraphic;

	public Frigate(Vector2 pos) {
		super(pos);

		guns.add(new BoltCannon(new Vector2(-1.2f, -1)));
		guns.add(new BoltCannon(new Vector2(0.8f, -1)));

		guns.add(new BoltCannon(new Vector2(-1.2f, 1)));
		guns.add(new BoltCannon(new Vector2(0.8f, 1)));
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
		return 45;
	}

	@Override
	public Image getRightThrustGraphic() {
		return null;
	}

	@Override
	Vector2 getSize() {
		return new Vector2(80.f / 16.f, 60.f / 16.f);
	}

	@Override
	public float getThrustPower() {
		return 150;
	}

	@Override
	public float getTurnPower() {
		return 20.0f;
	}

	@Override
	public float getUpgradeCost() {
		return 2000;
	}

	@Override
	public String getUpgradeText() {
		return "The cruiser has all the firepower of the frigate,\n" +
				"plus four point defense beams, giving it excellent\n" +
				"protection. It is however slow and ungainly.";
	}

	void graphicCheck() {
		if (mainGraphic == null) {
			mainGraphic = PlayN.assets().getImage("images/frigate.png");
			thrustGraphic = PlayN.assets().getImage("images/frigate_fwd.png");
		}
	}

	@Override
	public boolean hasTurrets() {
		return true;
	}

	@Override
	public void upgrade() {
		replaced = true;

		Ship replacement = new Cruiser(body.getPosition());

		SpaceImperatorGame.s.actors.add(replacement);
		SpaceImperatorGame.s.pc = replacement;

		mouse().setListener(replacement);
		keyboard().setListener(replacement);

		super.upgrade();
	}
}
