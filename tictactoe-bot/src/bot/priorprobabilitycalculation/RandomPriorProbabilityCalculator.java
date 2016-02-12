package bot.priorprobabilitycalculation;

import java.util.Random;

import bot.mcst.IGameState;

public class RandomPriorProbabilityCalculator implements IPriorProbabilityCalculator{

	private static final int UPPERBOUND = 100;
	private static final float BASEVALUE = 0.5f;
	
	private Random rnd;
	
	public RandomPriorProbabilityCalculator() {
		rnd = new Random();
	}
	
	@Override
	public float calculate(IGameState oldGameState) {
		float randomizedComponent = ((float) rnd.nextInt(UPPERBOUND)) / 100000f;
		float result = BASEVALUE + randomizedComponent;
		return result;
	}

}
