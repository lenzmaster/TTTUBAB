package bot.mcst;

import java.util.List;

import bot.Player;


public interface IGameState {

	public List<IAction> getAllowedActions();
	
	public IGameState simulateAction(IAction action);
	
	public Player getPlayerAtTurn();
	
}
