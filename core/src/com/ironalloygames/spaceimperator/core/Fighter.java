package com.ironalloygames.spaceimperator.core;









public class Fighter extends Ship {
	
	static Image mainGraphic;
	static Image strafeLeftGraphic;
	static Image strafeRightGraphic;
	static Image thrustGraphic;

	public Fighter(Vec2 pos) {
		super(pos);
		
		guns.add(new BoltCannon(new Vec2(1,0)));
	}
	
	void graphicCheck(){
		if(mainGraphic == null)
		{
			mainGraphic = PlayN.assets().getImage("images/fighter.png");
			strafeLeftGraphic = PlayN.assets().getImage("images/fighter_sl.png");
			strafeRightGraphic = PlayN.assets().getImage("images/fighter_sr.png");
			thrustGraphic = PlayN.assets().getImage("images/fighter_fwd.png");
		}
	}

	@Override
	public Image getGraphic() {
		graphicCheck();
		return mainGraphic;
	}

	@Override
	public Image getForwardThrustGraphic() {
		return thrustGraphic;
	}

	@Override
	public Image getLeftThrustGraphic() {
		return strafeLeftGraphic;
	}

	@Override
	public Image getRightThrustGraphic() {
		return strafeRightGraphic;
	}

	@Override
	public float getThrustPower() {
		return 20;
	}

	@Override
	public float getTurnPower() {
		return 25.0f;
	}

	@Override
	Vec2 getSize() {
		return new Vec2(25.f / 16.f, 25.f / 16.f);
	}

	@Override
	public float getMaxHP() {
		return 7;
	}

	@Override
	public String getUpgradeText()
	{
		return  "The heavier cousin to the light fighter,\n" +
				"the heavy fighter features double the firepower\n" +
				"and increased armor. It does trade some agility\n" +
				"for this however.";
	}
	
	@Override
	public float getUpgradeCost()
	{
		return 200;
	}

	@Override
	public void upgrade() {
		replaced = true;
		
		Ship replacement = new HeavyFighter(body.getPosition());
		
		SpaceImperatorGame.s.actors.add(replacement);
		SpaceImperatorGame.s.pc = replacement;
		
		mouse().setListener(replacement);
		keyboard().setListener(replacement);
		
		super.upgrade();
	}
}
