package bot.nodeselectioncalculation;

import bot.Player;
import bot.mcst.IGameState;
import bot.mcst.MCSTNode;

public interface INodeSelectionValueCalculator {

	public float calculate(MCSTNode node, Player playerPerformingTheAction);
	
}
