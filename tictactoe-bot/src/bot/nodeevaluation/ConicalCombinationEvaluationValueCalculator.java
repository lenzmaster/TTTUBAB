package bot.nodeevaluation;

import bot.Field;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.linearcombinationcalculation.ConicalCombinationAbstract;
import bot.mcst.MCSTNode;
import bot.util.GlobalDefinitions;

/**
 * Caluclates an evaluation value based on added <class>IConicalCombinationFunction</class> objetcs using
 * the formula: (w1 * f1(gameState) + w2 * f2(gameState) + ... + wn * fn(gameState)) / sum(w1,wn)
 * It is important that the value range of all of the added functions matches the evaluation value ranges defined
 * in <class>GlobalDefinitions</class>.
 * 
 * In case the field is won by one player the calculator will return the upper or lower bound of the
 * evaluation value defined in <class>GlobalDefinitions</class> and not call the added functions.
 * 
 * @author André
 *
 */
public class ConicalCombinationEvaluationValueCalculator extends ConicalCombinationAbstract implements INodeEvaluationValueCalculator{
	
	
	@Override
	public float calculate(MCSTNode node) {
		this.setCurrentNode(node);
		Field field = (Field) this.getCurrentNode().getGameState();
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
		//Calculate the conical combination
		float result = super.calculate();
		return result;
	}

	
}
