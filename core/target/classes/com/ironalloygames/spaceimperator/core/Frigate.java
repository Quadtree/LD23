package com.ironalloygames.spaceimperator.core;

import org.jbox2d.common.Vec2;

import playn.core.Image;
import playn.core.PlayN;

public class Frigate extends Ship {
	
	static Image mainGraphic;
	static Image thrustGraphic;

	public Frigate(Vec2 pos) {
		super(pos);
		
		guns.add(new BoltCannon(new Vec2(-1.2f,-1)));
		guns.add(new BoltCannon(new Vec2(0.8f,-1)));
		
		guns.add(new BoltCannon(new Vec2(-1.2f,1)));
		guns.add(new BoltCannon(new Vec2(0.8f,1)));
	}
	
	void graphicCheck(){
		if(mainGraphic == null)
		{
			mainGraphic = PlayN.assets().getImage("images/frigate.png");
			thrustGraphic = PlayN.assets().getImage("images/frigate_fwd.png");
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
		return null;
	}

	@Override
	public Image getRightThrustGraphic() {
		return null;
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
	Vec2 getSize() {
		return new Vec2(80.f / 16.f, 60.f / 16.f);
	}

	@Override
	public float getMaxHP() {
		return 45;
	}

	@Override
	public boolean hasTurrets() {
		return true;
	}
	
	@Override
	public Class<? extends Ship> upgradesTo()
	{
		return Cruiser.class;
	}

	public static String getUpgradeText()
	{
		return  "The smallest \"capital\" ship, the frigate has\n" +
				"four gun batteries that can swivel to fire in\n" +
				"any direction. It does trade the ability to strafe\n" +
				"for this however.";
	}
	
	public static float getUpgradeCost()
	{
		return 500;
	}
}
