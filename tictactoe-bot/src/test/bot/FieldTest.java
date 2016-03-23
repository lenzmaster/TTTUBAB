package test.bot;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bot.Field;
import bot.FieldCalculationHelper;
import bot.Move;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.Point;
import bot.memory.ObjectManager;

public class FieldTest {

	private Field testFieldInstance;
	
	private int[][] createNeutralMicrtoField(){
		int[][] result = new int[3][3];
		for (int i = 0; i < result.length; i++){
			for (int j = 0; j < result[i].length; j++){
				result[i][j] = 0;
			}
		}
		return result;
	}
	
	@BeforeClass
	public static void setUpGeneral(){
		ObjectManager.initalize();
	}
	
	@Before
	public void setUp(){
		testFieldInstance = new Field();		
		Player.initalizePlayers(1, 2, 0);
		testFieldInstance.setPlayerAtTurn(Player.getPlayer(PlayerTypes.Self));
	}
	
	@Test
	public void testWinnerInBoardHorizontal(){
		int[][] upperRow = createNeutralMicrtoField();
		upperRow[0][0] = 1;
		upperRow[0][1] = 1;
		upperRow[0][2] = 1;
		
		int[][] middleRow = createNeutralMicrtoField();
		middleRow[1][0] = 1;
		middleRow[1][1] = 1;
		middleRow[1][2] = 1;
		
		int[][] lowerRow = createNeutralMicrtoField();
		lowerRow[2][0] = 1;
		lowerRow[2][1] = 1;
		lowerRow[2][2] = 1;
		
		int result = testFieldInstance.calculateWinnerInBoard(upperRow);
		System.out.println("Horizontal upper row test - Winner id: " + result);
		assertTrue("Horizontal upper row test failed", result == 1);

		result = testFieldInstance.calculateWinnerInBoard(middleRow);
		System.out.println("Horizontal middle row test - Winner id: " + result);
		assertTrue("Horizontal middle row test failed", result == 1);

		result = testFieldInstance.calculateWinnerInBoard(lowerRow);
		System.out.println("Horizontal lower row test - Winner id: " + result);
		assertTrue("Horizontal lower row test failed", result == 1);

	}
	
	
	@Test
	public void testWinnerInBoardVertical(){
		int[][] leftRow = createNeutralMicrtoField();
		leftRow[0][0] = 1;
		leftRow[1][0] = 1;
		leftRow[2][0] = 1;
		
		int[][] middleRow = createNeutralMicrtoField();
		middleRow[0][1] = 1;
		middleRow[1][1] = 1;
		middleRow[2][1] = 1;
		
		int[][] rightRow = createNeutralMicrtoField();
		rightRow[0][2] = 1;
		rightRow[1][2] = 1;
		rightRow[2][2] = 1;
		
		int result = testFieldInstance.calculateWinnerInBoard(leftRow);
		System.out.println("Vertical left row test - Winner id: " + result);
		assertTrue("Vertical left row test failed", result == 1);
		
		result = testFieldInstance.calculateWinnerInBoard(middleRow);
		System.out.println("Vertical middle row test - Winner id: " + result);
		assertTrue("Vertical middle row test failed", result == 1);
		
		result = testFieldInstance.calculateWinnerInBoard(rightRow);
		System.out.println("Vertical right row test - Winner id: " + result);
		assertTrue("Vertical right row test failed", result == 1);
	}
	
	@Test
	public void testWinnerInBoardDiagonal(){
		int[][] leftTopStart = createNeutralMicrtoField();
		leftTopStart[0][0] = 1;
		leftTopStart[1][1] = 1;
		leftTopStart[2][2] = 1;
		
		int[][] leftBottomStart = createNeutralMicrtoField();
		leftBottomStart[0][2] = 1;
		leftBottomStart[1][1] = 1;
		leftBottomStart[2][0] = 1;

		
		int result = testFieldInstance.calculateWinnerInBoard(leftTopStart);
		System.out.println("Diagonal left top row test - Winner id: " + result);
		assertTrue("Diagonal left top row test failed", result == 1);
		
		result = testFieldInstance.calculateWinnerInBoard(leftBottomStart);
		System.out.println("Diagonal left bottom row test - Winner id: " + result);
		assertTrue("Diagonal left bottom row test failed", result == 1);

	}
	
