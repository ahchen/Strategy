/**
 * 
 */
package strategy.game.version.epsilon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import strategy.common.StrategyException;
import strategy.game.StrategyGameController;
import strategy.game.common.Coordinate;
import strategy.game.common.Location;
import strategy.game.common.Location2D;
import strategy.game.common.Piece;
import strategy.game.common.PieceLocationDescriptor;
import strategy.game.common.PieceType;
import strategy.game.version.StrategyGameControllerImpl;

/**
 * @author Alex C
 *
 */
public class EpsilonStrategyGameController extends StrategyGameControllerImpl
		implements StrategyGameController {
	
	private int redFlagsCaptured, blueFlagsCaptured;

	public EpsilonStrategyGameController(
			Collection<PieceLocationDescriptor> redPieces,
			Collection<PieceLocationDescriptor> bluePieces)
			throws StrategyException {
		super(redPieces, bluePieces);
	}

	/* (non-Javadoc)
	 * @see strategy.game.version.StrategyGameControllerImpl#setVariables(java.util.Collection, java.util.Collection)
	 */
	@Override
	protected void setVariables(Collection<PieceLocationDescriptor> redPieces,
			Collection<PieceLocationDescriptor> bluePieces) {
		gameStarted = false;
		gameOver = false;
		lastPlayerColor = null;
		redSetup = redPieces;
		blueSetup = bluePieces;
		board = new HashMap<Location,Piece>();
		lastRedPieceLocation = null;
		lastBluePieceLocation = null;
		redRepetitionFlag = false;
		blueRepetitionFlag = false;
		numRedMovablePieces = 33;
		numBlueMovablePieces = 33;
		
		redFlagsCaptured = 0;
		blueFlagsCaptured = 0;
		
		final Location[] chokeLocs = { 
				new Location2D(2,4), 
				new Location2D(2,5), 
				new Location2D(3,4), 
				new Location2D(3,5),
				new Location2D(6,4), 
				new Location2D(6,5), 
				new Location2D(7,4), 
				new Location2D(7,5) };
		
		NUM_PIECES = 40;
		CHOKE_POINT_LOCATIONS = chokeLocs;
		BOARD_WIDTH = 10;
		BOARD_HEIGHT = 10;
		
		// based on simple formula for translating locations( (0,0) = 1, (1,1) = 2 ...  (4,5) = 35,  (5,5) = 36)
		// when pieces are located at their correct locations, their location total should equal these numbers
		// Sum from 1 to 40
		RED_SPACE_TOTAL = 820;
		// Sum from 61 to 100
		BLUE_SPACE_TOTAL = 3220;
	}

	/* (non-Javadoc)
	 * @see strategy.game.version.StrategyGameControllerImpl#validatePiecesAndLocations(java.util.Collection)
	 */
	@Override
	protected void validatePiecesAndLocations(
			Collection<PieceLocationDescriptor> playerPieces)
			throws StrategyException {
		
		if (playerPieces == null) {
			throw new StrategyException("Given Null Configurations");
		}
		
		if (playerPieces.size() != NUM_PIECES) {
			throw new StrategyException("Invalid Number of Pieces");
		}
		
		// number of types of pieces each player is allowed to have
		Map<PieceType, Integer> requiredPieces = new HashMap<PieceType, Integer>();
		requiredPieces.put(PieceType.MARSHAL, 1);
		requiredPieces.put(PieceType.GENERAL, 1);
		requiredPieces.put(PieceType.COLONEL, 2);
		requiredPieces.put(PieceType.MAJOR, 3);
		requiredPieces.put(PieceType.CAPTAIN, 4);
		requiredPieces.put(PieceType.FIRST_LIEUTENANT, 2);
		requiredPieces.put(PieceType.LIEUTENANT, 2);
		requiredPieces.put(PieceType.SERGEANT, 4);
		requiredPieces.put(PieceType.MINER, 5);
		requiredPieces.put(PieceType.SCOUT, 8);
		requiredPieces.put(PieceType.SPY, 1);
		requiredPieces.put(PieceType.BOMB, 6);
		requiredPieces.put(PieceType.FLAG, 2);
		
		PieceLocationDescriptor thisPiece;
		final PieceLocationDescriptor firstPiece;
		int thisPieceLocation;
		int spaceTotal = 0;
		
		final Iterator<PieceLocationDescriptor> pieceIter, firstPieceIter;
		pieceIter = playerPieces.iterator();
		firstPieceIter = playerPieces.iterator();
		
		// determine which color this collection is 
		firstPiece = firstPieceIter.next();
		// set the spaceTotal to the appropriate number
		switch(firstPiece.getPiece().getOwner()) 
		{
			case RED:
				spaceTotal = RED_SPACE_TOTAL;
				break;
			case BLUE:
				spaceTotal = BLUE_SPACE_TOTAL;
				break;
		}
		
		while (pieceIter.hasNext()) 
		{
			thisPiece = pieceIter.next();
			// hash location to simple number
			thisPieceLocation = thisPiece.getLocation().getCoordinate(Coordinate.X_COORDINATE) + 
					(thisPiece.getLocation().getCoordinate(Coordinate.Y_COORDINATE) * BOARD_WIDTH) + 1;
			
			spaceTotal -= thisPieceLocation;
			
			
			requiredPieces.put(thisPiece.getPiece().getType(), requiredPieces.get(thisPiece.getPiece().getType()) - 1);
			
		}
		
		final Iterator<Integer> reqPieceIter = requiredPieces.values().iterator();
		int pieceTotal = 0;
		
		while (reqPieceIter.hasNext()) 
		{
			pieceTotal += Math.abs(reqPieceIter.next());
		}
		
		// if any of the counts are not 0, there was an invalid combination of pieces
		if (pieceTotal != 1 || requiredPieces.get(PieceType.FLAG) != 0) {
			throw new StrategyException("Invalid Combination of Pieces"); 
		}
		
		// if the space total is not exactly 0, there was an invalid placement
		if (spaceTotal != 0)
		{
			throw new StrategyException("Invalid Placement of Pieces");
		}

	}
	
	/**
	 * Initializes the board to contain all the spaces of the board
	 * Adds the validated red and blue pieces to the board
	 * Additionally, adds choke points to the board
	 */
	@Override
	protected void initializeBoard() {
		super.initializeBoard();
		
		// set choke pieces
		for (int i = 0; i < CHOKE_POINT_LOCATIONS.length; i++) {
			board.put(CHOKE_POINT_LOCATIONS[i], CHOKE_POINT);
		}
	}

}
