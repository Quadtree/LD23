package com.ironalloygames.spaceimperator.core;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.Sound;
import playn.core.Surface;

public class Bolt extends Actor {
	
	static Image graphic;
	
	int life;
	
	public Bolt(Vec2 source, Vec2 target, Vec2 velocityBase, short colGroup, int life)
	{
		this.life = life;
		this.colGroup = colGroup;
		CircleShape cs = new CircleShape();
		
		cs.m_radius = 0.3f;
		
		BodyDef bd = new BodyDef();
		
		bd.position.set(source);
		bd.type = BodyType.DYNAMIC;
		bd.userData = this;
		
		body = SpaceImperatorGame.s.world.createBody(bd);
		
		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 1;
		fd.filter.groupIndex = -colGroup;
		
		body.createFixture(fd);
		
		Vec2 vel = target.sub(source);
		vel.normalize();
		
		body.setLinearVelocity(vel.mulLocal(400).add(velocityBase));
		SoundPlayer.play("sfx/bolt");
	}

	@Override
	public void render(Surface target) {
		if(graphic == null) graphic = PlayN.assets().getImage("images/bolt.png");
		
		target.save();
		
		target.translate(body.getPosition().x, body.getPosition().y);
		target.drawImage(graphic, -0.25f, -0.25f, 0.5f, 0.5f);
		
		target.restore();
		super.render(target);
	}

	@Override
	void collidedWith(Actor other) {
		other.takeDamage(1);
		life = 0;
		
		SoundPlayer.play("sfx/bolt_hit");
		
		super.collidedWith(other);
	}

	@Override
	public boolean keep() {
		return life > 0;
	}

	@Override
	public void destroyed() {
		SpaceImperatorGame.s.actors.add(new Explosion(body.getPosition(), 2));
		
		SpaceImperatorGame.s.world.destroyBody(body);
		body = null;
		
		super.destroyed();
	}

	@Override
	public void update() {
		life--;
		super.update();
	}
	
	@Override
	void takeDamage(float amount) {
		life = 0;
		super.takeDamage(amount);
	}
}
