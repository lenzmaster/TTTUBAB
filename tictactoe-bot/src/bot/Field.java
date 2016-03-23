// // Copyright 2016 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//  
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package bot;

import java.util.ArrayList;
import java.util.List;

import bot.Player.PlayerTypes;
import bot.mcst.IAction;
import bot.mcst.IGameState;
import bot.memory.IReusable;
import bot.memory.ObjectManager;
import bot.util.GlobalDefinitions;
import bot.util.Logger;

/**
 * Field class
 * 
 * Handles everything that has to do with the field, such 
 * as storing the current state and performing calculations
 * on the field.
 * 
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */

public class Field implements IGameState, IReusable{
	
		private static Logger LOGGER = new Logger("Field");
		public static final int COLS = 9, ROWS = 9;
		public static final int MACROCOLS = 3, MACROROWS= 3; //TODO: use this in every code
	
	   	private int mRoundNr;
	    private int mMoveNr;
		private int[][] mBoard;
		private int[][] mMacroboard;
		private Player playerAtTurn;
		
		public void setPlayerAtTurn(Player player){
			this.playerAtTurn = player;
		}
		
		@Override
		public Player getPlayerAtTurn(){
			return this.playerAtTurn;
		}
		
		public int getRoundNr(){
			return mRoundNr;
		}
		
		public int getMoveNr(){
			return mMoveNr;
		}
		
		public int[][] getCopyOfBoard(){
			int[][] copyOfBoard = new int[mBoard.length][mBoard[0].length];
			for (int i = 0; i < this.mBoard.length; i++){
				for (int j = 0; j < this.mBoard[i].length; j++){
					copyOfBoard[i][j] = this.mBoard[i][j];
				}
			}
			return copyOfBoard;
		}
		
		public int[][] getCopyOfMicroboard(int microboardX, int microboardY){
			int[][] copyOfBoard = new int[COLS/3][ROWS/3];
			int startX = microboardX * 3;
			int startY = microboardY * 3;
			for (int i = 0; i < copyOfBoard.length; i++){
				for (int j = 0; j < copyOfBoard[i].length; j++){
					copyOfBoard[i][j] = this.mBoard[startX * 3 + i][startY * 3 + j];
				}
			}
			return copyOfBoard;
		}
		
		public int[][] getCopyOfMacroboard(){
			return FieldCalculationHelper.copyBoard(mMacroboard);
		}
		
		public Field() {
			mBoard = new int[COLS][ROWS];
			mMacroboard = new int[COLS / 3][ROWS / 3];
			clearBoard();
		}
		
		public void initalize(){
			//Nothing needs to be done here
			reset();
		}
		
		public void reset(){
			mRoundNr = 0;
			mMoveNr = 0;
			clearBoard();
			clearMacroboard();
			playerAtTurn = null;
		}

		private Field copy(){
			Field copy = ObjectManager.getNewField();
			copy.mRoundNr = this.mRoundNr;
			copy.mMoveNr = this.mMoveNr;
			for (int i = 0; i < this.mBoard.length; i++){
				for (int j = 0; j < this.mBoard[i].length; j++){
					copy.mBoard[i][j] = this.mBoard[i][j];
				}
			}
			for (int i = 0; i < this.mMacroboard.length; i++){
				for (int j = 0; j < this.mMacroboard[i].length; j++){
					copy.mMacroboard[i][j] = this.mMacroboard[i][j];
				}
			}
			copy.playerAtTurn = this.playerAtTurn;
			
			return copy;
			
		}
		
