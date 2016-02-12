package bot.util;

public class Logger {

	private String name;
	
	
	public Logger(String name){
		this.name = name;
	}
	
	
	public void log(String message){
		System.err.println(name +": " +message);
	}
	
	
}
