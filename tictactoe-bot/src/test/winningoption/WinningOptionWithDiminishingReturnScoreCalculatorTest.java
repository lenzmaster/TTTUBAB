package test.winningoption;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import bot.Field;
import bot.Move;
import bot.Player;
import bot.Player.PlayerTypes;
import bot.winningoption.WinningOptionWithDiminishingReturnsScoreCalculator;

public class WinningOptionWithDiminishingReturnScoreCalculatorTest {

	
	private WinningOptionWithDiminishingReturnsScoreCalculator testInstance = null;
	
	@Before
	public void setUp(){
		//TODO: Instantiate with parameters from global definitons
		testInstance = new WinningOptionWithDiminishingReturnsScoreCalculator(3, 0.4f, 0.01f, 0, 1, 0.5f, -1);
		Player.initalizePlayers(1, 2, 0);
	}
	
	@Test
	public void testMaxScoreBoard(){
		Field field = new Field();
		String board = "0,1,1,1,1,1,1,1,1,0,1,1,2,2,0,2,2,0,0,0,0,2,2,0,2,2,0,0,1,1,1,1,1,1,1,1,0,1,1,2,2,0,2,2,0,0,0,0,2,2,0,2,2,0,0,1,1,0,1,1,0,1,1,0,1,1,0,1,1,0,1,0,0,0,0,0,0,0,0,0,0";
		field.parseGameData("field", board);
		field.printBoard();
		float[] score = testInstance.calculate(field, new Move[]{new Move(8,7,Player.getPlayer(PlayerTypes.Self))});
		System.out.println("Score: " + score[0]);
	}
	
	@Test
	public void testMaxScoreBoardWithFinishingMove(){
		Field field = new Field();
		String board = "0,1,1,1,1,1,1,1,1,0,1,1,2,2,0,2,2,0,0,0,0,2,2,0,2,2,0,0,1,1,1,1,1,1,1,1,0,1,1,2,2,0,2,2,0,0,0,0,2,2,0,2,2,0,0,1,1,0,1,1,0,1,1,0,1,1,0,1,1,0,1,1,0,0,0,0,0,0,0,0,0";
		field.parseGameData("field", board);
		field.printBoard();
		float[] score = testInstance.calculate(field, new Move[]{new Move(8,8,Player.getPlayer(PlayerTypes.Self))});
		System.out.println("Score: " + score[0]);
	}
	
	@Test
	public void IGameState(){
		Field field = new Field();
		String board = "0,2,2,2,2,2,2,2,2,0,2,2,1,1,0,1,1,0,0,0,0,1,1,0,1,1,0,0,2,2,2,2,2,2,2,2,0,2,2,1,1,0,1,1,0,0,0,0,1,1,0,1,1,0,0,2,2,0,2,2,0,2,2,0,2,2,0,2,2,0,2,0,0,0,0,0,0,0,0,0,0";
		field.parseGameData("field", board);
		field.printBoard();
		float[] score = testInstance.calculate(field, new Move[]{new Move(8,7,Player.getPlayer(PlayerTypes.Opponent))});
		System.out.println("Score: " + score[0]);
	}
	
	@Test
	public void testMaxScoreBoardWithFinishingMoveOpponent(){
		Field field = new Field();
		String board = "0,2,2,2,2,2,2,2,2,0,2,2,1,1,0,1,1,0,0,0,0,1,1,0,1,1,0,0,2,2,2,2,2,2,2,2,0,2,2,1,1,0,1,1,0,0,0,0,1,1,0,1,1,0,0,2,2,0,2,2,0,2,2,0,2,2,0,2,2,0,2,2,0,0,0,0,0,0,0,0,0";
		field.parseGameData("field", board);
		field.printBoard();
		float[] score = testInstance.calculate(field, new Move[]{new Move(8,8,Player.getPlayer(PlayerTypes.Opponent))});
		System.out.println("Score: " + score[0]);
	}
}
