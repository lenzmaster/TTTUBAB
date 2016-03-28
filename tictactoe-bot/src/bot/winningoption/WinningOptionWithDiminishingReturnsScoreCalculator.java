package bot.winningoption;

import bot.Field;
import bot.FieldCalculationHelper;
import bot.Move;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.Point;
import bot.mcst.IAction;
import bot.mcst.IGameState;
import bot.util.BoardTransformer;
import bot.util.Logger;
import bot.winningoption.DiminishingReturnWinningOptionsAggregationScoreCalculator;
import bot.winningoption.WinningOptionPowerScoreCalculator;

//ToDo: Consider interdependencies between enemies dominance in a (sub) board and own position

/**
 * Calculates a score based on the wining option for each player.
 * Diminishing returns are used for additional winning options per player.
 * 
 * @author André
 *
 */
public class WinningOptionWithDiminishingReturnsScoreCalculator{

	private int potencyBasis;
	
	private float diminishingBaseFactor;
	
	//The threshold that indicates when a exponentiated diminishing factor is not used anymore, due to inefficiency
	//The threshold must not be lower than the diminishing factor
	private float diminishingFactorsThreshold;
	
	private static Logger LOGGER = new Logger("WinningOptionWithDiminishingReturnsScoreCalculator");

	
	private float lowerBound;//Equal to enemy winning value
	private float neutralValue;//
	private float upperBound;//Equal to self winning value
	//ToDo: Implement the notOccupiedValue variable more beautiful;
	//Right now it is very arbitrarily to set it to -1 and can´t be used in the formula, since the neutral element of the an addition
	//is already used as the opponents value
	private float notOccupiedValue;// Needs to out of the range of lower and upper bound
	
	//Always use the getter of these variables, otherwise they are not initalized!!!
	private Player self = null;
	private Player opponent = null;
	private Player none = null;

	private WinningOptionPowerScoreCalculator winningOptionScoreCalculator = null;
	private DiminishingReturnWinningOptionsAggregationScoreCalculator winningOptionAggScoreCalculator = null;
	private BoardTransformer boardTransformer = null;
	
	private Player getOpponent(){
		if (opponent == null){
			opponent = Player.getPlayer(PlayerTypes.Opponent);
		}
		return opponent;
	}
	
	private Player getNone(){
		if (none == null){
			none = Player.getPlayer(PlayerTypes.None);
		}
		return none;
	}
	
	private Player getSelf(){
		if (self == null){
			self = Player.getPlayer(PlayerTypes.Self);
		}
		return self;
	}
	
	/**
	 * Constructor
	 */
	public WinningOptionWithDiminishingReturnsScoreCalculator(int potencyBasis, float diminishingBaseFactor,
			float diminishingFactorsThreshold,float lowerBound, float upperBound, float neutralValue,
			float notOccupiedValue){
		this.potencyBasis = potencyBasis;
		this.diminishingBaseFactor = diminishingBaseFactor;
		this.diminishingFactorsThreshold = diminishingFactorsThreshold;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.neutralValue = neutralValue;
		this.notOccupiedValue = notOccupiedValue;
		
		this.winningOptionScoreCalculator = new WinningOptionPowerScoreCalculator(potencyBasis, lowerBound, upperBound, notOccupiedValue);
		this.winningOptionAggScoreCalculator = new DiminishingReturnWinningOptionsAggregationScoreCalculator(
					lowerBound, upperBound, notOccupiedValue, winningOptionScoreCalculator, 
					diminishingBaseFactor, diminishingFactorsThreshold);
		this.boardTransformer = new BoardTransformer(lowerBound, upperBound, notOccupiedValue);
	}
	