	@Test
	public void testGetMacroIndex(){
		//test field (0/0)
		Move move = new Move(2, 2, Player.getPlayer(PlayerTypes.Self));
		Point result = FieldCalculationHelper.getMacroIndex(move.getX(), move.getY());
		assertTrue("test field (0/0) X coordinate is " + result.getX(), result.getX() == 0);
		assertTrue("test field (0/0) Y coordinate is " + result.getY(), result.getY() == 0);
		
		//test field (1/0)
		move = new Move(3, 2, Player.getPlayer(PlayerTypes.Self));
		result = FieldCalculationHelper.getMacroIndex(move.getX(), move.getY());
		assertTrue("test field (1/0) X coordinate is " + result.getX(), result.getX() == 1);
		assertTrue("test field (1/0) Y coordinate is " + result.getY(), result.getY() == 0);
		
		//test field (2/0)
		move = new Move(6, 2, Player.getPlayer(PlayerTypes.Self));
		result = FieldCalculationHelper.getMacroIndex(move.getX(), move.getY());
		assertTrue("test field (2/0) X coordinate is " + result.getX(), result.getX() == 2);
		assertTrue("test field (2/0) Y coordinate is " + result.getY(), result.getY() == 0);
				
		//test field (0/1)
		move = new Move(2, 3, Player.getPlayer(PlayerTypes.Self));
		result = FieldCalculationHelper.getMacroIndex(move.getX(), move.getY());
		assertTrue("test field (0/1) X coordinate is " + result.getX(), result.getX() == 0);
		assertTrue("test field (0/1) Y coordinate is " + result.getY(), result.getY() == 1);
		
		//test field (1/1)
		move = new Move(3, 3, Player.getPlayer(PlayerTypes.Self));
		result = FieldCalculationHelper.getMacroIndex(move.getX(), move.getY());
		assertTrue("test field (1/1) X coordinate is " + result.getX(), result.getX() == 1);
		assertTrue("test field (1/1) Y coordinate is " + result.getY(), result.getY() == 1);
		
		//test field (2/1)
		move = new Move(6, 3, Player.getPlayer(PlayerTypes.Self));
		result = FieldCalculationHelper.getMacroIndex(move.getX(), move.getY());
		assertTrue("test field (2/1) X coordinate is " + result.getX(), result.getX() == 2);
		assertTrue("test field (2/1) Y coordinate is " + result.getY(), result.getY() == 1);
		
		//test field (0/2)
		move = new Move(2, 6, Player.getPlayer(PlayerTypes.Self));
		result = FieldCalculationHelper.getMacroIndex(move.getX(), move.getY());
		assertTrue("test field (0/2) X coordinate is " + result.getX(), result.getX() == 0);
		assertTrue("test field (0/2) Y coordinate is " + result.getY(), result.getY() == 2);
		
		//test field (1/2)
		move = new Move(5, 6, Player.getPlayer(PlayerTypes.Self));
		result = FieldCalculationHelper.getMacroIndex(move.getX(), move.getY());
		assertTrue("test field (1/2) X coordinate is " + result.getX(), result.getX() == 1);
		assertTrue("test field (1/2) Y coordinate is " + result.getY(), result.getY() == 2);
		
		//test field (2/2)
		move = new Move(6, 6, Player.getPlayer(PlayerTypes.Self));
		result = FieldCalculationHelper.getMacroIndex(move.getX(), move.getY());
		assertTrue("test field (2/2) X coordinate is " + result.getX(), result.getX() == 2);
		assertTrue("test field (2/2) Y coordinate is " + result.getY(), result.getY() == 2);
		
	}
	
