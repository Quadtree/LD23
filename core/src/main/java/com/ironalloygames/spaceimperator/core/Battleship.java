package com.ironalloygames.spaceimperator.core;

import org.jbox2d.common.Vec2;

import playn.core.Image;
import playn.core.PlayN;

public class Battleship extends Ship {
	
	static Image mainGraphic;
	static Image thrustGraphic;

	public Battleship(Vec2 pos) {
		super(pos);
		
		guns.add(new BoltCannon(new Vec2(-3,-2)));
		guns.add(new BoltCannon(new Vec2(-1,-2)));
		guns.add(new BoltCannon(new Vec2(0.5f,-1f)));
		guns.add(new BoltCannon(new Vec2(3.2f,-1f)));
		
		guns.add(new BoltCannon(new Vec2(-3,2)));
		guns.add(new BoltCannon(new Vec2(-1,2)));
		guns.add(new BoltCannon(new Vec2(0.5f,1f)));
		guns.add(new BoltCannon(new Vec2(3.2f,1f)));
		
		guns.add(new PointDefense(new Vec2(2.3f,0.8f), this));
		guns.add(new PointDefense(new Vec2(0.0f,0.8f), this));
		guns.add(new PointDefense(new Vec2(-2.3f,0.8f), this));
		
		guns.add(new PointDefense(new Vec2(2.3f,-0.8f), this));
		guns.add(new PointDefense(new Vec2(0.0f,-0.8f), this));
		guns.add(new PointDefense(new Vec2(-2.3f,-0.8f), this));
	}
	
	void graphicCheck(){
		if(mainGraphic == null)
		{
			mainGraphic = PlayN.assets().getImage("images/battleship.png");
			thrustGraphic = PlayN.assets().getImage("images/battleship_fwd.png");
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
		return 120;
	}

	@Override
	public float getTurnPower() {
		return 6.0f;
	}

	@Override
	Vec2 getSize() {
		return new Vec2(145.f / 16.f, 80.f / 16.f);
	}

	@Override
	public float getMaxHP() {
		return 220;
	}

	@Override
	public boolean hasTurrets() {
		return true;
	}

}
