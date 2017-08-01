package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Missile extends Actor {

	static Image graphic;

	int life;

	Ship target;

	public Missile(Vector2 source, Ship target, Vector2 velocityBase, short colGroup) {
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

		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 1;
		fd.filter.groupIndex = -colGroup;

		body.createFixture(fd);

		body.setLinearVelocity(velocityBase);
	}

	@Override
	void collidedWith(Actor other) {
		other.takeDamage(2);
		life = 0;
		super.collidedWith(other);
	}

	@Override
	public void destroyed() {
		SoundPlayer.play("sfx/missile_hit");

		SpaceImperatorGame.s.actors.add(new Explosion(body.getPosition(), 4));

		SpaceImperatorGame.s.world.destroyBody(body);
		body = null;

		super.destroyed();
	}

	@Override
	public boolean keep() {
		return life > 0;
	}

	@Override
	public void render(Surface target) {
		if (graphic == null)
			graphic = PlayN.assets().getImage("images/missile.png");

		target.save();

		target.translate(body.getPosition().x, body.getPosition().y);
		target.drawImage(graphic, -0.25f, -0.25f, 0.5f, 0.5f);

		target.restore();
		super.render(target);
	}

	@Override
	void takeDamage(float amount) {
		life = 0;
		super.takeDamage(amount);
	}

	@Override
	public void update() {
		if (!target.keep()) {
			life = 0;
			return;
		}

		Vector2 delta = target.body.getPosition().sub(body.getPosition());

		delta.normalize();
		delta.mulLocal(15);

		body.applyLinearImpulse(delta, body.getPosition());

		if (body.getLinearVelocity().length() > 750) {
			body.getLinearVelocity().normalize();
			body.getLinearVelocity().mulLocal(750);
		}

		SpaceImperatorGame.s.actors.add(new Explosion(body.getPosition().add(new Vector2(SpaceImperatorGame.s.rand.nextFloat() * 0.5f - 0.25f, SpaceImperatorGame.s.rand.nextFloat() * 0.5f - 0.25f)), 1.f));

		life--;
		super.update();
	}

}
