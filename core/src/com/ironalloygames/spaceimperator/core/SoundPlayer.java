package com.ironalloygames.spaceimperator.core;

import java.util.HashMap;

import com.badlogic.gdx.audio.Sound;

public class SoundPlayer {
	static Map<String, Sound[]> sounds = new HashMap<String, Sound[]>();

	static void play(String name) {
		if (!sounds.containsKey(name)) {
			sounds.put(name, new Sound[10]);

			for (int i = 0; i < 10; ++i)
				sounds.get(name)[i] = PlayN.assets().getSound(name);
		}

		sounds.get(name)[SpaceImperatorGame.s.rand.nextInt(10)].play();
	}
}
