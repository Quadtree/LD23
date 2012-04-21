package com.ironalloygames.spaceimperator.core;

import playn.core.ImmediateLayer;
import playn.core.Surface;

public abstract class Actor {
	public void update(){}
	public void render(Surface target){}
	public boolean keep(){ return true; }
	public void created(){}
	public void destroyed(){}
}
