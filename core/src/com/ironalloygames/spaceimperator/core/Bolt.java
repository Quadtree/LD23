package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Bolt extends Actor {

	static Image graphic;

	int life;

	public Bolt(Vector2 source, Vector2 target, Vector2 velocityBase, short colGroup, int life) {
		this.life = life;
		this.colGroup = colGroup;
		CircleShape cs = new CircleShape();

		cs.setRadius(0.3f);

		BodyDef bd = new BodyDef();

		bd.position.set(source);
		bd.type = BodyType.DynamicBody;

		body = SpaceImperatorGame.s.world.createBody(bd);
		body.setUserData(this);

		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 1;
		fd.filter.groupIndex = (short) (-colGroup);

		body.createFixture(fd);

		Vector2 vel = target.sub(source);
		vel.nor();

		body.setLinearVelocity(vel.scl(400).add(velocityBase));
		SoundPlayer.play("sfx/bolt");
	}

	@Override
	void collidedWith(Actor other) {
		other.takeDamage(1);
		life = 0;

		SoundPlayer.play("sfx/bolt_hit");

		super.collidedWith(other);
	}

	@Override
	public void destroyed() {
		SpaceImperatorGame.s.actors.add(new Explosion(body.getPosition(), 2));

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
			graphic = PlayN.assets().getImage("images/bolt.png");

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
		life--;
		super.update();
	}
}
