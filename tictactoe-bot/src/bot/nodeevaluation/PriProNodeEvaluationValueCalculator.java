package bot.nodeevaluation;

import bot.Field;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.mcst.IAction;
import bot.mcst.MCSTNode;
import bot.util.CalculationHelper;
import bot.util.GlobalDefinitions;

/**
 * This node evaluation calculator uses the prior probability as node evaluation value.
 * In case the scales of the are not equal, the value is transformed to the correct scale.
 * TODO: Implement transformation function.
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
		
		//TODO:Implement game state ode evaluation in own class that can be used in a chain of responsibility pattern
		//Check if game is in end state --> different node evaluation 
		Field field = (Field) node.getGameState();
		//Check, if board is in game ending state
		if (field.isEndState()){
			//Check, if someone won
			Player winner = field.getWinner();
			if (winner == Player.getPlayer(PlayerTypes.Self)){
				return GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND;
			} else if (winner == Player.getPlayer(PlayerTypes.Opponent)) {
				return GlobalDefinitions.NODE_EVALUATION_LOWER_BOUND;
			} else {
				//It´s a tie
				return GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE;
			}
		}
		
		float result;
		//Check, if an action was taken at all
		if (node.getTakenAction() == null){
			//root node --> no action value
			//use prior probability calculator to evaluate node
			result = GlobalDefinitions.getPriorProbabilityCalculator().calculate(node.getGameState(), new IAction[0])[0];
		} else {
			result = node.getPriorProbability();
		}
		//Check, if action was performed by opponent --> invert value, since the action probability is related to a player
		//and the node evaluation score is unrelated to the player/ universally valid
		if(field.getPlayerAtTurn() == Player.getPlayer(PlayerTypes.Self)){
			//Last action was performed by opponent --> invert value
			result = CalculationHelper.invertValue(result, GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE);
		}
		return result;
	}

}
