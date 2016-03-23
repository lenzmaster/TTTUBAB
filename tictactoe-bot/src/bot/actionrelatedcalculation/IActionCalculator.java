package bot.actionrelatedcalculation;

import bot.mcst.IAction;
import bot.mcst.IGameState;

/**
 * Ideas:
 * A simple action evaluation calculator that
 * considers the following aspects:
 * - If the action results in a "free turn"
 * - If the action wins a microboard
 * - If the action results in giving the opponent a favorable microboard to play in
 * 
 * The algorithm values good plays for the current player higher than it values
 * good opportunities for his opponent as low.
 * This means in detail:
 * formula to calculate the value:
 * actionScore = neutralValue + (WinningOptionValueGain - opponentPotentialWinningOptionGain)
 * upperBound: the player wins a microbard 
 * 						and does not give the opponent a microboard or "free turn" 
 * 
 * @author André
 *
 */

/**
 * Interface to generalize classes that perform calculations on actions
 * that are related to a current/previous 8after the action was taken) gamestate.
 * @author André
 *
 */
public interface IActionCalculator {

	/**
	 * Calculates an action value for one action
	 * on the given game state.
	 * @param oldGameState
	 * @param action
	 * @return
	 */
	public float calculate(IGameState oldGameState, IAction action);
	
	/**
	 * Calculates an action values for multiple actions
	 * that are performed on the given game state.
	 * This method should be preferred, since it might enable performance increases.
	 * If the length of actions is zero, the score of the game state is returned.
	 * @param oldGameState
	 * @param actions
	 * @return
	 */
	public float[] calculate(IGameState oldGameState, IAction[] actions);
	
}
