package bot.actionrelatedcalculation.priorprobabilitycalculation;

import bot.actionrelatedcalculation.WinningOptionWithDiminishingReturnsActionValueCalculator;
import bot.util.GlobalDefinitions;

public class WinningOptWithDimReturnPriorProbabilityCalculator extends WinningOptionWithDiminishingReturnsActionValueCalculator
														implements IPriorProbabilityCalculator{
	
	private static int potencyBasis = 3;
	
	private static float diminishingBaseFactor = 0.4f;
	
	//The threshold that indicates when a exponentiated diminishing factor is not used anymore, due to inefficiency
	//The threshold must not be lower than the diminishing factor
	private static float diminishingFactorsThreshold = 0.01f;
	//TODO: Implement the notOccupiedValue variable more beautiful;
	//Right now it is very arbitrarily to set it to -1 and can´t be used in the formula, since the neutral element of the an addition
	//is already used as the opponents value
	private static float notOccupiedValue = -1;// Needs to out of the range of lower and upper bound 
	
	public WinningOptWithDimReturnPriorProbabilityCalculator() {
		super(potencyBasis, diminishingBaseFactor, diminishingFactorsThreshold,
				GlobalDefinitions.NODE_PRIOR_PROBABILITY_LOWER_BOUND,
				GlobalDefinitions.NODE_PRIOR_PROBABILITY_UPPER_BOUND,
				GlobalDefinitions.NODE_PRIOR_PROBABILITY_NEUTRAL_VALUE,
				notOccupiedValue);
	}

}
