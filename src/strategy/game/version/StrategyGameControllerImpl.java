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
import strategy.game.common.Coordinate;
import strategy.game.common.Location;
import strategy.game.common.Location2D;
import strategy.game.common.MoveResult;
import strategy.game.common.MoveResultStatus;
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
	protected int numMoves;
	
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
	
	/**
	 * Sets necessary variables for the given type of game being created
	 * @param redPieces collection of red pieces
	 * @param bluePieces collection of blue pieces
	 */
	protected abstract void setVariables(Collection<PieceLocationDescriptor> redPieces, Collection<PieceLocationDescriptor> bluePieces);
	
	/**
	 * Validates that the collection given only contains valid piece/location combinations for type of game created
	 * @param pieces the collection of pieces to validate
	 * @throws StrategyException thrown if the collection is deemed invalid
	 */
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
	
	/*
	 * @see strategy.game.StrategyGameController#move()
	 */
	public MoveResult move(PieceType piece, Location from, Location to)
			throws StrategyException {
		
		checkValidMoveRequest(piece, from, to);
		
		MoveResult result;
		final Piece fromPiece, toPiece;
		fromPiece = getPieceAt(from);
		toPiece = getPieceAt(to);
		
		if (fromPiece.getType() == PieceType.SCOUT) {
			checkScoutLocation(from,to);
		}
		else {
			checkLocations(from, to);
		}
		
		result = checkRepetition(piece, from, to);
		
		// if check repetition returned a MoveResult, then repetition rule is violated and 
		// we return the result
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
	 * TODO
	 * @param piece
	 * @param from
	 * @param to
	 * @throws StrategyException
	 */
	protected void checkValidMoveRequest(PieceType piece, Location from,
			Location to) throws StrategyException {
		if (gameOver) {
			throw new StrategyException("The game is over, you cannot make a move");
		}
		if (!gameStarted) {
			throw new StrategyException("You must start the game!");
		}
		if (piece == PieceType.FLAG) {
			throw new StrategyException("You cannot move the flag");
		}
		if (piece == PieceType.BOMB) {
			throw new StrategyException("You cannot move the bomb");
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
	}

	/**
	 * Determines if moving to the given to location is valid from the given from location
	 * @param from base location
	 * @param to location to go
	 * @throws StrategyException
	 */
	private void checkLocations(Location from, Location to) throws StrategyException {
		try {
			if (from.distanceTo(to) > 1) {
				throw new StrategyException("Locations are too far apart");
			}
		}
		catch (StrategyRuntimeException e) {
			throw new StrategyException(e.getMessage());
		}
	}
	
	/**
	 * TODO
	 * @param piece
	 * @param from
	 * @param to
	 * @return
	 * @throws StrategyException
	 */
	private void checkScoutLocation(Location from, Location to) throws StrategyException {
		
		try {
			int moveDist = from.distanceTo(to);
			
			if (moveDist > 1) {
				if (board.get(to) != null) {
					throw new StrategyException("Cannot attack when moving scout more than 1 space");
				}
				// scout moving vertically multiple spaces
				else if (from.getCoordinate(Coordinate.X_COORDINATE) - to.getCoordinate(Coordinate.X_COORDINATE) == 0) {
					int fromY = from.getCoordinate(Coordinate.Y_COORDINATE);
					int toY = to.getCoordinate(Coordinate.Y_COORDINATE);
					int staticX = from.getCoordinate(Coordinate.X_COORDINATE);

					// scout moving up the board
					if (toY > fromY) {
						for (int y = fromY + 1; y < toY; y++) {
							if (board.get(new Location2D(staticX, y)) != null) {
								throw new StrategyException("Not all spaces clear between movement locations for Scout");
							}
						}
					}
					// scout moving down the board
					else {
						for (int y = fromY - 1; y > toY; y--) {
							if (board.get(new Location2D(staticX, y)) != null) {
								throw new StrategyException("Not all spaces clear between movement locations for Scout");
							}
						}
					}
				}
				// scout moving horizontally multiple spaces
				else {
					int fromX = from.getCoordinate(Coordinate.X_COORDINATE);
					int toX = to.getCoordinate(Coordinate.X_COORDINATE);
					int staticY = from.getCoordinate(Coordinate.Y_COORDINATE);
					
					// scout moving left
					if (toX > fromX) {
						for (int x = fromX + 1; x < toX; x++) {
							if (board.get(new Location2D(x, staticY)) != null) {
								throw new StrategyException("Not all spaces clear between movement locations for Scout");
							}
						}
					}
					//scout moving right
					else {
						for (int x = fromX - 1; x > toX; x--) {
							if (board.get(new Location2D(x, staticY)) != null) {
								throw new StrategyException("Not all spaces clear between movement locations for Scout");
							}
						}
					}
				}
			}
		}
		catch (StrategyRuntimeException e) {
			throw new StrategyException(e.getMessage());
		}
	}

	
	/**
	 * Handles battling and updates the board accordingly
	 * @param from piece being moved
	 * @param to piece being attacked
	 * @return move result after the battle
	 */
	private MoveResult battle(PieceLocationDescriptor from,PieceLocationDescriptor to) {
		final Piece fromPiece = from.getPiece();
		final Piece toPiece = to.getPiece();
		
		final Location fromLoc = from.getLocation();
		final Location toLoc = to.getLocation();
		
		final PieceLocationDescriptor newFrom = new PieceLocationDescriptor(fromPiece, toLoc);
		final PieceLocationDescriptor newTo = new PieceLocationDescriptor(toPiece, fromLoc);
		
		final PlayerColor fromColor = fromPiece.getOwner();
		
		final int pieceComparison = fromPiece.getType().compareTo(toPiece.getType());
		
		// draw
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
		
		
		// if attacking BOMB and attacker is not a miner
		if (toPiece.getType() == PieceType.BOMB && fromPiece.getType() != PieceType.MINER) {
			// piece gets destroyed
			board.put(fromLoc, null);
			
			if (fromColor == PlayerColor.BLUE) {
				numBlueMovablePieces--;
			}
			else {
				numRedMovablePieces--;
			}
			
			return new MoveResult(MoveResultStatus.OK, new PieceLocationDescriptor(toPiece, toLoc));
		}
		// special case of spy attacking marshal
		else if (fromPiece.getType() == PieceType.SPY && toPiece.getType() == PieceType.MARSHAL) {
			// spy wins
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
		// from Wins (general case)
		else if (pieceComparison < 0) {
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
		// to Wins (general case)
		else { 
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
	 * Checks if the current piece + move will violate the move repetition rule
	 * @param piece piece being moved
	 * @param from location the piece is being moved from
	 * @param to location piece is being moved to
	 * @return MoveResult if the move results in a repetition violation and the other play wins
	 * 		   otherwise returns null if move is valid
	 */
	private MoveResult checkRepetition(PieceType piece, Location from, Location to) {
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
	 * Checks to see if either player (or both) have no remaining movable pieces
	 * @param result the move result from a given move
	 * @return a move result corresponding to a winner or draw if either player, or both
	 * 			don't have any movable pieces remaining. If both have movable pieces,
	 * 			the given MoveResult is returned.
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
	
	
	/*
	 * @see strategy.game.StrategyGameController#getPieceAt()
	 */
	public Piece getPieceAt(Location location) {
		return board.get(location);
	}

}
