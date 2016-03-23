package bot.mcst;

import java.util.List;

import bot.memory.IReusable;
import bot.util.Logger;

public class MCSTTree implements IReusable{

	private MCSTNode root = null;
	
	private MCSTNode oldRoot = null;
	
	private IGameState previousGameState = null;
	
	public static Logger LOGGER = new Logger("MCSTTree");
	
	public MCSTNode getRoot(){
		return root;
	}
	
	public void setRoot(MCSTNode newRoot){
		this.oldRoot = this.root;
		this.root = newRoot;
	}
	
	public MCSTNode getOldRoot(){
		return this.oldRoot;
	}
	
	public IGameState getPreviousGameState(){
		return previousGameState;
	}
	
	public void setPreviousGameState(IGameState previousGameState){
		this.previousGameState = previousGameState;
	}
	
	public MCSTTree() {
		
	}
	
	public MCSTTree(MCSTNode root) {
		this.root = root;
	}
	
	public void initalize(MCSTNode root){
		reset();
		this.root = root;
		
	}
	
	public void reset(){
		root = null;
	}
	
	/**
	 * Calculates the best action by using the monte carlo approach for the given duration.
	 * @param duration duration in nano seconds
	 * @return the calculated action
	 */
	public IAction calculateBestAction(long duration){
		long timeElapsed = 0;
		long longestIterationDuration = 0;
		long iterationCount = 0;
		while (duration - (timeElapsed + longestIterationDuration) > 0){
			long iterationStartTime = System.nanoTime();
			root.visitNode(previousGameState);
			long iterationDuration = System.nanoTime() - iterationStartTime;
			if (iterationDuration > longestIterationDuration){
				longestIterationDuration = iterationDuration;
			}
			timeElapsed += iterationDuration;
			iterationCount++;
		}
		LOGGER.log("Iteration count: " + iterationCount);
		LOGGER.log("Total time elapsed: " + timeElapsed + " --> " + timeElapsed / 1000000 + "ms");
		LOGGER.log("Longest iteration duration: " + longestIterationDuration + " --> " + longestIterationDuration / 1000000 + "ms");
		return root.getActionWithMostVisits();
	}
	
	/**
	 * Sets the root of the tree to a child of the root according to the performed action.
	 * @param performedAction the performed action
	 * @return true, if a child with the taken action was found; otherwise false
	 */
	public boolean setNewRoot(IAction performedAction){
		List<MCSTNode> childNodes = root.getChildNodes();
		for (MCSTNode childNode : childNodes) {
			if (childNode.getTakenAction().equals(performedAction)){
				this.previousGameState = this.getRoot().getGameState();
				this.setRoot(childNode);
				return true;
			}
		}
		LOGGER.log("No child with the given action!");
		return false;
	}
	
	public int getTreeDepth(){
		return this.getRoot().getSubTreeDepth();
	}
	
	public void print(){
		System.out.println("Tree: ");
		root.printTree();
	}
}
