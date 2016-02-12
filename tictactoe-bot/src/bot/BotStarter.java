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
import bot.mcst.MCSTTree;
import bot.memory.ObjectManager;
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
	
    /**
     * Makes a turn. Edit this method to make your bot smarter.
     * Currently does only random moves.
     *
     * @return The column where the turn was made.
     */
	public Move makeTurn(Field field, int totalTimeLeft) {
		field.setPlayerAtTurn(Player.getPlayer(PlayerTypes.Self));
		LOGGER.log("Total time left (in ms): " + totalTimeLeft);
		long turnTimeInNS = 1000000 * GlobalDefinitions.TIME_USED_PER_TURN;
		MCSTTree tree = ObjectManager.getNewMCSTTree();
		tree.initalize(ObjectManager.getNewMCSTNode());
		IAction action = tree.calculateBestAction(field, turnTimeInNS);
		return ((Move) action);
	}


	public static void main(String[] args) {
		BotParser parser = new BotParser(new BotStarter(), System.in, System.out);
		parser.run();
	}
}