		//ToDo: Implement the method and its usage more efficiently
		/**
		 * Checks, if the given microboard is full or has already been won.
		 * @param microboardX x coordinate of microboard
		 * @param microboardY y coordinate of microboard
		 * @return
		 */
		public boolean isMicroboardFullOrWon(int microboardX, int microboardY){
			int macroboardValue = mMacroboard[microboardX][microboardY];
			if (macroboardValue != GlobalDefinitions.PLAYER_NEUTRAL_ID
					&& macroboardValue != GlobalDefinitions.MACRO_FIELD_NEEDS_TO_BE_USED_ID){
				return true;
			}
			int startX = microboardX * 3;
			int startY = microboardY * 3;
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < 3; j++){
					if (mBoard[startX + i][startY + j] == GlobalDefinitions.PLAYER_NEUTRAL_ID){
						return false;
					}
				}
			}
			return true;
			
		}
		
		/**
		 * Checks, if a winner exists in the given board
		 * @param board a 3x3 board as two dimensional array
		 * @return the winner or null, if no one has won yet
		 */
		public int calculateWinnerInBoard(int[][] board){
			return FieldCalculationHelper.getWinner(board);
		}

		/**
		 * Calculates the macro index for the next move.
		 * @param lastMove the last move made
		 * @return A point representing the coordinates or null, if all available fields can be played
		 */
		public Point calculateMacroCoordinatesForNextMove(Move lastMove){
			if (lastMove == null) throw new IllegalArgumentException("Move object is null");
			int macroX = lastMove.getX() % 3;
			int macroY = lastMove.getY() % 3;
			if (mMacroboard[macroX][macroY] != GlobalDefinitions.MACRO_FIELD_NEEDS_TO_BE_USED_ID
					&& mMacroboard[macroX][macroY] != GlobalDefinitions.PLAYER_NEUTRAL_ID){
				return null;
			}
			Point point = ObjectManager.getNewPoint();
			point.initalize(macroX, macroY);
			return point;
		}
		
		/**
		 * Parse data about the game given by the engine
		 * @param key : type of data given
		 * @param value : value
		 */
		public void parseGameData(String key, String value) {
		    if (key.equals("round")) {
		        mRoundNr = Integer.parseInt(value);
		    } else if (key.equals("move")) {
		        mMoveNr = Integer.parseInt(value);
		    } else if (key.equals("field")) {
	            parseFromString(value); /* Parse Field with data */
	        } else if (key.equals("macroboard")) {
	            parseMacroboardFromString(value); /* Parse macroboard with data */
	        }
		}
		
		/**
		 * Initialise field from comma separated String
		 * @param String : 
		 */
		public void parseFromString(String s) {
		    System.err.println("Move " + mMoveNr);
			s = s.replace(";", ",");
			String[] r = s.split(",");
			int counter = 0;
			for (int y = 0; y < ROWS; y++) {
				for (int x = 0; x < COLS; x++) {
					mBoard[x][y] = Integer.parseInt(r[counter]); 
					counter++;
				}
			}
		}
		
		/**
		 * Initialise macroboard from comma separated String
		 * @param String : 
		 */
		public void parseMacroboardFromString(String s) {
			String[] r = s.split(",");
			int counter = 0;
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 3; x++) {
					mMacroboard[x][y] = Integer.parseInt(r[counter]);
					counter++;
				}
			}
		}
		
		public void clearBoard() {
			for (int x = 0; x < COLS; x++) {
				for (int y = 0; y < ROWS; y++) {
					mBoard[x][y] = 0;
				}
			}
		}
		
		public void clearMacroboard() {
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					mBoard[x][y] = 0;
				}
			}
		}

		public ArrayList<Move> getAvailableMoves() {
		    ArrayList<Move> moves = new ArrayList<Move>();
			
			for (int y = 0; y < ROWS; y++) {
	            for (int x = 0; x < COLS; x++) {
	                if (isInActiveMicroboard(x, y) && mBoard[x][y] == 0) {
	                	Move move = ObjectManager.getNewMove();
	                	move.initalize(x, y, getPlayerAtTurn());
	                    moves.add(move);
	                }
	            }
	        }

			return moves;
		}
		
		public boolean isMoveAllowed(Move move){
			if (move.getPlayer() != getPlayerAtTurn()){
				LOGGER.log("Player was not at turn. Player at turn: " + move.getPlayer().getPlayerType().name());
				return false;
			}
			
			if (!isInActiveMicroboard(move.getX(), move.getY())){
				LOGGER.log("Microboard was not active; current value:" +
						mBoard[move.getX()][move.getY()]+ 
						"; position: (" + move.getX() + "/" + move.getY() + ")");
				return false;
			}
			if (mBoard[move.getX()][move.getY()] == GlobalDefinitions.PLAYER_NEUTRAL_ID){
				return true;
			}
			
			LOGGER.log("Tile was already marked by id: " + mBoard[move.getX()][move.getY()]);
			return false;
		}
		
		public Boolean isInActiveMicroboard(int x, int y) {
		    return mMacroboard[(int) x/3][(int) y/3] == GlobalDefinitions.MACRO_FIELD_NEEDS_TO_BE_USED_ID;
		}

		
		@Override
		/**
		 * Creates comma separated String with player ids for the microboards.
		 * @param args : 
		 * @return : String with player names for every cell, or 'empty' when cell is empty.
		 */
		public String toString() {
			String r = "";
			int counter = 0;
			for (int y = 0; y < ROWS; y++) {
				for (int x = 0; x < COLS; x++) {
					if (counter > 0) {
						r += ",";
					}
					r += mBoard[x][y];
					counter++;
				}
			}
			return r;
		}
		
		public void printBoard(){
			
			for (int y = 0; y < ROWS; y++) {
				String line = "";
				for (int x = 0; x < COLS; x++) {
					if (x > 0) {
						line += "|";
					}
					line += mBoard[x][y];
				}
				System.out.println(line);
			}
		}
		
		/**
		 * Checks whether the field is full
		 * @param args : 
		 * @return : Returns true when field is full, otherwise returns false.
		 */
		public boolean isFull() {
			for (int x = 0; x < COLS; x++)
			  for (int y = 0; y < ROWS; y++)
			    if (mBoard[x][y] == 0)
			      return false; // At least one cell is not filled
			// All cells are filled
			return true;
		}
		
		public int getNrColumns() {
			return COLS;
		}
		
		public int getNrRows() {
			return ROWS;
		}

		public boolean isEmpty() {
			for (int x = 0; x < COLS; x++) {
				  for (int y = 0; y < ROWS; y++) {
					  if (mBoard[x][y] > 0) {
						  return false;
					  }
				  }
			}
			return true;
		}
		
		/**
		 * Returns the player id on given column and row
		 * @param args : int column, int row
		 * @return : int
		 */
		public int getPlayerId(int column, int row) {
			return mBoard[column][row];
		}
		
		/**
		 * Simulates the given move and returns a new Field object with the new game state.
		 * @param move the move to simulate
		 * @return the resulting field
		 */
		public Field simulateMove(Move move){
			Field newField = this.copy();
			newField.executeMove(move);
			return newField;
		}

		
		@Override
		public List<IAction> getAllowedActions(){
			List<Move> moves = getAvailableMoves();
			List<IAction> actions = new ArrayList<IAction>();
			for (Move move : moves) {
				actions.add(move);
			}
			return actions;
		}

		@Override
		public IGameState simulateAction(IAction action) {
			if (!(action instanceof Move)) {
				throw new IllegalArgumentException("Action not of type Move");
			}
			Move move = (Move) action;
			return simulateMove(move);
		}
		
		public void executeMove(Move move){
			if(!isMoveAllowed(move)){
				throw new IllegalArgumentException("Move is not allowed");
			}
			//Place move
			mBoard[move.getX()][move.getY()] = move.getPlayer().getId();
			//Update Player
			playerAtTurn = Player.getInvertedPlayer(move.getPlayer());
			//Update macroboard
			Point macroCoordinates = FieldCalculationHelper.getMacroIndex(move.getX(), move.getY());
			int[][] microboard = new int[3][3];
			for (int i = 0; i < microboard.length; i++){
				for (int j = 0; j < microboard[i].length; j++){
					microboard[i][j] = mBoard[macroCoordinates.getX()*3+i][macroCoordinates.getY()*3+j];
				}
			}
			//Check, if new field is won
			mMacroboard[macroCoordinates.getX()][macroCoordinates.getY()] = calculateWinnerInBoard(microboard);
			Point newMacroField = calculateMacroCoordinatesForNextMove(move);
			boolean isEveryFieldAllowed;
			if (newMacroField == null){
				isEveryFieldAllowed = true;
			} else {
				if (isMicroboardFullOrWon(newMacroField.getX(), newMacroField.getY())){
					isEveryFieldAllowed = true;
				} else {
					isEveryFieldAllowed = false;
				}
			}
			
			//Update macroboard for new move
			for (int i = 0; i < mMacroboard.length; i++){
				for (int j = 0; j < mMacroboard[i].length; j++){
					if (isEveryFieldAllowed){//Is every Field allowed?
						if (mMacroboard[i][j] == GlobalDefinitions.PLAYER_NEUTRAL_ID){
							if (!isMicroboardFullOrWon(i, j)){
								mMacroboard[i][j] = GlobalDefinitions.MACRO_FIELD_NEEDS_TO_BE_USED_ID;
							}
						}
					} else {//Only the new macro field is allowed
						if (newMacroField.getX() == i && newMacroField.getY() == j){//Is it the field for the new move?
							//Yes
							mMacroboard[newMacroField.getX()][newMacroField.getY()] = GlobalDefinitions.MACRO_FIELD_NEEDS_TO_BE_USED_ID;	
						} else {//No
							if (mMacroboard[i][j] == GlobalDefinitions.MACRO_FIELD_NEEDS_TO_BE_USED_ID){
								mMacroboard[i][j] = GlobalDefinitions.PLAYER_NEUTRAL_ID;
							}
						}		
					}
				}
			}
				
			//Update move and round counter
			mMoveNr++;
			if(mMoveNr % 2 == 1){
				mRoundNr++;
			}
		}
		
		/**
		 * 
		 * @return the winner or Players.None, if no one has won yet
		 */
		public Player getWinner(){
			return Player.getPlayer(calculateWinnerInBoard(mMacroboard));
		}
		
		
		public Move getPerformedMove(int[][] newBoard){
			Move performedMove = ObjectManager.getNewMove();
			for (int i = 0; i < this.mBoard.length; i++){
				for (int j = 0; j < this.mBoard[i].length; j++){
					if (this.mBoard[i][j] != newBoard[i][j]){
						performedMove.initalize(i, j, this.playerAtTurn);
						return performedMove;
					}
				}
			}
			return null;
		}
		
		/**
		 * returns a the macroboard as comma separated string
		 * @return
		 */
		public String getMacroBoardAsString(){
			StringBuilder builder = new StringBuilder(9);
			for (int i = 0; i < this.mMacroboard.length; i++){
				for (int j = 0; j < this.mMacroboard[i].length; j++){
					String stringToAppend = (i == 0 && j == 0) ? "" + this.mMacroboard[i][j] : "," + this.mMacroboard[i][j];
					builder.append(stringToAppend);
				}
			}
			return builder.toString();
		}
		
		@Override
		public boolean equals(Object o){

	        // If the object is compared with itself then return true  
	        if (o == this) {
	            return true;
	        }

	        /* Check if o is an instance of Field or not
	          "null instanceof [type]" also returns false */
	        if (!(o instanceof Field)) {
	            return false;
	        }
	        
	        // typecast o to Field so that we can compare data members 
	        Field field = (Field) o;	
	        
	        //Check equality based on member variables
	        if (this.mRoundNr != field.mRoundNr) return false;
	        
	        if (this.mMoveNr != field.mMoveNr) return false;
		    
	        for (int i = 0; i < this.mBoard.length; i++){
	        	for (int j = 0; j < this.mBoard[i].length; j++){
	        		if (this.mBoard[i][j] != field.mBoard[i][j]){
	        			return false;
	        		}
	        	}
	        }
	        
	        for (int i = 0; i < this.mMacroboard.length; i++){
	        	for (int j = 0; j < this.mMacroboard[i].length; j++){
	        		if (this.mMacroboard[i][j] != field.mMacroboard[i][j]){
	        			return false;
	        		}
	        	}
	        }
		    
		    if (!this.playerAtTurn.equals(field.playerAtTurn)) return false;
	        
	        return true;
		}
		
}