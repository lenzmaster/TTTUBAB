package bot.time;

import bot.Field;
import bot.mcst.MCSTNode;
import bot.util.GlobalDefinitions;
import bot.util.Logger;

public class FieldDensityTimeCalculator  implements ITimeCalculator{

	private static Logger LOGGER = new Logger("FieldDensityTimeCalculator");
	
	//Indicates a threshold, at which a different time consumption should be used.
	//The threshold amount needs to be equal to the time consumption percentages amount.
	//Additionally the thresholds need to be ordered asc.
	private static final float[] DENSITY_THRESHOLDS = new float[]{0.35f, 0.50f, 0.65f};
	
	private static final long[] TIME_CONSUMPTION_PERCENTAGES = new long[]{20, 30, 50};
	
	@Override
	public long calculateTimeForTurn(MCSTNode root, long timeInBank) {
		Field currentField = (Field) root.getGameState();
		if (currentField == null){
			//First turn
			return GlobalDefinitions.TIME_USED_PER_TURN_MIN * GlobalDefinitions.TIME_MS_TO_NS_FACTOR;
		}
		int[][] macroboard = currentField.getCopyOfMacroboard();
		float sumOfDensities = 0;
		for (int x = 0; x < macroboard.length; x++){
			for (int y = 0; y < macroboard[x].length; y++){
				if (macroboard[x][y] == GlobalDefinitions.PLAYER_NEUTRAL_ID ||
						macroboard[x][y] == GlobalDefinitions.MACRO_FIELD_NEEDS_TO_BE_USED_ID){
					//Field is not won
					sumOfDensities += calculateDensity(currentField.getCopyOfMicroboard(x, y));
				} else {
					//Field is won
					sumOfDensities++;
				}
			}
		}
		
		float densityTotal = sumOfDensities / 9;
		return calculateTimeForTurn(densityTotal, timeInBank);
	}
	
	
	
	/**
	 * Calculates the density of a microboard.
	 * @param microboard
	 * @return
	 */
	private float calculateDensity(int[][] microboard){
		float sumOfField = 0;
		for (int x = 0; x < microboard.length; x++){
			for (int y = 0; y < microboard[x].length; y++){
				if (microboard[x][y] != GlobalDefinitions.PLAYER_NEUTRAL_ID){
					sumOfField++;
				}
			}
		}
		return sumOfField / 9;
	}
	
	private long calculateTimeForTurn(float density, long timeInBank){
		//Determine time consumption percentage to use
		long timeConsumptionPercentage = 0;
		for (int i = 0; i < DENSITY_THRESHOLDS.length; i++){
			if (density > DENSITY_THRESHOLDS[i]){
				timeConsumptionPercentage =TIME_CONSUMPTION_PERCENTAGES[i];
			} else {
				break;
			}
		}
		
		//Calculate time for move
		long timeUsedPerTurnMinInNs = GlobalDefinitions.TIME_USED_PER_TURN_MIN * GlobalDefinitions.TIME_MS_TO_NS_FACTOR;
		long safetyStockInNs = GlobalDefinitions.TIME_SAFETY_STOCK * GlobalDefinitions.TIME_MS_TO_NS_FACTOR; 
		long time;
		if (timeInBank < safetyStockInNs){
			time = timeUsedPerTurnMinInNs;
		} else {
			time = timeUsedPerTurnMinInNs + (timeInBank - safetyStockInNs) * timeConsumptionPercentage / 100;
		}
		
		LOGGER.log("Density of gamestate: " + density);
		LOGGER.log("Time consumption percentage used: " + timeConsumptionPercentage);
		LOGGER.log("Time for turn used: " + time);
		return time;
	}

}
