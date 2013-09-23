/**
 * 
 */
package strategy.game.version.gamma;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import strategy.common.PlayerColor;
import strategy.common.StrategyException;
import strategy.game.common.Coordinate;
import strategy.game.common.Location;
import strategy.game.common.Location2D;
import strategy.game.common.MoveResult;
import strategy.game.common.MoveResultStatus;
import strategy.game.common.Piece;
import strategy.game.common.PieceLocationDescriptor;
import strategy.game.common.PieceType;
import strategy.game.version.StrategyGameControllerImpl;

/**
 * @author Alex C
 * @version September 24, 2013
 */
public class GammaStrategyGameController extends StrategyGameControllerImpl {
	
	public GammaStrategyGameController(Collection<PieceLocationDescriptor> redPieces, 
			Collection<PieceLocationDescriptor> bluePieces) throws StrategyException {
		super(redPieces, bluePieces);
	}

	/*
	 * @see strategy.game.StrategyGameController#move()
	 */
	@Override
	public MoveResult move(PieceType piece, Location from, Location to)
			throws StrategyException {
		if (gameOver) {
			throw new StrategyException("The game is over, you cannot make a move");
		}
		if (!gameStarted) {
			throw new StrategyException("You must start the game!");
		}
		if (piece == PieceType.FLAG) {
			throw new StrategyException("You cannot move the flag");
		}
		if (piece == PieceType.CHOKE_POINT) {
			throw new StrategyException("You cannot move the choke point");
		}
		if (!board.containsKey(from) || !board.containsKey(to)) {
			throw new StrategyException("Coordinates not on board");
		}
		if (getPieceAt(from) == null || getPieceAt(from).getType() != piece) {
			throw new StrategyException("Specified piece is not located at given location");
		}
		
		final Piece fromPiece, toPiece;
		fromPiece = getPieceAt(from);
		toPiece = getPieceAt(to);
		
		// if last player color is not set, this is the first move
		if (lastPlayerColor == null && fromPiece.getOwner() == PlayerColor.BLUE) {
			// first move cannot come from blue
			throw new StrategyException("Blue cannot start the game");
		}
		
		if (lastPlayerColor == fromPiece.getOwner()) {
			throw new StrategyException("Same player cannot move twice in a row");
		}
		 
		if (toPiece != null && fromPiece.getOwner() == toPiece.getOwner()) {
			throw new StrategyException("Cannot move to a space with your own piece on it already");
		}
		
		MoveResult result;
		
		checkLocations(from, to);
		result = checkRepetition(piece, from, to);
		
		if (result != null) {
			return result;
		}
		
		// if moving to an empty space
		if (toPiece == null) {
			board.put(from, null);
			board.put(to, fromPiece);
			result = new MoveResult(MoveResultStatus.OK, new PieceLocationDescriptor(fromPiece, to));
		}
		else if (toPiece.getType() == PieceType.CHOKE_POINT) {
			throw new StrategyException("Cannot Move to a Choke Point");
		}
		else {
			result = battle(new PieceLocationDescriptor(fromPiece, from),
					new PieceLocationDescriptor(toPiece, to));
		}
		
		lastPlayerColor = fromPiece.getOwner();
		
		result = checkMovablePieces(result);
		 
		// game over
		if (result.getStatus() != MoveResultStatus.OK) {
			gameOver = true;
		}
		
		return result;
	}
	
	/**
	 * Checks if the current piece + move will violate the move repetition rule
	 * @param piece piece being moved
	 * @param from location the piece is being moved from
	 * @param to location piece is being moved to
	 * @return MoveResult if the move results in a repetition violation and the other play wins
	 * 		   otherwise returns null if move is valid
	 * @throws StrategyException if the move violates the repetition rule
	 */
	private MoveResult checkRepetition(PieceType piece, Location from, Location to) throws StrategyException {
		final Piece fromPiece = board.get(from);
		final PlayerColor pColor = fromPiece.getOwner();
		
		if (pColor == PlayerColor.RED) {
			// if last location is null, this is the first move.
			// just set the last piece location
			if (lastRedPieceLocation == null) {
				lastRedPieceLocation = new PieceLocationDescriptor(fromPiece, from);
			}
			else {
				// if the locations is equal to the previous location and the pieces are the same
				if (lastRedPieceLocation.getPiece().equals(fromPiece) && lastRedPieceLocation.getLocation().equals(to)) {
					// if the repetition flag is set, the rule is violated
					if (redRepetitionFlag) {
						return new MoveResult(MoveResultStatus.BLUE_WINS, null);
					}
					else {
						// no violation yet. set the last piece and location and flag to true
						lastRedPieceLocation = new PieceLocationDescriptor(fromPiece, from);
						redRepetitionFlag = true;
					}
				}
				else {
					// different piece or location, reset the location and flag to false
					lastRedPieceLocation = new PieceLocationDescriptor(fromPiece, from);
					redRepetitionFlag = false;
				}
			}
		}
		else { // BLUE player
			// if last location is null, this is the first move.
			// just set the last piece location
			if (lastBluePieceLocation == null) {
				lastBluePieceLocation = new PieceLocationDescriptor(fromPiece, from);
			}
			else {
				// if the locations is equal to the previous location and the pieces are the same
				if (lastBluePieceLocation.getPiece().equals(fromPiece) && lastBluePieceLocation.getLocation().equals(to)) {
					// if the repetition flag is set, the rule is violated
					if (blueRepetitionFlag) {
						return new MoveResult(MoveResultStatus.RED_WINS, null);
					}
					else {
						// no violation yet. set the last piece and location and flag to true
						lastBluePieceLocation = new PieceLocationDescriptor(fromPiece, from);
						blueRepetitionFlag = true;
					}
				}
				else {
					// different piece or location, reset the location and flag to false
					lastBluePieceLocation = new PieceLocationDescriptor(fromPiece, from);
					blueRepetitionFlag = false;
				}
			}
		}
		return null;
	}
	
