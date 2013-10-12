/**
 * 
 */
package strategy.game.reporter;

/**
 * @author Alex C
 *
 */
public interface StrategyGameObservable 
{ 
	// Registers an observer 
	void register(StrategyGameObserver observer); 

	// Removes an observer 
	void unregister(StrategyGameObserver observer); 
} 
