/**
 * 
 */
package strategy.game.version.delta;

import java.util.Collection;
import java.util.Map;

import strategy.common.StrategyException;
import strategy.game.common.Location;
import strategy.game.common.Piece;
import strategy.game.common.PieceLocationDescriptor;


/**
 * @author Alex C
 *
 */
public class MockDeltaStrategyGameController extends DeltaStrategyGameController {

	/**
	 * Public Constructor for MockDeltaStrategyGameController.
	 * Creates a DeltaStrategyGameController and then sets the board and 
	 * additional variables that are given to make it easier for testing 
	 * 
	 * @param redPieces collection of red pieces
	 * @param bluePieces collection of blue pieces
	 * @param board what you want to set the board to
	 * @param numRedMoveable number of remaining red movable pieces left
	 * @param numBlueMovable number of remaining blue movable pieces left
	 * @throws StrategyException thrown if collections are deemed invalid per the normal constructor
	 */
	public MockDeltaStrategyGameController(Collection<PieceLocationDescriptor> redPieces,
			Collection<PieceLocationDescriptor> bluePieces, Map<Location, Piece> board,
			int numRedMoveable, int numBlueMovable) throws StrategyException {
		super(redPieces, bluePieces);
		
		this.board = board;
		this.numRedMovablePieces = numRedMoveable;
		this.numBlueMovablePieces = numBlueMovable;
	}
}
