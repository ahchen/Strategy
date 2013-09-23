/**
 * 
 */
package strategy.game.version;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import strategy.common.PlayerColor;
import strategy.common.StrategyException;
import strategy.common.StrategyRuntimeException;
import strategy.game.StrategyGameController;
import strategy.game.common.Location;
import strategy.game.common.Location2D;
import strategy.game.common.Piece;
import strategy.game.common.PieceLocationDescriptor;
import strategy.game.common.PieceType;


/**
 * @author Alex C
 * @version September 14, 2013
 */
public abstract class StrategyGameControllerImpl implements StrategyGameController {
	
	protected boolean gameStarted;
	protected boolean gameOver;
	protected PlayerColor lastPlayerColor;
	protected Collection<PieceLocationDescriptor> redSetup, blueSetup;
	protected Map<Location, Piece> board;
	protected PieceLocationDescriptor lastRedPieceLocation, lastBluePieceLocation;
	protected boolean redRepetitionFlag, blueRepetitionFlag;
	protected int numRedMovablePieces, numBlueMovablePieces;
	
	protected static int NUM_PIECES = 0;
	protected static final Piece CHOKE_POINT = new Piece(PieceType.CHOKE_POINT, null);
	protected static Location[] CHOKE_POINT_LOCATIONS = null;
	protected static int BOARD_WIDTH = 0;
	protected static int BOARD_HEIGHT = 0;
	// based on simple formula for translating locations( (0,0) = 1, (1,1) = 2 ...  (4,5) = 35,  (5,5) = 36)
	// when pieces are located at their correct locations, their location total should equal these numbers
	protected static int RED_SPACE_TOTAL = 0;
	protected static int BLUE_SPACE_TOTAL = 0;

	/**
	 * constructor for creating a strategy game
	 * @param redPieces collection of red pieces and locations
	 * @param bluePieces collection of blue pieces and locations
	 * @throws StrategyException
	 */
	protected StrategyGameControllerImpl(Collection<PieceLocationDescriptor> redPieces, 
			Collection<PieceLocationDescriptor> bluePieces) throws StrategyException {
		setVariables(redPieces, bluePieces);
		validatePiecesAndLocations(redPieces);
		validatePiecesAndLocations(bluePieces);
		initializeBoard();
	}
	
	protected abstract void setVariables(Collection<PieceLocationDescriptor> redPieces, Collection<PieceLocationDescriptor> bluePieces);
	
	protected abstract void validatePiecesAndLocations(Collection<PieceLocationDescriptor> pieces) throws StrategyException;
	
	/*
	 * @see strategy.game.StrategyGameController#startGame()
	 */
	public void startGame() throws StrategyException {
		if(gameStarted) {
			throw new StrategyException("Game Already In Progress, Make a New Game");
		}
		gameStarted = true;
		gameOver = false;
		lastPlayerColor = null;
	}
	
	/**
	 * Initializes the board to contain all the spaces of the board
	 * Adds the validated red and blue pieces to the board
	 */
	protected void initializeBoard() 
	{
		final Iterator<PieceLocationDescriptor> redIter, blueIter;
		redIter = redSetup.iterator();
		blueIter = blueSetup.iterator();
		
		// add all spaces to board
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				board.put(new Location2D(j, i), null);
			}
		}
				
		PieceLocationDescriptor singleRedPiece, singleBluePiece;
		
		while (redIter.hasNext()) {
			singleRedPiece = redIter.next();
			singleBluePiece = blueIter.next();
					
			// fill board with pieces
			board.put(singleRedPiece.getLocation(), singleRedPiece.getPiece());
			board.put(singleBluePiece.getLocation(), singleBluePiece.getPiece());
		}
	}
	
	/**
	 * Determines if moving to the given to location is valid from the given from location
	 * @param from base location
	 * @param to location to go
	 * @throws StrategyException
	 */
	protected void checkLocations(Location from, Location to) throws StrategyException {
		try {
			if (from.distanceTo(to) > 1) {
				throw new StrategyException("Locations are too far apart");
			}
		}
		catch (StrategyRuntimeException e) {
			throw new StrategyException(e.getMessage());
		}
	}
	
	/*
	 * @see strategy.game.StrategyGameController#getPieceAt()
	 */
	public Piece getPieceAt(Location location) {
		return board.get(location);
	}

}
