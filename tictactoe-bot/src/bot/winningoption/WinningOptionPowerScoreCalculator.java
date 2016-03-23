package bot.winningoption;

import bot.Point;

public class WinningOptionPowerScoreCalculator implements IWinningOptionScoreCalculator{

	private int potencyBasis;
	private float lowerBound;
	private float upperBound;
	private float notOccupiedValue;
	private float maxIntermediateValue;
	private float maxPossibleScoreDifference;
	
	@Override
	public float getMaxPossibleScoreDifference() {
		return maxPossibleScoreDifference;
	}
	
	/**
	 * Instantiates an object using the defined scale between lower and upper bound.
	 * Additionally, a potency basis has to be given as well as
	 * a not occupied value. The not occupied value must not be in the range of the scale.
	 * @param potencyBasis
	 * @param lowerBound
	 * @param upperBound
	 * @param notOccupiedValue
	 */
	public WinningOptionPowerScoreCalculator(int potencyBasis, float lowerBound, float upperBound, float notOccupiedValue){
		this.potencyBasis = potencyBasis;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.notOccupiedValue = notOccupiedValue;
		this.maxIntermediateValue = ((float) Math.pow(potencyBasis, 4 * upperBound));
		this.maxPossibleScoreDifference = calculateMaxPossibleScoreDifference(potencyBasis, upperBound, maxIntermediateValue);
	}
	
	
	private float calculateMaxPossibleScoreDifference(float potencyBasis, float upperBound,
			float maxIntermediateValue) {
		float lowerValue = ((float) Math.pow(potencyBasis, 3 * upperBound));
		return maxIntermediateValue - lowerValue;
	}
	
	/**
	 * Calculates the winning option scores for the given transformed board and the given player.
	 * The given board needs to have the dimension: 3x3.
	 * The score of an option is: potencyBasis^(scoreOfOwnTilesInOption + 1)
	 * and 0 for every option, in which the opponent has at least 1 tile occupied
	 * @param transformedBoard the board to calculate the score on; the board values need to be scaled according to the scale defined by lowerBound and upperBound
	 * @param player
	 * @return
	 */
	public float[] calcScores(float[][] transformedBoard){		
		WinningOptions[] winningOptions = WinningOptions.values();
		float[] winningOptionScores = new float[winningOptions.length];
		for (int i = 0; i < winningOptions.length; i++){
			WinningOptions currentWinningOption = winningOptions[i];
			Point winningTiles[] = currentWinningOption.getTiles();
			boolean winningOptionAvailable = true;
			float exponent = 0;
			innerLoop:
				for (Point point : winningTiles) {
					float tileValue = transformedBoard[point.getX()][point.getY()];
					if (tileValue == this.lowerBound){//Opponent owns tile
						winningOptionAvailable = false;
						break innerLoop;
					} else if (tileValue == this.upperBound){//Player owns tile
						exponent += this.upperBound;
					} else if (tileValue == this.notOccupiedValue) {// No one owns tile
						//Do nothing
						
					} else {
						//Add value of tile
						exponent += tileValue;
					}
				}
			
			if (winningOptionAvailable){
				//Add 1 to exponent, since the winning option is available
				exponent++;
				//Calculate Winning option score for the i´s winning option
				//otherwise using the formula: potencyBasis ^ exponent
				winningOptionScores[i] = (float) Math.pow(potencyBasis, exponent);
			} else {
				//Set winning option score to lower bound, since opponent destroyed the winning option
				winningOptionScores[i] = this.lowerBound;
			}
		}
		
		return winningOptionScores;
	}

	@Override
	public float getMaxIntermediateValue() {
		return maxIntermediateValue;
	}

}
