package bot.linearcombinationcalculation;

import bot.mcst.MCSTNode;

public interface IConicalCombinationFunction {

	public void setCurrentNode(MCSTNode node);
	
	public MCSTNode getCurrentNode();
	
	public float calculate();
	
}
