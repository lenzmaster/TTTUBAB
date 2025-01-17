package bot.mcst;

import java.util.ArrayList;
import java.util.List;

import bot.Move;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.actionrelatedcalculation.priorprobabilitycalculation.IPriorProbabilityCalculator;
import bot.actionvaluecalculation.IActionValueCalculator;
import bot.memory.IReusable;
import bot.memory.ObjectManager;
import bot.nodeevaluation.INodeEvaluationValueCalculator;
import bot.nodeselectioncalculation.INodeSelectionValueCalculator;
import bot.util.GlobalDefinitions;
import bot.util.Logger;

/**
 * A node representing a gamestate and additional context and evaluation information.
 * In case no takenAction is set, the previous gamestate will be interpreted as current gamestate.
 * @author André
 *
 */
public class MCSTNode implements IReusable{
	
	public static final float LAST_ACTION_VALUE_INITAL_VALUE = -1;
	private static Logger LOGGER = new Logger("MCSTNode");
	
	private IAction takenAction;
	private IGameState gameState;
	private float priorProbability;
	private int visitCount = 0;
	private float evaluationValue = GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE;
	private float actionValue = GlobalDefinitions.ACTION_VALUE_NEUTRAL_VALUE;
	private float oldActionValue = LAST_ACTION_VALUE_INITAL_VALUE;
	private List<MCSTNode> childNodes = new ArrayList<MCSTNode>();
	private float nodeSelectionValue = -1;
	private int nodeLevel;
	private int subTreeDepth = 1;
	private MCSTNode winingChildNode;
	private Boolean actionValueUpdated = false;
	
	
	public IAction getTakenAction() {
		return takenAction;
	}

	public IGameState getGameState() {
		return gameState;
	}
	
	public float getPriorProbability() {
		return priorProbability;
	}

	public int getVisitCount() {
		return visitCount;
	}
	
	private void increaseVisitCount() {
		this.visitCount++;
	}
	
	public float getEvaluationValue(){
		return evaluationValue;
	}

	public float getActionValue() {
		return actionValue;
	}
	
	public void setActionValue(float value){
		oldActionValue = actionValue;
		actionValue = value;
	}
	
	public float getOldActionValue(){
		return oldActionValue;
	}
	
	public boolean isLeaf(){
		return childNodes.isEmpty();
	}
	
	public List<MCSTNode> getChildNodes(){
		return childNodes;
	}
	
	public Boolean wasActionValueUpdated(){
		return actionValueUpdated;
	}
	
	public float getNodeSelectionValue(){
		if (nodeSelectionValue == -1){
			INodeSelectionValueCalculator nodeSelectionCalculator = GlobalDefinitions.getNodeSelectionValueCalculator();
			nodeSelectionValue = nodeSelectionCalculator.calculate(this, takenAction.getPerformingPlayer());
		}
		return nodeSelectionValue;
	}
	
	public int getSubTreeDepth(){
		return this.subTreeDepth;
	}
	
	private void updateNodeSelectionValue(){
		//Check, if root node --> no update needed, since only one root node exists
		if (takenAction == null) return;
		
		INodeSelectionValueCalculator nodeSelectionCalculator = GlobalDefinitions.getNodeSelectionValueCalculator();
		nodeSelectionValue = nodeSelectionCalculator.calculate(this, takenAction.getPerformingPlayer());
	}
	
	private void updateSubTreeDepth(MCSTNode updatedChildNode){
		if (updatedChildNode == null) return;
		
		int potentialNewDepth = updatedChildNode.subTreeDepth + 1;
		if (potentialNewDepth > this.subTreeDepth){
			this.subTreeDepth = potentialNewDepth;
		}
	}
	
	/**
	 * Cosntructor for root node
	 */
	public MCSTNode(){
		this.nodeLevel = 0;
	}
	
	public MCSTNode(IAction takenAction, IGameState previousGameState, int nodeLevel)
	{
		this.takenAction = takenAction;
		this.priorProbability = GlobalDefinitions.getPriorProbabilityCalculator().calculate(previousGameState, takenAction);
		this.nodeLevel = nodeLevel;
	}
	
	/**
	 * This initialization method should not be used, since the prior probability
	 * might be inefficiently calculated.
	 * @param takenAction
	 * @param previousGameState
	 * @param nodeLevel
	 */
	public void initalize(IAction takenAction, IGameState previousGameState, int nodeLevel){
		this.reset();
		this.takenAction = takenAction;
		this.priorProbability = GlobalDefinitions.getPriorProbabilityCalculator().calculate(previousGameState, takenAction);
		this.nodeLevel = nodeLevel;
	}
	
