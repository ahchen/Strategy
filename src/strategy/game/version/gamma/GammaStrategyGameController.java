/**
 * 
 */
package strategy.game.version.gamma;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import strategy.common.PlayerColor;
import strategy.common.StrategyException;
import strategy.game.StrategyGameController;
import strategy.game.common.Coordinate;
import strategy.game.common.Location;
import strategy.game.common.Location2D;
import strategy.game.common.MoveResult;
import strategy.game.common.Piece;
import strategy.game.common.PieceLocationDescriptor;
import strategy.game.common.PieceType;
import strategy.game.version.StrategyGameControllerImpl;

/**
 * @author Alex C
 *
 */
public class GammaStrategyGameController extends StrategyGameControllerImpl {

	private boolean gameStarted;
	private boolean gameOver;	
	private PlayerColor lastPlayerColor;
	Collection<PieceLocationDescriptor> redSetup, blueSetup;
	private Map<Location, Piece> board;
	
	private static final int NUM_PIECES = 12;
	private static final Location[] CHOKE_POINT_LOCATIONS = 
		{ new Location2D(2,2), new Location2D(2,3), new Location2D(3,2), new Location2D(3,3) };
	// based on simple formula for translating locations( (0,0) = 1, (1,1) = 2 ...  (4,5) = 35,  (5,5) = 36)
	// when pieces are located at their correct locations, their location total should equal these numbers
	// this can be guaranteed because each collection has exactly 12 pieces (check made before function gets called)
	private static final int RED_SPACE_TOTAL = 78;
	private static final int BLUE_SPACE_TOTAL = 366;
	
	public GammaStrategyGameController(Collection<PieceLocationDescriptor> redPieces, Collection<PieceLocationDescriptor> bluePieces) throws StrategyException {
		super(redPieces, bluePieces);
	}

	@Override
	public void startGame() throws StrategyException {
		if(gameStarted) {
			throw new StrategyException("Game Already In Progress");
		}
		gameStarted = true;
		gameOver = false;
		lastPlayerColor = null;		
	}

	@Override
	public MoveResult move(PieceType piece, Location from, Location to)
			throws StrategyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Piece getPieceAt(Location location) {
		return board.get(location);
	}
	
	/**
	 * Validates a collection of pieces for valid PieceType and Location
	 * Throws StrategyException if the piece collection is invalid
	 * @param playerPieces the collection of pieces to be verified
	 * @throws StrategyException
	 */
	protected void validatePiecesAndLocations(Collection<PieceLocationDescriptor> playerPieces) throws StrategyException 
	{
		if (playerPieces == null) {
			throw new StrategyException("Given Null Configurations");
		}
		
		if (playerPieces.size() != NUM_PIECES) {
			throw new StrategyException("Invalid Number of Pieces");
		}
		
		// number of types of pieces each player is allowed to have
		int numFlags = 1;
		int numMarshal = 1;
		int numColonel = 2;
		int numCaptain = 2;
		int numLieutenant = 3;
		int numSergeant = 3;
		
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
			thisPieceLocation = thisPiece.getLocation().getCoordinate(Coordinate.X_COORDINATE) + (thisPiece.getLocation().getCoordinate(Coordinate.Y_COORDINATE) * 6) + 1;
			
			spaceTotal -= thisPieceLocation;
			
			// subtract piece from count of pieces
			switch(thisPiece.getPiece().getType()) 
			{
				case FLAG:
					numFlags--;
					break;
				case MARSHAL:
					numMarshal--;
					break;
				case COLONEL:
					numColonel--;
					break;
				case CAPTAIN:
					numCaptain--;
					break;
				case LIEUTENANT:
					numLieutenant--;
					break;
				case SERGEANT:
					numSergeant--;
					break;
				default:
					throw new StrategyException("Invalid Piece");	
			}
		}
		
		// if any of the counts are not 0, there was an invalid combination of pieces
		if (numFlags != 0 || numMarshal != 0 || numColonel != 0 || numCaptain != 0 
				|| numLieutenant != 0 || numSergeant != 0) 
		{
			throw new StrategyException("Invalid Combination of Pieces"); 
		}
		
		// if the space total is not exactly 0, there was an invalid placement
		if (spaceTotal != 0)
		{
			throw new StrategyException("Invalid Placement of Pieces");
		}
	}
	
	protected void setVariables(Collection<PieceLocationDescriptor> redPieces, Collection<PieceLocationDescriptor> bluePieces) 
	{
		gameStarted = false;
		gameOver = false;	
		lastPlayerColor = null;
		redSetup = redPieces;
		blueSetup = bluePieces;
		board = new HashMap<Location,Piece>();
	}
	
	protected void initializeBoard() 
	{
		final Iterator<PieceLocationDescriptor> redIter, blueIter;
		redIter = redSetup.iterator();
		blueIter = blueSetup.iterator();
		
		// add all spaces to board
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
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
		
		// set choke pieces
		final Piece chokePoint = new Piece(PieceType.CHOKE_POINT, null);
		
		for (int i = 0; i < CHOKE_POINT_LOCATIONS.length; i++) {
			board.put(CHOKE_POINT_LOCATIONS[i], chokePoint);
		}
	}
	
	


}
