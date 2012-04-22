package com.ironalloygames.spaceimperator.core;

import org.jbox2d.common.Vec2;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.Surface;

public class Beam extends Actor {
	Vec2 pos;
	float angle;
	float length;
	float width;
	
	static Image graphic;
	
	public Beam(Vec2 source, Vec2 target)
	{
		Vec2 delta = target.sub(source);
		
		angle = (float)Math.atan2(delta.y, delta.x);
		length = delta.length();
		pos = target.add(source).mul(0.5f);
		
		width = 0.5f;
	}

	@Override
	public void render(Surface target) {
		target.save();
		target.translate(pos.x, pos.y);
		target.rotate(angle);
		
		if(graphic == null) graphic = PlayN.assets().getImage("images/beam.png");
		
		target.drawImage(graphic, -length / 2, -width / 2, length, width);
		
		target.restore();
		
		width *= 0.8f;
		
		super.render(target);
	}

	@Override
	public boolean keep() {
		return width > 0.01f;
	}
}
