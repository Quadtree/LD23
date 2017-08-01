package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Fighter extends Ship {

	static Image mainGraphic;
	static Image strafeLeftGraphic;
	static Image strafeRightGraphic;
	static Image thrustGraphic;

	public Fighter(Vector2 pos) {
		super(pos);

		guns.add(new BoltCannon(new Vector2(1, 0)));
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
		return 7;
	}

	@Override
	public Image getRightThrustGraphic() {
		return strafeRightGraphic;
	}

	@Override
	Vector2 getSize() {
		return new Vector2(25.f / 16.f, 25.f / 16.f);
	}

	@Override
	public float getThrustPower() {
		return 20;
	}

	@Override
	public float getTurnPower() {
		return 25.0f;
	}

	@Override
	public float getUpgradeCost() {
		return 200;
	}

	@Override
	public String getUpgradeText() {
		return "The heavier cousin to the light fighter,\n" +
				"the heavy fighter features double the firepower\n" +
				"and increased armor. It does trade some agility\n" +
				"for this however.";
	}

	void graphicCheck() {
		if (mainGraphic == null) {
			mainGraphic = PlayN.assets().getImage("images/fighter.png");
			strafeLeftGraphic = PlayN.assets().getImage("images/fighter_sl.png");
			strafeRightGraphic = PlayN.assets().getImage("images/fighter_sr.png");
			thrustGraphic = PlayN.assets().getImage("images/fighter_fwd.png");
		}
	}

	@Override
	public void upgrade() {
		replaced = true;

		Ship replacement = new HeavyFighter(body.getPosition().cpy());

		SpaceImperatorGame.s.actors.add(replacement);
		SpaceImperatorGame.s.pc = replacement;

		Gdx.input.setInputProcessor(replacement);

		super.upgrade();
	}
}
