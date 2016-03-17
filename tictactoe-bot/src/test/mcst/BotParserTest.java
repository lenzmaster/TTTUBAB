package test.mcst;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bot.BotParser;
import bot.BotStarter;
import bot.memory.ObjectManager;
import bot.Field;

//ToDo: create test files (to read from) for the different test cases
public class BotParserTest {

	private static final long timePerTurn = 10000;//in ms
	
	private Scanner scannerForBotOutput;
	private PrintStream outStream;
	private InputStream inStream;
	private PrintStream printStreamForBotInput;
	private BotParser testInstanceParser;
	private BotStarter testInstanceBot;
	private Thread testThread;
	
	
	@Before
	public void setUp() throws IOException{
		//Set up output stream of bot
		PipedOutputStream pos = new PipedOutputStream();
		outStream = new PrintStream(pos);
		PipedInputStream intermediateStream = new PipedInputStream(pos);
		scannerForBotOutput = new Scanner(intermediateStream);
		
		//Set up input stream of bot
		PipedOutputStream pos2 = new PipedOutputStream();
		inStream = new PipedInputStream(pos2);
		printStreamForBotInput = new PrintStream(pos2);
		testInstanceBot = new BotStarter();
		testInstanceParser = new BotParser(testInstanceBot, inStream, outStream);
//		testThread = new Thread()
//		{
//		    public void run() {
//		        testInstanceParser.run();
//		    }
//		};
//		testThread.start();
		//testInstanceParser.run();
		
	}
	
	@After
	public void cleanUp(){
		
	}
	
	
	private void sendGameSettings(){
		List<String> settings = new ArrayList<String>();
		settings.add("settings timebank 10000");
		settings.add("settings time_per_move 500");
		settings.add("settings player_names player1,player2");
		settings.add("settings your_bot player1");
		settings.add("settings your_botid 1");
		//settings.add("exit");
		for (String setting : settings) {
			testInstanceParser.parseCommand(setting);
		}
	}
	
	/**
	 * Test field:
	 * 	0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
	 */
	@Test
	public void testFirstMove() {
		sendGameSettings();

		List<String> commands = new ArrayList<String>();
		commands.add("update game round 1");
		commands.add("update game move 1");
		commands.add("update game field 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
		commands.add("update game macroboard -1,-1,-1,-1,-1,-1,-1,-1,-1");
		commands.add("action move 10000");
		//commands.add("exit");
		List<String> results = new ArrayList<String>();
		for (String command : commands) {
			String result = testInstanceParser.parseCommand(command);
			results.add(result);
			if (result != null){
				System.out.println(result);
			}
		}
		((Field) testInstanceBot.getTree().getRoot().getGameState()).printBoard();
		
		
	}
	
	
	/**
	 * Test field:
	 * 	1|1|1|1|1|1|2|2|2
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		2|2|2|1|1|1|2|2|2
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		1|1|1|0|1|0|2|0|0
		0|0|0|0|1|0|0|2|1
		0|0|0|0|0|0|0|0|0
		
	 * field to play in: (1/2)
	 */
	@Test
	public void testFinishMove() {
		sendGameSettings();

		List<String> commands = new ArrayList<String>();
		commands.add("update game round 14");
		commands.add("update game move 27");
		commands.add("update game field 1,1,1,1,1,1,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,1,1,1,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,0,2,0,0,0,0,0,0,1,0,0,2,1,0,0,0,0,0,0,0,0,0");
		commands.add("update game macroboard 1,1,2,2,1,2,1,-1,0");
		commands.add("action move 10000");
		//commands.add("exit");
		List<String> results = new ArrayList<String>();
		for (String command : commands) {
			String result = testInstanceParser.parseCommand(command);
			results.add(result);
			if (result != null){
				System.out.println(result);
			}
		}
		((Field) testInstanceBot.getTree().getRoot().getGameState()).printBoard();
		
	}
	
	/**
	 * Test field:
	 * 	1|1|1|1|1|1|2|2|2
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		2|2|2|1|1|1|2|2|2
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		1|1|1|1|1|2|1|1|2
		0|0|0|2|2|1|1|2|1
		0|0|0|1|0|0|0|2|1
		
	 * field to play in: (1/2)
	 */
	@Test
	public void testFinishSituation1() {
		sendGameSettings();

		List<String> commands = new ArrayList<String>();
		commands.add("update game round 14");
		commands.add("update game move 27");
		commands.add("update game field 1,1,1,1,1,1,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,1,1,1,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,2,1,1,2,0,0,0,2,2,1,1,2,1,0,0,0,1,0,0,0,2,1");
		commands.add("update game macroboard 1,1,2,2,1,2,1,-1,0");
		commands.add("action move 10000");
		//commands.add("exit");
		List<String> results = new ArrayList<String>();
		for (String command : commands) {
			String result = testInstanceParser.parseCommand(command);
			results.add(result);
			if (result != null){
				System.out.println(result);
			}
		}
		((Field) testInstanceBot.getTree().getRoot().getGameState()).printBoard();
		testInstanceBot.getTree().print();
	}
	
	/**
	 * Test field:
	 * 	1|1|1|1|1|1|2|2|2
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		2|2|2|1|1|1|2|2|2
		0|0|0|0|0|0|0|0|0
		0|0|0|0|0|0|0|0|0
		1|1|1|1|1|2|1|2|2
		0|0|0|2|2|1|2|2|1
		0|0|0|1|0|0|1|0|0
		
	 * field to play in: (2/2)
	 */
	@Test
	public void testFinishSituation2() {
		sendGameSettings();

		List<String> commands = new ArrayList<String>();
		commands.add("update game round 14");
		commands.add("update game move 27");
		commands.add("update game field 1,1,1,1,1,1,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,1,1,1,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,2,1,2,2,0,0,0,2,2,1,2,2,1,0,0,0,1,0,0,1,0,0");
		commands.add("update game macroboard 1,1,2,2,1,2,1,0,-1");
		commands.add("action move 10000");
		//commands.add("exit");
		List<String> results = new ArrayList<String>();
		for (String command : commands) {
			String result = testInstanceParser.parseCommand(command);
			results.add(result);
			if (result != null){
				System.out.println(result);
			}
		}
		((Field) testInstanceBot.getTree().getRoot().getGameState()).printBoard();
		testInstanceBot.getTree().print();
	}
}
