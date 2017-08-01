package com.ironalloygames.spaceimperator.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class DropPod extends Actor {

	static Image graphic;

	int life;

	boolean playerOwned;

	public DropPod(Vector2 source, Vector2 target, Vector2 velocityBase, short colGroup, int life, boolean playerOwned) {
		this.playerOwned = playerOwned;
		this.life = life;
		this.colGroup = colGroup;
		System.out.println(colGroup);
		CircleShape cs = new CircleShape();

		cs.setRadius(1.f);

		BodyDef bd = new BodyDef();

		bd.position.set(source);
		bd.type = BodyType.DynamicBody;

		bd.angle = (float) Math.atan2(target.sub(source).y, target.sub(source).x);

		body = SpaceImperatorGame.s.world.createBody(bd);
		body.setUserData(this);

		claimColGroup();

		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 1;
		fd.filter.groupIndex = (short) (-colGroup);

		body.createFixture(fd);

		Vector2 vel = target.sub(source);
		vel.nor();

		body.setLinearVelocity(velocityBase);
	}

	@Override
	void collidedWith(Actor other) {
		if (other instanceof Planet && ((Planet) other).ownedByPlayer != playerOwned) {
			SoundPlayer.play("sfx/pod_hit");
			((Planet) other).dropAttack(playerOwned);
			life = 0;
		}
		super.collidedWith(other);
	}

	@Override
	public void destroyed() {
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
			graphic = PlayN.assets().getImage("images/drop_pod.png");

		target.save();

		target.translate(body.getPosition().x, body.getPosition().y);
		target.rotate(body.getAngle() + (float) Math.PI / 2);
		target.drawImage(graphic, -25 / 16.f / 2.f, -25 / 16.f / 2.f, 25 / 16.f, 25 / 16.f);

		target.restore();
		super.render(target);
	}

	@Override
	public void update() {
		life--;
		super.update();
	}
}
