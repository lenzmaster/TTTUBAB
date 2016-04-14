package bot.threading;

import bot.mcst.MCSTTree;

public class TreeVisitorThread extends Thread{

	MCSTTree tree;
	
	public TreeVisitorThread(MCSTTree tree){
		this.tree = tree;
	}
	
	public void run(){
		while(true){
			try {
				tree.lock();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tree.performIteration();
			tree.unlock();
		}
	}
	
}
