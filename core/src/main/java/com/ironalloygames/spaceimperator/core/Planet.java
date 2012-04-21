package com.ironalloygames.spaceimperator.core;

import java.util.EnumMap;

import playn.core.Image;
import playn.core.ImmediateLayer;
import playn.core.PlayN;
import playn.core.Surface;

public class Planet extends Actor {
	
	enum PlanetSize
	{
		Tiny,
		Small,
		Medium,
		Large
	}
	
	static EnumMap<PlanetSize, Image> planetGraphics;

	@Override
	public void render(Surface target) {
		
		if(planetGraphics == null)
		{
			planetGraphics = new EnumMap<PlanetSize, Image>(PlanetSize.class);
			
			planetGraphics.put(PlanetSize.Tiny, PlayN.assets().getImage("images/planet_tiny.png"));
		}
		
		target.drawImage(planetGraphics.get(PlanetSize.Tiny), 0, 0);
		
		super.render(target);
	}

}
