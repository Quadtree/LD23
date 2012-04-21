package com.ironalloygames.spaceimperator.core;

import static playn.core.PlayN.*;

import java.util.ArrayList;

import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.ImmediateLayer.Renderer;
import playn.core.Surface;

public class SpaceImperatorGame implements Game, Renderer {
	
	Ship pc;
	
	ArrayList<Actor> actors = new ArrayList<Actor>();
	
	@Override
	public void init() {
		graphics().rootLayer().add(graphics().createImmediateLayer(this));
		
		actors.add(new Planet());
	}

	@Override
	public void paint(float alpha) {
		// the background automatically paints itself, so no need to do anything here!
	}

	@Override
	public void update(float delta) {
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
