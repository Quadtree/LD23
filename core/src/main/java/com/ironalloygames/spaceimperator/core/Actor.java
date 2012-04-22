package com.ironalloygames.spaceimperator.core;

import org.jbox2d.dynamics.Body;

import playn.core.ImmediateLayer;
import playn.core.Surface;

public abstract class Actor {
	public void update(){}
	public void render(Surface target){}
	public boolean keep(){ return true; }
	public void created(){}
	public void destroyed(){}
	
	Body body;
	
	short colGroup;
	
	static boolean[] claimedColGroups = new boolean[32768];
	
	void claimColGroup()
	{
		for(int i=1;i<32768;++i)
		{
			if(!claimedColGroups[i])
			{
				colGroup = (short)i;
				claimedColGroups[colGroup] = true;
				break;
			}
		}
	}
}
