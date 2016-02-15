package bot.linearcombinationcalculation;

import java.util.ArrayList;
import java.util.List;

import bot.mcst.IGameState;

public class ConicalCombinationAbstract implements IConicalCombination{

	private IGameState gameState;
	
	private List<IConicalCombinationFunction> functions = new ArrayList<IConicalCombinationFunction>();
	private List<Integer> weights = new ArrayList<Integer>();
	private int weightSum = 0;
	private int functionsCount = 0;
	
	@Override
	public void setGameState(IGameState gameState){
		this.gameState = gameState;
		for (IConicalCombinationFunction func : functions) {
			func.setGameState(gameState);
		}
	}
	
	@Override
	public IGameState getGameState(){
		return gameState;
	}
	

	
	public void addFunction(int weight, IConicalCombinationFunction function){
		this.weights.add(weight);
		this.functions.add(function);
		this.weightSum += weight;
		this.functionsCount++;
	}
	
	
	@Override
	public float calculate() {
		float result = 0;
		for (int i = 0; i < functionsCount; i++) {
			result += weights.get(i) * functions.get(i).calculate();
		}
		result = result / weightSum;
		return result;
	}
	
	
}
