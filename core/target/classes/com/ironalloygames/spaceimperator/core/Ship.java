package com.ironalloygames.spaceimperator.core;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Keyboard.Event;
import playn.core.Keyboard.TypedEvent;
import playn.core.Mouse.ButtonEvent;
import playn.core.Mouse.Listener;
import playn.core.Mouse.MotionEvent;
import playn.core.Mouse.WheelEvent;
import playn.core.Surface;

public abstract class Ship extends Actor implements Listener, playn.core.Keyboard.Listener {
	
	static Image aimCircle;
	static Image aimPoint;
	
	public abstract Image getGraphic();
	public abstract Image getForwardThrustGraphic();
	public abstract Image getLeftThrustGraphic();
	public abstract Image getRightThrustGraphic();
	
	public float getThrustPower(){ return 0; }
	public float getTurnPower(){ return 0; }
	
	public float getMaxHP(){ return 1; }
	
	public boolean hasTurrets(){ return false; }
	
	Body body;
	
	float hp;
	
	Vec2 aim = new Vec2();
	Vec2 firePoint = new Vec2();
	
	Vec2 mousePos;
	
	int turn;
	int thrust;
	
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
		//if(body.getAngularVelocity() < 0.5f) body.applyAngularImpulse(.5f);
		
		if(mousePos != null) aim = screenToReal(mousePos);
		
		if(hasTurrets())
		{
			firePoint.set(aim);
		} else {
			Vec2 delta = aim.sub(body.getPosition());
			//System.out.println(delta);
			float angle = body.getAngle();
			firePoint.x = (float)Math.cos(angle) * delta.length();
			firePoint.y = (float)Math.sin(angle) * delta.length();
			firePoint.addLocal(body.getPosition());
		}
		
		Vec2 pLeft = body.getPosition().add(new Vec2((float)Math.cos(body.getAngle() - 0.1f), (float)Math.sin(body.getAngle() - 0.1f)));
		Vec2 pCenter = body.getPosition().add(new Vec2((float)Math.cos(body.getAngle()), (float)Math.sin(body.getAngle())));
		Vec2 pRight = body.getPosition().add(new Vec2((float)Math.cos(body.getAngle() + 0.1f), (float)Math.sin(body.getAngle() + 0.1f)));
		
		float lLeft = aim.sub(pLeft).lengthSquared();
		float lCenter = aim.sub(pCenter).lengthSquared();
		float lRight = aim.sub(pRight).lengthSquared();
		
		if(lLeft < lCenter && lLeft < lRight)
			turn = -1;
		else if(lRight < lCenter && lRight < lLeft)
			turn = 1;
		else
			turn = 0;
		
		body.setAngularVelocity(getTurnPower() * turn);
		
		super.update();
	}
	
	@Override
	public void render(Surface target) {
		target.save();
		target.translate(body.getPosition().x, body.getPosition().y);
		target.rotate(body.getAngle());
		
		//System.out.println(body.getPosition());
		
		Image graphic = getGraphic();
		
		target.drawImage(graphic, -graphic.width() / 2.f / 16.f, -graphic.height() / 2.f / 16.f, graphic.width() / 16.f, graphic.height() / 16.f);
		target.restore();
		
		
		
		if(SpaceImperatorGame.s.pc == this)
		{
			if(aimCircle == null)
			{
				aimCircle = PlayN.assets().getImage("images/aimcircle.png");
				aimPoint = PlayN.assets().getImage("images/aimpoint.png");
			}
			
			target.save();
			
			target.translate(aim.x, aim.y);
			target.rotate(body.getAngle());
			target.drawImage(aimCircle, -aimCircle.width() / 2.f / 16.f, -aimCircle.height() / 2.f / 16.f, aimCircle.width() / 16.f, aimCircle.height() / 16.f);
			
			target.restore();
			
			target.save();
			
			target.translate(firePoint.x, firePoint.y);
			target.rotate(body.getAngle());
			target.drawImage(aimPoint, -aimPoint.width() / 2.f / 16.f, -aimPoint.height() / 2.f / 16.f, aimPoint.width() / 16.f, aimPoint.height() / 16.f);
			
			target.restore();
		}
		
		
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
	
	static CanvasImage starfield;
	
	public void cameraTrack(Surface target)
	{
		//target.setTransform(1, 0, 0, 1, 0, 0);
		
		if(starfield == null)
		{
			/*ImageLayer il = PlayN.graphics().createImageLayer();
			
			il.setWidth(2000);
			il.setHeight(2000);
			il.setDepth(32);
			il.setImage(image)
			
			starfield = il.image();*/
			
			starfield = PlayN.graphics().createImage(2000, 2000);
			
			for(int i=0;i<1500;++i)
			{
				int r,g,b,a;
				
				a = SpaceImperatorGame.s.rand.nextInt(255);
				r = SpaceImperatorGame.s.rand.nextInt(50) + 200;
				g = SpaceImperatorGame.s.rand.nextInt(50) + 200;
				b = SpaceImperatorGame.s.rand.nextInt(50) + 200;
				
				starfield.canvas().setStrokeColor((a << 24) + (r << 16) + (g << 8) + r);
				starfield.canvas().drawPoint(SpaceImperatorGame.s.rand.nextInt(starfield.width()), SpaceImperatorGame.s.rand.nextInt(starfield.height()));
			}
		}
		
		target.save();
		
		target.translate(-body.getPosition().x + SpaceImperatorGame.WINDOW_WIDTH / 2, -body.getPosition().y + SpaceImperatorGame.WINDOW_HEIGHT / 2);
		target.rotate(-body.getAngle() - (float)Math.PI / 2);
		
		//target.translate(-body.getPosition().x, -body.getPosition().y);
		target.drawImageCentered(starfield, 0, 0);
		
		target.restore();
		
		target.translate(SpaceImperatorGame.WINDOW_WIDTH / 2, SpaceImperatorGame.WINDOW_HEIGHT / 2);
		
		target.scale(16, 16);
		target.rotate(-body.getAngle() - (float)Math.PI / 2);
		target.translate(-body.getPosition().x, -body.getPosition().y);
	}
	
	public Vec2 screenToReal(Vec2 screen)
	{
		Vec2 delta = screen.sub(new Vec2(SpaceImperatorGame.WINDOW_WIDTH / 2, SpaceImperatorGame.WINDOW_HEIGHT / 2));
		float angle = (float)Math.atan2(delta.y, delta.x) + body.getAngle() + (float)Math.PI / 2;
		float dist = delta.length() / 16;
		
		Vec2 pos = body.getPosition().add(new Vec2((float)Math.cos(angle) * dist, (float)Math.sin(angle) * dist));
		
		return pos;
	}
	@Override
	public void onKeyDown(Event event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onKeyTyped(TypedEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onKeyUp(Event event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMouseDown(ButtonEvent event) {
		System.out.println(screenToReal(new Vec2(event.x(), event.y())));
		
	}
	@Override
	public void onMouseUp(ButtonEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMouseMove(MotionEvent event) {
		mousePos = new Vec2(event.x(), event.y());
	}
	@Override
	public void onMouseWheelScroll(WheelEvent event) {
		// TODO Auto-generated method stub
		
	}
}
