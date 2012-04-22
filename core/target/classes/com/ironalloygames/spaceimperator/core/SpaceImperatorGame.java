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

import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.ImmediateLayer.Renderer;
import playn.core.Surface;

public class SpaceImperatorGame implements Game, Renderer, ContactListener {
	
	public final static int WINDOW_WIDTH = 1024;
	public final static int WINDOW_HEIGHT = 768;
	
	public final static int WORLD_WIDTH = 600;
	public final static int WORLD_HEIGHT = 600;
	
	public static SpaceImperatorGame s;
	
	public World world;
	
	public Ship pc;
	
	ArrayList<Actor> actors = new ArrayList<Actor>();
	
	Random rand = new Random();
	
	Image minimap;
	
	@Override
	public void init() {
		s = this;
		
		graphics().rootLayer().add(graphics().createImmediateLayer(this));
		
		world = new World(new Vec2(), false);
		world.setContactListener(this);
		
		actors.add(new Planet(new Vec2(), Planet.PlanetSize.Tiny, true));
		
		//actors.add(new Planet(new Vec2(0, 40), Planet.PlanetSize.Tiny, false));
		
		for(int i=0;i<10;++i)
			actors.add(new Planet());
		
		graphics().setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		actors.add(new Fighter(new Vec2(30,30)));
		
		minimap = assets().getImage("images/minimap.png");
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
			pc = new Fighter(new Vec2(20,20));
			actors.add(pc);
			mouse().setListener(pc);
			keyboard().setListener(pc);
		}
		
		for(int i=0;i<actors.size();++i)
		{
			if(actors.get(i).keep())
			{
				actors.get(i).update();
			} else {
				actors.get(i).destroyed();
				actors.remove(i);
				--i;
			}
		}
		
		world.step(0.0016f, 3, 3);
	}

	@Override
  	public int updateRate() {
		return 16;
	}

	@Override
	public void render(Surface surface) {
		pc.cameraTrack(surface);
		
		for(Actor a : actors) a.render(surface);
		
		surface.setTransform(1, 0, 0, 1, 0, 0);
		
		Vec2 minimapUL = new Vec2(748, 48);
		
		surface.drawImage(minimap, minimapUL.x - 8, minimapUL.y - 8);
		
		for(Actor a : actors) a.renderToMinimap(minimapUL, surface);
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
