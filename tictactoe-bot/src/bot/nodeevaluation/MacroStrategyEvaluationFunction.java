package bot.nodeevaluation;

import bot.Field;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.linearcombinationcalculation.ConicalCombinationFunctionAbstract;
import bot.util.GlobalDefinitions;

/**
 * Evaluation functions that evaluate the macro state of the board.
 * ToDo: This is a first implementation and needs some more serious additional work.
 * @author André
 *
 */
public class MacroStrategyEvaluationFunction extends ConicalCombinationFunctionAbstract{

	private static final float BASE_VALUE_OF_CORNER_BOARD = 0.08f;
	private static final float BASE_VALUE_OF_EDGE_BOARD = 0.06f; 
	private static final float BASE_VALUE_OF_MIDDLE_BOARD = 0.12f; 
	
	private float calculateWinnerScore(Player wonPlayer, int macroX, int macroY){
		if (wonPlayer.getPlayerType() == PlayerTypes.None) return 0;
		
		float result = 0;
		//If winner == self kepp normal value; otherwise negate it
		int sign = (wonPlayer.getPlayerType() == PlayerTypes.Self) ? 1 : -1;
		
		//Check if corner board
		if (macroX == 0 && macroY == 0
				|| macroX == 2 && macroY == 0
				|| macroX == 0 && macroY == 2
				|| macroX == 2 && macroY == 2){
			result = sign * BASE_VALUE_OF_CORNER_BOARD;
		//Check if edge board
		} else if(macroX == 0 && macroY == 1
				|| macroX == 1 && macroY == 0
				|| macroX == 1 && macroY == 2
				|| macroX == 2 && macroY == 1){
			result = sign * BASE_VALUE_OF_EDGE_BOARD;
		// --> middle board
		} else {
			result = sign * BASE_VALUE_OF_MIDDLE_BOARD;
		}
		return result;
	}
	
	public float calculate(){
		Field field = (Field) getGameState();
		int[][] board = field.getCopyOfBoard();
		float scoreOfWonAndLostMacroboards = GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE;
		//Check winner in different microboards
		for (int macroX = 0; macroX < 3; macroX++){
			for (int macroY = 0; macroY < 3; macroY++){
				//Extract microboard
				int[][] microboard = new int[3][3];
				for (int k = 0; k < microboard.length; k++){
					for (int l = 0; l < microboard[k].length; l++){
						microboard[k][l] = board[macroX*3+k][macroY*3+l];
					}
				}
				Player wonPlayer = Player.getPlayer(field.calculateWinnerInBoard(microboard));
				scoreOfWonAndLostMacroboards += calculateWinnerScore(wonPlayer, macroX, macroY);
			}
		}
		
		return scoreOfWonAndLostMacroboards;
	}
	
}
