package bot.actionrelatedcalculation;

import bot.mcst.IAction;
import bot.mcst.IGameState;
import bot.winningoption.WinningOptionWithDiminishingReturnsScoreCalculator;

public class WinningOptionWithDiminishingReturnsActionValueCalculator implements IActionCalculator {
	
	private int potencyBasis;
	private float diminishingBaseFactor;
	private float diminishingFactorsThreshold;
	private float lowerBound;
	private float upperBound;
	private float neutralValue;
	private float notOccupiedValue;
	
	private WinningOptionWithDiminishingReturnsScoreCalculator calculator = null;
	

	public WinningOptionWithDiminishingReturnsActionValueCalculator(
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
		if (actions.length == 0){
			return this.calculator.calculate(oldGameState, null);
		}
		return this.calculator.calculate(oldGameState, actions);
	}

}
