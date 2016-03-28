package bot;

public interface IFieldObserver {
	
	/**
	 * This method will be called by the field and is not allowed to be called otherwise.
	 * @param move
	 */
	public void moveExecuted(Field sender, Move move);
	
	/**
	 * Clones the observer object.
	 * @return
	 */
	public IFieldObserver clone();
}
