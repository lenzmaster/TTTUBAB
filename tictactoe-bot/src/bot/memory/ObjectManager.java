package bot.memory;

import java.util.ArrayList;

import bot.Field;
import bot.Move;
import bot.Point;
import bot.mcst.MCSTNode;
import bot.mcst.MCSTTree;
import bot.util.GlobalDefinitions;
import bot.util.Logger;

public class ObjectManager {
	
	private static Logger LOGGER = new Logger("ObjectManager");

	private static ArrayList<Field> managedFields = new ArrayList<Field>(GlobalDefinitions.MANAGED_FIELDS_INITAL_CAPACITY);
	private static ArrayList<Move> managedMoves = new ArrayList<Move>(GlobalDefinitions.MANAGED_MOVES_INITAL_CAPACITY);
	private static ArrayList<Point> managedPoints = new ArrayList<Point>(GlobalDefinitions.MANAGED_POINTS_INITAL_CAPACITY);
	private static ArrayList<MCSTNode> managedMCSTNodes = new ArrayList<MCSTNode>(GlobalDefinitions.MANAGED_MCSTNODES_INITAL_CAPACITY);
	private static ArrayList<MCSTTree> managedMCSTTrees = new ArrayList<MCSTTree>(GlobalDefinitions.MANAGED_MCSTTREES_INITAL_CAPACITY);
	
	private static int managedFieldsIndex = 0;
	private static int managedMovesIndex = 0;
	private static int managedPointsIndex = 0;
	private static int managedMCSTNodeIndex = 0;
	private static int managedMCSTTreeIndex = 0;
	
	private static boolean isInitalized = false;
	
	public static void initalize(){
		if(!GlobalDefinitions.USE_PRECREATED_OBJECTS){
			return;
		}
		if (!isInitalized){
			for (int i = 0; i < GlobalDefinitions.MANAGED_FIELDS_INITAL_CAPACITY; i++){
				managedFields.add(new Field());
			}
			for (int i = 0; i < GlobalDefinitions.MANAGED_MOVES_INITAL_CAPACITY; i++){
				managedMoves.add(new Move());
			}
			for (int i = 0; i < GlobalDefinitions.MANAGED_POINTS_INITAL_CAPACITY; i++){
				managedPoints.add(new Point());
			}
			for (int i = 0; i < GlobalDefinitions.MANAGED_MCSTNODES_INITAL_CAPACITY; i++){
				managedMCSTNodes.add(new MCSTNode());
			}
			for (int i = 0; i < GlobalDefinitions.MANAGED_MCSTTREES_INITAL_CAPACITY; i++){
				managedMCSTTrees.add(new MCSTTree());
			}
			isInitalized = true;
		}
	}
	
	public static void reset(){
		managedFieldsIndex = 0;
		managedMovesIndex = 0;
		managedPointsIndex = 0;
		managedMCSTNodeIndex = 0;
		managedMCSTTreeIndex = 0;
	}
	
	public static Field getNewField(){
		if(!GlobalDefinitions.USE_PRECREATED_OBJECTS){
			return new Field();
		}
		if (managedFieldsIndex >= GlobalDefinitions.MANAGED_FIELDS_INITAL_CAPACITY){
			LOGGER.log("Creating new Field object");
			managedFields.add(new Field());
		}
		return managedFields.get(managedFieldsIndex++);
	}
	
	public static Move getNewMove(){
		if(!GlobalDefinitions.USE_PRECREATED_OBJECTS){
			return new Move();
		}
		if (managedMovesIndex >= GlobalDefinitions.MANAGED_MOVES_INITAL_CAPACITY){
			LOGGER.log("Creating new Move object");
			managedMoves.add(new Move());
		}
		return managedMoves.get(managedMovesIndex++);
	}
	
	public static Point getNewPoint(){
		if(!GlobalDefinitions.USE_PRECREATED_OBJECTS){
			return new Point();
		}
		if (managedPointsIndex >= GlobalDefinitions.MANAGED_POINTS_INITAL_CAPACITY){
			LOGGER.log("Creating new Point object");
			managedPoints.add(new Point());
		}
		return managedPoints.get(managedPointsIndex++);
	}
	
	public static MCSTNode getNewMCSTNode(){
		if(!GlobalDefinitions.USE_PRECREATED_OBJECTS){
			return new MCSTNode();
		}
		if (managedMCSTNodeIndex >= GlobalDefinitions.MANAGED_MCSTNODES_INITAL_CAPACITY){
			LOGGER.log("Creating new MCSTNode object");
			managedMCSTNodes.add(new MCSTNode());
		}
		return managedMCSTNodes.get(managedMCSTNodeIndex++);
	}
	
	public static MCSTTree getNewMCSTTree(){
		if(!GlobalDefinitions.USE_PRECREATED_OBJECTS){
			return new MCSTTree();
		}
		if (managedMCSTTreeIndex >= GlobalDefinitions.MANAGED_MCSTTREES_INITAL_CAPACITY){
			LOGGER.log("Creating new MCSTTree object");
			managedMCSTTrees.add(new MCSTTree());
		}
		return managedMCSTTrees.get(managedMCSTTreeIndex++);
	}
	
	
}
