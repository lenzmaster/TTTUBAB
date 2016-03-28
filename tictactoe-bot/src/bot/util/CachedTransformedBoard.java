package bot.util;

import bot.Field;
import bot.FieldCalculationHelper;
import bot.IFieldObserver;
import bot.Move;
import bot.Player;

public class CachedTransformedBoard implements IFieldObserver{

	private Move lastMove;
	private BoardTransformer boardTransformer;
	private float[][] transformedBoard = null;
	private Player playerWhoIsViewpoint;
	
	public Move getLastMove(){
		return lastMove;
	}
	
	
	public float[][] getTransformedBoard(){
		return transformedBoard;
	}
	
	public Player getPlayerWhoIsViewpoint(){
		return playerWhoIsViewpoint;
	}
	
	/**
	 * Empty constructor for the clone method.
	 */
	private CachedTransformedBoard(){
		
	}
	
	/**
	 * Initializes an object.
	 * @param lowerBound
	 * @param upperBound
	 * @param notOccupiedValue
	 * @param playerWhoIsViewPoint the player that the board is transformed for
	 */
	public CachedTransformedBoard(float lowerBound, float upperBound, float notOccupiedValue, Player playerWhoIsViewPoint) {
		this.boardTransformer = new BoardTransformer(lowerBound, upperBound, notOccupiedValue);
		this.playerWhoIsViewpoint = playerWhoIsViewPoint;
	}
	
	/**
	 * Initializes an object by using a given board transformer.
	 * @param boardTransformer
	 * @param playerWhoIsViewPoint the player that the board is transformed for
	 */
	public CachedTransformedBoard(BoardTransformer boardTransformer, Player playerWhoIsViewPoint) {
		this.boardTransformer = boardTransformer;
		this.playerWhoIsViewpoint = playerWhoIsViewPoint;
	}
	
	
	@Override
	public void moveExecuted(Field sender, Move move) {
		//Check, if transformed board already exists
		if (transformedBoard == null){
			//Initialize transformed board with new board from the field object
			transformedBoard = boardTransformer.transform(
					sender.getCopyOfBoard(), playerWhoIsViewpoint);
		} else {
			//Only transform the new added tile
			int x = move.getX();
			int y = move.getY();
			int changedTile = sender.getTile(x, y);
			transformedBoard[move.getX()][move.getY()] = boardTransformer.transform(changedTile, playerWhoIsViewpoint);
		}
		lastMove = move;
	}

	@Override
	public CachedTransformedBoard clone(){
		CachedTransformedBoard copy = new CachedTransformedBoard();
		copy.lastMove = this.lastMove;
		copy.boardTransformer = this.boardTransformer;
		copy.transformedBoard = FieldCalculationHelper.copyBoard(transformedBoard);
		copy.playerWhoIsViewpoint = playerWhoIsViewpoint;
		return copy;
	}
	
}
