package bot.winningoption;

import java.util.Arrays;

import bot.FieldCalculationHelper;
import bot.Player;
import bot.Point;
import bot.Player.PlayerTypes;
import bot.util.CalculationHelper;


//TODO: Check, if the class uses the upper and lower bound properly --> some calculations might be normed to [0;1]
//Multiplications with the upper or lower bound might be missing
public class DiminishingReturnWinningOptionsAggregationScoreCalculator {

	private float lowerBound;
	private float upperBound;
	private float notOccupiedValue;
	
	private IWinningOptionScoreCalculator winningOptionScoreCalculator = null;
	private float diminishingBaseFactor;
	private float diminishingFactorsThreshold;
	private float[] diminishingReturnFactors = null;
	
	private float denominator;
	
	private Player none = null;
	
	private Player getNone(){
		if (none == null){
			none = Player.getPlayer(PlayerTypes.None);
		}
		return none;
	}
	
	public IWinningOptionScoreCalculator getWinningOptionScoreCalculator(){
		return winningOptionScoreCalculator;
	}
	
	public DiminishingReturnWinningOptionsAggregationScoreCalculator(float lowerBound, float upperBound,
			float notOccupiedValue, IWinningOptionScoreCalculator winningOptionScoreCalculator,
											float diminishingBaseFactor, float diminishingFactorsThreshold){
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.notOccupiedValue = notOccupiedValue;
		this.winningOptionScoreCalculator = winningOptionScoreCalculator;
		this.diminishingBaseFactor = diminishingBaseFactor;
		this.diminishingFactorsThreshold = diminishingFactorsThreshold;
		this.diminishingReturnFactors = CalculationHelper.createDiminishingReturnFactors(diminishingBaseFactor, diminishingFactorsThreshold);
		this.initalizeDenominator(diminishingReturnFactors, winningOptionScoreCalculator);
	}
	
	/**
	 * Initializes the denominator. This should be done in the constructor.
	 * @param factors an array of the diminishing return factors
	 * @param winOptScoreCalculator
	 */
	private void initalizeDenominator(float[] factors , IWinningOptionScoreCalculator winOptScoreCalculator){
		//Sum up the diminishing return factors
//		float sumOfFactors = 0;
//		for (int i = 0; i < factors.length; i++){
//			sumOfFactors += factors[i];
//		}
		//Get the maximum value that a single winning option score can have
		float maxSingleScoreValue = winOptScoreCalculator.getMaxIntermediateValue();
		//This is the max achievable value --> using this as denominator will keep the result in the upper and lower bound
		//TODO: this value sucks --> find a solution that does not make the denominator that big
		this.denominator = maxSingleScoreValue; //sumOfFactors * maxSingleScoreValue;
	}
	
	/**
	 * Calculates the winning option scores for a given 3x3 board for both players.
	 * The winning option scores do not incorporate each other; they are separated at this point.
	 * In case untransforedMicroboard is set to null, it is assumed that no winner exists yet in the board
	 * @param untransformedMicroboard the untransformed board with the values defined in GlobalDefinitions
	 * @param transformedBoardSelf
	 * @param transformedBoardOpponent
	 * @return An array with two entries containing the score for each player.
	 */
	public float[] calculateScore(int[][] untransformedMicroboard, float[][] transformedBoardSelf,
							float[][] transformedBoardOpponent){
		//Check, if winner already exists
		Player winner = (untransformedMicroboard == null) ? null : Player.getPlayer(FieldCalculationHelper.getWinner(untransformedMicroboard));
		return calculateScore(winner, transformedBoardSelf, transformedBoardOpponent);
	}
	
