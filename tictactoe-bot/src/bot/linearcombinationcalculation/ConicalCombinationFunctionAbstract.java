package bot.linearcombinationcalculation;

import bot.mcst.MCSTNode;

public abstract class ConicalCombinationFunctionAbstract implements IConicalCombinationFunction{

	private MCSTNode node;
	
	@Override
	public void setCurrentNode(MCSTNode node){
		this.node = node;
	}
	
	@Override
	public MCSTNode getCurrentNode(){
		return node;
	}
	
}
