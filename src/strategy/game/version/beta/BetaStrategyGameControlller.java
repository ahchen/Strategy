/**
 * 
 */
package strategy.game.version.beta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import strategy.common.*;
import strategy.game.*;
import strategy.game.common.*;

/**
 * 
 * @author Alex C
 * @version September 13, 2013
 */
public class BetaStrategyGameControlller implements StrategyGameController {
	
	private int numMoves;
	private boolean gameStarted;
	private boolean gameOver;	
	private PlayerColor lastPlayerColor;
	private final Map<Location, Piece> board;
	
	/**
	 * Public constructor for BetaStrategyGameControlller
	 * Validates the players pieces and initializes a HashMap to represent the board
	 * @param redPieces red player pieces
	 * @param bluePieces blue player pieces
	 * @throws StrategyException
	 */
	public BetaStrategyGameControlller(Collection<PieceLocationDescriptor> redPieces, Collection<PieceLocationDescriptor> bluePieces) throws StrategyException {
		
		if (redPieces == null || bluePieces == null) {
			throw new StrategyException("Not Given Both Configurations");
		}
		
		if (redPieces.size() != 12 || bluePieces.size() != 12) {
			throw new StrategyException("Invalid Number of Pieces");
		}
		
		validatePiecesAndLocations(redPieces);
		validatePiecesAndLocations(bluePieces);
		
		numMoves = 0;
		gameStarted = false;
		gameOver = false;
		lastPlayerColor = null;
		board = new HashMap<Location, Piece>();
		
		final Iterator<PieceLocationDescriptor> redIter, blueIter;
		redIter = redPieces.iterator();
		blueIter = bluePieces.iterator();
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				// initialize board
				board.put(new Location2D(j, i), null);
			}
		}
		
		PieceLocationDescriptor singleRedPiece, singleBluePiece;
		
		while (redIter.hasNext()) {
			singleRedPiece = redIter.next();
			singleBluePiece = blueIter.next();
			
			board.put(singleRedPiece.getLocation(), singleRedPiece.getPiece());
			board.put(singleBluePiece.getLocation(), singleBluePiece.getPiece());
		}
	}
	
	/* 
	 * @see strategy.game.StrategyGameController#startGame()
	 */
	@Override
	public void startGame() throws StrategyException {
		if(gameStarted) {
			throw new StrategyException("Game Already In Progress");
		}
		gameStarted = true;
		gameOver = false;
		numMoves = 0;
		lastPlayerColor = null;
	}

	/* 
	 * @see strategy.game.StrategyGameController#move(strategy.game.common.PieceType, strategy.game.common.Location, strategy.game.common.Location)
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
		if (!board.containsKey(from) || !board.containsKey(to)) {
			throw new StrategyException("Coordinates not on board");
		}
		if (board.get(from).getType() != piece) {
			throw new StrategyException("Specified piece is not located at given location");
		}
		
		final Piece fromPiece, toPiece;
		fromPiece = getPieceAt(from);
		toPiece = getPieceAt(to);
		
		if (lastPlayerColor == null) {
			if (fromPiece.getOwner() == PlayerColor.BLUE) {
				throw new StrategyException("Blue cannot start the game");
			}
		}
		
		if (lastPlayerColor == fromPiece.getOwner()) {
			throw new StrategyException("Same player cannot move twice in a row");
		}
		
		if (toPiece != null && fromPiece.getOwner() == toPiece.getOwner()) {
			throw new StrategyException("Cannot move to a space with your own piece on it already");	
		}
		
		checkLocations(from, to);
		MoveResult result;
		
		if (toPiece == null) {
			board.put(from, null);
			board.put(to, fromPiece);
			
			result = new MoveResult(MoveResultStatus.OK, new PieceLocationDescriptor(fromPiece, to));
		}
		else {
			result = Battle(new PieceLocationDescriptor(fromPiece, from),
					new PieceLocationDescriptor(toPiece, to));
			
			if (result.getStatus() == MoveResultStatus.BLUE_WINS ||
					result.getStatus() == MoveResultStatus.RED_WINS) {
				gameOver = true;
				return result;
			}
		}
		
		lastPlayerColor = fromPiece.getOwner();
		if (lastPlayerColor == PlayerColor.BLUE) {
			numMoves++;
			
			if (numMoves == 6) {
				gameOver = true;
				return new MoveResult(MoveResultStatus.DRAW, null);
			}
		}
		
		return result;
	}
	
	private MoveResult Battle(PieceLocationDescriptor from, PieceLocationDescriptor to) {
		if (from.getPiece().getType() == to.getPiece().getType()) {
			board.put(from.getLocation(), null);
			board.put(to.getLocation(), null);
			
			return new MoveResult(MoveResultStatus.OK, null);
		}
		
		final PieceType fromType, toType;
		final Location fromLoc, toLoc;
		final PieceLocationDescriptor newFrom, newTo;
		
		fromType = from.getPiece().getType();
		toType = to.getPiece().getType();
		
		fromLoc = from.getLocation();
		toLoc = to.getLocation();
		
		newFrom = new PieceLocationDescriptor(from.getPiece(), toLoc);
		newTo = new PieceLocationDescriptor(to.getPiece(), fromLoc);
		
		MoveResult result = null;
		
		if (toType == PieceType.FLAG) {
			board.put(fromLoc, null);
			board.put(toLoc, from.getPiece());
			
			if (from.getPiece().getOwner() == PlayerColor.BLUE) {
				return new MoveResult(MoveResultStatus.BLUE_WINS, newFrom);
			}
			return new MoveResult(MoveResultStatus.RED_WINS, newFrom);
		}
		
		switch (fromType) {
			case MARSHAL:
				board.put(from.getLocation(), null);
				board.put(to.getLocation(), from.getPiece());
				
				result = new MoveResult(MoveResultStatus.OK, newFrom);
				break;
			case COLONEL:
				if (toType == PieceType.MARSHAL) {
					board.put(to.getLocation(), null);
					board.put(from.getLocation(), to.getPiece());
									
					result = new MoveResult(MoveResultStatus.OK, newTo);
				}
				else {
					board.put(from.getLocation(), null);
					board.put(to.getLocation(), from.getPiece());
					
					result = new MoveResult(MoveResultStatus.OK, newFrom);
				}
				break;
			case CAPTAIN:
				if (toType == PieceType.LIEUTENANT || toType == PieceType.SERGEANT) {
					board.put(from.getLocation(), null);
					board.put(to.getLocation(), from.getPiece());
					
					result = new MoveResult(MoveResultStatus.OK, newFrom);
				}
				else {
					board.put(to.getLocation(), null);
					board.put(from.getLocation(), to.getPiece());
									
					result = new MoveResult(MoveResultStatus.OK, newTo);
				}
				break;
			case LIEUTENANT:
				if (toType == PieceType.SERGEANT) {
					board.put(from.getLocation(), null);
					board.put(to.getLocation(), from.getPiece());
					
					result = new MoveResult(MoveResultStatus.OK, newFrom);
				}
				else {
					board.put(to.getLocation(), null);
					board.put(from.getLocation(), to.getPiece());
									
					result = new MoveResult(MoveResultStatus.OK, newTo);
				}
				break;
			case SERGEANT:
				board.put(to.getLocation(), null);
				board.put(from.getLocation(), to.getPiece());
								
				result = new MoveResult(MoveResultStatus.OK, newTo);
				break;
		}
		
		return result;
	}

	
	/**
	 * 
	 * @param from
	 * @param to
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
	
	/* 
	 * @see strategy.game.StrategyGameController#getPieceAt(strategy.game.common.Location)
	 */
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
	private void validatePiecesAndLocations(Collection<PieceLocationDescriptor> playerPieces) throws StrategyException 
	{
		int numFlags = 1;
		int numMarshal = 1;
		int numColonel = 2;
		int numCaptain = 2;
		int numLieutenant = 3;
		int numSergeant = 3;
		final int redSpaceTotal = 78;
		final int blueSpaceTotal = 366;
		PieceLocationDescriptor thisPiece;
		final PieceLocationDescriptor firstPiece;
		int thisPieceLocation;
		int spaceTotal = 0;	
		
		final Iterator<PieceLocationDescriptor> pieceIter, firstPieceIter;
		pieceIter = playerPieces.iterator();
		firstPieceIter = playerPieces.iterator();
		
		firstPiece = firstPieceIter.next();
		switch(firstPiece.getPiece().getOwner()) 
		{
			case RED:
				spaceTotal = redSpaceTotal;
				break;
			case BLUE:
				spaceTotal = blueSpaceTotal;
				break;
		}
		
		while (pieceIter.hasNext()) 
		{
			thisPiece = pieceIter.next();
			thisPieceLocation = thisPiece.getLocation().getCoordinate(Coordinate.X_COORDINATE) + (thisPiece.getLocation().getCoordinate(Coordinate.Y_COORDINATE) * 6) + 1;

			spaceTotal -= thisPieceLocation;
			
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
		
		if (numFlags != 0 || numMarshal != 0 || numColonel != 0 || numCaptain != 0 
				|| numLieutenant != 0 || numSergeant != 0) 
		{
			throw new StrategyException("Invalid Combination of Pieces"); 
		}
		
		if (spaceTotal != 0)
		{
			throw new StrategyException("Invalid Placement of Pieces");
		}
	}

}
