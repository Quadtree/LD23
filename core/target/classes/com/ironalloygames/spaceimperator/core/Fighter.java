package com.ironalloygames.spaceimperator.core;

import org.jbox2d.common.Vec2;

import playn.core.Image;
import playn.core.PlayN;

public class Fighter extends Ship {
	
	static Image mainGraphic;

	public Fighter(Vec2 pos) {
		super(pos);
		
		guns.add(new BoltCannon(new Vec2(1,0)));
	}
	
	void graphicCheck(){
		if(mainGraphic == null)
		{
			mainGraphic = PlayN.assets().getImage("images/fighter.png");
		}
	}

	@Override
	public Image getGraphic() {
		graphicCheck();
		return mainGraphic;
	}

	@Override
	public Image getForwardThrustGraphic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getLeftThrustGraphic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getRightThrustGraphic() {
		// TODO Auto-generated method stub
		return null;
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
	Vec2 getSize() {
		return new Vec2(25.f / 16.f, 25.f / 16.f);
	}

}
