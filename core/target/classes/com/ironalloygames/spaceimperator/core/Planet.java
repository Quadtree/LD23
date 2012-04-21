package com.ironalloygames.spaceimperator.core;

import java.util.EnumMap;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import playn.core.Image;
import playn.core.ImmediateLayer;
import playn.core.PlayN;
import playn.core.Surface;

public class Planet extends Actor {
	
	enum PlanetSize
	{
		Tiny,
		Small,
		Medium,
		Large
	}
	
	static EnumMap<PlanetSize, Image> planetGraphics;
	
	Body body;
	
	public Planet()
	{
		CircleShape cs = new CircleShape();
		
		cs.m_radius = 160;
		
		BodyDef bd = new BodyDef();
		
		bd.position.set(new Vec2(SpaceImperatorGame.s.rand.nextFloat() * 50, SpaceImperatorGame.s.rand.nextFloat() * 50));
		bd.type = BodyType.STATIC;
		bd.userData = this;
		
		body = SpaceImperatorGame.s.world.createBody(bd);
		
		body.createFixture(cs, 0);
	}

	@Override
	public void render(Surface target) {
		
		if(planetGraphics == null)
		{
			planetGraphics = new EnumMap<PlanetSize, Image>(PlanetSize.class);
			
			planetGraphics.put(PlanetSize.Tiny, PlayN.assets().getImage("images/planet_tiny.png"));
		}
		
		target.save();
		
		target.translate(body.getPosition().x, body.getPosition().y);
		target.drawImageCentered(planetGraphics.get(PlanetSize.Tiny), 0, 0);
		
		target.restore();
		
		super.render(target);
	}

}
