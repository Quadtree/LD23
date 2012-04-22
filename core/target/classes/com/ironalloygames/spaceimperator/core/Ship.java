package com.ironalloygames.spaceimperator.core;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import playn.core.Image;
import playn.core.Surface;

public abstract class Ship extends Actor {
	public abstract Image getGraphic();
	public abstract Image getForwardThrustGraphic();
	public abstract Image getLeftThrustGraphic();
	public abstract Image getRightThrustGraphic();
	
	public float getThrustPower(){ return 0; }
	public float getTurnPower(){ return 0; }
	
	public float getMaxHP(){ return 1; }
	
	Body body;
	
	float hp;
	
	public Ship(Vec2 pos)
	{
		PolygonShape ps = new PolygonShape();
		
		ps.setAsBox(getGraphic().width() / 2 / 16, getGraphic().height() / 2 / 16);
		
		BodyDef bd = new BodyDef();
		
		bd.position.set(pos);
		bd.type = BodyType.DYNAMIC;
		bd.userData = this;
		
		body = SpaceImperatorGame.s.world.createBody(bd);
		
		body.createFixture(ps, 1);
		
		hp = getMaxHP();
	}
	
	@Override
	public void update() {
		body.applyAngularImpulse(5);
		
		super.update();
	}
	
	@Override
	public void render(Surface target) {
		target.save();
		target.translate(body.getPosition().x, body.getPosition().y);
		target.rotate(body.getAngle());
		
		System.out.println(body.getPosition());
		
		Image graphic = getGraphic();
		
		target.drawImage(graphic, -graphic.width() / 2.f / 16.f, -graphic.height() / 2.f / 16.f, graphic.width() / 16.f, graphic.height() / 16.f);
		target.restore();
		super.render(target);
	}
	
	@Override
	public boolean keep() {
		return hp > 0;
	}
	
	@Override
	public void created() {
		super.created();
	}
	
	@Override
	public void destroyed() {
		SpaceImperatorGame.s.world.destroyBody(body);
		body = null;
		super.destroyed();
	}
	
	public void cameraTrack(Surface target)
	{
		//target.setTransform(1, 0, 0, 1, 0, 0);
		
		target.translate(SpaceImperatorGame.WINDOW_WIDTH / 2, SpaceImperatorGame.WINDOW_HEIGHT / 2);
		
		target.scale(16, 16);
		target.rotate(-body.getAngle());
		target.translate(-body.getPosition().x, -body.getPosition().y);
	}
}
