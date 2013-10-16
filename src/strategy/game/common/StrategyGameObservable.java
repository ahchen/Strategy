/**
 * 
 */
package strategy.game.common;


/**
 * @author Alex C
 * @version October 15, 2013
 */
public interface StrategyGameObservable 
{ 
	/**
	 * Adds the specified observer from the list of observers for the observable
	 * @param observer the observer to be added
	 */
	void register(StrategyGameObserver observer); 

	/**
	 * Removes the specified observer from the list of observers for the observable
	 * @param observer the observer to be removed
	 */
	void unregister(StrategyGameObserver observer); 
} 
