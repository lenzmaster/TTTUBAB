package bot.time;

import bot.mcst.MCSTNode;

public interface ITimeCalculator {

	/**
	 * Calculates the time that should be used for the current turn.
	 * @param root the current root node of the search tree
	 * @param timeInBank the time left in the bank in ns
	 * @return
	 */
	public long calculateTimeForTurn(MCSTNode root, long timeInBank);
	
}
