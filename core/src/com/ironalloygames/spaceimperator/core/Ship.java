package com.ironalloygames.spaceimperator.core;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public abstract class Ship extends Actor implements InputProcessor {

	static Image aimCircle;

	static Image aimPoint;

	final static float MISSILE_COST = 10;
	static Image mmiPlayer;

	static Image mmiShip;
	final static float REPAIR_COST = 10;

	// static CanvasImage starfield;
	Vector2 aim = new Vector2();
	int dropPodCooldown = 0;
	boolean fireBolts;

	boolean fireMissiles;
	Vector2 firePoint = new Vector2();

	boolean forwardThrust;

	ArrayList<Gun> guns = new ArrayList<Gun>();

	float hp;

	boolean leftThrust;
	int missileCooldown;

	Vector2 mousePos;

	boolean replaced;

	boolean rightThrust;
	Vector2 startPos;
	boolean stopThrust;

	int strafe;
	int thrust;

	int turn;

	public Ship(Vector2 pos) {
		startPos = new Vector2(pos);

		PolygonShape ps = new PolygonShape();

		ps.setAsBox(getSize().x / 2, getSize().y / 2);

		BodyDef bd = new BodyDef();

		bd.position.set(pos);
		bd.type = BodyType.DynamicBody;

		body = SpaceImperatorGame.s.world.createBody(bd);
		body.setUserData(this);

		claimColGroup();

		FixtureDef fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 1;
		fd.filter.groupIndex = (short) (-colGroup);

		body.createFixture(fd);

		hp = getMaxHP();
	}

	public void cameraTrack(Surface target) {
		target.translate(SpaceImperatorGame.WINDOW_WIDTH / 2, SpaceImperatorGame.WINDOW_HEIGHT / 2);

		target.scale(16, 16);
		// target.rotate(-body.getAngle() - (float)Math.PI / 2);
		target.translate(-body.getPosition().x, -body.getPosition().y);
	}

	@Override
	public void created() {
		super.created();
	}

	@Override
	public void destroyed() {
		if (!replaced) {
			SpaceImperatorGame.s.actors.add(new Explosion(body.getPosition().cpy(), getSize().x * 4));
			SoundPlayer.play("sfx/ship_die");
		}

		SpaceImperatorGame.s.world.destroyBody(body);
		body = null;
		claimedColGroups[colGroup] = false;
		super.destroyed();
	}

	public void drawStarfield(Surface target) {
		/*
		 * if (starfield == null) {
		 *
		 * mmiPlayer = PlayN.assets().getImage("images/mmi_player.png"); mmiShip
		 * = PlayN.assets().getImage("images/mmi_ship.png");
		 *
		 * starfield = PlayN.graphics().createImage(2000, 2000);
		 *
		 * for (int i = 0; i < 1500; ++i) { int r, g, b, a;
		 *
		 * a = SpaceImperatorGame.s.rand.nextInt(255); r =
		 * SpaceImperatorGame.s.rand.nextInt(50) + 200; g =
		 * SpaceImperatorGame.s.rand.nextInt(50) + 200; b =
		 * SpaceImperatorGame.s.rand.nextInt(50) + 200;
		 *
		 * starfield.canvas().setStrokeColor((a << 24) + (r << 16) + (g << 8) +
		 * b); starfield.canvas().drawPoint(SpaceImperatorGame.s.rand.nextInt(
		 * starfield.width()),
		 * SpaceImperatorGame.s.rand.nextInt(starfield.height())); } }
		 */

		target.save();

		target.translate(-body.getPosition().x + SpaceImperatorGame.WINDOW_WIDTH / 2, -body.getPosition().y + SpaceImperatorGame.WINDOW_HEIGHT / 2);

		// target.drawImage(starfield, -600, -600);

		// target.drawImage(getGraphic(), -5, -5);

		target.restore();
	}

	public abstract Image getForwardThrustGraphic();

	public abstract Image getGraphic();

	public abstract Image getLeftThrustGraphic();

	public float getMaxHP() {
		return 1;
	}

	public abstract Image getRightThrustGraphic();

	abstract Vector2 getSize();

	public float getThrustPower() {
		return 0;
	}

	public float getTurnPower() {
		return 0;
	}

	public float getUpgradeCost() {
		return 0;
	}

	public String getUpgradeText() {
		return "";
	}

	public boolean hasTurrets() {
		return false;
	}

	boolean inputGotten() {
		if (SpaceImperatorGame.s.titleScreen) {
			SpaceImperatorGame.s.titleScreen = false;
			return true;
		}
		if (SpaceImperatorGame.s.introScreen) {
			SpaceImperatorGame.s.introScreen = false;
			return true;
		}

		return false;
	}

	@Override
	public boolean keep() {
		return hp > 0 && !replaced;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (inputGotten())
			return false;

		if (keycode == Input.Keys.W)
			forwardThrust = true;
		if (keycode == Input.Keys.S)
			stopThrust = true;
		if (keycode == Input.Keys.A)
			leftThrust = true;
		if (keycode == Input.Keys.D)
			rightThrust = true;

		final float DROP_POD_COST = 25;

		if (keycode == Input.Keys.G && SpaceImperatorGame.s.credits >= DROP_POD_COST) {
			SpaceImperatorGame.s.credits -= DROP_POD_COST;
			launchDropPod();
		}

		if (keycode == Input.Keys.U && SpaceImperatorGame.s.upgradeText.length() > 0) {
			SoundPlayer.play("sfx/upgrade_ship");
			SpaceImperatorGame.s.credits -= getUpgradeCost();
			upgrade();
		}

		if (keycode == Input.Keys.F1)
			SpaceImperatorGame.s.introScreen = true;

		thrustCheck();
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Input.Keys.W)
			forwardThrust = false;
		if (keycode == Input.Keys.S)
			stopThrust = false;
		if (keycode == Input.Keys.A)
			leftThrust = false;
		if (keycode == Input.Keys.D)
			rightThrust = false;

		thrustCheck();
		return true;
	}

	void launchDropPod() {
		if (dropPodCooldown <= 0) {
			SpaceImperatorGame.s.actors.add(new DropPod(body.getPosition().cpy(), aim, body.getLinearVelocity(), colGroup, 600, this == SpaceImperatorGame.s.pc));
			dropPodCooldown = 60;
		}
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mousePos = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
		// System.out.println("mousePos=" + mousePos);
		return true;
	}

	@Override
	public void render(Surface target) {
		target.save();
		target.translate(body.getPosition().x, body.getPosition().y);
		target.rotate(body.getAngle());

		Vector2 size = getSize();

		target.drawImage(getGraphic(), -size.x / 2, -size.y / 2, size.x, size.y);

		if (thrust == 1)
			target.drawImage(getForwardThrustGraphic(), -size.x / 2, -size.y / 2, size.x, size.y);
		if (strafe == -1 && getLeftThrustGraphic() != null)
			target.drawImage(getLeftThrustGraphic(), -size.x / 2, -size.y / 2, size.x, size.y);
		if (strafe == 1 && getRightThrustGraphic() != null)
			target.drawImage(getRightThrustGraphic(), -size.x / 2, -size.y / 2, size.x, size.y);

		target.restore();

		if (SpaceImperatorGame.s.pc == this) {
			if (aimCircle == null) {
				aimCircle = PlayN.assets().getImage("images/aimcircle.png");
				aimPoint = PlayN.assets().getImage("images/aimpoint.png");
			}

			target.save();

			target.translate(aim.x, aim.y);
			// target.rotate(body.getAngle());
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
	void renderToMinimap(Vector2 upperLeft, Surface target) {
		Image img = null;

		if (SpaceImperatorGame.s.pc == this) {
			img = mmiPlayer;
		} else {
			img = mmiShip;
		}

		if (img != null)
			target.drawImage(img, upperLeft.x + body.getPosition().x / SpaceImperatorGame.WORLD_WIDTH * 234 - 12.5f, upperLeft.y + body.getPosition().y / SpaceImperatorGame.WORLD_HEIGHT * 234 - 12.5f, 25, 25);

		super.renderToMinimap(upperLeft, target);
	}

	public void runAI() {
		float range = SpaceImperatorGame.s.pc.body.getPosition().cpy().sub(body.getPosition()).len();

		thrust = -1;
		strafe = 0;

		fireBolts = false;
		fireMissiles = false;

		if (range < 100)
			aim.set(SpaceImperatorGame.s.pc.body.getPosition().cpy());
		else
			aim.set(startPos);

		if (!hasTurrets()) {
			if (turn == 0) {
				if (range < 20) {
					strafe = 1;
					thrust = 0;
					fireBolts = true;
					// fireMissiles = true;
				} else {
					thrust = 1;
					strafe = 0;
				}
			}
		} else {
			if (range < 50)
				fireBolts = true;

			Vector2 movePos = aim.add(new Vector2(20, 0));

			Vector2 pLeft = body.getPosition().cpy().add(new Vector2((float) Math.cos(body.getAngle() - 0.1f), (float) Math.sin(body.getAngle() - 0.1f)));
			Vector2 pCenter = body.getPosition().cpy().add(new Vector2((float) Math.cos(body.getAngle()), (float) Math.sin(body.getAngle())));
			Vector2 pRight = body.getPosition().cpy().add(new Vector2((float) Math.cos(body.getAngle() + 0.1f), (float) Math.sin(body.getAngle() + 0.1f)));

			float lLeft = movePos.cpy().sub(pLeft).len2();
			float lCenter = movePos.cpy().sub(pCenter).len2();
			float lRight = movePos.cpy().sub(pRight).len2();

			thrust = -1;

			if (lLeft < lCenter && lLeft < lRight)
				strafe = -1;
			else if (lRight < lCenter && lRight < lLeft)
				strafe = 1;
			else {
				strafe = 0;
				thrust = 1;
			}
		}
	}

	public Vector2 screenToReal(Vector2 screen) {
		Vector2 delta = screen.cpy().sub(new Vector2(SpaceImperatorGame.WINDOW_WIDTH / 2, SpaceImperatorGame.WINDOW_HEIGHT / 2));
		float angle = (float) Math.atan2(delta.y, delta.x) /*
															 * + body.getAngle()
															 * + (float)Math.PI
															 * / 2
															 */;
		float dist = delta.len() / 16;

		Vector2 pos = body.getPosition().cpy().add(new Vector2((float) Math.cos(angle) * dist, (float) Math.sin(angle) * dist));

		return pos;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	void takeDamage(float amount) {
		hp -= amount;
		super.takeDamage(amount);
	}

	void thrustCheck() {
		if (leftThrust && !rightThrust)
			strafe = -1;
		else if (!leftThrust && rightThrust)
			strafe = 1;
		else
			strafe = 0;

		if (forwardThrust && !stopThrust)
			thrust = 1;
		else if (!forwardThrust && stopThrust)
			thrust = -1;
		else
			thrust = 0;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (inputGotten())
			return false;

		// System.out.println(screenToReal(new Vector2(event.x(), event.y())));

		if (button == Input.Buttons.LEFT)
			fireBolts = true;
		if (button == Input.Buttons.RIGHT)
			fireMissiles = true;

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		mousePos = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT)
			fireBolts = false;
		if (button == Input.Buttons.RIGHT)
			fireMissiles = false;
		return true;
	}

	@Override
	public void update() {
		// if(body.getAngularVelocity() < 0.5f) body.applyAngularImpulse(.5f);

		if (mousePos != null)
			aim = screenToReal(mousePos.cpy());

		if (hasTurrets()) {
			firePoint.set(aim);
		} else {
			Vector2 delta = aim.cpy().sub(body.getPosition().cpy());
			// System.out.println(delta);
			float angle = body.getAngle();
			firePoint.x = (float) Math.cos(angle) * delta.len();
			firePoint.y = (float) Math.sin(angle) * delta.len();
			firePoint.add(body.getPosition());
		}

		if (!hasTurrets()) {
			Vector2 pLeft = body.getPosition().cpy().add(new Vector2((float) Math.cos(body.getAngle() - 0.1f), (float) Math.sin(body.getAngle() - 0.1f)));
			Vector2 pCenter = body.getPosition().cpy().add(new Vector2((float) Math.cos(body.getAngle()), (float) Math.sin(body.getAngle())));
			Vector2 pRight = body.getPosition().cpy().add(new Vector2((float) Math.cos(body.getAngle() + 0.1f), (float) Math.sin(body.getAngle() + 0.1f)));

			float lLeft = aim.cpy().sub(pLeft).len2();
			float lCenter = aim.cpy().sub(pCenter).len2();
			float lRight = aim.cpy().sub(pRight).len2();

			if (lLeft < lCenter && lLeft < lRight)
				turn = -1;
			else if (lRight < lCenter && lRight < lLeft)
				turn = 1;
			else {
				turn = 0;
				body.setTransform(body.getPosition().cpy(), (float) Math.atan2(aim.y - body.getPosition().y, aim.x - body.getPosition().x));
			}
		} else {
			turn = strafe;
		}

		body.setAngularVelocity(getTurnPower() * turn);

		if (thrust == 1) {
			Vector2 velVec = new Vector2((float) Math.cos(body.getAngle()) * this.getThrustPower(), (float) Math.sin(body.getAngle()) * this.getThrustPower());

			body.applyLinearImpulse(velVec, body.getPosition().cpy(), true);
			// System.out.println("FULL PO");
		}

		if (thrust == -1) {
			Vector2 velVec = new Vector2(body.getLinearVelocity());
			velVec.nor();

			velVec.scl(-getThrustPower());

			body.applyLinearImpulse(velVec, body.getPosition().cpy(), true);
		}

		if (!hasTurrets())
			body.applyLinearImpulse(new Vector2((float) Math.cos(body.getAngle() + Math.PI / 2) * this.getThrustPower() * strafe, (float) Math.sin(body.getAngle() + Math.PI / 2) * this.getThrustPower() * strafe),
					body.getPosition().cpy(), true);

		for (Gun g : guns) {
			g.update();
			if (g instanceof BoltCannon && fireBolts)
				g.fire(this);
		}

		if (SpaceImperatorGame.s.pc != this)
			runAI();

		dropPodCooldown--;

		final float SPEED_LIMIT = 400;

		if (body.getLinearVelocity().len() > SPEED_LIMIT) {
			body.getLinearVelocity().nor();
			body.getLinearVelocity().scl(SPEED_LIMIT);
		}

		if (fireMissiles && missileCooldown <= 0 && (this != SpaceImperatorGame.s.pc || SpaceImperatorGame.s.credits >= MISSILE_COST)) {
			float bestDist = Float.MAX_VALUE;
			Ship target = null;

			for (Actor a : SpaceImperatorGame.s.actors) {
				if (a instanceof Ship && a != this) {
					float dist = a.body.getPosition().cpy().sub(aim).len2();

					if (dist < bestDist) {
						bestDist = dist;
						target = (Ship) a;
					}
				}
			}

			if (target != null) {
				SpaceImperatorGame.s.actors.add(new Missile(body.getPosition().cpy(), target, body.getLinearVelocity(), colGroup));
				missileCooldown = 60;

				if (this == SpaceImperatorGame.s.pc)
					SpaceImperatorGame.s.credits -= MISSILE_COST;
			}
		}

		SpaceImperatorGame.s.upgradeText = "";

		float repairRate = getMaxHP() / 60 / 120;

		if (this == SpaceImperatorGame.s.pc) {

			boolean nearAlliedPlanet = false;

			for (Actor a : SpaceImperatorGame.s.actors) {
				if (a instanceof Planet && ((Planet) a).ownedByPlayer && a.body.getPosition().cpy().sub(body.getPosition()).len() < ((Planet) a).getRadius() + 10)
					nearAlliedPlanet = true;
			}

			if (nearAlliedPlanet) {
				if (SpaceImperatorGame.s.credits >= getUpgradeCost()) {
					SpaceImperatorGame.s.upgradeText = "Upgrade Available!\n" + getUpgradeText() + "\nUpgrade Cost: " + (int) getUpgradeCost() + " Credits";
				}

				repairRate *= 16;
			}
		}

		repairRate = Math.min(repairRate, getMaxHP() - hp);

		if (SpaceImperatorGame.s.credits >= repairRate * REPAIR_COST) {
			hp += repairRate;

			SpaceImperatorGame.s.credits -= repairRate * REPAIR_COST;
		}

		missileCooldown--;

		super.update();
	}

	public void upgrade() {
	}
}
