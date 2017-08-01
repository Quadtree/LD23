package com.ironalloygames.spaceimperator.core;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.Surface;

public class DropPod extends Actor {
	
	static Image graphic;
	
	int life;
	
	boolean playerOwned;
	
	public DropPod(Vec2 source, Vec2 target, Vec2 velocityBase, short colGroup, int life, boolean playerOwned)
	{
		this.playerOwned = playerOwned;
		this.life = life;
		this.colGroup = colGroup;
		System.out.println(colGroup);
		CircleShape cs = new CircleShape();
		
		cs.m_radius = 1.f;
		
		BodyDef bd = new BodyDef();
		
		bd.position.set(source);
		bd.type = BodyType.DYNAMIC;
		bd.userData = this;
		bd.angle = (float)Math.atan2(target.sub(source).y, target.sub(source).x);
		
		body = SpaceImperatorGame.s.world.createBody(bd);
		
		claimColGroup();
		
		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 1;
		fd.filter.groupIndex = -colGroup;
		
		body.createFixture(fd);
		
		Vec2 vel = target.sub(source);
		vel.normalize();
		
		body.setLinearVelocity(velocityBase);
	}

	@Override
	public void render(Surface target) {
		if(graphic == null) graphic = PlayN.assets().getImage("images/drop_pod.png");
		
		target.save();
		
		target.translate(body.getPosition().x, body.getPosition().y);
		target.rotate(body.getAngle() + (float)Math.PI / 2);
		target.drawImage(graphic, -25 / 16.f / 2.f, -25 / 16.f / 2.f, 25 / 16.f, 25 / 16.f);
		
		target.restore();
		super.render(target);
	}

	@Override
	void collidedWith(Actor other) {
		if(other instanceof Planet && ((Planet)other).ownedByPlayer != playerOwned)
		{
			SoundPlayer.play("sfx/pod_hit");
			((Planet)other).dropAttack(playerOwned);
			life = 0;
		}
		super.collidedWith(other);
	}

	@Override
	public boolean keep() {
		return life > 0;
	}

	@Override
	public void destroyed() {
		SpaceImperatorGame.s.world.destroyBody(body);
		body = null;
		super.destroyed();
	}

	@Override
	public void update() {
		life--;
		super.update();
	}
}
