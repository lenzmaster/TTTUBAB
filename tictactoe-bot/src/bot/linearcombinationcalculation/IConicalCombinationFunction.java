package bot.linearcombinationcalculation;

import bot.mcst.IGameState;

public interface IConicalCombinationFunction {

	public void setGameState(IGameState gameState);
	
	public IGameState getGameState();
	
	public float calculate();
	
}
