package com.ironalloygames.spaceimperator.core;

import static playn.core.PlayN.*;

import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.ImmediateLayer.Renderer;
import playn.core.Surface;

public class SpaceImperatorGame implements Game, Renderer {
	
	public final static int WINDOW_WIDTH = 1024;
	public final static int WINDOW_HEIGHT = 768;
	
	public static SpaceImperatorGame s;
	
	public World world;
	
	Ship pc;
	
	ArrayList<Actor> actors = new ArrayList<Actor>();
	
	Random rand = new Random();
	
	@Override
	public void init() {
		s = this;
		
		graphics().rootLayer().add(graphics().createImmediateLayer(this));
		
		world = new World(new Vec2(), false);
		
		actors.add(new Planet(new Vec2(), Planet.PlanetSize.Tiny));
		
		graphics().setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
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
	}
}
