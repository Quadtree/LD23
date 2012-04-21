package com.ironalloygames.spaceimperator.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import com.ironalloygames.spaceimperator.core.SpaceImperatorGame;

public class SpaceImperatorGameJava {

  public static void main(String[] args) {
    JavaPlatform platform = JavaPlatform.register();
    platform.assets().setPathPrefix("com/ironalloygames/spaceimperator/resources");
    PlayN.run(new SpaceImperatorGame());
  }
}
