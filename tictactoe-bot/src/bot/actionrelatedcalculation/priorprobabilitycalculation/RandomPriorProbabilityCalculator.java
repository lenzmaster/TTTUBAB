package bot.actionrelatedcalculation.priorprobabilitycalculation;

import java.util.Random;

import bot.mcst.IAction;
import bot.mcst.IGameState;
import bot.util.GlobalDefinitions;

public class RandomPriorProbabilityCalculator implements IPriorProbabilityCalculator{

	private static final int UPPERBOUND = 100;
	
	private Random rnd;
	
	public RandomPriorProbabilityCalculator() {
		rnd = new Random();
	}
	
	@Override
	public float calculate(IGameState oldGameState, IAction action) {
		
		float randomizedComponent = ((float) rnd.nextInt(UPPERBOUND)) / 100000f;
		float result = GlobalDefinitions.NODE_PRIOR_PROBABILITY_NEUTRAL_VALUE + randomizedComponent;
		return result;
	}

	@Override
	public float[] calculate(IGameState oldGameState, IAction[] actions) {
		float[] result = new float[actions.length];
		for (int i = 0; i < result.length; i++){
			result[i] = this.calculate(oldGameState, actions[i]);
		}
		return result;
	}

}
