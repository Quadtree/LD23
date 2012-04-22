package com.ironalloygames.spaceimperator.core;

import org.jbox2d.common.Vec2;

public class BoltCannon extends Gun {

	int recharge = 0;
	
	public BoltCannon(Vec2 offset) {
		super(offset);
	}

	@Override
	public void update() {
		if(recharge > 0) recharge--;
		super.update();
	}

	@Override
	public void fire(Ship firer) {
		if(recharge <= 0)
		{
			recharge = 20;
			
			SpaceImperatorGame.s.actors.add(new Bolt(getWorldPosition(firer), firer.firePoint, firer.body.getLinearVelocity(), firer.colGroup));
			
			System.out.println("FIRE!!!");
		}
		
		super.fire(firer);
	}

}
