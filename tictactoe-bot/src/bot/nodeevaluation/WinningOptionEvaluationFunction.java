package bot.nodeevaluation;

import java.util.ArrayList;
import java.util.List;

import bot.Field;
import bot.Player;
import bot.Point;
import bot.Player.PlayerTypes;
import bot.linearcombinationcalculation.ConicalCombinationFunctionAbstract;
import bot.util.CalculationHelper;
import bot.util.GlobalDefinitions;
import bot.util.Logger;
import bot.util.WinningOptions;

//ToDo: Consider interdependencies between enemies dominance in a (sub) board and own position

public class WinningOptionEvaluationFunction extends ConicalCombinationFunctionAbstract{

	private static int potencyBasis = 7;
	
	private static Logger LOGGER = new Logger("WinningOptionEvaluationFunction");

	//This value is experimental!!!
	//1.0 would be a secure, but probably bad value
	//2.0 might be too high
	private static float scoreDifferenceScallingValue = 1.5f;
	
	private float lowerBound = GlobalDefinitions.NODE_EVALUATION_LOWER_BOUND;//Equal to enemy winning value
	private float neutralValue = GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE;//
	private float upperBound = GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND;//Equal to self winning value
	//ToDo: Implement the notOccupiedValue variable more beautiful;
	//Right now it is very arbitrarily to set it to -1 and can´t be used in the formular, since the neutral element of the an addition
	//is already used as the opponents value
	private float notOccupiedValue = -1;// Needs to out of the range of lower and upper bound
	
	//Always use the getter of these variables, otherwise they are not initalized!!!
	private float denominator = -1;
	private Player self = null;
	private Player opponent = null;
	private Player none = null;
	
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
	
	private float getDenominator(){
		if (denominator == -1){
			denominator = 3 * this.upperBound * ((float) Math.log(potencyBasis));
		}
		return denominator;
	}