	/**
	 * Initializes the object
	 * @param takenAction
	 * @param previousGameState
	 * @param nodeLevel
	 * @param priorProbability
	 */
	public void initalize(IAction takenAction, IGameState previousGameState, int nodeLevel, float priorProbability){
		this.reset();
		this.takenAction = takenAction;
		this.priorProbability = priorProbability;
		this.nodeLevel = nodeLevel;
	}
	
	public void reset(){
		takenAction = null;
		gameState = null;
		priorProbability = 0.0f;
		visitCount = 0;
		evaluationValue = GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE;
		actionValue = GlobalDefinitions.ACTION_VALUE_NEUTRAL_VALUE;
		oldActionValue = LAST_ACTION_VALUE_INITAL_VALUE;
		childNodes = new ArrayList<MCSTNode>();
		nodeLevel = 0;
		winingChildNode = null;
		actionValueUpdated = false;
	}
	

	private void visitNodeFirstTime(IGameState previousGameState){
		if (takenAction == null){//root --> no action need to be taken
			this.gameState = previousGameState.clone();
		} else {//action need to be simulated to get new game state
			this.gameState = previousGameState.clone();
			this.gameState.simulateAction(takenAction);
		}
		evaluateNode();
		if (getEvaluationValue() == GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND){//Upper bound = win
			return;
		}
		if (getEvaluationValue() == GlobalDefinitions.NODE_EVALUATION_LOWER_BOUND){//Lower bound = loss
			return;
		}
		List<IAction> allowedActionsList = getGameState().getAllowedActions();
//		if (allowedActionsList.isEmpty() || allowedActionsList.size() == 0){
//			LOGGER.log("There were no allowed actions.");
//			getGameState().logGameState();
//		}
		IPriorProbabilityCalculator priProCalculator = GlobalDefinitions.getPriorProbabilityCalculator();
		IAction[] allowedActions = new IAction[allowedActionsList.size()];
		allowedActionsList.toArray(allowedActions);
		float[] priorProbabilities = priProCalculator.calculate(getGameState(), allowedActions);
		//TODO: Check why the line below is never true
		if (priorProbabilities == null || priorProbabilities.length == 0){
			LOGGER.log("There  were no prior probabilities");
		}
		for (int i = 0; i < allowedActions.length; i++) {
			MCSTNode node = ObjectManager.getNewMCSTNode();
			node.initalize(allowedActions[i], getGameState(), this.nodeLevel+1, 
					priorProbabilities[i]);
			this.childNodes.add(node);
		}
	}
	
	private MCSTNode selectChildNode(){
		if (childNodes.isEmpty()) return null;
		//If wining child node exists, visit it
		if (winingChildNode != null) return winingChildNode;
		
		MCSTNode currentlyBestNode = childNodes.get(0);
		Player playerAtTurn = getGameState().getPlayerAtTurn();
		if (playerAtTurn == null){
			LOGGER.log("PlayerAtTurn is null!");
			return null;
		}
		for (int i = 1; i < childNodes.size(); i++) {
			MCSTNode child = childNodes.get(i);
			//if (playerAtTurn == Player.getPlayer(PlayerTypes.Self)){
				if(child.getNodeSelectionValue() > currentlyBestNode.getNodeSelectionValue()){
					currentlyBestNode = child;
				}
//			} else if (playerAtTurn == Player.getPlayer(PlayerTypes.Opponent)){
//				if(child.getNodeSelectionValue() < currentlyBestNode.getNodeSelectionValue()){
//					currentlyBestNode = child;
//				}
//			} else {
//				LOGGER.log("Player of Turn not correctly set: " + playerAtTurn.getPlayerType().name());
//				return null;
//			}
				
		}
		return currentlyBestNode;
	}
	
	private void evaluateNode(){
		INodeEvaluationValueCalculator evaluationCalculator = GlobalDefinitions.getNodeEvaluationCalculator();
		evaluationValue = evaluationCalculator.calculate(this);
	}
	
	private void updateNode(Boolean needToUpdateActionValue, MCSTNode updatedChildNode){
		if (needToUpdateActionValue){
			IActionValueCalculator actionValueCalculator = GlobalDefinitions.getActionValueCalculator();
			setActionValue(actionValueCalculator.calculate(this, updatedChildNode));
			actionValueUpdated = true;
		} else {
			actionValueUpdated = false;
		}
		updateNodeSelectionValue();
		updateSubTreeDepth(updatedChildNode);
	}
	
