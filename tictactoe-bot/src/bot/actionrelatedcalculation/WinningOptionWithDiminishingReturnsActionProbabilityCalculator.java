package bot.actionrelatedcalculation;

import bot.Field;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.mcst.IAction;
import bot.mcst.IGameState;
import bot.util.CalculationHelper;
import bot.winningoption.WinningOptionWithDiminishingReturnsScoreCalculator;

public class WinningOptionWithDiminishingReturnsActionProbabilityCalculator implements IActionCalculator {
	
	private int potencyBasis;
	private float diminishingBaseFactor;
	private float diminishingFactorsThreshold;
	private float lowerBound;
	private float upperBound;
	private float neutralValue;
	private float notOccupiedValue;
	
	private WinningOptionWithDiminishingReturnsScoreCalculator calculator = null;
	

	public WinningOptionWithDiminishingReturnsActionProbabilityCalculator(
			int potencyBasis, float diminishingBaseFactor, float diminishingFactorsThreshold,
			float lowerBound, float upperBound, float neutralValue,
			float notOccupiedValue){
		this.potencyBasis = potencyBasis;
		this.diminishingBaseFactor = diminishingBaseFactor;
		this.diminishingFactorsThreshold = diminishingFactorsThreshold;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.neutralValue = neutralValue;
		this.notOccupiedValue = notOccupiedValue;
		
		this.calculator = new WinningOptionWithDiminishingReturnsScoreCalculator(
				potencyBasis, diminishingBaseFactor, diminishingFactorsThreshold,
				lowerBound, upperBound, neutralValue, notOccupiedValue);
	}
	
	
	
	@Override
	public float calculate(IGameState oldGameState, IAction action) {
		IAction[] wrappedAction = new IAction[1];
		wrappedAction[0] = action;
		return calculate(oldGameState, wrappedAction)[0];
	}

	@Override
	public float[] calculate(IGameState oldGameState, IAction[] actions) {
		float[] result;
		if (actions.length == 0){
			result = this.calculator.calculate(oldGameState, null);
		} else {
			result = this.calculator.calculate(oldGameState, actions);
		}
		//Invert values if opponent is at turn, since the calculated value is not universally valid
		Field fieldOld = (Field) oldGameState;
		if (fieldOld.getPlayerAtTurn() == Player.getPlayer(PlayerTypes.Opponent)){
			for (int i = 0; i < result.length; i++){
				result[i] = CalculationHelper.invertValue(result[i], neutralValue);
			}
		}
		return result;
	}

}
