package com.ironalloygames.spaceimperator.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import com.ironalloygames.spaceimperator.core.SpaceImperatorGame;

public class SpaceImperatorGameJava {

  public static void main(String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    // use config to customize the Java platform, if needed
    JavaPlatform.register(config);
    PlayN.run(new SpaceImperatorGame());
  }
}
