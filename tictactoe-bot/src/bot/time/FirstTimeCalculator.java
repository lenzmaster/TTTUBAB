package bot.time;

import bot.Field;
import bot.mcst.MCSTNode;

public class FirstTimeCalculator  implements ITimeCalculator{

	@Override
	public long calculatTimeForTurn(MCSTNode root, long timeInBank) {
		Field currentField = (Field) root.getGameState();
		return 0;
	}
	
	//private calculate

}
