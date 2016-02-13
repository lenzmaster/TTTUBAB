package bot.util;

import bot.actionvaluecalculation.FirstActionValueCalculator;
import bot.actionvaluecalculation.IActionValueCalculator;
import bot.nodeevaluation.FirstNodeEvaluationValueCalculator;
import bot.nodeevaluation.INodeEvaluationValueCalculator;
import bot.nodeselectioncalculation.FirstNodeSelectionValueCalculator;
import bot.nodeselectioncalculation.INodeSelectionValueCalculator;
import bot.priorprobabilitycalculation.IPriorProbabilityCalculator;
import bot.priorprobabilitycalculation.RandomPriorProbabilityCalculator;


public class GlobalDefinitions {

	private static IPriorProbabilityCalculator _probabilityCalculator = null;
	private static INodeSelectionValueCalculator _nodeSelectionValueCalculator = null;
	private static IActionValueCalculator _actionValueCalculator = null;
	private static INodeEvaluationValueCalculator _nodeEvaluationCalculator = null;
	public static final int TIME_USED_PER_TURN = 450;
	public static final int TIME_BUFFER_FOR_FINISHING_DECISION_PROCESS = 50;
	public static final int PLAYER_NEUTRAL_ID = 0;
	public static final int MACRO_FIELD_NEEDS_TO_BE_USED_ID = -1;
	
	//Prior probability constants
	public static final float NODE_PRIOR_PROBABILITY_LOWER_BOUND = 0.0f;
	public static final float NODE_PRIOR_PROBABILITY_UPPER_BOUND = 1.0f;
	public static final float NODE_PRIOR_PROBABILITY_NEUTRAL_VALUE = 0.5f;
	
	//Node evaluation constants
	public static final float NODE_EVALUATION_LOWER_BOUND = 0.0f;
	public static final float NODE_EVALUATION_UPPER_BOUND = 1.0f;
	public static final float NODE_EVALUATION_NEUTRAL_VALUE = 0.5f;
	
	//Action value constants
	public static final float ACTION_VALUE_LOWER_BOUND = 0.0f;
	public static final float ACTION_VALUE_UPPER_BOUND = 1.0f;
	public static final float ACTION_VALUE_NEUTRAL_VALUE = 0.5f;
	
	//Memory constants
	public static final int MANAGED_FIELDS_INITAL_CAPACITY = 500000;
	public static final int MANAGED_MOVES_INITAL_CAPACITY = 500000;
	public static final int MANAGED_POINTS_INITAL_CAPACITY = 1000000;
	public static final int MANAGED_MCSTNODES_INITAL_CAPACITY = 500000;
	public static final int MANAGED_MCSTTREES_INITAL_CAPACITY = 90;
	public static final boolean USE_PRECREATED_OBJECTS = false;
	
	
	public static IPriorProbabilityCalculator getPriorProbabilityCalculator(){
		if (_probabilityCalculator == null){
			_probabilityCalculator = new RandomPriorProbabilityCalculator();
		}
		return _probabilityCalculator;
	}

	
	public static INodeSelectionValueCalculator getNodeSelectionValueCalculator(){
		if (_nodeSelectionValueCalculator == null){
			_nodeSelectionValueCalculator = new FirstNodeSelectionValueCalculator();
		}
		return _nodeSelectionValueCalculator;
	}
	
	public static IActionValueCalculator getActionValueCalculator(){
		if (_actionValueCalculator == null){
			_actionValueCalculator = new FirstActionValueCalculator();
		}
		return _actionValueCalculator;
	}
	
	public static INodeEvaluationValueCalculator getNodeEvaluationCalculator(){
		if (_nodeEvaluationCalculator == null){
			_nodeEvaluationCalculator = new FirstNodeEvaluationValueCalculator();
		}
		return _nodeEvaluationCalculator;
	}
	
	
	
}
