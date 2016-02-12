package bot.priorprobabilitycalculation;

import bot.mcst.IGameState;

public interface IPriorProbabilityCalculator {

	public float calculate(IGameState oldGameState);
	
}
