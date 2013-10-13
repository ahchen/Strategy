/**
 * 
 */
package strategy.game.version.epsilon;

import java.util.Collection;
import java.util.Map;

import strategy.common.StrategyException;
import strategy.game.StrategyGameController;
import strategy.game.common.Location;
import strategy.game.common.Piece;
import strategy.game.common.PieceLocationDescriptor;

/**
 * @author Alex C
 *
 */
public class MockEpsilonStrategyGameController extends
		EpsilonStrategyGameController implements StrategyGameController {

	public MockEpsilonStrategyGameController(
			Collection<PieceLocationDescriptor> redPieces,
			Collection<PieceLocationDescriptor> bluePieces,
			Map<Location, Piece> board,
			int numRedMovable, int numBlueMovable) 
			throws StrategyException {
		super(redPieces, bluePieces);
		
		this.board = board;
		numBlueMovablePieces = numBlueMovable;
		numRedMovablePieces = numRedMovable;
	}

}
