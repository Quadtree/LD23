package com.ironalloygames.spaceimperator.html;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

import com.ironalloygames.spaceimperator.core.SpaceImperatorGame;

public class SpaceImperatorGameHtml extends HtmlGame {

  @Override
  public void start() {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    // use config to customize the HTML platform, if needed
    HtmlPlatform platform = HtmlPlatform.register(config);
    platform.assets().setPathPrefix("spaceimperator/");
    PlayN.run(new SpaceImperatorGame());
  }
}
