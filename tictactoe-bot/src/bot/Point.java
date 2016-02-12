package bot;

import bot.memory.IReusable;

public class Point implements IReusable{

	private int x;
	private int y;
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Point(){
		
	}

	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void initalize(int x, int y){
		reset();
		this.x = x;
		this.y = y;
		
	}
	
	public void reset(){
		x = 0;
		y = 0;
	}
	
	
}
