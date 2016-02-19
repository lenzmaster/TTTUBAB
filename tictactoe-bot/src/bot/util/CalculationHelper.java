package bot.util;

public class CalculationHelper {

	public static float invertActionValue(float actionValue){
		float differenceToNeutralActionValue = GlobalDefinitions.ACTION_VALUE_NEUTRAL_VALUE - actionValue;
		return GlobalDefinitions.ACTION_VALUE_NEUTRAL_VALUE + differenceToNeutralActionValue;
		
	}
	
	public static float invertNodeEvaluationValue(float nodeEvaluationValue){
		float differenceToNeutralNodeEvaluationValue = GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE - nodeEvaluationValue;
		return GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE + differenceToNeutralNodeEvaluationValue;
		
	}
	
}