	@Test
	public void testCalculateMacroCoordinatesForNextMove(){
		//test next move in field (0/0) from field (0/0)
		Move move = new Move(0, 0, Player.getPlayer(PlayerTypes.Self));
		Point result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (0/0) from field (0/0) - X coordinate is " + result.getX(), result.getX() == 0);
		assertTrue("test next move in field (0/0) from field (0/0) - Y coordinate is " + result.getY(), result.getY() == 0);
		
		//test next move in field (0/0) from field (1/1)
		move = new Move(3, 3, Player.getPlayer(PlayerTypes.Self));
		result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (0/0) from field (1/1) - X coordinate is " + result.getX(), result.getX() == 0);
		assertTrue("test next move in field (0/0) from field (1/1) - Y coordinate is " + result.getY(), result.getY() == 0);
		
		//test next move in field (0/0) from field (2/2)
		move = new Move(6, 6, Player.getPlayer(PlayerTypes.Self));
		result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (0/0) from field (2/2) - X coordinate is " + result.getX(), result.getX() == 0);
		assertTrue("test next move in field (0/0) from field (2/2) - Y coordinate is " + result.getY(), result.getY() == 0);
		
		//test next move in field (1/0) from field (1/1)
		move = new Move(4, 3, Player.getPlayer(PlayerTypes.Self));
		result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (1/0) from field (1/1) - X coordinate is " + result.getX(), result.getX() == 1);
		assertTrue("test next move in field (1/0) from field (1/1) - Y coordinate is " + result.getY(), result.getY() == 0);
		
		//test next move in field (2/0) from field (1/1)
		move = new Move(5, 3, Player.getPlayer(PlayerTypes.Self));
		result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (2/0) from field (1/1) - X coordinate is " + result.getX(), result.getX() == 2);
		assertTrue("test next move in field (2/0) from field (1/1) - Y coordinate is " + result.getY(), result.getY() == 0);
		
		//test next move in field (0/1) from field (1/1)
		move = new Move(3, 4, Player.getPlayer(PlayerTypes.Self));
		result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (0/1) from field (1/1) - X coordinate is " + result.getX(), result.getX() == 0);
		assertTrue("test next move in field (0/1) from field (1/1) - Y coordinate is " + result.getY(), result.getY() == 1);
		
		//test next move in field (1/1) from field (1/1)
		move = new Move(4, 4, Player.getPlayer(PlayerTypes.Self));
		result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (1/1) from field (1/1) - X coordinate is " + result.getX(), result.getX() == 1);
		assertTrue("test next move in field (1/1) from field (1/1) - Y coordinate is " + result.getY(), result.getY() == 1);
		
		//test next move in field (2/1) from field (1/1)
		move = new Move(5, 4, Player.getPlayer(PlayerTypes.Self));
		result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (2/1) from field (1/1) - X coordinate is " + result.getX(), result.getX() == 2);
		assertTrue("test next move in field (2/1) from field (1/1) - Y coordinate is " + result.getY(), result.getY() == 1);
		
		//test next move in field (0/2) from field (1/1)
		move = new Move(3, 5, Player.getPlayer(PlayerTypes.Self));
		result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (0/2) from field (1/1) - X coordinate is " + result.getX(), result.getX() == 0);
		assertTrue("test next move in field (0/2) from field (1/1) - Y coordinate is " + result.getY(), result.getY() == 2);
		
		//test next move in field (1/2) from field (1/1)
		move = new Move(4, 5, Player.getPlayer(PlayerTypes.Self));
		result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (1/2) from field (1/1) - X coordinate is " + result.getX(), result.getX() == 1);
		assertTrue("test next move in field (1/2) from field (1/1) - Y coordinate is " + result.getY(), result.getY() == 2);
		
		//test next move in field (2/2) from field (1/1)
		move = new Move(5, 5, Player.getPlayer(PlayerTypes.Self));
		result = testFieldInstance.calculateMacroCoordinatesForNextMove(move);
		assertTrue("test next move in field (2/2) from field (1/1) - X coordinate is " + result.getX(), result.getX() == 2);
		assertTrue("test next move in field (2/2) from field (1/1) - Y coordinate is " + result.getY(), result.getY() == 2);
	}
	
}