	/**
	 * TODO
	 * @param result
	 * @return
	 */
	private MoveResult checkMovablePieces(MoveResult result) {
		
		if (numRedMovablePieces == 0 && numBlueMovablePieces == 0) {
			return new MoveResult(MoveResultStatus.DRAW, null);
		}
		else if (numRedMovablePieces == 0) {
			return new MoveResult(MoveResultStatus.BLUE_WINS, result.getBattleWinner());
		}
		else if (numBlueMovablePieces == 0) {
			return new MoveResult(MoveResultStatus.RED_WINS, result.getBattleWinner());
		}
		else {
			return result;
		}		
	}
	
	/**
	 * Handles battling and updates the board accordingly
	 * @param from piece being moved
	 * @param to piece being attacked
	 * @return move result after the battle
	 * @throws StrategyException
	 */
	private MoveResult battle(PieceLocationDescriptor from,PieceLocationDescriptor to) throws StrategyException {
		final Piece fromPiece = from.getPiece();
		final Piece toPiece = to.getPiece();
		
		final Location fromLoc = from.getLocation();
		final Location toLoc = to.getLocation();
		
		final PieceLocationDescriptor newFrom = new PieceLocationDescriptor(fromPiece, toLoc);
		final PieceLocationDescriptor newTo = new PieceLocationDescriptor(toPiece, fromLoc);
		
		final PlayerColor fromColor = fromPiece.getOwner();
		
		final int pieceComparison = fromPiece.getType().compareTo(toPiece.getType());
		
		if (pieceComparison == 0) {
			board.put(fromLoc, null);
			board.put(toLoc, null);
			numRedMovablePieces--;
			numBlueMovablePieces--;
			return new MoveResult(MoveResultStatus.OK, null);
		}
		
		// if the piece being attacked is a flag, that player wins
		if (toPiece.getType() == PieceType.FLAG) {
			board.put(fromLoc, null);
			board.put(toLoc, fromPiece);
					
			if (fromColor == PlayerColor.BLUE) {
				return new MoveResult(MoveResultStatus.BLUE_WINS, newFrom);
			}
			return new MoveResult(MoveResultStatus.RED_WINS, newFrom);
		}
		
		// from Wins
		if (pieceComparison < 0) {
			board.put(fromLoc, null);
			board.put(toLoc, fromPiece);
			if (fromColor == PlayerColor.BLUE) {
				numRedMovablePieces--;
			}
			else {
				numBlueMovablePieces--;
			}
			return new MoveResult(MoveResultStatus.OK, newFrom);
		}
		else { // to Wins
			board.put(toLoc, null);
			board.put(fromLoc, toPiece);
			if (fromColor == PlayerColor.BLUE) {
				numBlueMovablePieces--;
			}
			else {
				numRedMovablePieces--;
			}
			return new MoveResult(MoveResultStatus.OK, newTo);
		}
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
			thisPieceLocation = thisPiece.getLocation().getCoordinate(Coordinate.X_COORDINATE) 
					+ (thisPiece.getLocation().getCoordinate(Coordinate.Y_COORDINATE) * 6) + 1;
			
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
	
	/**
	 * 
	 */
	protected void setVariables(Collection<PieceLocationDescriptor> redPieces, Collection<PieceLocationDescriptor> bluePieces) 
	{
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
		numRedMovablePieces = 11;
		numBlueMovablePieces = 11;
		
		final Location[] chokeLocs = { new Location2D(2,2), new Location2D(2,3), new Location2D(3,2), new Location2D(3,3) };
		
		NUM_PIECES = 12;
		CHOKE_POINT_LOCATIONS = chokeLocs;
		BOARD_WIDTH = 6;
		BOARD_HEIGHT = 6;
		// based on simple formula for translating locations( (0,0) = 1, (1,1) = 2 ...  (4,5) = 35,  (5,5) = 36)
		// when pieces are located at their correct locations, their location total should equal these numbers
		RED_SPACE_TOTAL = 78;
		BLUE_SPACE_TOTAL = 366;
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
