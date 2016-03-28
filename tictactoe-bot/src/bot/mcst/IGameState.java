package bot.mcst;

import java.util.List;

import bot.Player;


public interface IGameState {

	public List<IAction> getAllowedActions();
	
	public void simulateAction(IAction action);
	
	public Player getPlayerAtTurn();
	
	public boolean isEndState();
	
	public Player getWinner();
	
	public IGameState clone();
	
}
