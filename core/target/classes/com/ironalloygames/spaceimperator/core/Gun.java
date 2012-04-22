package com.ironalloygames.spaceimperator.core;

import org.jbox2d.common.Vec2;

public abstract class Gun {
	Vec2 offset;
	
	public Gun(Vec2 offset)
	{
		this.offset = new Vec2(offset);
	}
	
	public void update(){}
	public void fire(Ship firer){}
	
	protected Vec2 getWorldPosition(Ship on)
	{
		Vec2 pos = on.body.getPosition();
		
		float mat11 = (float)Math.cos(on.body.getAngle());
		float mat12 = (float)Math.sin(on.body.getAngle());
		float mat21 = -(float)Math.sin(on.body.getAngle());
		float mat22 = (float)Math.cos(on.body.getAngle());
		
		Vec2 temp = new Vec2(offset);
		
		temp.x = (temp.x * mat11) + (temp.y * mat21);
		temp.y = (temp.x * mat12) + (temp.y * mat22);
		
		pos.addLocal(temp);
		
		return pos;
	}
}
