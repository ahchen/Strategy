/**
 * 
 */
package strategy.game.reporter;

import java.util.Collection;

import strategy.common.StrategyException;
import strategy.game.common.Location;
import strategy.game.common.MoveResult;
import strategy.game.common.PieceLocationDescriptor;
import strategy.game.common.PieceType;

/**
 * @author Alex C
 *
 */
public interface StrategyGameObserver
{ 
	// Called at the beginning of the game with the initial 
	// configurations. 
	void gameStart( 
			Collection<PieceLocationDescriptor> redConfiguration, 
			Collection<PieceLocationDescriptor> blueConfiguration); 

	// Called whenever a move is made by the game controller. If 
	// the controller caught an exception, it returns null for the 
	// result, but the exception in the fault; otherwise, fault 
	// is null. 
	void moveHappened(PieceType piece, Location from, Location to, 
			MoveResult result, StrategyException fault); 
}