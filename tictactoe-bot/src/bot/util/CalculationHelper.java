package bot.util;

public class CalculationHelper {

	public static float invertActionValue(float actionValue){
		float differenceToNeutralActionValue = GlobalDefinitions.ACTION_VALUE_NEUTRAL_VALUE - actionValue;
		return GlobalDefinitions.ACTION_VALUE_NEUTRAL_VALUE + differenceToNeutralActionValue;
		
	}
	
	
}