	/**
	 * Transforms the board to the scale used in this class.
	 * The scale is defined in the variables lowerBound and upperBound.
	 * The board is transformed based on the perspective of the given player.
	 * 
	 * @param player the player for whom the board is transformed
	 * @param board a board from a gamestate
	 * @return transformed board to the defined scale
	 */
	private float[][] transformBoard(int[][] board, Player player){
		if (player.getPlayerType() == PlayerTypes.None) throw new IllegalArgumentException("PlayerType none is not allowed for this method");
		
		Player opponent = Player.getInvertedPlayer(player);
		Player none = this.getNone();
		float[][] transformedBoard = new float[board.length][board[0].length];
		
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[i].length; j++){
				if (board[i][j] == player.getId()){
					transformedBoard[i][j] = this.upperBound;
				} else if (board[i][j] == opponent.getId()){
					transformedBoard[i][j] = this.lowerBound;
				} else if (board[i][j] == none.getId()){
					transformedBoard[i][j] = this.notOccupiedValue;
				} else {
					throw new IllegalArgumentException("Board contained an unidentifiable id: " + board[i][j]);
				}
			}
		}
		
		return transformedBoard;
	}
	
	
	private float[][] getMicroboard(float[][] transformedBoard, int macroX, int macroY){
		float[][] microboard = new float[3][3];
		for (int k = 0; k < microboard.length; k++){
			for (int l = 0; l < microboard[k].length; l++){
				microboard[k][l] = transformedBoard[macroX*3+k][macroY*3+l];
			}
		}
		return microboard;
	}
	
	private int[][] getMicroboard(int[][] untransformedBoard, int macroX, int macroY){
		int[][] microboard = new int[3][3];
		for (int k = 0; k < microboard.length; k++){
			for (int l = 0; l < microboard[k].length; l++){
				microboard[k][l] = untransformedBoard[macroX*3+k][macroY*3+l];
			}
		}
		return microboard;
	}
	
	private Player getWinner(int[][] untransformedBoard){
		WinningOptions[] winningOptions = WinningOptions.values();
		for (WinningOptions winningOption : winningOptions) {
			Point tile1 = winningOption.getTile1();
			Point tile2 = winningOption.getTile2();
			Point tile3 = winningOption.getTile3();
			if(untransformedBoard[tile1.getX()][tile1.getY()] != GlobalDefinitions.PLAYER_NEUTRAL_ID &&
				untransformedBoard[tile1.getX()][tile1.getY()] == untransformedBoard[tile2.getX()][tile2.getY()] &&
				untransformedBoard[tile2.getX()][tile2.getY()] == untransformedBoard[tile3.getX()][tile3.getY()]){
				return Player.getPlayer(untransformedBoard[tile1.getX()][tile1.getY()]);
			}
		}
		return getNone();
	}
	
	/**
	 * Calculates the winning option scores for the given transformed board and the given player.
	 * The given board needs to have the dimension: 3x3.
	 * @param transformedBoard
	 * @param player
	 * @return
	 */
	private float[] calcWinningOptionScores(float[][] transformedBoard){		
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
					} else if (tileValue == this.notOccupiedValue) {// Noone owns tile
						//Do nothing
						
					} else {
						//Add value of tile
						exponent += tileValue;
					}
				}
			
			if (winningOptionAvailable){
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
	
	/**
	 * Calculates a board score based on the board´s winning options scores.
	 * The foloowing formular is used:
	 * board score = ln(sum(winning options scores)) / (3 * upperBound * ln(potency basis))
	 * @param winningOptionsScores
	 * @return
	 */
	private float calcBoardScore(float[] winningOptionsScores){
		float winningOptionScoreSum = 0;
		for (float currentScore : winningOptionsScores) {
			winningOptionScoreSum += currentScore;
		}
		
		//Check if winning option scores are a equals 1 (happens by (potency basis)^0)
		if (winningOptionScoreSum == WinningOptions.values().length && winningOptionsScores[0] == 1){
			//then board is not occupied
			return this.notOccupiedValue;
		}
		
		float lnOfSum = (float) Math.log(winningOptionScoreSum);
		//Check if board was not yet played
		if (lnOfSum < 0){
			return this.lowerBound;
		}
		float denominator = getDenominator();
		float boardScore = lnOfSum / denominator;
		return boardScore;
	}
	
	@Override
	public float calculate() {
		Field field = (Field) getGameState();
		int[][] untransformedBoard = field.getCopyOfBoard();
		float[][] transformedBoardSelf = transformBoard(untransformedBoard, this.getSelf());
		float[][] macroBoardSelf = new float[3][3];
		float[][] transformedBoardOpponent = transformBoard(untransformedBoard, this.getOpponent());
		float[][] macroBoardOpponent = new float[3][3];

		//1. Calculate scores for mircoboards for both players
		for (int macroX = 0; macroX < 3; macroX++){
			for (int macroY = 0; macroY < 3; macroY++){
				//Extract untransformed microboard
				int[][] microboard = getMicroboard(untransformedBoard, macroX, macroY);
				//Check, if winner already exists
				Player winner = getWinner(microboard);
				if (winner.getPlayerType() == PlayerTypes.None){
					//Board not won
					//Use formulars...
					//a. Calculate winning option scores
					float[][] microboardSelf = getMicroboard(transformedBoardSelf, macroX, macroY);
					float[] winningOptionScoresSelf = this.calcWinningOptionScores(microboardSelf);
					float[][] microboardOpponent = getMicroboard(transformedBoardOpponent, macroX, macroY);
					float[] winningOptionScoresOpponent = this.calcWinningOptionScores(microboardOpponent);
					
					//b. Aggregate winning option scores to one microboard score for each player
					macroBoardSelf[macroX][macroY] = this.calcBoardScore(winningOptionScoresSelf);
					macroBoardOpponent[macroX][macroY] = this.calcBoardScore(winningOptionScoresOpponent);
					
				} else if (winner.getPlayerType() == PlayerTypes.Self){
					//Board won by self
					macroBoardSelf[macroX][macroY] = 1;// 1, becuase won by self
					macroBoardOpponent[macroX][macroY] = 0;//0, because oponent lost board
				} else {
					//Board won by opponent
					macroBoardSelf[macroX][macroY] = 0;// 1, becuase one lost that board
					macroBoardOpponent[macroX][macroY] = 1;//0, because oponent won fboard
				}
				
			}
		}
		
		//2. Calculate scores for macroboard
		
		//2a. Calculate winning option scores for macroboard for each player
		float[] winningOptionScoresSelf = this.calcWinningOptionScores(macroBoardSelf);
		float[] winningOptionScoresOpponent = this.calcWinningOptionScores(macroBoardOpponent);
		
		//2b. Aggregate winning option scores to one macroboard score for each player
		float macroBoardScoreSelf = this.calcBoardScore(winningOptionScoresSelf);
		float macroBoardScoreOpponent = this.calcBoardScore(winningOptionScoresOpponent);
		
		//3.Aggregate both macroboard scores to one score --> node evaluation score
		//Invert node evaluation value of opponent
		float scoreDifferenceOfPlayers = macroBoardScoreSelf - macroBoardScoreOpponent;
		float normedScoreDifference = GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE * scoreDifferenceOfPlayers;
		if (normedScoreDifference > 0.5 || normedScoreDifference < -0.5){
			LOGGER.log("Absolute normed score differnece is above 0.5: " + normedScoreDifference);
			if (normedScoreDifference > 0.5){
				normedScoreDifference = 0.49f;
			} else {
				normedScoreDifference = -0.49f;
			}
		}
		
		float totalBoardScore = GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE + 
				normedScoreDifference;
		return totalBoardScore;
	}
}
