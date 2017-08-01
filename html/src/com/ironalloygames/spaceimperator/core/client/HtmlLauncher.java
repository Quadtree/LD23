package com.ironalloygames.spaceimperator.core.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.ironalloygames.spaceimperator.core.LD23;

public class HtmlLauncher extends GwtApplication {

	@Override
	public ApplicationListener createApplicationListener() {
		return new LD23();
	}

	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(1024, 768);
	}
}