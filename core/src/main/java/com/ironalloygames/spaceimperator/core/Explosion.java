package com.ironalloygames.spaceimperator.core;

import org.jbox2d.common.Vec2;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.Surface;

public class Explosion extends Actor{
	Vec2 pos;
	float size;
	
	static Image graphic;
	
	public Explosion(Vec2 pos, float size)
	{
		this.pos = new Vec2(pos);
		this.size = size;
	}

	@Override
	public void update() {
		size *= 0.8f;
		super.update();
	}

	@Override
	public void render(Surface target) {
		target.save();
		target.translate(pos.x, pos.y);
		target.rotate(SpaceImperatorGame.s.rand.nextFloat() * (float)Math.PI * 2);
		
		if(graphic == null) graphic = PlayN.assets().getImage("images/shot_hit.png");
		
		target.drawImage(graphic, -size / 2, -size / 2, size, size);
		
		target.restore();
		super.render(target);
	}
}
