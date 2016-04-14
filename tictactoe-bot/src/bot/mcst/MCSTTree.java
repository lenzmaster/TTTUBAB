package bot.mcst;

import java.util.List;

import bot.memory.IReusable;
import bot.threading.Lock;
import bot.util.Logger;

public class MCSTTree extends Lock implements IReusable{

	private MCSTNode root = null;
	
	private MCSTNode oldRoot = null;
	
	private IGameState previousGameState = null;
	
	private long maxVisitTime = 0;
	
	private long iterationCount = 0;
	
	public static Logger LOGGER = new Logger("MCSTTree");
	
	public MCSTNode getRoot(){
		this.checkLock();
		return root;
	}
	
	public synchronized void setRoot(MCSTNode newRoot){
		this.checkLock();
		this.oldRoot = this.root;
		this.root = newRoot;
	}
	
	public MCSTNode getOldRoot(){
		this.checkLock();
		return this.oldRoot;
	}
	
	public IGameState getPreviousGameState(){
		this.checkLock();
		return previousGameState;
	}
	
	public void setPreviousGameState(IGameState previousGameState){
		this.checkLock();
		this.previousGameState = previousGameState;
	}
	
	public long getMaxVisitTime(){
		this.checkLock();
		return maxVisitTime;
	}
	
	public long getIterationCount(){
		this.checkLock();
		return iterationCount;
	}
	
	public void resetIterationCount(){
		this.checkLock();
		iterationCount = 0;
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
		maxVisitTime = 0;
		iterationCount = 0;
	}
	
	public IAction getMostVisitedAction(){
		this.checkLock();
		return getRoot().getActionWithMostVisits();
	}
	
	public void performIteration(){
		this.checkLock();
		long startTime = System.nanoTime();
		getRoot().visitNode(getPreviousGameState());
		long elapsedTime = System.nanoTime() - startTime;
		if (elapsedTime > maxVisitTime){
			maxVisitTime = elapsedTime;
		}
		iterationCount++;
	}
	
	/**
	 * Sets the root of the tree to a child of the root according to the performed action.
	 * @param performedAction the performed action
	 * @return true, if a child with the taken action was found; otherwise false
	 */
	public boolean setNewRoot(IAction performedAction){
		this.checkLock();
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
	
	/**
	 * Prints the tree to the given tree depth.
	 * if the tree depth is -1 the whole tree is printed.
	 * @param treeDepth
	 */
	public void print(int treeDepth){
		this.checkLock();
		System.out.println("Tree: ");
		root.printTree(treeDepth);
	}
}
