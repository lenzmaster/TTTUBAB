package bot.linearcombinationcalculation;

import bot.mcst.IGameState;


public interface IConicalCombination {
	
	public void setGameState(IGameState gameState);
	
	public IGameState getGameState();
	
	public float calculate();
	
}
