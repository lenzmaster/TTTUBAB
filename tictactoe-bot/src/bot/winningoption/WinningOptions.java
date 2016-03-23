package bot.winningoption;

import bot.Point;

public enum WinningOptions {

	Horizontal0(new Point(0,0), new Point(1,0), new Point(2,0)),
	Horizontal1(new Point(0,1), new Point(1,1), new Point(2,1)),
	Horizontal2(new Point(0,2), new Point(1,2), new Point(2,2)),
	Vertical0(new Point(0,0), new Point(0,1), new Point(0,2)),
	Vertical1(new Point(1,0), new Point(1,1), new Point(1,2)),
	Vertical2(new Point(2,0), new Point(2,1), new Point(2,2)),
	Diagonal0(new Point(0,0), new Point(1,1), new Point(2,2)),
	Diagonal1(new Point(2,0), new Point(1,1), new Point(0,2));
	
	
	private Point[] tiles = new Point[3];
	
	private WinningOptions(Point tile1, Point tile2, Point tile3){
		this.tiles[0] = tile1;
		this.tiles[1] = tile2;
		this.tiles[2] = tile3;
	}
	
	public Point getTile1(){
		return this.tiles[0];
	}
	
	public Point getTile2(){
		return this.tiles[1];
	}
	
	public Point getTile3(){
		return this.tiles[2];
	}
	
	public Point[] getTiles(){
		return this.tiles;
	}
}
