package bot.winningoption;

public interface IWinningOptionScoreCalculator {

	public float getMaxPossibleScoreDifference();
	
	public float[] calcScores(float[][] transformedBoard);
	
	public float getMaxIntermediateValue();
	
}
