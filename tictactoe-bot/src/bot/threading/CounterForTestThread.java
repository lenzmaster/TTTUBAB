package bot.threading;

public class CounterForTestThread extends Lock{

	private long counter = 0;
	
	public void resetCounter(){
		counter = 0;
	}
	
	public void increaseCounter(long addValue){
		counter += addValue;
	}
	
	public long getCounter(){
		return counter;
	}
	
}
