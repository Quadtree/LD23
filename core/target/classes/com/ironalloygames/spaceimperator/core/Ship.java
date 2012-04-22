package com.ironalloygames.spaceimperator.core;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import playn.core.CanvasImage;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Mouse;
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
	
	static Image mmiPlayer;
	
	public abstract Image getGraphic();
	public abstract Image getForwardThrustGraphic();
	public abstract Image getLeftThrustGraphic();
	public abstract Image getRightThrustGraphic();
	
	public float getThrustPower(){ return 0; }
	public float getTurnPower(){ return 0; }
	
	public float getMaxHP(){ return 1; }
	
	public boolean hasTurrets(){ return false; }
	
	float hp;
	
	Vec2 aim = new Vec2();
	Vec2 firePoint = new Vec2();
	
	Vec2 mousePos;
	
	int turn;
	int thrust;
	int strafe;
	
	boolean fireBolts;
	boolean fireMissiles;
	
	ArrayList<Gun> guns = new ArrayList<Gun>();
	
	public Ship(Vec2 pos)
	{
		PolygonShape ps = new PolygonShape();
		
		ps.setAsBox(getSize().x / 2, getSize().y / 2);
		
		BodyDef bd = new BodyDef();
		
		bd.position.set(pos);
		bd.type = BodyType.DYNAMIC;
		bd.userData = this;
		
		body = SpaceImperatorGame.s.world.createBody(bd);
		
		claimColGroup();
		
		FixtureDef fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 1;
		fd.filter.groupIndex = -colGroup;
		
		body.createFixture(fd);
		
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
		{
			turn = 0;
			body.setTransform(body.getPosition(), (float)Math.atan2(aim.y - body.getPosition().y, aim.x - body.getPosition().x));
		}
		
		body.setAngularVelocity(getTurnPower() * turn);
		
		if(thrust == 1)
		{
			Vec2 velVec = new Vec2((float)Math.cos(body.getAngle()) * this.getThrustPower(), (float)Math.sin(body.getAngle()) * this.getThrustPower());
			
			
			body.applyLinearImpulse(velVec, body.getPosition());
			//System.out.println("FULL PO");
		}
		
		if(thrust == -1)
		{
			Vec2 velVec = new Vec2(body.getLinearVelocity());
			velVec.normalize();
			
			velVec.mulLocal(-getThrustPower());
			
			body.applyLinearImpulse(velVec, body.getPosition());
		}
		
		body.applyLinearImpulse(new Vec2((float)Math.cos(body.getAngle() + Math.PI / 2) * this.getThrustPower() * strafe, (float)Math.sin(body.getAngle() + Math.PI / 2) * this.getThrustPower() * strafe), body.getPosition());
		
		for(Gun g : guns)
		{
			g.update();
			if(g instanceof BoltCannon && fireBolts) g.fire(this);
		}
		
		if(SpaceImperatorGame.s.pc != this) runAI();
		
		dropPodCooldown--;
		
		final float SPEED_LIMIT = 400;
		
		if(body.getLinearVelocity().length() > SPEED_LIMIT)
		{
			body.getLinearVelocity().normalize();
			body.getLinearVelocity().mulLocal(SPEED_LIMIT);
		}
		
		super.update();
	}
	
	@Override
	public void render(Surface target) {
		target.save();
		target.translate(body.getPosition().x, body.getPosition().y);
		target.rotate(body.getAngle());
		
		Vec2 size = getSize();
		
		target.drawImage(getGraphic(), -size.x / 2, -size.y / 2, size.x, size.y);
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
	
	int dropPodCooldown = 0;
	
	void launchDropPod()
	{
		if(dropPodCooldown <= 0)
		{
			SpaceImperatorGame.s.actors.add(new DropPod(body.getPosition(), aim, body.getLinearVelocity(), colGroup, 600, this == SpaceImperatorGame.s.pc));
			dropPodCooldown = 60;
		}
	}
	
	public void runAI()
	{
		float range = SpaceImperatorGame.s.pc.body.getPosition().sub(body.getPosition()).length();
		
		aim = SpaceImperatorGame.s.pc.body.getPosition();
		
		thrust = -1;
		strafe = 0;
		fireBolts = false;
		fireMissiles = false;
		
		if(turn == 0)
		{
			if(range < 20)
			{
				strafe = 1;
				thrust = 0;
				fireBolts = true;
				fireMissiles = true;
			} else if(range < 100)
			{
				thrust = 1;
				strafe = 0;
			}
		}
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
		claimedColGroups[colGroup] = false;
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
			
			mmiPlayer = PlayN.assets().getImage("images/mmi_player.png");
			
			starfield = PlayN.graphics().createImage(2000, 2000);
			
			for(int i=0;i<1500;++i)
			{
				int r,g,b,a;
				
				a = SpaceImperatorGame.s.rand.nextInt(255);
				r = SpaceImperatorGame.s.rand.nextInt(50) + 200;
				g = SpaceImperatorGame.s.rand.nextInt(50) + 200;
				b = SpaceImperatorGame.s.rand.nextInt(50) + 200;
				
				starfield.canvas().setStrokeColor((a << 24) + (r << 16) + (g << 8) + b);
				starfield.canvas().drawPoint(SpaceImperatorGame.s.rand.nextInt(starfield.width()), SpaceImperatorGame.s.rand.nextInt(starfield.height()));
			}
		}
		
		target.save();
		
		target.translate(-body.getPosition().x + SpaceImperatorGame.WINDOW_WIDTH / 2, -body.getPosition().y + SpaceImperatorGame.WINDOW_HEIGHT / 2);
		//target.rotate(-body.getAngle() - (float)Math.PI / 2);
		
		//target.translate(-body.getPosition().x, -body.getPosition().y);
		target.drawImageCentered(starfield, 0, 0);
		
		target.restore();
		
		target.translate(SpaceImperatorGame.WINDOW_WIDTH / 2, SpaceImperatorGame.WINDOW_HEIGHT / 2);
		
		target.scale(16, 16);
		//target.rotate(-body.getAngle() - (float)Math.PI / 2);
		target.translate(-body.getPosition().x, -body.getPosition().y);
	}
	
	public Vec2 screenToReal(Vec2 screen)
	{
		Vec2 delta = screen.sub(new Vec2(SpaceImperatorGame.WINDOW_WIDTH / 2, SpaceImperatorGame.WINDOW_HEIGHT / 2));
		float angle = (float)Math.atan2(delta.y, delta.x) /*+ body.getAngle() + (float)Math.PI / 2*/;
		float dist = delta.length() / 16;
		
		Vec2 pos = body.getPosition().add(new Vec2((float)Math.cos(angle) * dist, (float)Math.sin(angle) * dist));
		
		return pos;
	}
	
	boolean leftThrust;
	boolean rightThrust;
	boolean forwardThrust;
	boolean stopThrust;
	
	void thrustCheck()
	{
		if(leftThrust && !rightThrust)
			strafe = -1;
		else if(!leftThrust && rightThrust)
			strafe = 1;
		else
			strafe = 0;
		
		if(forwardThrust && !stopThrust)
			thrust = 1;
		else if(!forwardThrust && stopThrust)
			thrust = -1;
		else
			thrust = 0;
	}
	
	@Override
	public void onKeyDown(Event event) {
		if(event.key() == Key.W) forwardThrust = true;
		if(event.key() == Key.S) stopThrust = true;
		if(event.key() == Key.A) leftThrust = true;
		if(event.key() == Key.D) rightThrust = true;
		
		if(event.key() == Key.G) launchDropPod();
		
		thrustCheck();
	}
	@Override
	public void onKeyTyped(TypedEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	@Override
	public void onKeyUp(Event event) {
		if(event.key() == Key.W) forwardThrust = false;
		if(event.key() == Key.S) stopThrust = false;
		if(event.key() == Key.A) leftThrust = false;
		if(event.key() == Key.D) rightThrust = false;
		
		thrustCheck();
	}
	@Override
	public void onMouseDown(ButtonEvent event) {
		//System.out.println(screenToReal(new Vec2(event.x(), event.y())));
		
		if(event.button() == Mouse.BUTTON_LEFT) fireBolts = true;
		if(event.button() == Mouse.BUTTON_RIGHT) fireMissiles = true;
	}
	@Override
	public void onMouseUp(ButtonEvent event) {
		if(event.button() == Mouse.BUTTON_LEFT) fireBolts = false;
		if(event.button() == Mouse.BUTTON_RIGHT) fireMissiles = false;
	}
	@Override
	public void onMouseMove(MotionEvent event) {
		mousePos = new Vec2(event.x(), event.y());
	}
	@Override
	public void onMouseWheelScroll(WheelEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	abstract Vec2 getSize();
	
	@Override
	void takeDamage(float amount) {
		hp -= amount;
		super.takeDamage(amount);
	}
	@Override
	void renderToMinimap(Vec2 upperLeft, Surface target) {
		Image img = null;
		
		if(SpaceImperatorGame.s.pc == this)
		{
			img = mmiPlayer;
		}
		
		if(img != null)
			target.drawImage(img, upperLeft.x + body.getPosition().x / SpaceImperatorGame.WORLD_WIDTH * 234 - 12.5f, upperLeft.y + body.getPosition().y / SpaceImperatorGame.WORLD_HEIGHT * 234 - 12.5f, 25, 25);
		
		super.renderToMinimap(upperLeft, target);
	}
}