	private float calcTotalBoardScore(float macroBoardScoreSelf, float macroBoardScoreOpponent)
	{
		//Aggregate both macroboard scores to one score
		float scoreDifferenceOfPlayers = macroBoardScoreSelf - macroBoardScoreOpponent;
		//Compute score
		float normedScoreDifference = scoreDifferenceOfPlayers / 2;
		float maxDifferenceBetweenNeutralElementAndBounds = upperBound - neutralValue;
		if (normedScoreDifference > maxDifferenceBetweenNeutralElementAndBounds
				|| normedScoreDifference < -maxDifferenceBetweenNeutralElementAndBounds){
			LOGGER.log("Absolute normed score difference is above "+ 
				maxDifferenceBetweenNeutralElementAndBounds + ": " + normedScoreDifference);
			LOGGER.log("The board values for both players(self ;opponent) are: " + macroBoardScoreSelf + "; "
					+ macroBoardScoreOpponent);
			if (normedScoreDifference > maxDifferenceBetweenNeutralElementAndBounds){
				normedScoreDifference = maxDifferenceBetweenNeutralElementAndBounds - 0.01f;
			} else {
				normedScoreDifference = -maxDifferenceBetweenNeutralElementAndBounds + 0.01f;
			}
		}
		
		float totalBoardScore = neutralValue + normedScoreDifference;
		return totalBoardScore;
	}
	
	/**
	 * Calculates the winning option score of a given game state.
	 * This method does not use caching and might be inefficient in certain circumstances.
	 * @param gameState
	 * @return
	 */
	public float calculate(IGameState gameState) {
		Field field = (Field) gameState;
		int[][] untransformedBoard = field.getCopyOfBoard();
		float[][] transformedBoardSelf = boardTransformer.transform(untransformedBoard, this.getSelf());
		float[][] macroBoardSelf = new float[3][3];
		
		float[][] transformedBoardOpponent = boardTransformer.transform(untransformedBoard, this.getOpponent());
		float[][] macroBoardOpponent = new float[3][3];

		//1. Calculate scores for mircoboards for both players
		for (int macroX = 0; macroX < 3; macroX++){
			for (int macroY = 0; macroY < 3; macroY++){
				//Calculated scores for both players
				float[][] microboardSelf = BoardTransformer.getMicroboard(transformedBoardSelf, macroX, macroY);
				float[][] microboardOpponent = BoardTransformer.getMicroboard(transformedBoardOpponent, macroX, macroY);
				float[] scores = winningOptionAggScoreCalculator.calculateScore(
						microboardSelf, microboardOpponent);
				macroBoardSelf[macroX][macroY] = scores[0];
				macroBoardOpponent[macroX][macroY] = scores[1];
				
			}
		}
		
		//2. Calculate scores for macroboard
		float[] scores = winningOptionAggScoreCalculator.calculateScore(macroBoardSelf, macroBoardOpponent);
		float macroBoardScoreSelf = scores[0];
		float macroBoardScoreOpponent = scores[1];
		
		return calcTotalBoardScore(macroBoardScoreSelf, macroBoardScoreOpponent);
	}
	
	/**
	 * Calculates the winning option scores of the given game state
	 * executing one of the given actions at a time.
	 * For each move a score is calculated.
	 * The order of the result is the same order as the actions.
	 * This method uses caches intermediate results to increase efficiency.
	 * If possibleActionsAfterGameState is null or its length zero the score of the game state is returned.
	 * @param gameState
	 * @param possibleActionsAfterGameState the actions that can be performed at the given game state
	 * @return
	 */
	public float[] calculate(IGameState gameState, IAction[] possibleActionsAfterGameState) {
		if (possibleActionsAfterGameState == null || possibleActionsAfterGameState.length == 0){
			float[] resultScores = new float[1];
			resultScores[0] = calculate(gameState);
			return resultScores;
		}
		
		Field field = (Field) gameState;
		int[][] untransformedBoard = field.getCopyOfBoard();
		float[][] transformedBoardSelf = boardTransformer.transform(untransformedBoard, this.getSelf());
		float[][] macroBoardSelf = new float[3][3];
		
		float[][] transformedBoardOpponent = boardTransformer.transform(untransformedBoard, this.getOpponent());
		float[][] macroBoardOpponent = new float[3][3];

		//1. Calculate scores of mircoboards for both players for the given game state
		for (int macroX = 0; macroX < 3; macroX++){
			for (int macroY = 0; macroY < 3; macroY++){
				//Calculated scores for both players
				float[][] microboardSelf = BoardTransformer.getMicroboard(transformedBoardSelf, macroX, macroY);
				float[][] microboardOpponent = 
						BoardTransformer.getMicroboard(transformedBoardOpponent, macroX, macroY);
				float[] scores = winningOptionAggScoreCalculator.calculateScore(
						microboardSelf, microboardOpponent);
				macroBoardSelf[macroX][macroY] = scores[0];
				macroBoardOpponent[macroX][macroY] = scores[1];
			}
		}
		
		//2. Calculate scores for each of the given moves
		float[] resultScores = new float[possibleActionsAfterGameState.length];
		for (int i = 0; i < resultScores.length; i++){
			Move move = (Move) possibleActionsAfterGameState[i];
			resultScores[i] = calculate(transformedBoardSelf, transformedBoardOpponent,
					macroBoardSelf, macroBoardOpponent,
					move.getX(), move.getY(), move.getPlayer());
		}
		
		return resultScores;
	}
	
