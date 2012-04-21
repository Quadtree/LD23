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
		
		actors.add(new Planet());
		
		actors.add(new Fighter(new Vec2(50,50)));
	}

	@Override
	public void paint(float alpha) {
	}

	@Override
	public void update(float delta) {
		world.step(0.0016f, 3, 3);
	}

	@Override
  	public int updateRate() {
		return 16;
	}

	@Override
	public void render(Surface surface) {
		for(Actor a : actors) a.render(surface);
	}
}
