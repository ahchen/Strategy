/**
 * 
 */
package strategy.game.version.epsilon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import strategy.game.common.StrategyGameObservable;
import strategy.game.common.StrategyGameObserver;
import strategy.game.version.StrategyGameControllerImpl;

/**
 * @author Alex C
 * @version October 15, 2013
 */
public class EpsilonStrategyGameController extends StrategyGameControllerImpl
		implements StrategyGameController, StrategyGameObservable {
	
	private Collection<StrategyGameObserver> observerList;
	private boolean redFlagCaptured, blueFlagCaptured;
	private boolean firstLT2SpaceAttack;

	/**
	 * Constructor for EpsilonStrategyGameController
	 * @param redPieces the initial starting configuration for the RED pieces
	 * @param bluePieces the initial starting configuration for the BLUE pieces
	 * @param observers the collection of observers to add to this game (null if no observers are needed)
	 * @throws StrategyException if either configuration is incorrect
	 */
	public EpsilonStrategyGameController(
			Collection<PieceLocationDescriptor> redPieces,
			Collection<PieceLocationDescriptor> bluePieces,
			Collection<StrategyGameObserver>observers)
			throws StrategyException {
		super(redPieces, bluePieces);
		
		if (observers == null) {
			observerList = new ArrayList<StrategyGameObserver>();
		}
		else {
			observerList = observers;
		}
	}
	
	/*
	 * @see strategy.game.StrategyGameController#startGame()
	 */
	@Override
	public void startGame() throws StrategyException {
		super.startGame();
		
		for (StrategyGameObserver obs : observerList) {
			obs.gameStart(redSetup, blueSetup);
		}
		
	}

	/* 
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

		redFlagCaptured = false;
		blueFlagCaptured = false;
		firstLT2SpaceAttack= false;
		
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

	/* 
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
		final Map<PieceType, Integer> requiredPieces = new HashMap<PieceType, Integer>();
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
		final PlayerColor whichColor = firstPiece.getPiece().getOwner();
		// set the spaceTotal to the appropriate number
		switch(whichColor) 
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
		
		// total should only be 1, 1 extra piece left over
		// number of flags left over should be 0 (required to have 2 flags)
		if (pieceTotal != 1 || requiredPieces.get(PieceType.FLAG) != 0) {
			throw new StrategyException("Invalid Combination of Pieces"); 
		}
		
		// if the space total is not exactly 0, there was an invalid placement
		if (spaceTotal != 0)
		{
			throw new StrategyException("Invalid Placement of Pieces");
		}
		
		// a bomb wasn't replaced so a movable piece was taken out
		// for a flag (non-movable piece)
		// number of movable pieces decreases
		if (requiredPieces.get(PieceType.BOMB) == 0) {
			switch(whichColor) {
				case RED:
					numRedMovablePieces--;
					break;
				case BLUE:
					numBlueMovablePieces--;
			}
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
	
	@Override
	public MoveResult move(PieceType piece, Location from, Location to)
			throws StrategyException {
		
		MoveResult result;
		StrategyException fault = null;
		
		if (piece == null && from == null && to == null) {
			// blue was last to move so red resigned
			if (lastPlayerColor == PlayerColor.BLUE || lastPlayerColor == null) {

				result = new MoveResult(MoveResultStatus.BLUE_WINS, null);
			}
			else {
				// otherwise red wins	
				result = new MoveResult(MoveResultStatus.RED_WINS, null);
			}
		}
		else {
			try {
				result = super.move(piece, from, to);
			}
			catch (StrategyException e) {
				result = null;
				fault = e;
			}
		}
		
		for (StrategyGameObserver obs : observerList) {
			obs.moveHappened(piece, from, to, result, fault);
		}
		
		// if result is null, throw the exception that was caught
		if (result == null) {
			throw fault;
		}
		
		return result;
	}
	
	@Override
	protected MoveResult battle(PieceLocationDescriptor from, PieceLocationDescriptor to) {
		final Piece fromPiece = from.getPiece();
		final Piece toPiece = to.getPiece();
		
		final Location fromLoc = from.getLocation();
		final Location toLoc = to.getLocation();
		 
		// red attacking first blue flag
		if (toPiece.getType() == PieceType.FLAG && lastPlayerColor == PlayerColor.BLUE && !blueFlagCaptured) {
			board.put(fromLoc, null);
			board.put(toLoc, fromPiece);
			
			blueFlagCaptured = true;
			
			return new MoveResult(MoveResultStatus.OK, new PieceLocationDescriptor(fromPiece, toLoc));
		}
		// blue attacking first red flag
		else if (toPiece.getType() == PieceType.FLAG && lastPlayerColor == PlayerColor.RED && !redFlagCaptured) {
			board.put(fromLoc, null);
			board.put(toLoc, fromPiece);
			
			redFlagCaptured = true;
			
			return new MoveResult(MoveResultStatus.OK, new PieceLocationDescriptor(fromPiece, toLoc));
		}
		// if attacking BOMB and attacker is not a miner
		else if (toPiece.getType() == PieceType.BOMB && fromPiece.getType() != PieceType.MINER) {
			// piece gets destroyed
			board.put(fromLoc, null);
			
			if (fromPiece.getOwner() == PlayerColor.BLUE) {
				numBlueMovablePieces--;
			}
			else {
				numRedMovablePieces--;
			}

			return new MoveResult(MoveResultStatus.OK, to);
		}
		// special case of spy attacking marshal
		else if (fromPiece.getType() == PieceType.SPY && toPiece.getType() == PieceType.MARSHAL) {
			// spy wins
			board.put(fromLoc, null);
			board.put(toLoc, fromPiece);
			
			if (fromPiece.getOwner() == PlayerColor.BLUE) {
				numRedMovablePieces--;
			}
			else {
				numBlueMovablePieces--;
			}

			return new MoveResult(MoveResultStatus.OK, new PieceLocationDescriptor(fromPiece, toLoc));
		}
		// special Draw Case
		else if (fromPiece.getType() == PieceType.FIRST_LIEUTENANT && toPiece.getType() == PieceType.LIEUTENANT ||
				fromPiece.getType() == PieceType.LIEUTENANT && toPiece.getType() == PieceType.FIRST_LIEUTENANT) {
			// in case this was a 2 space attack, set the flag back to false
			firstLT2SpaceAttack = false;
			
			// both pieces are destroyed
			board.put(fromLoc, null);
			board.put(toLoc, null);

			numRedMovablePieces--;
			numBlueMovablePieces--;

			return new MoveResult(MoveResultStatus.OK, null);
		}
		// if set true, from piece is 1st LT and is attacking using 2 space rule.
		else if (firstLT2SpaceAttack) {
			// set the flag back to false
			firstLT2SpaceAttack = false;
			
			final int pieceComparison = fromPiece.getType().compareTo(toPiece.getType());
			
			// 1st LT Wins
			if (pieceComparison < 0) {
				board.put(fromLoc, null);
				board.put(toLoc, fromPiece);
				
				return new MoveResult(MoveResultStatus.OK, new PieceLocationDescriptor(fromPiece, toLoc));
			}
			// 1st LT Loses
			else { 
				// only remove 1st LT, dont move other piece
				board.put(fromLoc, null);
				
				return new MoveResult(MoveResultStatus.OK, to);
			}
			
		}
		else {
			 return super.battle(from, to);
		}
	}
	
	/**
	 * Determines if moving to the given to location is valid from the given from location
	 * @param from base location
	 * @param to location to go
	 * @throws StrategyException
	 */
	@Override
	protected void checkLocations(Location from, Location to) throws StrategyException {
		
		if (getPieceAt(from).getType() == PieceType.SCOUT) {
			checkScoutLocation(from, to);
		}
		else if (getPieceAt(from).getType() == PieceType.FIRST_LIEUTENANT) {
			checkFirstLieutenantLocation(from, to);
		}
		else {
			super.checkLocations(from, to);
		}
	}
	

	/**
	 * Checks the validity of a move if the piece moving is a scout 
	 * @param from the location the scout is moving form
	 * @param to the location the scout is moving to
	 * @throws StrategyException thrown if the move for the scout is deemed to be invalid
	 */
	private void checkScoutLocation(Location from, Location to) throws StrategyException {
		
		try {
			final int moveDist = from.distanceTo(to);
			
			if (moveDist > 1) {
				if (board.get(to) != null) {
					throw new StrategyException("Cannot attack when moving scout more than 1 space");
				}
				// scout moving vertically multiple spaces
				else if (from.getCoordinate(Coordinate.X_COORDINATE) - to.getCoordinate(Coordinate.X_COORDINATE) == 0) {
					final int fromY = from.getCoordinate(Coordinate.Y_COORDINATE);
					final int toY = to.getCoordinate(Coordinate.Y_COORDINATE);
					final int staticX = from.getCoordinate(Coordinate.X_COORDINATE);

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
					final int fromX = from.getCoordinate(Coordinate.X_COORDINATE);
					final int toX = to.getCoordinate(Coordinate.X_COORDINATE);
					final int staticY = from.getCoordinate(Coordinate.Y_COORDINATE);
					
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
	 * Checks the validity of a move if the piece moving is a First Lieutenant 
	 * @param from the location the First Lieutenant is moving form
	 * @param to the location the First Lieutenant is moving to
	 * @throws StrategyException thrown if the move for the First Lieutenant is deemed to be invalid
	 */
	private void checkFirstLieutenantLocation(Location from, Location to) throws StrategyException {
		try {
			final int moveDist = from.distanceTo(to);
			if (moveDist > 1) {
				if (moveDist == 2) {
					if (getPieceAt(to) == null) {
						throw new StrategyException("1ST LT cannot move 2 spaces if not attacking");
					}
					else {
						final int midpointX = (from.getCoordinate(Coordinate.X_COORDINATE) + to.getCoordinate(Coordinate.X_COORDINATE)) / 2;
						final int midpointY = (from.getCoordinate(Coordinate.Y_COORDINATE) + to.getCoordinate(Coordinate.Y_COORDINATE)) / 2;
						
						if (getPieceAt(new Location2D(midpointX, midpointY)) != null) {
							throw new StrategyException("Piece in the way of attack");
						}
						
						firstLT2SpaceAttack = true;
					}
				}
				else {
					throw new StrategyException("Cannot move more than 2 spaces");
				}
			}
		}
		catch (StrategyRuntimeException e) {
			throw new StrategyException(e.getMessage());
		}
	}

	/* 
	 * @see strategy.game.common.StrategyGameObservable#register(strategy.game.common.StrategyGameObserver)
	 */
	@Override
	public void register(StrategyGameObserver observer) {
		observerList.add(observer);
	}

	/* 
	 * @see strategy.game.common.StrategyGameObservable#unregister(strategy.game.common.StrategyGameObserver)
	 */
	@Override
	public void unregister(StrategyGameObserver observer) {
		observerList.remove(observer);
	}

}
