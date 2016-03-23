package bot.nodeevaluation;

import bot.mcst.IAction;
import bot.mcst.MCSTNode;
import bot.util.GlobalDefinitions;

/**
 * This node evaluation calculator uses the prior probability as node evaluation value.
 * In case the scales of the are not equal, the value is transformed to the correct scale.
 * TODO: Implement transformation funtion.
 * @author André
 *
 */
public class PriProNodeEvaluationValueCalculator implements INodeEvaluationValueCalculator{

	@Override
	public float calculate(MCSTNode node) {
		if (GlobalDefinitions.NODE_EVALUATION_LOWER_BOUND != GlobalDefinitions.NODE_PRIOR_PROBABILITY_LOWER_BOUND
				|| GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND != GlobalDefinitions.NODE_PRIOR_PROBABILITY_UPPER_BOUND){
			throw new RuntimeException("Transformation of prior probability to node evaluation value not implemented yet.");
		}
		//Check, if an action was taken at all
		if (node.getTakenAction() == null){
			//root node --> no action value
			//use prior probability calculator to evaluate node
			return GlobalDefinitions.getPriorProbabilityCalculator().calculate(node.getGameState(), new IAction[0])[0];
		}
		
		return node.getPriorProbability();
	}

}
