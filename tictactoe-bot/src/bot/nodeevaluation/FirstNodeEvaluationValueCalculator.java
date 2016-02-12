package bot.nodeevaluation;

import bot.Field;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.mcst.MCSTNode;
import bot.util.GlobalDefinitions;

public class FirstNodeEvaluationValueCalculator implements INodeEvaluationValueCalculator{

	@Override
	public float calculate(MCSTNode node) {
		Field field = (Field) node.getGameState();
		if (field.getWinner() == Player.getPlayer(PlayerTypes.Self)){
			return GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND;
		} else if (field.getWinner() == Player.getPlayer(PlayerTypes.Opponent)) {
			return GlobalDefinitions.NODE_EVALUATION_LOWER_BOUND;
		}
		return GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE;
	}

}