	/**
	 * Visits the node and thus executes one monte carlo iteration.
	 * In case takenAction is not set the previousGameSate parameter will be interpreted as
	 * current gamestate.
	 * @param previousGameState the previous gamestate
	 */
	public void visitNode(IGameState previousGameState){
		if(isLeaf()){
			if (getVisitCount() == 0){
				visitNodeFirstTime(previousGameState);
				updateNode(true, null);
			} else {
				updateNode(false, null);
			}
		} else {
			MCSTNode child = selectChildNode();
			if (child == null){
				LOGGER.log("It was tried to select a child node although there was none");
			} else {
				if (evaluationValue == GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND){
					LOGGER.log("Node with game ending state was visited the " + visitCount + " time");
				}
				child.visitNode(this.gameState);
				//If winning node, then store it
				if (child.getEvaluationValue() == GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND){
					this.winingChildNode = child;
				}
				updateNode(child.wasActionValueUpdated(), child);
			}
		}
		increaseVisitCount();
		
	}
	
	public IAction getActionWithMostVisits(){
		if (childNodes.isEmpty()) {
			LOGGER.log("Node had no child nodes to select from.");
			getGameState().logGameState();
			LOGGER.log("Is end state? " + getGameState().isEndState());
			logTree(-1);
			return null;
		}
		
		MCSTNode currentlyMostVisitedNode = childNodes.get(0);
		for (int i = 1; i < childNodes.size(); i++) {
			MCSTNode child = childNodes.get(i);
			if (child.getVisitCount() == currentlyMostVisitedNode.getVisitCount()){
				if (child.getActionValue() > currentlyMostVisitedNode.getActionValue()){
					currentlyMostVisitedNode = child;
				}
			} else if (child.getVisitCount() > currentlyMostVisitedNode.getVisitCount()){
				currentlyMostVisitedNode = child;
			}
		}
		LOGGER.log("Action taken with value: " + currentlyMostVisitedNode.getActionValue());
		LOGGER.log("Action taken with visit amount of: " + currentlyMostVisitedNode.getVisitCount());
		LOGGER.log("Action Prioprobability value: " + currentlyMostVisitedNode.getPriorProbability());
		LOGGER.log("Resulting state evaluation value: " + currentlyMostVisitedNode.getEvaluationValue());
		return currentlyMostVisitedNode.getTakenAction();
	}
	
	/**
	 * Prints the sub tree to the given tree depth.
	 * if the tree depth is -1 the whole sub tree is printed.
	 * @param treeDepth
	 */
	public void printTree(int treeDepth){
		if (treeDepth != -1 && treeDepth < this.nodeLevel){
			return;
		}
		StringBuilder printRepresentationOfNode = new StringBuilder();
		for (int i = 0; i < nodeLevel; i++){
			printRepresentationOfNode.append(" ");
		}
		printRepresentationOfNode.append(nodeLevel);
		printRepresentationOfNode.append(": ");
		if (takenAction != null){
			printRepresentationOfNode.append(takenAction.toString());
		}
		printRepresentationOfNode.append(" - ");
		printRepresentationOfNode.append("a-value " + getActionValue());
		if (takenAction != null){
			printRepresentationOfNode.append("; ");
			printRepresentationOfNode.append("n.sel-value " + getNodeSelectionValue());
		}
		if (takenAction != null){
			printRepresentationOfNode.append("; ");
			printRepresentationOfNode.append("PriPro " + getPriorProbability());
		}
		printRepresentationOfNode.append("; ");
		printRepresentationOfNode.append("eval-value " + getEvaluationValue());
		printRepresentationOfNode.append("; ");
		printRepresentationOfNode.append("visits " + getVisitCount());
		
		System.out.println(printRepresentationOfNode.toString());
		for (MCSTNode mcstNode : childNodes) {
			mcstNode.printTree(treeDepth);
		}
	}
	
	public void logTree(int treeDepth){
		if (treeDepth != -1 && treeDepth < this.nodeLevel){
			return;
		}
		StringBuilder printRepresentationOfNode = new StringBuilder();
		for (int i = 0; i < nodeLevel; i++){
			printRepresentationOfNode.append(" ");
		}
		printRepresentationOfNode.append(nodeLevel);
		printRepresentationOfNode.append(": ");
		if (takenAction != null){
			printRepresentationOfNode.append(takenAction.toString());
		}
		printRepresentationOfNode.append(" - ");
		printRepresentationOfNode.append("a-value " + getActionValue());
		if (takenAction != null){
			printRepresentationOfNode.append("; ");
			printRepresentationOfNode.append("n.sel-value " + getNodeSelectionValue());
		}
		if (takenAction != null){
			printRepresentationOfNode.append("; ");
			printRepresentationOfNode.append("PriPro " + getPriorProbability());
		}
		printRepresentationOfNode.append("; ");
		printRepresentationOfNode.append("eval-value " + getEvaluationValue());
		printRepresentationOfNode.append("; ");
		printRepresentationOfNode.append("visits " + getVisitCount());
		
		LOGGER.log(printRepresentationOfNode.toString());
		for (MCSTNode mcstNode : childNodes) {
			mcstNode.printTree(treeDepth);
		}
	}
}
