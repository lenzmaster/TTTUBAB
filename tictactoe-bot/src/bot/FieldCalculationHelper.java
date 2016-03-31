package bot;

import bot.Player.PlayerTypes;
import bot.memory.ObjectManager;
import bot.util.GlobalDefinitions;
import bot.winningoption.WinningOptions;

public class FieldCalculationHelper {
	
	/**
	 * Calculates the winner in a given 3x3 board and returns the winner id.
	 * In case there is no winner the value of <code>GlobalDefinitions.PLAYER_NEUTRAL_ID</code>
	 * is returned.
	 * @param untransformedBoard an untransformed board
	 * @return
	 */
	public static int getWinner(int[][] untransformedBoard){
		WinningOptions[] winningOptions = WinningOptions.values();
		for (WinningOptions winningOption : winningOptions) {
			Point tile1 = winningOption.getTile1();
			Point tile2 = winningOption.getTile2();
			Point tile3 = winningOption.getTile3();
			if(untransformedBoard[tile1.getX()][tile1.getY()] != GlobalDefinitions.PLAYER_NEUTRAL_ID &&
				untransformedBoard[tile1.getX()][tile1.getY()] != GlobalDefinitions.MACRO_FIELD_NEEDS_TO_BE_USED_ID &&	
				untransformedBoard[tile1.getX()][tile1.getY()] == untransformedBoard[tile2.getX()][tile2.getY()] &&
				untransformedBoard[tile2.getX()][tile2.getY()] == untransformedBoard[tile3.getX()][tile3.getY()]){
				return untransformedBoard[tile1.getX()][tile1.getY()];
			}
		}
		return GlobalDefinitions.PLAYER_NEUTRAL_ID;
	}
	

	/**
	 * Calculates the winner in a given 3x3 board and returns the palyer that won the board.
	 * If no one has won the board yet Player None will we returned.
	 * @param transformedBoardSelf
	 * @param transformedBoardOpponent
	 * @param upperBound
	 * @return
	 */
	public static Player getWinner(float[][] transformedBoardSelf, float[][] transformedBoardOpponent, float upperBound){
		WinningOptions[] winningOptions = WinningOptions.values();
		for (WinningOptions winningOption : winningOptions) {
			Point tile1 = winningOption.getTile1();
			Point tile2 = winningOption.getTile2();
			Point tile3 = winningOption.getTile3();
			//Check, if self won
			if( transformedBoardSelf[tile1.getX()][tile1.getY()] == upperBound	&&
				transformedBoardSelf[tile1.getX()][tile1.getY()] == transformedBoardSelf[tile2.getX()][tile2.getY()] &&
				transformedBoardSelf[tile2.getX()][tile2.getY()] == transformedBoardSelf[tile3.getX()][tile3.getY()]){
				return Player.getPlayer(PlayerTypes.Self);
			}
			//Check, if opponent won
			if( transformedBoardOpponent[tile1.getX()][tile1.getY()] == upperBound	&&
				transformedBoardOpponent[tile1.getX()][tile1.getY()] == transformedBoardOpponent[tile2.getX()][tile2.getY()] &&
						transformedBoardOpponent[tile2.getX()][tile2.getY()] == transformedBoardOpponent[tile3.getX()][tile3.getY()]){
					return Player.getPlayer(PlayerTypes.Opponent);
				}
		}
		return Player.getPlayer(PlayerTypes.None);
		
	}
	
	/**
	 * Returns the macro index to a given move assuming the 9x9 field.
	 * @param move
	 * @return
	 */
	public static Point getMacroIndex(int moveX, int moveY){
		int moduloX = moveX % 9;
		int macroX = -1;
		if(moduloX < 3){
			macroX = 0;
		} else if (moduloX > 5){
			macroX = 2;
		} else {
			macroX = 1;
		}
		
		int macroY = -1;
		if(moveY < 3){
			macroY = 0;
		} else if (moveY > 5){
			macroY = 2;
		} else {
			macroY = 1;
		}
		
		Point point = ObjectManager.getNewPoint();
		point.initalize(macroX, macroY);
		return point;
	}
	
	/**
	 * Creates a copy of the given untransformed board.
	 * @param boardToCopy
	 * @return
	 */
	public static int[][] copyBoard(int[][] boardToCopy){
		int[][] copyOfBoard = new int[boardToCopy.length][boardToCopy[0].length];
		for (int i = 0; i < boardToCopy.length; i++){
			for (int j = 0; j < boardToCopy[i].length; j++){
				copyOfBoard[i][j] = boardToCopy[i][j];
			}
		}
		return copyOfBoard;
	}
	
	/**
	 * Creates a copy of the given transformed board.
	 * @param boardToCopy
	 * @return
	 */
	public static float[][] copyBoard(float[][] boardToCopy){
		float[][] copyOfBoard = new float[boardToCopy.length][boardToCopy[0].length];
		for (int i = 0; i < boardToCopy.length; i++){
			for (int j = 0; j < boardToCopy[i].length; j++){
				copyOfBoard[i][j] = boardToCopy[i][j];
			}
		}
		return copyOfBoard;
	}
	
}
