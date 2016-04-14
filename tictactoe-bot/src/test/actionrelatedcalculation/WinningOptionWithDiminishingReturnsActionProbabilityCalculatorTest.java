package test.actionrelatedcalculation;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bot.Field;
import bot.Move;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.actionrelatedcalculation.IActionCalculator;
import bot.actionrelatedcalculation.WinningOptionWithDiminishingReturnsActionProbabilityCalculator;
import bot.mcst.IAction;
import bot.mcst.MCSTNode;
import bot.time.FieldDensityTimeCalculator;
import bot.time.ITimeCalculator;
import bot.util.GlobalDefinitions;

public class WinningOptionWithDiminishingReturnsActionProbabilityCalculatorTest {
	
	private Field field;
	private IActionCalculator testInstance;
	
	@Before
	public void setUp(){
		field = new Field();
		testInstance = new WinningOptionWithDiminishingReturnsActionProbabilityCalculator
				(3, 0.4f, 0.01f, 0, 1, 0.5f, -1);
	}
	
	@After
	public void cleanUp(){
		
	}
	
	/**
	 * Test field:
	 * 	0|1|0||0|2|0||0|0|0
		2|0|0||1|0|1||1|0|0
		0|1|0||1|1|0||2|2|0
		-------------------
		2|0|0||0|0|0||0|2|1
		2|0|1||0|2|0||0|0|2
		0|0|1||0|2|1||0|0|0
		-------------------
		1|0|2||0|2|1||2|2|0
		0|0|0||0|1|0||0|0|0
		0|0|0||0|2|0||0|0|0
	 */
	@Test
	public void testActionScoresSituationTrump() {
		Player.initalizePlayers(2, 1, GlobalDefinitions.PLAYER_NEUTRAL_ID);
		field.setPlayerAtTurn(Player.getPlayer(PlayerTypes.Self));
		field.parseGameData("round", "15");
		field.parseGameData("move", "30");
		field.parseGameData("field", "0,2,0,0,1,0,0,0,0,1,0,0,2,0,2,2,0,0,0,2,0,2,2,0,1,1,0,1,0,0,0,0,0,0,1,2,1,0,2,0,1,0,0,0,1,0,0,2,0,1,2,0,0,0,2,0,1,0,1,2,1,1,0,0,0,0,0,2,0,0,0,0,0,0,0,0,1,0,0,0,0");
		field.parseGameData("macroboard", "0,0,0,0,0,0,0,-1,0");
		
		List<Move> moveList = field.getAvailableMoves();
		IAction[] actions = new IAction[moveList.size()];
		moveList.toArray(actions);
		float[] result = testInstance.calculate(field, actions);
		
		field.printBoard();
		System.out.println("");
		System.out.println("Scores:");
		int counter = 0;
		for (Move move : moveList) {
			System.out.println("(" + move.getX() + "/" + move.getY()+ "):" + result[counter++]);
		}
		
	}
}
