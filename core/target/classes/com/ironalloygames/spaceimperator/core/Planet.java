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
	PlanetSize type;
	
	public Planet()
	{
		int size = 0;
		
		while(SpaceImperatorGame.s.rand.nextInt(3) == 0) size++;
		
		if(size >= PlanetSize.values().length) size = PlanetSize.values().length - 1;
		
		init(new Vec2(SpaceImperatorGame.s.rand.nextFloat() * 5000, SpaceImperatorGame.s.rand.nextFloat() * 5000), PlanetSize.values()[size]);
	}
	
	public Planet(Vec2 pos, PlanetSize type)
	{
		init(pos,type);
	}
	
	void init(Vec2 pos, PlanetSize type)
	{
		this.type = type;
		CircleShape cs = new CircleShape();
		
		switch(type)
		{
			case Tiny: cs.m_radius = 10; break;
			case Small: cs.m_radius = 20; break;
			case Medium: cs.m_radius = 40; break;
			case Large: cs.m_radius = 80; break;
		}
		
		BodyDef bd = new BodyDef();
		
		bd.position.set(pos);
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
		
		Image graphic = planetGraphics.get(type);
		
		target.drawImage(graphic, -graphic.width() / 2.f / 16.f, -graphic.height() / 2.f / 16.f, graphic.width() / 16.f, graphic.height() / 16.f);
		
		target.restore();
		
		super.render(target);
	}

}
