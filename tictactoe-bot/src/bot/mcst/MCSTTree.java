package bot.mcst;

import bot.memory.IReusable;
import bot.util.Logger;

public class MCSTTree implements IReusable{

	private MCSTNode root = null;
	
	public static Logger LOGGER = new Logger("MCSTTree");
	
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
	public IAction calculateBestAction(IGameState gameState, long duration){
		long timeElapsed = 0;
		long longestIterationDuration = 0;
		long iterationCount = 0;
		while (duration - (timeElapsed + longestIterationDuration) > 0){
			long iterationStartTime = System.nanoTime();
			root.visitNode(gameState);
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
	
	
	public void print(){
		System.out.println("Tree: ");
		root.printTree();
	}
}
