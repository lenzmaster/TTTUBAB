// Copyright 2016 theaigames.com (developers@theaigames.com)

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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import bot.memory.ObjectManager;
import bot.util.GlobalDefinitions;

/**
 * BotParser class
 * 
 * Main class that will keep reading output from the engine.
 * Will either update the bot state or get actions.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotParser {

	final Scanner scan;
	final PrintStream outStream;
	final BotStarter bot;

	private int mBotId = 0;
	private int opponentBotId = 0;

	public BotParser(BotStarter bot, InputStream inStream, PrintStream outStream) {
		this.scan = new Scanner(inStream);
		this.bot = bot;
		this.outStream = outStream;
		//Initalize chached objects
		ObjectManager.initalize();
	}

	public void run() {
		
		while(scan.hasNextLine()) {
			String line = scan.nextLine();

			if(line.length() == 0) {
				continue;
			}

			String result = parseCommand(line);
			if (result != null){
				//ugly, but easier for testing
				if (result.startsWith("place_move")){
					//ObjectManager.reset();
				}
				outStream.println(result);
			}
		}
	}
	
	
	public String parseCommand(String commandLine){
		String[] parts = commandLine.split(" ");
		if(parts[0].equals("settings")) {
			if (parts[1].equals("your_botid")) {
				mBotId = Integer.parseInt(parts[2]);
				opponentBotId = 3 - mBotId;//bot ids are 1 and 2
				Player.initalizePlayers(mBotId, opponentBotId, GlobalDefinitions.PLAYER_NEUTRAL_ID);
			}
			return null;
		} else if(parts[0].equals("update") && parts[1].equals("game")) { /* new game data */
		    this.bot.updateGameState(parts[2], parts[3]);
		    return null;
		} else if(parts[0].equals("action")) {
			if (parts[1].equals("move")) { /* move requested */
				int totalTimeLeft = Integer.parseInt(parts[2]);
				Move move = this.bot.makeTurn(totalTimeLeft);
				return "place_move " + move.getX() + " " + move.getY();
			}
			return "unknown action";
		} else {
			return "unknown command";
		}
	}
}