	/**
	 * Calculates the winning option score.
	 * This method assumes that the parameters are pre-calculated and only
	 * one tile with the coordinates (moveX, moveY) was changed.
	 * Using this method can increase performance in case scores of
	 * consecutive game states need to be calculated.
	 * The given boards are not modified during the calculation.
	 * Therefore, they can be reused, if necessary.
	 * @param transformedBoardSelf
	 * @param transformedBoardOpponent
	 * @param transformedMacroboardSelf
	 * @param transformedMacroboardOpponent
	 * @param moveX
	 * @param moveY
	 * @param playerThatPerformsMove
	 * @return
	 */
	public float calculate(float[][] transformedBoardSelf, float[][] transformedBoardOpponent,
			float[][] transformedMacroboardSelf, float[][] transformedMacroboardOpponent,
			int moveX, int moveY, Player playerThatPerformsMove){
		//Extract macro coordinates of microboard, where a tile was changed in
		Point macroCoordinates =  FieldCalculationHelper.getMacroIndex(moveX, moveY);
		//Transform tile
		float transformedTileSelf = boardTransformer.transform(playerThatPerformsMove.getId(), getSelf());
		float transformedTileOpponent = boardTransformer.transform(playerThatPerformsMove.getId(), getOpponent());
		//Calculate microboard scores of the changed microboard for both players
		//1. Create new microboards
		int moveMicroX = moveX % 3;
		int moveMicroY = moveY % 3;
		float[][] changedMicroboardSelf = BoardTransformer.getMicroboard(transformedBoardSelf,
				macroCoordinates.getX(), macroCoordinates.getY());
		changedMicroboardSelf[moveMicroX][moveMicroY] = transformedTileSelf;
		float[][] changedMicroboardOpponent = BoardTransformer.getMicroboard(transformedBoardOpponent,
				macroCoordinates.getX(), macroCoordinates.getY());
		changedMicroboardOpponent[moveMicroX][moveMicroY] = transformedTileOpponent;
		//2. Calculate new microboard scores
		float[] changedMicroboardScores = winningOptionAggScoreCalculator.calculateScore(
				changedMicroboardSelf, changedMicroboardOpponent);
		//Calculate new macroboard scores
		//1. Create new macroboards
		float[][] changedMacroboardSelf = FieldCalculationHelper.copyBoard(transformedMacroboardSelf);
		changedMacroboardSelf[macroCoordinates.getX()][macroCoordinates.getY()] = changedMicroboardScores[0];
		float[][] changedMacroboardOpponent = FieldCalculationHelper.copyBoard(transformedMacroboardOpponent);
		changedMacroboardOpponent[macroCoordinates.getX()][macroCoordinates.getY()] = changedMicroboardScores[1];
		
		//2. Calculate new macroboard scores
		float[] changedMacroboardScores = winningOptionAggScoreCalculator.calculateScore(
				changedMacroboardSelf, changedMacroboardOpponent);
		return this.calcTotalBoardScore(changedMacroboardScores[0], changedMacroboardScores[1]);
		
	}
	
	
}
