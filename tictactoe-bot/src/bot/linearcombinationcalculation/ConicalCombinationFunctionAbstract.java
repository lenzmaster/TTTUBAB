package bot.linearcombinationcalculation;

import bot.mcst.IGameState;

public abstract class ConicalCombinationFunctionAbstract implements IConicalCombinationFunction{

	private IGameState gameState;
	
	@Override
	public void setGameState(IGameState gameState){
		this.gameState = gameState;
	}
	
	@Override
	public IGameState getGameState(){
		return gameState;
	}
	
}
