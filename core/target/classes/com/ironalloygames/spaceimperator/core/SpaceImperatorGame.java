package com.ironalloygames.spaceimperator.core;

import static playn.core.PlayN.*;

import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import playn.core.CanvasImage;
import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.ImmediateLayer.Renderer;
import playn.core.Surface;
import playn.core.SurfaceLayer;

public class SpaceImperatorGame implements Game, Renderer, ContactListener {
	
	public final static int WINDOW_WIDTH = 1024;
	public final static int WINDOW_HEIGHT = 768;
	
	public final static int WORLD_WIDTH = 600;
	public final static int WORLD_HEIGHT = 600;
	
	Image dust;
	
	public static SpaceImperatorGame s;
	
	public World world;
	
	public Ship pc;
	
	ArrayList<Actor> actors = new ArrayList<Actor>();
	
	Random rand = new Random();
	
	Image minimap;
	
	Image healthFull;
	Image healthDepleted;
	
	CanvasImage overlay;
	
	float credits = 0;
	
	Vec2[] dustPos;
	
	String upgradeText = "";
	
	boolean titleScreen = true;
	boolean introScreen = true;
	boolean victoryScreen = false;
	boolean defeatScreen = false;
	
	Image titleImage;
	Image introImage;
	Image victoryImage;
	Image defeatImage;
	
	@Override
	public void init() {
		s = this;
		
		graphics().rootLayer().add(graphics().createImmediateLayer(this));
		
		world = new World(new Vec2(), false);
		world.setContactListener(this);
		
		actors.add(new Planet(new Vec2(50,50), Planet.PlanetSize.Tiny, true));
		
		overlay = graphics().createImage(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		//actors.add(new Planet(new Vec2(0, 40), Planet.PlanetSize.Tiny, false));
		
		actors.add(new Planet(new Vec2(550, 050), Planet.PlanetSize.Tiny, false));
		actors.add(new Planet(new Vec2(050, 550), Planet.PlanetSize.Tiny, false));
		actors.add(new Planet(new Vec2(350, 050), Planet.PlanetSize.Tiny, false));
		actors.add(new Planet(new Vec2(050, 350), Planet.PlanetSize.Tiny, false));
		actors.add(new Planet(new Vec2(250, 250), Planet.PlanetSize.Tiny, false));
		
		actors.add(new Planet(new Vec2(550, 250), Planet.PlanetSize.Small, false));
		actors.add(new Planet(new Vec2(350, 350), Planet.PlanetSize.Small, false));
		actors.add(new Planet(new Vec2(250, 550), Planet.PlanetSize.Small, false));
		
		actors.add(new Planet(new Vec2(420, 550), Planet.PlanetSize.Medium, false));
		actors.add(new Planet(new Vec2(550, 420), Planet.PlanetSize.Medium, false));
		
		actors.add(new Planet(new Vec2(550, 550), Planet.PlanetSize.Large, false));
		
		graphics().setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		//actors.add(new Frigate(new Vec2(100,100)));
		
		minimap = assets().getImage("images/minimap.png");
		
		healthFull = assets().getImage("images/health_full.png");
		healthDepleted = assets().getImage("images/health_depleted.png");
		
		dust = assets().getImage("images/dust.png");
		
		dustPos = new Vec2[20];
		
		for(int i=0;i<dustPos.length;++i)
			dustPos[i] = new Vec2(rand.nextFloat() * WINDOW_WIDTH, rand.nextFloat() * WINDOW_HEIGHT);
		
		titleImage = assets().getImage("images/title.png");
		introImage = assets().getImage("images/intro.png");
		victoryImage = assets().getImage("images/victory.png");
		defeatImage = assets().getImage("images/defeat.png");
	}

	@Override
	public void paint(float alpha) {
	}

	@Override
	public void update(float delta) {
		
		
		if(pc != null && !pc.keep())
		{
			pc = null;
			mouse().setListener(pc);
			keyboard().setListener(pc);
		}
		if(pc == null)
		{
			credits *= 0.6f;
			pc = new Fighter(new Vec2(56,56));
			actors.add(pc);
			mouse().setListener(pc);
			keyboard().setListener(pc);
		}
		
		if(introScreen || titleScreen || defeatScreen || victoryScreen) return;
		
		float alliedPop = 0;
		float enemyPop = 0;
		
		for(int i=0;i<actors.size();++i)
		{
			if(actors.get(i).keep())
			{
				actors.get(i).update();
				
				if(actors.get(i) instanceof Planet)
				{
					Planet p = (Planet) actors.get(i);
					
					if(p.ownedByPlayer)
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
		
		if(alliedPop < 0.01f)
			defeatScreen = true;
		
		if(enemyPop < 0.01f)
			victoryScreen = true;
		
		world.step(0.0016f, 3, 3);
	}

	@Override
  	public int updateRate() {
		return 16;
	}

	@Override
	public void render(Surface surface) {
		
		if(titleScreen)
		{
			surface.drawImage(titleImage, 0, 0);
			return;
		}
		
		if(defeatScreen)
		{
			surface.drawImage(defeatImage, 0, 0);
			return;
		}
		
		if(victoryScreen)
		{
			surface.drawImage(victoryImage, 0, 0);
			return;
		}
		
		
		pc.drawStarfield(surface);
		
		float dustFacing = (float)Math.atan2(pc.body.getLinearVelocity().y, pc.body.getLinearVelocity().x);
		float dustLength = pc.body.getLinearVelocity().length() / 4;
		
		for(Vec2 v2 : dustPos)
		{
			v2.addLocal(pc.body.getLinearVelocity().mul(-0.016f));
			
			if(v2.x > WINDOW_WIDTH + 50 ||
			   v2.y > WINDOW_HEIGHT + 50 ||
			   v2.x < -50 ||
			   v2.y < -50)
			{
				if(rand.nextBoolean())
				{
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
		
		for(Actor a : actors) a.render(surface);
		
		surface.setTransform(1, 0, 0, 1, 0, 0);
		
		Vec2 minimapUL = new Vec2(748, 48);
		
		surface.drawImage(minimap, minimapUL.x - 8, minimapUL.y - 8);
		
		for(Actor a : actors) a.renderToMinimap(minimapUL, surface);
		
		float tileSize = pc.getMaxHP() > 50 ? 8 : 20;
		
		for(int i=0;i<pc.getMaxHP();++i)
		{
			int x = i % (int)(500 / tileSize);
			int y = i / (int)(500 / tileSize);
			
			Image img = null;
			
			if(i >= (int)pc.hp)
				img = healthDepleted;
			else
				img = healthFull;
			
			surface.drawImage(img, x * tileSize + 20, y * tileSize + 20, tileSize, tileSize);
		}
		
		overlay.canvas().clear();
		overlay.canvas().setFillColor(0xff00ff00);
		overlay.canvas().drawText("Credits: " + (int)credits, 20, 740);
		
		int cy = 500;
		
		for(String s : upgradeText.split("\n"))
			overlay.canvas().drawText(s, 20, cy += 18);
		
		surface.drawImage(overlay, 0, 0);
		
		if(introScreen)
		{
			surface.drawImage(introImage, 0, 0);
		}
	}

	@Override
	public void beginContact(Contact contact) {
		Object u1 = contact.getFixtureA().getBody().getUserData();
		Object u2 = contact.getFixtureB().getBody().getUserData();
		
		if(u1 != null && u2 != null && u1 instanceof Actor && u2 instanceof Actor)
		{
			((Actor)u1).collidedWith((Actor)u2);
			((Actor)u2).collidedWith((Actor)u1);
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
