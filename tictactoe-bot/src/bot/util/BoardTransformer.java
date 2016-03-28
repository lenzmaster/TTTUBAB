package bot.util;

import bot.Player;
import bot.Player.PlayerTypes;

public class BoardTransformer {

	private float lowerBound;
	private float upperBound;
	private float notOccupiedValue;
	
	public float getLowerBound(){
		return lowerBound;
	}
	
	public float getUpperBound(){
		return upperBound;
	}
	
	public float getNotOccupiedValue(){
		return notOccupiedValue;
	}
	
	public BoardTransformer(float lowerBound, float upperBound, float notOccupiedValue){
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.notOccupiedValue = notOccupiedValue;
	}
	
	/**
	 * Extracts a 3x3 microboard from a 9x9 board.
	 * A new array is created.
	 * @param transformedBoard
	 * @param microboardX
	 * @param microboardY
	 * @return
	 */
	public static float[][] getMicroboard(float[][] transformedBoard, int microboardX, int microboardY){
		float[][] microboard = new float[3][3];
		int microboardStartX = microboardX * 3;
		int microboardStartY = microboardY * 3;
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				microboard[i][j] = transformedBoard[microboardStartX + i][microboardStartY + j];
			}
		}
		return microboard;
	}
	
	/**
	 * Transforms the board to the given scale.
	 * The scale is defined by the parameters lowerBound and upperBound.
	 * The board is transformed based on the perspective of the given player.
	 * Additionally a not occupied value has to be set, which must not be in the range of the scale
	 * --> this is now allowed: lowerBound <= notOccupiedValue <= upperBound
	 * 
	 * @param player the player for whom the board is transformed
	 * @param board a board from a gamestate
	 * @param upperBound the upper bound of the scale
	 * @param lowerBound the lower bound of the scale
	 * @param notOccupiedValue the not occupied value after the transformation, which must not be in the range of the scale
	 * @return transformed board to the defined scale
	 */
	public float[][] transform(int[][] board, Player player){
		float[][] transformedBoard = new float[board.length][board[0].length];
		
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[i].length; j++){
				transformedBoard[i][j] = transform(board[i][j], player);
			}
		}
		
		return transformedBoard;
	}
	
	/**
	 * Transforms the tile to the given scale.
	 * The scale is defined by the parameters lowerBound and upperBound.
	 * The tile is transformed based on the perspective of the given player.
	 * Additionally, a not occupied value has to be set, which must not be in the range of the scale
	 * --> this is now allowed: lowerBound <= notOccupiedValue <= upperBound
	 * 
	 * @param player the player for whom the board is transformed
	 * @param tile a tile from a gamestate
	 * @param upperBound the upper bound of the scale
	 * @param lowerBound the lower bound of the scale
	 * @param notOccupiedValue the not occupied value after the transformation, which must not be in the range of the scale
	 * @return transformed tile to the defined scale
	 */
	public float transform(int tile, Player player){
		Player none = Player.getPlayer(PlayerTypes.None);
		if (player.getPlayerType() == none.getPlayerType()) throw new IllegalArgumentException("PlayerType none is not allowed for this method");
		
		Player opponent = Player.getInvertedPlayer(player);
		float transformedTile;
		if (tile == player.getId()){
			transformedTile = upperBound;
		} else if (tile == opponent.getId()){
			transformedTile = lowerBound;
		} else if (tile == none.getId()){
			transformedTile = notOccupiedValue;
		} else {
			throw new IllegalArgumentException("Board contained an unidentifiable id: " + tile);
		}
		
		return transformedTile;
	}
	
}
