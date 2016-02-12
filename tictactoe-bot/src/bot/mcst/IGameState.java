package bot.mcst;

import java.util.List;


public interface IGameState {

	public List<IAction> getAllowedActions();
	
	public IGameState simulateAction(IAction action);
	
}
