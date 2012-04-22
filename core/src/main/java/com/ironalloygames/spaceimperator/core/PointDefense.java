package com.ironalloygames.spaceimperator.core;

import org.jbox2d.common.Vec2;

public class PointDefense extends Gun {

	Ship owner;
	
	int cooldown;
	
	public PointDefense(Vec2 offset, Ship owner) {
		super(offset);
		this.owner = owner;
	}

	@Override
	public void update() {
		if(cooldown <= 0)
		{
			Vec2 pos = getWorldPosition(owner);
			
			float bestDist = Float.MAX_VALUE;
			Actor target = null;
			
			for(Actor a : SpaceImperatorGame.s.actors)
			{
				if((a instanceof Ship || a instanceof Bolt || a instanceof Missile) && a != owner && a.keep() && a.colGroup != owner.colGroup)
				{
					float dist = a.body.getPosition().sub(pos).lengthSquared();
					
					if(dist < 10*10)
					{
						if(!(a instanceof Missile)) dist += 1000000;
						
						if(dist < bestDist)
						{
							bestDist = dist;
							target = a;
						}
					}
				}
			}
			
			if(target != null)
			{
				SpaceImperatorGame.s.actors.add(new Beam(pos, target.body.getPosition()));
				target.takeDamage(1);
				cooldown = 25;
			}
		} else {
			cooldown--;
		}
		
		super.update();
	}

}
