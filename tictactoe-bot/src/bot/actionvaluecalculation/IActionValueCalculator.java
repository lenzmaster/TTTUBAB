package bot.actionvaluecalculation;

import bot.mcst.MCSTNode;

public interface IActionValueCalculator {

	public float calculate(MCSTNode node, MCSTNode lastUpdatedChildNode);
	
}
