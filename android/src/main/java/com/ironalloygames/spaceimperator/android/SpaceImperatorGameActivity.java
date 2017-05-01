package com.ironalloygames.spaceimperator.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import com.ironalloygames.spaceimperator.core.SpaceImperatorGame;

public class SpaceImperatorGameActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new SpaceImperatorGame());
  }
}
