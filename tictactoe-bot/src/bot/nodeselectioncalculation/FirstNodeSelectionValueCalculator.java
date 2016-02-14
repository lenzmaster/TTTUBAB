package bot.nodeselectioncalculation;

import bot.Player;
import bot.Player.PlayerTypes;
import bot.mcst.MCSTNode;
import bot.util.CalculationHelper;

public class FirstNodeSelectionValueCalculator implements INodeSelectionValueCalculator{

	@Override
	public float calculate(MCSTNode node, Player playerPerformingTheAction) {
		Boolean isOpponent = (playerPerformingTheAction != null && playerPerformingTheAction == Player.getPlayer(PlayerTypes.Opponent));
		float correctedActionValue = (isOpponent) ? CalculationHelper.invertActionValue(node.getActionValue()) : node.getActionValue() ;
		
		float u = node.getPriorProbability() / ( (float) 1 + node.getVisitCount());
		float result = correctedActionValue + u;
		return result;
	}

}
