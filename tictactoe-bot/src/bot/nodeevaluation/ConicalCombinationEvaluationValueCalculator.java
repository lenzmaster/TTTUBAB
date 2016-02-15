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
		this.setGameState(node.getGameState());
		Field field = (Field) this.getGameState();
		//Check, if someone already won
		if (field.getWinner() == Player.getPlayer(PlayerTypes.Self)){
			return GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND;
		} else if (field.getWinner() == Player.getPlayer(PlayerTypes.Opponent)) {
			return GlobalDefinitions.NODE_EVALUATION_LOWER_BOUND;
		}
		//Calculate the conical combination
		float result = super.calculate();
		return result;
	}

	
}
