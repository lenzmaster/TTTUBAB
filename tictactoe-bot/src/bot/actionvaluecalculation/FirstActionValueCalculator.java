package bot.actionvaluecalculation;


import bot.mcst.MCSTNode;
import bot.util.GlobalDefinitions;

public class FirstActionValueCalculator implements IActionValueCalculator{

	@Override
	public float calculate(MCSTNode node, MCSTNode lastUpdatedChildNode) {
		float newActionValue = 0;
		if (lastUpdatedChildNode == null){// action value of leaf is calculated
			newActionValue = node.getEvaluationValue();
		} else if(lastUpdatedChildNode.getOldActionValue() == MCSTNode.LAST_ACTION_VALUE_INITAL_VALUE){//Child was not part of the old calculation --> was leaf
			//reverse devision by old visit count
			float cleanedActionValue = node.getActionValue() * (node.getVisitCount() -1);
			//Add new action value of child and average again
			newActionValue = (cleanedActionValue + lastUpdatedChildNode.getActionValue()) / node.getVisitCount();
		} else if (lastUpdatedChildNode.getEvaluationValue() == GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND) {//child was wining node --> only care about winning child, when calculating the action value
			newActionValue = (node.getEvaluationValue() + lastUpdatedChildNode.getEvaluationValue()) / 2;
		
		} else {//Child was part of the last average calculation
			//Old value needs to be removed from the average value
			//reverse devision by old visit count
			float cleanedActionValue = node.getActionValue() * (node.getVisitCount() -1);
			//Substract old action value of child
			cleanedActionValue = cleanedActionValue - (lastUpdatedChildNode.getOldActionValue() * (lastUpdatedChildNode.getVisitCount()-1));
			//Add new action value of child and average again
			newActionValue = (cleanedActionValue + lastUpdatedChildNode.getActionValue() * lastUpdatedChildNode.getVisitCount()) / node.getVisitCount();
			
		}
		return newActionValue;
	}

}
