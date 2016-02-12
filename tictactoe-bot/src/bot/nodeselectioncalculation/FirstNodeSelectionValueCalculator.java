package bot.nodeselectioncalculation;

import bot.mcst.MCSTNode;

public class FirstNodeSelectionValueCalculator implements INodeSelectionValueCalculator{

	@Override
	public float calculate(MCSTNode node) {
		float u = node.getPriorProbability() / ( (float) 1 + node.getVisitCount());
		float result = node.getActionValue() + u;
		return result;
	}

}
