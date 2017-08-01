package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class HeavyFighter extends Ship {

	static Image mainGraphic;
	static Image strafeLeftGraphic;
	static Image strafeRightGraphic;
	static Image thrustGraphic;

	public HeavyFighter(Vector2 pos) {
		super(pos);

		guns.add(new BoltCannon(new Vector2(0.5f, 1)));
		guns.add(new BoltCannon(new Vector2(0.5f, -1)));
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
		return strafeLeftGraphic;
	}

	@Override
	public float getMaxHP() {
		return 15;
	}

	@Override
	public Image getRightThrustGraphic() {
		return strafeRightGraphic;
	}

	@Override
	Vector2 getSize() {
		return new Vector2(51.f / 16.f, 51.f / 16.f);
	}

	@Override
	public float getThrustPower() {
		return 30;
	}

	@Override
	public float getTurnPower() {
		return 22.0f;
	}

	@Override
	public float getUpgradeCost() {
		return 500;
	}

	@Override
	public String getUpgradeText() {
		return "The smallest \"capital\" ship, the frigate has\n" +
				"four gun batteries that can swivel to fire in\n" +
				"any direction. It does trade the ability to strafe\n" +
				"for this however.";
	}

	void graphicCheck() {
		if (mainGraphic == null) {
			mainGraphic = PlayN.assets().getImage("images/heavy_fighter.png");
			strafeLeftGraphic = PlayN.assets().getImage("images/heavy_fighter_sl.png");
			strafeRightGraphic = PlayN.assets().getImage("images/heavy_fighter_sr.png");
			thrustGraphic = PlayN.assets().getImage("images/heavy_fighter_fwd.png");
		}
	}

	@Override
	public void upgrade() {
		replaced = true;

		Ship replacement = new Frigate(body.getPosition());

		SpaceImperatorGame.s.actors.add(replacement);
		SpaceImperatorGame.s.pc = replacement;

		Gdx.input.setInputProcessor(replacement);

		super.upgrade();
	}
}
