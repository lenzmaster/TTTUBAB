package bot.util;

import bot.Player;
import bot.Player.PlayerTypes;
import bot.winningoption.WinningOptions;

public class CalculationHelper {

	public static float invertActionValue(float actionValue){
		float differenceToNeutralActionValue = GlobalDefinitions.ACTION_VALUE_NEUTRAL_VALUE - actionValue;
		return GlobalDefinitions.ACTION_VALUE_NEUTRAL_VALUE + differenceToNeutralActionValue;
		
	}
	
	public static float invertNodeEvaluationValue(float nodeEvaluationValue){
		float differenceToNeutralNodeEvaluationValue = GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE - nodeEvaluationValue;
		return GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE + differenceToNeutralNodeEvaluationValue;
		
	}
	
	public static float[] createDiminishingReturnFactors(float diminishingBaseFactor, float diminishingFactorsThreshold){
		int numberOfWinningOptions = WinningOptions.values().length;
		float[] temp = new float[numberOfWinningOptions];
		for (int i = 0; i < numberOfWinningOptions; i++) {
			temp[i] = (float) Math.pow(diminishingBaseFactor, i);
			//The array needs only to be as long till the threshold is reached
			if (temp[i] < diminishingFactorsThreshold){
				float[] result = new float[i];
				for (int j = 0; j < i; j++) {//Don´t include the last element in temp, since it contains the value below the threshold
					result[j] = temp[j];
				}
				return result;
			}
		}
		//The threshold was never reached
		return temp;
	}
	
}
