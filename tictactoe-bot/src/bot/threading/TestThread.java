package bot.threading;

public class TestThread extends Thread {

	private CounterForTestThread counter;
	private long elapsedTime = 0;
	private long iterationStartTime = 0;
	
	public TestThread(CounterForTestThread counter){
		this.counter = counter;
	}
	
	public void run(){
		while(true){
			iterationStartTime = System.nanoTime();
			try {
				counter.lock();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter.increaseCounter(elapsedTime);
			counter.unlock();
			elapsedTime = System.nanoTime() - iterationStartTime;
		}
	}
	
}
