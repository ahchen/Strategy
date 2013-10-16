/**
 * 
 */
package strategy.game.common;

import java.util.Collection;

import strategy.common.StrategyException;

/**
 * @author Alex C
 * @version October 15, 2013
 */
public interface StrategyGameObserver
{ 

	/**
	 * Should be called when startGame is called from a StrategyGameController
	 * @param redConfiguration the initial red configuration of pieces
	 * @param blueConfiguration the initial blue configuration of pieces
	 */
	void gameStart( 
			Collection<PieceLocationDescriptor> redConfiguration, 
			Collection<PieceLocationDescriptor> blueConfiguration); 

 
	/**
	 * Should be called anytime the move method is called from a StrategyGameController
	 * @param piece the piece being moved
	 * @param from the from location
	 * @param to the to location
	 * @param result the MoveResult returned from the move method
	 * @param fault any exception that may have been thrown by the move method. (null if no exception was thrown).
	 */
	void moveHappened(PieceType piece, Location from, Location to, 
			MoveResult result, StrategyException fault); 
}