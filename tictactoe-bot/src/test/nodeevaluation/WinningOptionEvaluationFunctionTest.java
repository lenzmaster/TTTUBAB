package test.nodeevaluation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bot.Field;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.memory.ObjectManager;
import bot.nodeevaluation.WinningOptionEvaluationFunction;
import bot.util.GlobalDefinitions;

public class WinningOptionEvaluationFunctionTest {

	private Field testFieldInstance;
	private WinningOptionEvaluationFunction testEvaluationFunctionInstance;
	
	@BeforeClass
	public static void setUpGeneral(){
		ObjectManager.initalize();
	}
	
	@Before
	public void setUp(){
		testFieldInstance = new Field();		
		Player.initalizePlayers(1, 2, 0);
		testFieldInstance.setPlayerAtTurn(Player.getPlayer(PlayerTypes.Self));
		testEvaluationFunctionInstance = new WinningOptionEvaluationFunction();
		testEvaluationFunctionInstance.setGameState(testFieldInstance);
	}
	
	
	@Test
	public void testFirstMove() {
		//Set up field
		testFieldInstance.parseGameData("field", "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
		float nodeEvaluationValue = testEvaluationFunctionInstance.calculate();
		assertTrue("Node evaluation value was not equal to the defined neutral value. Result: " + nodeEvaluationValue,
				nodeEvaluationValue == GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE);
	}

	@Test
	public void testFinishMove() {
		//Set up field
		testFieldInstance.parseGameData("field", "1,1,1,1,1,1,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,1,1,1,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1,0,2,0,0,0,0,0,0,1,0,0,2,1,0,0,0,0,0,0,0,0,0");
		float nodeEvaluationValue = testEvaluationFunctionInstance.calculate();
		assertTrue("Node evaluation value was not equal to the defined neutral value. Result: " + nodeEvaluationValue,
				nodeEvaluationValue == GlobalDefinitions.NODE_EVALUATION_NEUTRAL_VALUE);
	}
	
}
