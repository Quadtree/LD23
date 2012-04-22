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
	
	final static float MISSILE_COST = 10;
	
	final static float REPAIR_COST = 10;
	
	static Image aimCircle;
	static Image aimPoint;
	
	static Image mmiPlayer;
	static Image mmiShip;
	
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
	
	int missileCooldown;
	
	int turn;
	int thrust;
	int strafe;
	
	boolean fireBolts;
	boolean fireMissiles;
	
	boolean replaced;
	
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
		
		if(!hasTurrets())
		{
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
		} else {
			turn = strafe;
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
		
		if(!hasTurrets())
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
		
		if(fireMissiles && missileCooldown <= 0 && (this != SpaceImperatorGame.s.pc || SpaceImperatorGame.s.credits >= MISSILE_COST))
		{
			float bestDist = Float.MAX_VALUE;
			Ship target = null;
			
			for(Actor a : SpaceImperatorGame.s.actors)
			{
				if(a instanceof Ship && a != this)
				{
					float dist = a.body.getPosition().sub(aim).lengthSquared();
					
					if(dist < bestDist)
					{
						bestDist = dist;
						target = (Ship)a;
					}
				}
			}
			
			if(target != null)
			{
				SpaceImperatorGame.s.actors.add(new Missile(body.getPosition(), target, body.getLinearVelocity(), colGroup));
				missileCooldown = 60;
				
				if(this == SpaceImperatorGame.s.pc) SpaceImperatorGame.s.credits -= MISSILE_COST;
			}
		}
		
		SpaceImperatorGame.s.upgradeText = "";
		
		float repairRate = getMaxHP() / 60 / 120;
		
		if(this == SpaceImperatorGame.s.pc)
		{
			
			boolean nearAlliedPlanet = false;
		
			for(Actor a : SpaceImperatorGame.s.actors)
			{
				if(a instanceof Planet && ((Planet)a).ownedByPlayer && a.body.getPosition().sub(body.getPosition()).length() < ((Planet)a).getRadius() + 10) nearAlliedPlanet = true;
			}
			
			if(nearAlliedPlanet)
			{
				if(SpaceImperatorGame.s.credits >= getUpgradeCost())
				{
					SpaceImperatorGame.s.upgradeText = "Upgrade Available!\n" + getUpgradeText() + "\nUpgrade Cost: " + (int)getUpgradeCost() + " Credits";
				}
				
				repairRate *= 16;
			}
		}
		
		repairRate = Math.min(repairRate, getMaxHP() - hp);
		
		if(SpaceImperatorGame.s.credits >= repairRate * REPAIR_COST)
		{
			hp += repairRate;
			
			SpaceImperatorGame.s.credits -= repairRate * REPAIR_COST;
		}
		
		missileCooldown--;
		
		super.update();
	}
	
	@Override
	public void render(Surface target) {
		target.save();
		target.translate(body.getPosition().x, body.getPosition().y);
		target.rotate(body.getAngle());
		
		Vec2 size = getSize();
		
		target.drawImage(getGraphic(), -size.x / 2, -size.y / 2, size.x, size.y);
		
		if(thrust == 1) target.drawImage(getForwardThrustGraphic(), -size.x / 2, -size.y / 2, size.x, size.y);
		if(strafe == -1 && getLeftThrustGraphic() != null) target.drawImage(getLeftThrustGraphic(), -size.x / 2, -size.y / 2, size.x, size.y);
		if(strafe == 1 && getRightThrustGraphic() != null) target.drawImage(getRightThrustGraphic(), -size.x / 2, -size.y / 2, size.x, size.y);
		
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
				//fireMissiles = true;
			} else if(range < 100)
			{
				thrust = 1;
				strafe = 0;
			}
		}
	}
	
	@Override
	public boolean keep() {
		return hp > 0 && !replaced;
	}
	
	@Override
	public void created() {
		super.created();
	}
	
	@Override
	public void destroyed() {
		if(!replaced)
		{
			SpaceImperatorGame.s.actors.add(new Explosion(body.getPosition(), getSize().x * 4));
			PlayN.assets().getSound("sfx/ship_die").play();
		}
		
		SpaceImperatorGame.s.world.destroyBody(body);
		body = null;
		claimedColGroups[colGroup] = false;
		super.destroyed();
	}
	
	static CanvasImage starfield;
	
	public void drawStarfield(Surface target)
	{
		if(starfield == null)
		{
			
			mmiPlayer = PlayN.assets().getImage("images/mmi_player.png");
			mmiShip = PlayN.assets().getImage("images/mmi_ship.png");
			
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
		target.drawImage(starfield, -600, -600);
		
		target.restore();
	}
	
	public void cameraTrack(Surface target)
	{
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
		
		final float DROP_POD_COST = 25;
		
		if(event.key() == Key.G && SpaceImperatorGame.s.credits >= DROP_POD_COST)
		{
			SpaceImperatorGame.s.credits -= DROP_POD_COST;
			launchDropPod();
		}
		
		if(event.key() == Key.U && SpaceImperatorGame.s.upgradeText.length() > 0)
		{
			PlayN.assets().getSound("sfx/upgrade_ship").play();
			SpaceImperatorGame.s.credits -= getUpgradeCost();
			upgrade();
		}
		
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
		} else {
			img = mmiShip;
		}
		
		if(img != null)
			target.drawImage(img, upperLeft.x + body.getPosition().x / SpaceImperatorGame.WORLD_WIDTH * 234 - 12.5f, upperLeft.y + body.getPosition().y / SpaceImperatorGame.WORLD_HEIGHT * 234 - 12.5f, 25, 25);
		
		super.renderToMinimap(upperLeft, target);
	}
	
	public void upgrade(){}
	public String getUpgradeText(){ return ""; }
	public float getUpgradeCost(){ return 0; }
}
