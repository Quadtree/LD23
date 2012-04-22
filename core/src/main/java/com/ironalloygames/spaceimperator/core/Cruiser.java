package com.ironalloygames.spaceimperator.core;

import static playn.core.PlayN.keyboard;
import static playn.core.PlayN.mouse;

import org.jbox2d.common.Vec2;

import playn.core.Image;
import playn.core.PlayN;

public class Cruiser extends Ship {
	
	static Image mainGraphic;
	static Image thrustGraphic;

	public Cruiser(Vec2 pos) {
		super(pos);
		
		guns.add(new BoltCannon(new Vec2(-1,-1)));
		guns.add(new BoltCannon(new Vec2(1,-1)));
		
		guns.add(new BoltCannon(new Vec2(-1,1)));
		guns.add(new BoltCannon(new Vec2(1,1)));
		
		guns.add(new PointDefense(new Vec2(0.5f,1.7f), this));
		guns.add(new PointDefense(new Vec2(-0.5f,1.7f), this));
		
		guns.add(new PointDefense(new Vec2(0.5f,-1.7f), this));
		guns.add(new PointDefense(new Vec2(-0.5f,-1.7f), this));
	}
	
	void graphicCheck(){
		if(mainGraphic == null)
		{
			mainGraphic = PlayN.assets().getImage("images/cruiser.png");
			thrustGraphic = PlayN.assets().getImage("images/cruiser_fwd.png");
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
		return 140;
	}

	@Override
	public float getTurnPower() {
		return 11.0f;
	}

	@Override
	Vec2 getSize() {
		return new Vec2(90.f / 16.f, 95.f / 16.f);
	}

	@Override
	public float getMaxHP() {
		return 100;
	}

	@Override
	public boolean hasTurrets() {
		return true;
	}

	@Override
	public String getUpgradeText()
	{
		return  "A fleet mainstay, the battleship has eight gun batteries,\n" +
				"plus six point defenses, giving it unrivaled firepower.\n" +
				"It does suffer from low maneuverability, but this is usually\n" +
				"compensated for by its huge amount of armor.";
	}
	
	@Override
	public float getUpgradeCost()
	{
		return 5000;
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
