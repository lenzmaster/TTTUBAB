package bot.simulation;

import bot.Field;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.mcst.IAction;
import bot.mcst.IGameState;

public abstract class PlayoutSimulatorAbstract {

	protected float lowerBound;
	protected float upperBound;
	protected float neutralValue;
	
	public PlayoutSimulatorAbstract(float lowerBound, float upperBound, float neutralValue){
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.neutralValue = neutralValue;
	}
	
	
	public float playOut(IGameState gamestate){
		//Simulate till end state is reached
		while (!gamestate.isEndState()){
			//Calculate action
			IAction actionToTake = calculateActionToPerform();
			
			//simulate action
			gamestate.simulateAction(actionToTake);
		}
		
		//End state reached --> check winner or tie
		float result;
		switch (gamestate.getWinner().getPlayerType()) {
		case Self:
			result = upperBound;
			break;
		case Opponent:
			result = lowerBound;
			break;
		case None:
			result = neutralValue;
			break;

		default:
			throw new RuntimeException("Winner in simulation was not of types: Self, Opponent or None");
		}
		return result;
	}
	
	public abstract IAction calculateActionToPerform();
	
}
