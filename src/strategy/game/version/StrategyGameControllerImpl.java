/**
 * 
 */
package strategy.game.version;

import java.util.Collection;

import strategy.common.StrategyException;
import strategy.game.StrategyGameController;
import strategy.game.common.PieceLocationDescriptor;


/**
 * @author Alex C
 *
 */
public abstract class StrategyGameControllerImpl implements StrategyGameController {

	public StrategyGameControllerImpl(Collection<PieceLocationDescriptor> redPieces, Collection<PieceLocationDescriptor> bluePieces) throws StrategyException {
		validatePiecesAndLocations(redPieces);
		validatePiecesAndLocations(bluePieces);
		setVariables(redPieces, bluePieces);
		initializeBoard();
		
	}
	
	protected abstract void validatePiecesAndLocations(Collection<PieceLocationDescriptor> pieces) throws StrategyException;
	
	protected abstract void initializeBoard();
	
	protected abstract void setVariables(Collection<PieceLocationDescriptor> redPieces, Collection<PieceLocationDescriptor> bluePieces);

}