	/**
	 * Calculates the winning option scores for a given 3x3 board for both players.
	 * The winning option scores do not incorporate each other; they are separated at this point.
	 * @param transformedBoardSelf
	 * @param transformedBoardOpponent
	 * @return An array with two entries containing the score for each player.
	 */
	public float[] calculateScore(float[][] transformedBoardSelf,
							float[][] transformedBoardOpponent){
		//Check, if winner already exists
		float winnerValue = FieldCalculationHelper.getWinner(transformedBoardSelf, lowerBound, upperBound, notOccupiedValue);
		Player winner;
		if (winnerValue == notOccupiedValue){
			winner = Player.getPlayer(PlayerTypes.None);
		} else if (winnerValue == upperBound){
			//Self won, since board of self was used
			winner = Player.getPlayer(PlayerTypes.Self);
		} else {
			//Opponent won, since board of self was used and the result had to be lowerBound
			winner = Player.getPlayer(PlayerTypes.Opponent);
		}
		return calculateScore(winner, transformedBoardSelf, transformedBoardOpponent);
	}
	
	/**
	 * Calculates the winning option scores for a given 3x3 board for both players.
	 * The winning option scores do not incorporate each other; they are separated at this point.
	 * In case there is no winner in this board yet the Player can be set to the player "none".
	 * @param winner the winner of the board or in case of no winner yet the player "none" or null
	 * @param transformedBoardSelf
	 * @param transformedBoardOpponent
	 * @return An array with two entries containing the score for each player.
	 */
	public float[] calculateScore(Player winner, float[][] transformedBoardSelf,
			float[][] transformedBoardOpponent){
		if (transformedBoardSelf.length != 3 || transformedBoardSelf[0].length != 3 ||
				transformedBoardOpponent.length != 3 || transformedBoardOpponent[0].length != 3){
			throw new IllegalArgumentException("The transformed boards do not have the dimension 3x3");
		}
		float[] scores = new float[2];
		//Check, if winner already exists
		if (winner == null || winner.getPlayerType() == PlayerTypes.None){
			//Board not won
			//Use formulas...
			//a. Calculate winning option scores
			float[] winningOptionScoresSelf = winningOptionScoreCalculator.calcScores(transformedBoardSelf);
			float[] winningOptionScoresOpponent = winningOptionScoreCalculator.calcScores(transformedBoardOpponent);
			
			//b. Aggregate winning option scores to one microboard score for each player
			scores[0] = this.calcBoardScore(winningOptionScoresSelf);
			scores[1] = this.calcBoardScore(winningOptionScoresOpponent);
		
		} else if (winner.getPlayerType() == PlayerTypes.Self){
			//Board won by self
			scores[0] = upperBound;// upper bound, because won by self
			scores[1] = lowerBound;//lower bound, because opponent lost board
		} else {
			//Board won by opponent
			scores[0] = lowerBound;// lower bound, because one lost that board
			scores[1] = upperBound;//upper bound, because opponent won board
		}
		return scores;
	}
	
	/**
	 * Calculates a board score based on the board´s winning options scores.
	 * The following formula is used:
	 * board score = sum(dimishingFactor^0 * s1 + dimishingFactor^1 * s2 + ...) / (potencyBasis^4 * sumOfDiminishingFactors)
	 * where the winningOptionScores s1...sn are ordered descendingly
	 * @param winningOptionsScores
	 * @return
	 */
	private float calcBoardScore(float[] winningOptionsScores){
		float weightedWinningOptionScoreSum = 0;
		
		//Sort winning option scores
		float[] ascOrderedScores = winningOptionsScores.clone();
		Arrays.sort(ascOrderedScores);
		
		//Calculate the weighted sum, but iterate backwards through the scores, since the scores are ordered ascendingly
		int counter = 0;
		int indexOflastElementOfAscOrderedScores = ascOrderedScores.length-1;
		while (counter < diminishingReturnFactors.length && counter < ascOrderedScores.length){
			//asc ordered factors backwards, due to ascending order
			weightedWinningOptionScoreSum += diminishingReturnFactors[counter] * ascOrderedScores[indexOflastElementOfAscOrderedScores - counter];
			counter++;
		}
		
		//Calculate final score
		float boardScore = weightedWinningOptionScoreSum / denominator;
		return boardScore;
	}
	
}
