package test.time;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bot.BotParser;
import bot.BotStarter;
import bot.Field;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.mcst.MCSTNode;
import bot.time.FieldDensityTimeCalculator;
import bot.time.ITimeCalculator;
import bot.util.GlobalDefinitions;

public class FieldDensityTimeCalculatorTest {

	private Field field;
	private MCSTNode node;
	private ITimeCalculator testInstance;
	
	@Before
	public void setUp(){
		field = new Field();
		node = new MCSTNode();
		testInstance = new FieldDensityTimeCalculator();
		Player.initalizePlayers(1, 2, GlobalDefinitions.PLAYER_NEUTRAL_ID);
		field.setPlayerAtTurn(Player.getPlayer(PlayerTypes.Self));
	}
	
	@After
	public void cleanUp(){
		
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
	public void testTimeConsumptionInStartSituation() {
		field.parseGameData("round", "1");
		field.parseGameData("move", "1");
		field.parseGameData("field", "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
		field.parseGameData("macroboard", "-1,-1,-1,-1,-1,-1,-1,-1,-1");
		node.visitNode(field);
		
		testInstance.calculateTimeForTurn(node, 1000l * GlobalDefinitions.TIME_MS_TO_NS_FACTOR);
		
	}
	
	/**
	 * Test field:
	 * 	0|2|0|1|2|0|0|0|2
		0|0|0|2|2|1|1|1|1
		2|2|2|0|1|1|2|0|0
		0|0|0|1|2|2|1|1|2
		0|0|0|2|1|1|0|2|2
		1|1|1|2|1|2|2|2|0
		0|0|1|0|1|0|1|2|1
		0|0|1|0|1|2|2|2|1
		0|2|1|0|1|0|0|0|2
		
	 * fields to play in: (1/0), (2/2)
	 */
	@Test
	public void testTimeConsumptionInNearEndSituation() {
		field.parseGameData("round", "26");
		field.parseGameData("move", "51");
		field.parseGameData("field", "0,2,0,1,2,0,0,0,2,0,0,0,2,2,1,1,1,1,2,2,2,0,1,1,2,0,0,0,0,0,1,2,2,1,1,2,0,0,0,2,1,1,0,2,2,1,1,1,2,1,2,2,2,0,0,0,1,0,1,0,1,2,1,0,0,1,0,1,2,2,2,1,0,2,1,0,1,0,0,0,2");
		field.parseGameData("macroboard", "2,-1,1,1,0,2,1,1,-1");
		node.visitNode(field);
		
		testInstance.calculateTimeForTurn(node, 10000l * GlobalDefinitions.TIME_MS_TO_NS_FACTOR);
		
	}
	
	
	
}
