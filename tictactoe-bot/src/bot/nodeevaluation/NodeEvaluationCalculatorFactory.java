package bot.nodeevaluation;

public class NodeEvaluationCalculatorFactory {

	
	public static INodeEvaluationValueCalculator createMacroStrategyOnlyCalculator(){
		ConicalCombinationEvaluationValueCalculator calculator = new ConicalCombinationEvaluationValueCalculator();
		calculator.addFunction(1, new MacroStrategyEvaluationFunction());
		return calculator;
	}
	
	public static INodeEvaluationValueCalculator createWinningOptionOnlyCalculator(){
		ConicalCombinationEvaluationValueCalculator calculator = new ConicalCombinationEvaluationValueCalculator();
		calculator.addFunction(1, new WinningOptionEvaluationFunction());
		return calculator;
	}
	
	public static INodeEvaluationValueCalculator createWinningOptionWithDiminishingReturnsOnlyCalculator(){
		ConicalCombinationEvaluationValueCalculator calculator = new ConicalCombinationEvaluationValueCalculator();
		calculator.addFunction(1, new WinningOptionEvaluationFunctionWithDiminishingReturns());
		return calculator;
	}
	
}
