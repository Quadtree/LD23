package com.ironalloygames.spaceimperator.core;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.Surface;

public class Missile extends Actor {

	static Image graphic;
	
	int life;
	
	Ship target;
	
	public Missile(Vec2 source, Ship target, Vec2 velocityBase, short colGroup)
	{
		this.target = target;
		this.life = 240;
		this.colGroup = colGroup;
		System.out.println(colGroup);
		CircleShape cs = new CircleShape();
		
		cs.m_radius = 0.3f;
		
		BodyDef bd = new BodyDef();
		
		bd.position.set(source);
		bd.type = BodyType.DYNAMIC;
		bd.userData = this;
		
		body = SpaceImperatorGame.s.world.createBody(bd);
		
		claimColGroup();
		
		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 1;
		fd.filter.groupIndex = -colGroup;
		
		body.createFixture(fd);
		
		body.setLinearVelocity(velocityBase);
	}

	@Override
	public void render(Surface target) {
		if(graphic == null) graphic = PlayN.assets().getImage("images/missile.png");
		
		target.save();
		
		target.translate(body.getPosition().x, body.getPosition().y);
		target.drawImage(graphic, -0.25f, -0.25f, 0.5f, 0.5f);
		
		target.restore();
		super.render(target);
	}

	@Override
	void collidedWith(Actor other) {
		other.takeDamage(2);
		life = 0;
		super.collidedWith(other);
	}

	@Override
	public boolean keep() {
		return life > 0;
	}

	@Override
	public void destroyed() {
		SpaceImperatorGame.s.actors.add(new Explosion(body.getPosition(), 4));
		
		SpaceImperatorGame.s.world.destroyBody(body);
		body = null;
		
		super.destroyed();
	}

	@Override
	public void update() {
		if(!target.keep())
		{
			life = 0;
			return;
		}
		
		Vec2 delta = target.body.getPosition().sub(body.getPosition());
		
		delta.normalize();
		delta.mulLocal(15);
		
		body.applyLinearImpulse(delta, body.getPosition());
		
		if(body.getLinearVelocity().length() > 750)
		{
			body.getLinearVelocity().normalize();
			body.getLinearVelocity().mulLocal(750);
		}
		
		SpaceImperatorGame.s.actors.add(new Explosion(body.getPosition().add(new Vec2(SpaceImperatorGame.s.rand.nextFloat() * 0.5f - 0.25f, SpaceImperatorGame.s.rand.nextFloat() * 0.5f - 0.25f)), 1.f));
		
		life--;
		super.update();
	}

	@Override
	void takeDamage(float amount) {
		life = 0;
		super.takeDamage(amount);
	}

}
