package com.ironalloygames.spaceimperator.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class PlayN {
	static class Assets {

		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));

		Map<String, Image> imgs = new HashMap<String, Image>();
		Map<String, Sound> sounds = new HashMap<String, Sound>();

		public Image getImage(String name) {
			name = name.replace(".png", "").replaceAll("images/", "");

			if (!imgs.containsKey(name)) {
				imgs.put(name, new Image(atlas.createSprite(name)));
			}

			return imgs.get(name);
		}

		public Sound getSound(String name) {
			name = name.replaceAll("sounds/", "");

			if (!sounds.containsKey(name)) {
				sounds.put(name, Gdx.audio.newSound(Gdx.files.internal(name)));
			}

			return sounds.get(name);
		}
	}

	static Assets assets = null;

	public static Assets assets() {
		if (assets == null)
			assets = new Assets();

		return assets;
	}
}
