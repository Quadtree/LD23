package com.ironalloygames.spaceimperator.core.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.ironalloygames.spaceimperator.core.LD23;

public class DesktopLauncher {
	public static void main(String[] arg) {

		Settings st = new Settings();
		st.maxWidth = 2048;
		st.maxHeight = 2048;
		TexturePacker.processIfModified(st, "../../raw_assets/", ".", "./main");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new LD23(), config);
	}
}
