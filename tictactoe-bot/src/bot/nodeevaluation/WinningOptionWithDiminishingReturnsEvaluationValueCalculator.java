package bot.nodeevaluation;

import bot.Field;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.linearcombinationcalculation.ConicalCombinationFunctionAbstract;
import bot.util.BoardTransformer;
import bot.util.GlobalDefinitions;
import bot.util.Logger;
import bot.winningoption.DiminishingReturnWinningOptionsAggregationScoreCalculator;
import bot.winningoption.WinningOptionPowerScoreCalculator;
import bot.winningoption.WinningOptionWithDiminishingReturnsScoreCalculator;

//ToDo: Consider interdependencies between enemies dominance in a (sub) board and own position

/**
 * Usage of diminishing returns for additional winning options.
 * The algorithm transforms
 * @author André
 *
 */
public class WinningOptionWithDiminishingReturnsEvaluationValueCalculator extends ConicalCombinationFunctionAbstract{

	private static int potencyBasis = 3;
	
	private static float diminishingBaseFactor = 0.4f;
	
	//The threshold that indicates when a exponentiated diminishing factor is not used anymore, due to inefficiency
	//The threshold must not be lower than the diminishing factor
	private static float diminishingFactorsThreshold = 0.01f;
	
	private static Logger LOGGER = new Logger("WinningOptionWithDiminishingReturnsEvaluationValueCalculator");

	
	private static float lowerBound = GlobalDefinitions.NODE_EVALUATION_LOWER_BOUND;//Equal to enemy winning value
	private static float neutralValue = GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE;//
	private static float upperBound = GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND;//Equal to self winning value
	//TODO: Implement the notOccupiedValue variable more beautiful;
	//Right now it is very arbitrarily to set it to -1 and can´t be used in the formula, since the neutral element of the an addition
	//is already used as the opponents value
	private static float notOccupiedValue = -1;// Needs to out of the range of lower and upper bound
	
	private WinningOptionWithDiminishingReturnsScoreCalculator calculator = null;
	
	/**
	 * Constructor
	 */
	public WinningOptionWithDiminishingReturnsEvaluationValueCalculator(){
		calculator = new WinningOptionWithDiminishingReturnsScoreCalculator(potencyBasis, 
				diminishingBaseFactor, diminishingFactorsThreshold, lowerBound, upperBound, 
				neutralValue, notOccupiedValue);
	}
	
	@Override
	public float calculate() {
		return calculator.calculate(getCurrentNode().getGameState());
	}
}
