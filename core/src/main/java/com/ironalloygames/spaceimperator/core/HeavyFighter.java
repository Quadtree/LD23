package com.ironalloygames.spaceimperator.core;

import org.jbox2d.common.Vec2;

import playn.core.Image;
import playn.core.PlayN;

public class HeavyFighter extends Ship {
	
	static Image mainGraphic;
	static Image strafeLeftGraphic;
	static Image strafeRightGraphic;
	static Image thrustGraphic;

	public HeavyFighter(Vec2 pos) {
		super(pos);
		
		guns.add(new BoltCannon(new Vec2(0.5f,1)));
		guns.add(new BoltCannon(new Vec2(0.5f,-1)));
	}
	
	void graphicCheck(){
		if(mainGraphic == null)
		{
			mainGraphic = PlayN.assets().getImage("images/heavy_fighter.png");
			strafeLeftGraphic = PlayN.assets().getImage("images/heavy_fighter_sl.png");
			strafeRightGraphic = PlayN.assets().getImage("images/heavy_fighter_sr.png");
			thrustGraphic = PlayN.assets().getImage("images/heavy_fighter_fwd.png");
		}
	}

	@Override
	public Image getGraphic() {
		graphicCheck();
		return mainGraphic;
	}

	@Override
	public Image getForwardThrustGraphic() {
		return thrustGraphic;
	}

	@Override
	public Image getLeftThrustGraphic() {
		return strafeLeftGraphic;
	}

	@Override
	public Image getRightThrustGraphic() {
		return strafeRightGraphic;
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
	Vec2 getSize() {
		return new Vec2(51.f / 16.f, 51.f / 16.f);
	}

	@Override
	public float getMaxHP() {
		return 15;
	}

	@Override
	public Class<? extends Ship> upgradesTo()
	{
		return Frigate.class;
	}
	
	public static String getUpgradeText()
	{
		return  "The heavier cousin to the light fighter,\n" +
				"the heavy fighter features double the firepower\n" +
				"and increased armor. It does trade some agility\n" +
				"for this however.";
	}
	
	public static float getUpgradeCost()
	{
		return 200;
	}
}
