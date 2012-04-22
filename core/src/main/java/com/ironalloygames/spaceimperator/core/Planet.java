package com.ironalloygames.spaceimperator.core;

import java.util.EnumMap;
import java.util.Random;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

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
	
	static EnumMap<PlanetSize, Image> mmAlliedGraphics;
	static EnumMap<PlanetSize, Image> mmEnemyGraphics;
	
	static Image popGraphic;
	static Image enemyDefense;
	static Image alliedDefense;
	
	PlanetSize type;
	
	boolean ownedByPlayer;
	
	float population;
	float defenses;
	
	int nextDefense = 0;
	int defCooldown = 0;
	
	public Planet(PlanetSize type)
	{
		//int size = 0;
		
		//while(SpaceImperatorGame.s.rand.nextInt(3) == 0) size++;
		
		//if(size >= PlanetSize.values().length) size = PlanetSize.values().length - 1;
		
		init(new Vec2(SpaceImperatorGame.s.rand.nextFloat() * SpaceImperatorGame.WORLD_WIDTH, SpaceImperatorGame.s.rand.nextFloat() * SpaceImperatorGame.WORLD_HEIGHT), type);
	}
	
	public Planet(Vec2 pos, PlanetSize type, boolean ownedByPlayer)
	{
		init(pos,type);
		this.ownedByPlayer = ownedByPlayer;
	}
	
	float getPopCap()
	{
		switch(type)
		{
			case Tiny: return 5;
			case Small: return 10;
			case Medium: return 20;
			case Large: return 40;
		}
		
		return 0;
	}
	
	float getRadius()
	{
		switch(type)
		{
			case Tiny: return 5;
			case Small: return 10;
			case Medium: return 20;
			case Large: return 40;
		}
		
		return 0;
	}
	
	void init(Vec2 pos, PlanetSize type)
	{
		this.type = type;
		CircleShape cs = new CircleShape();
		
		cs.m_radius = getRadius();
		
		BodyDef bd = new BodyDef();
		
		bd.position.set(pos);
		bd.type = BodyType.STATIC;
		bd.userData = this;
		
		body = SpaceImperatorGame.s.world.createBody(bd);
		
		claimColGroup();
		
		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 0;
		fd.filter.groupIndex = -colGroup;
		
		body.createFixture(fd);
		
		population = getPopCap();
		defenses = population / 2;
	}

	@Override
	public void render(Surface target) {
		
		if(planetGraphics == null)
		{
			planetGraphics = new EnumMap<PlanetSize, Image>(PlanetSize.class);
			mmAlliedGraphics = new EnumMap<PlanetSize, Image>(PlanetSize.class);
			mmEnemyGraphics = new EnumMap<PlanetSize, Image>(PlanetSize.class);
			
			planetGraphics.put(PlanetSize.Tiny, PlayN.assets().getImage("images/planet_tiny.png"));
			planetGraphics.put(PlanetSize.Small, PlayN.assets().getImage("images/planet_small.png"));
			planetGraphics.put(PlanetSize.Medium, PlayN.assets().getImage("images/planet_medium.png"));
			planetGraphics.put(PlanetSize.Large, PlayN.assets().getImage("images/planet_large.png"));
			
			mmAlliedGraphics.put(PlanetSize.Tiny, PlayN.assets().getImage("images/mmi_planet_tiny_allied.png"));
			mmAlliedGraphics.put(PlanetSize.Small, PlayN.assets().getImage("images/mmi_planet_small_allied.png"));
			mmAlliedGraphics.put(PlanetSize.Medium, PlayN.assets().getImage("images/mmi_planet_medium_allied.png"));
			mmAlliedGraphics.put(PlanetSize.Large, PlayN.assets().getImage("images/mmi_planet_large_allied.png"));
			
			mmEnemyGraphics.put(PlanetSize.Tiny, PlayN.assets().getImage("images/mmi_planet_tiny_enemy.png"));
			mmEnemyGraphics.put(PlanetSize.Small, PlayN.assets().getImage("images/mmi_planet_small_enemy.png"));
			mmEnemyGraphics.put(PlanetSize.Medium, PlayN.assets().getImage("images/mmi_planet_medium_enemy.png"));
			mmEnemyGraphics.put(PlanetSize.Large, PlayN.assets().getImage("images/mmi_planet_large_enemy.png"));
			
			popGraphic = PlayN.assets().getImage("images/pop.png");
			enemyDefense = PlayN.assets().getImage("images/defense_enemy.png");
			alliedDefense = PlayN.assets().getImage("images/defense.png");
		}
		
		target.save();
		
		target.translate(body.getPosition().x, body.getPosition().y);
		
		Image graphic = planetGraphics.get(type);
		
		target.drawImage(graphic, -graphic.width() / 2.f / 16.f, -graphic.height() / 2.f / 16.f, graphic.width() / 16.f, graphic.height() / 16.f);
		
		target.restore();
		
		Random popPosRand = new Random((int)body.getPosition().x);
		
		for(int i=0;i<(int)population;++i)
		{
			target.save();
			
			float angle = popPosRand.nextFloat() * (float)Math.PI * 2;
			float dist = popPosRand.nextFloat() * (getRadius() - 2);
			
			target.translate(body.getPosition().x + (float)Math.cos(angle) * dist, body.getPosition().y + (float)Math.sin(angle) * dist);
			
			target.drawImage(popGraphic, -popGraphic.width() / 2.f / 16.f, -popGraphic.height() / 2.f / 16.f, popGraphic.width() / 16.f, popGraphic.height() / 16.f);
			
			target.restore();
		}
		
		Random defPosRand = new Random((int)body.getPosition().x + 3000);
		
		for(int i=0;i<(int)defenses;++i)
		{
			target.save();
			
			float angle = defPosRand.nextFloat() * (float)Math.PI * 2;
			float dist = defPosRand.nextFloat() * (getRadius() - 2);
			
			target.translate(body.getPosition().x + (float)Math.cos(angle) * dist, body.getPosition().y + (float)Math.sin(angle) * dist);
			
			Image img;
			
			if(ownedByPlayer)
				img = alliedDefense;
			else
				img = enemyDefense;
			
			target.drawImage(img, -img.width() / 2.f / 16.f, -img.height() / 2.f / 16.f, img.width() / 16.f, img.height() / 16.f);
			
			target.restore();
		}
		
		super.render(target);
	}

	@Override
	public void update() {
		Random defPosRand = new Random((int)body.getPosition().x + 3000);
		
		if(defCooldown > 0)
		{
			for(int i=0;i<(int)defenses;++i)
			{
				float angle = defPosRand.nextFloat() * (float)Math.PI * 2;
				float dist = defPosRand.nextFloat() * (getRadius() - 2);
				
				if(i != nextDefense) continue;
				
				Vec2 pos = new Vec2(body.getPosition().x + (float)Math.cos(angle) * dist, body.getPosition().y + (float)Math.sin(angle) * dist);
				
				Ship target = null;
				
				if(ownedByPlayer)
				{
					float bestDist = Float.MAX_VALUE;
					
					for(Actor a : SpaceImperatorGame.s.actors)
					{
						if(a instanceof Ship && a != SpaceImperatorGame.s.pc)
						{
							float curDist = pos.sub(a.body.getPosition()).lengthSquared();
							
							if(curDist < bestDist)
							{
								bestDist = curDist;
								target = (Ship) a;
							}
						}
					}
				} else {
					target = SpaceImperatorGame.s.pc;
				}
				
				if(target != null && pos.sub(target.body.getPosition()).length() < 150)
				{
					SpaceImperatorGame.s.actors.add(new Bolt(pos, target.body.getPosition().add(new Vec2(SpaceImperatorGame.s.rand.nextFloat() * 6 - 3, SpaceImperatorGame.s.rand.nextFloat() * 6 - 3)), new Vec2(), colGroup, 120));
					defCooldown -= 15;
					nextDefense++;
					
					if(defCooldown <= 0)
						break;
				}
			}
		} else {
			defCooldown += defenses;
		}
		
		nextDefense = nextDefense % Math.max((int)defenses, 1);
		
		population *= 1.00019;
		
		defenses += population / 2 / 60;
		
		if(population > getPopCap()) population = getPopCap();
		if(defenses > population / 2) defenses = population / 2;
		
		super.update();
	}

	@Override
	void takeDamage(float amount) {
		population = Math.max(population - 0.15f, 0);
		defenses = Math.max(defenses - 0.15f, 0);
		super.takeDamage(amount);
	}

	void dropAttack(boolean playerOwned)
	{
		if((int)defenses <= 0)
		{
			ownedByPlayer = playerOwned;
			defenses = 1.01f;
		} else {
			defenses -= 1;
		}
	}

	@Override
	void renderToMinimap(Vec2 upperLeft, Surface target) {
		
		Image img;
		
		if(ownedByPlayer)
			img = mmAlliedGraphics.get(type);
		else
			img = mmEnemyGraphics.get(type);
		
		target.drawImage(img, upperLeft.x + body.getPosition().x / SpaceImperatorGame.WORLD_WIDTH * 234 - 12.5f, upperLeft.y + body.getPosition().y / SpaceImperatorGame.WORLD_HEIGHT * 234 - 12.5f, 25, 25);
		
		super.renderToMinimap(upperLeft, target);
	}
}
