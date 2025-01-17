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
import java.util.Random;

import bot.Player.PlayerTypes;
import bot.mcst.IAction;
import bot.mcst.MCSTNode;
import bot.mcst.MCSTTree;
import bot.memory.ObjectManager;
import bot.threading.CounterForTestThread;
import bot.threading.TestThread;
import bot.threading.TreeVisitorThread;
import bot.util.GlobalDefinitions;
import bot.util.Logger;

/**
 * BotStarter class
 * 
 * Magic happens here. You should edit this file, or more specifically
 * the makeTurn() method to make your bot do more than random moves.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotStarter {

	private static Logger LOGGER = new Logger("BotStarter");
	
	private MCSTTree tree = null;
	
	private Field _fieldForDoubleChecking = null;
	
	private boolean initalization = true;
	
	private Move lastPerformedMoveByOpponent = null;
	
	private long timeEndLastTurn = 0;
	
	/**
	 * Always use this getter and never the variable, because of lazy loading.
	 * @return
	 */
	private Field getFieldForDoubleChecking(){
		if (_fieldForDoubleChecking == null){
			_fieldForDoubleChecking = ObjectManager.getNewField();
			_fieldForDoubleChecking.initalize();
			_fieldForDoubleChecking.setPlayerAtTurn(Player.getPlayer(PlayerTypes.Self));//Needs to be added for completeness
		}
		return _fieldForDoubleChecking;
	}
	
	/**
	 * Always use this getter and never the variable, because of lazy loading.
	 * @return
	 */
	public MCSTTree getTree(){
		if (tree == null){
			tree = ObjectManager.getNewMCSTTree();
			tree.initalize(ObjectManager.getNewMCSTNode());
		}
		return tree;
	}
	
    /**
     * Makes a turn. Edit this method to make your bot smarter.
     * Currently does only random moves.
     *
     * @return The column where the turn was made.
     */
	public Move makeTurn(long totalTimeLeft) {
		long startTime = System.nanoTime();
		//During initialization set root to a field containing all information received
		if(initalization){
			//This is dirty, since the game state of the root is the previous game state, since no action was taken
			this.getTree();
			
			//Lock tree
			try {
				getTree().lock();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.getTree().setPreviousGameState(getFieldForDoubleChecking());//Set initialization to false the first time a move needs made
			this.initalization = false;
			//Start visitor thread
			TreeVisitorThread visitorThread = new TreeVisitorThread(this.tree);
			visitorThread.start();
		} else {
			//Lock tree
			try {
				getTree().lock();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOGGER.log("Time elapsed since last turn: " + (startTime - timeEndLastTurn));
			//Log move made by opponent and the current move made by the current root
			LOGGER.log("Last move of opponent: (" + lastPerformedMoveByOpponent.getX() +  "/" + lastPerformedMoveByOpponent.getY() + ")");
			//Log current tree depth
			LOGGER.log("Current Tree depth: " + getTree().getTreeDepth());
			LOGGER.log("Iterations since last turn: " + getTree().getIterationCount());
			getTree().resetIterationCount();
			//Check if game states are equal
			Field currentRootField = (Field) getTree().getRoot().getGameState();
			if (!currentRootField.equals(getFieldForDoubleChecking())){
				LOGGER.log("Root field is unequal the field created based on the received game data of the gamestate.");
				//Log current macroboard of root
				LOGGER.log("Current macroboard: " + ((Field) getTree().getRoot().getGameState()).getMacroBoardAsString());
				LOGGER.log("Round nr of root state: " + currentRootField.getRoundNr());
				LOGGER.log("Move nr of root state: " + currentRootField.getMoveNr());
				//Don´t reuse old tree
				MCSTNode newRoot = ObjectManager.getNewMCSTNode();
				getTree().setRoot(newRoot);
				getTree().setPreviousGameState(getFieldForDoubleChecking().clone());
			} else {
				LOGGER.log("Root field is equal to current gamestate:)");
			}
		}
		
		
		LOGGER.log("Total time left (in ms): " + totalTimeLeft);
		long totalTimeLeftInNs = totalTimeLeft * GlobalDefinitions.TIME_MS_TO_NS_FACTOR;
		long turnTimeInNS = GlobalDefinitions.getTimeCalculator().calculateTimeForTurn(getTree().getRoot(), totalTimeLeftInNs);
		long turnTimeleft = (turnTimeInNS - (System.nanoTime() - startTime)) / GlobalDefinitions.TIME_MS_TO_NS_FACTOR;
		try {
			if (turnTimeleft < 0){
				//Don´t unlock and just get the next action
				LOGGER.log("Thread didn´t sleep, since turn time left was (in ms): " + turnTimeleft);
			} else {
				//Unlock and sleep
				getTree().unlock();
				LOGGER.log("Thread sleept (in ms): " + turnTimeleft);
				Thread.sleep(turnTimeleft);
				//Lock to get action
				getTree().lock();
			}
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		IAction actionToTake = getTree().getMostVisitedAction();
		LOGGER.log("Iterations this turn: " + getTree().getIterationCount());
		getTree().setNewRoot(actionToTake);
		getTree().resetIterationCount();
		//Unlock and finish move
		getTree().unlock();
		
		timeEndLastTurn = System.nanoTime();
		return ((Move) actionToTake);
	}
	
	/**
	 * Updates the game state.
	 */
	public void updateGameState(String key, String value){
		getFieldForDoubleChecking().parseGameData(key, value);
		//The remaining code is not needed during initialization
		if (initalization) return;
		
		if (key.equals("field")){
			//Extract board from string
			int[][] newBoard = new int[Field.COLS][Field.ROWS];
			String s = value.replace(";", ",");
			String[] r = s.split(",");
			int counter = 0;
			for (int y = 0; y < Field.COLS; y++) {
				for (int x = 0; x < Field.ROWS; x++) {
					newBoard[x][y] = Integer.parseInt(r[counter]); 
					counter++;
				}
			}
			//Lock
			try {
				getTree().lock();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Get performed move
			lastPerformedMoveByOpponent = ((Field) tree.getRoot().getGameState()).getPerformedMove(newBoard);
			//Set new root of tree
			//TODO: use the return value
			boolean newRootFound = getTree().setNewRoot(lastPerformedMoveByOpponent);
			//Unlock
			getTree().unlock();
			
		}
	}


	public static void main(String[] args) {
		BotParser parser = new BotParser(new BotStarter(), System.in, System.out);
		parser.run();
	}
}
