package com.ironalloygames.spaceimperator.core;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class SpaceImperatorGame implements ContactListener {

	public static SpaceImperatorGame s;
	public final static int WINDOW_HEIGHT = 768;

	public final static int WINDOW_WIDTH = 1024;
	public final static int WORLD_HEIGHT = 600;

	public final static int WORLD_WIDTH = 600;

	ArrayList<Actor> actors = new ArrayList<Actor>();

	float credits = 0;

	Image defeatImage;

	boolean defeatScreen = false;

	Image dust;

	Vector2[] dustPos;

	Image healthDepleted;
	Image healthFull;

	boolean hitSoundThisFrame = false;

	Image introImage;

	boolean introScreen = true;

	Image minimap;

	// CanvasImage overlay;
	public Ship pc;
	Random rand = new Random();
	boolean shotSoundThisFrame = false;

	Image titleImage;
	boolean titleScreen = true;
	String upgradeText = "";
	Image victoryImage;

	boolean victoryScreen = false;
	public World world;

	public PlayN.Assets assets() {
		return PlayN.assets();
	}

	@Override
	public void beginContact(Contact contact) {
		Object u1 = contact.getFixtureA().getBody().getUserData();
		Object u2 = contact.getFixtureB().getBody().getUserData();

		if (u1 != null && u2 != null && u1 instanceof Actor && u2 instanceof Actor) {
			((Actor) u1).collidedWith((Actor) u2);
			((Actor) u2).collidedWith((Actor) u1);
		}
	}

	@Override
	public void endContact(Contact contact) {
	}

	public void init() {
		s = this;

		// graphics().rootLayer().add(graphics().createImmediateLayer(this));

		world = new World(new Vector2(), false);
		world.setContactListener(this);

		actors.add(new Planet(new Vector2(50, 50), Planet.PlanetSize.Tiny, true));

		// overlay = graphics().createImage(WINDOW_WIDTH, WINDOW_HEIGHT);

		// actors.add(new Planet(new Vector2(0, 40), Planet.PlanetSize.Tiny,
		// false));

		actors.add(new Planet(new Vector2(550, 050), Planet.PlanetSize.Tiny, false));
		actors.add(new Planet(new Vector2(050, 550), Planet.PlanetSize.Tiny, false));
		actors.add(new Planet(new Vector2(350, 050), Planet.PlanetSize.Tiny, false));
		actors.add(new Planet(new Vector2(050, 350), Planet.PlanetSize.Tiny, false));
		actors.add(new Planet(new Vector2(250, 250), Planet.PlanetSize.Tiny, false));

		actors.add(new Planet(new Vector2(550, 250), Planet.PlanetSize.Small, false));
		actors.add(new Planet(new Vector2(350, 350), Planet.PlanetSize.Small, false));
		actors.add(new Planet(new Vector2(250, 550), Planet.PlanetSize.Small, false));

		actors.add(new Planet(new Vector2(420, 550), Planet.PlanetSize.Medium, false));
		actors.add(new Planet(new Vector2(550, 420), Planet.PlanetSize.Medium, false));

		actors.add(new Planet(new Vector2(550, 550), Planet.PlanetSize.Large, false));

		// graphics().setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		// actors.add(new Battleship(new Vector2(100,100)));

		minimap = assets().getImage("images/minimap.png");

		healthFull = assets().getImage("images/health_full.png");
		healthDepleted = assets().getImage("images/health_depleted.png");

		dust = assets().getImage("images/dust.png");

		dustPos = new Vector2[20];

		for (int i = 0; i < dustPos.length; ++i)
			dustPos[i] = new Vector2(rand.nextFloat() * WINDOW_WIDTH, rand.nextFloat() * WINDOW_HEIGHT);

		titleImage = assets().getImage("images/title.png");
		introImage = assets().getImage("images/intro.png");
		victoryImage = assets().getImage("images/victory.png");
		defeatImage = assets().getImage("images/defeat.png");
	}

	public void paint(float alpha) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	public void render(Surface surface) {

		if (titleScreen) {
			surface.drawImage(titleImage, 0, 0);
			return;
		}

		if (defeatScreen) {
			surface.drawImage(defeatImage, 0, 0);
			return;
		}

		if (victoryScreen) {
			surface.drawImage(victoryImage, 0, 0);
			return;
		}

		surface.setTransform(1, 0, 0, 1, 0, 0);

		// surface.drawImage(minimap, 8, 8);

		pc.drawStarfield(surface);

		float dustFacing = (float) Math.atan2(pc.body.getLinearVelocity().y, pc.body.getLinearVelocity().x);
		float dustLength = pc.body.getLinearVelocity().len() / 4;

		for (Vector2 v2 : dustPos) {
			v2.add(pc.body.getLinearVelocity().scl(-0.016f));

			if (v2.x > WINDOW_WIDTH + 50 ||
					v2.y > WINDOW_HEIGHT + 50 ||
					v2.x < -50 ||
					v2.y < -50) {
				if (rand.nextBoolean()) {
					v2.x = rand.nextBoolean() ? -50 : WINDOW_WIDTH + 50;
					v2.y = rand.nextFloat() * WINDOW_HEIGHT;
				} else {
					v2.y = rand.nextBoolean() ? -50 : WINDOW_HEIGHT + 50;
					v2.x = rand.nextFloat() * WINDOW_WIDTH;
				}
			}

			surface.save();

			surface.translate(v2.x, v2.y);
			surface.rotate(dustFacing);

			surface.drawImage(dust, -dustLength / 2, -3 / 2, dustLength, 3);

			surface.restore();
		}

		pc.cameraTrack(surface);

		for (Actor a : actors)
			a.render(surface);

		surface.setTransform(1, 0, 0, 1, 0, 0);

		Vector2 minimapUL = new Vector2(748, 48);

		surface.drawImage(minimap, minimapUL.x - 8, minimapUL.y - 8);

		for (Actor a : actors)
			a.renderToMinimap(minimapUL, surface);

		float tileSize = pc.getMaxHP() > 50 ? 8 : 20;

		for (int i = 0; i < pc.getMaxHP(); ++i) {
			int x = i % (int) (500 / tileSize);
			int y = i / (int) (500 / tileSize);

			Image img = null;

			if (i >= (int) pc.hp)
				img = healthDepleted;
			else
				img = healthFull;

			surface.drawImage(img, x * tileSize + 20, y * tileSize + 20, tileSize, tileSize);
		}

		// overlay.canvas().clear();
		// overlay.canvas().setFillColor(0xff00ff00);
		surface.drawText("Credits: " + (int) credits, 20, 740);

		int cy = 500;

		for (String s : upgradeText.split("\n"))
			surface.drawText(s, 20, cy += 18);

		// surface.drawImage(overlay, 0, 0);

		if (introScreen) {
			surface.drawImage(introImage, 0, 0);
		}
	}

	public void update(float delta) {
		shotSoundThisFrame = false;
		hitSoundThisFrame = false;

		if (pc != null && !pc.keep()) {
			pc = null;
			// mouse().setListener(pc);
			// keyboard().setListener(pc);
			Gdx.input.setInputProcessor(pc);
		}
		if (pc == null) {
			credits *= 0.6f;
			pc = new Fighter(new Vector2(56, 56));
			actors.add(pc);
			// mouse().setListener(pc);
			// keyboard().setListener(pc);

			Gdx.input.setInputProcessor(pc);
		}

		if (introScreen || titleScreen || defeatScreen || victoryScreen)
			return;

		float alliedPop = 0;
		float enemyPop = 0;

		for (int i = 0; i < actors.size(); ++i) {
			if (actors.get(i).keep()) {
				actors.get(i).update();

				if (actors.get(i) instanceof Planet) {
					Planet p = (Planet) actors.get(i);

					if (p.ownedByPlayer)
						alliedPop += p.population;
					else
						enemyPop += p.population;
				}
			} else {
				actors.get(i).destroyed();
				actors.remove(i);
				--i;
			}
		}

		if (alliedPop < 0.01f)
			defeatScreen = true;

		if (enemyPop < 0.01f)
			victoryScreen = true;

		world.step(0.0016f, 3, 3);
	}

	public int updateRate() {
		return 16;
	}
}
