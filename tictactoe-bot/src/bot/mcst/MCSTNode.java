package bot.mcst;

import java.util.ArrayList;
import java.util.List;

import bot.Move;
import bot.actionvaluecalculation.IActionValueCalculator;
import bot.memory.IReusable;
import bot.memory.ObjectManager;
import bot.nodeevaluation.INodeEvaluationValueCalculator;
import bot.nodeselectioncalculation.INodeSelectionValueCalculator;
import bot.util.GlobalDefinitions;
import bot.util.Logger;

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
	private MCSTNode winingChildNode;
	private Boolean wasUpdated = false;
	
	
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
	
	public Boolean wasUpdated(){
		return wasUpdated;
	}
	
	public float getNodeSelectionValue(){
		if (nodeSelectionValue == -1){
			INodeSelectionValueCalculator nodeSelectionCalculator = GlobalDefinitions.getNodeSelectionValueCalculator();
			nodeSelectionValue = nodeSelectionCalculator.calculate(this);
		}
		return nodeSelectionValue;
	}
	
	private void updateNodeSelectionValue(){
		INodeSelectionValueCalculator nodeSelectionCalculator = GlobalDefinitions.getNodeSelectionValueCalculator();
		nodeSelectionValue = nodeSelectionCalculator.calculate(this);
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
	
	public void initalize(IAction takenAction, IGameState previousGameState, int nodeLevel){
		this.reset();
		this.takenAction = takenAction;
		this.priorProbability = GlobalDefinitions.getPriorProbabilityCalculator().calculate(previousGameState, takenAction);
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
		wasUpdated = false;
	}
	
	/**
	 * Visits the node and thus executes one monte carlo iteration.
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
				updateNode(child.wasUpdated(), child);
			}
		}
		increaseVisitCount();
		
	}
	
	public IAction getActionWithMostVisits(){
		if (childNodes.isEmpty()) return null;
		
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
		return currentlyMostVisitedNode.getTakenAction();
	}
	
	private void visitNodeFirstTime(IGameState previousGameState){
		if (takenAction == null){//root --> no action need to be taken
			this.gameState = previousGameState;
		} else {//action need to be simulated to get new game state
			this.gameState = previousGameState.simulateAction(takenAction);
		}
		evaluateNode();
		if (getEvaluationValue() == GlobalDefinitions.NODE_EVALUATION_UPPER_BOUND){//Upper bound = win
			return;
		}
		if (getEvaluationValue() == GlobalDefinitions.NODE_EVALUATION_LOWER_BOUND){//Lower bound = loss
			return;
		}
		List<IAction> allowedActions = getGameState().getAllowedActions();
		for (IAction action : allowedActions) {
			MCSTNode node = ObjectManager.getNewMCSTNode();
			node.initalize(action, getGameState(), this.nodeLevel+1);
			this.childNodes.add(node);
		}
	}
	
	private MCSTNode selectChildNode(){
		if (childNodes.isEmpty()) return null;
		//If wining child node exists, visit it
		if (winingChildNode != null) return winingChildNode;
		
		MCSTNode currentlyHighestNode = childNodes.get(0);
		for (int i = 1; i < childNodes.size(); i++) {
			MCSTNode child = childNodes.get(i);
			if(child.getNodeSelectionValue() > currentlyHighestNode.getNodeSelectionValue()){
				currentlyHighestNode = child;
			}
		}
		return currentlyHighestNode;
	}
	
	private void evaluateNode(){
		INodeEvaluationValueCalculator evaluationCalculator = GlobalDefinitions.getNodeEvaluationCalculator();
		evaluationValue = evaluationCalculator.calculate(this);
	}
	
	private void updateNode(Boolean needsUpdate, MCSTNode updatedChildNode){
		if (needsUpdate){
			IActionValueCalculator actionValueCalculator = GlobalDefinitions.getActionValueCalculator();
			setActionValue(actionValueCalculator.calculate(this, updatedChildNode));
			updateNodeSelectionValue();
			wasUpdated = true;
		} else {
			wasUpdated = false;
		}
		
	}

}
