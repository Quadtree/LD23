package com.ironalloygames.spaceimperator.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class PlayN {
	static class Assets {

		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));

		Map<String, Image> imgs = new HashMap<String, Image>();

		public Image getImage(String name) {
			name = name.replace(".png", "").replaceAll("images/", "");

			if (!imgs.containsKey(name)) {
				imgs.put(name, new Image(atlas.createSprite(name)));
			}

			return imgs.get(name);
		}
	}

	static Assets assets = null;

	public static Assets assets() {
		if (assets == null)
			assets = new Assets();

		return assets;
	}
}
