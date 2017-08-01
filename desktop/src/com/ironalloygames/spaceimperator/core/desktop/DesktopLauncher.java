package com.ironalloygames.spaceimperator.core.desktop;





public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new LD23(), config);
	}
}
