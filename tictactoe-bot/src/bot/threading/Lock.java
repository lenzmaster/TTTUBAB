package bot.threading;

public class Lock{

	  private boolean isLocked = false;

	  public synchronized void lock() throws InterruptedException{
	    while(isLocked){
	      wait();
	    }
	    isLocked = true;
	  }

	  public synchronized void unlock(){
	    isLocked = false;
	    notify();
	  }
	  
	  protected void checkLock(){
		  if(!isLocked){
			  throw new RuntimeException("Lock not aquirred!");
		  }
	  }
	}
