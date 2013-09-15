/**
 * 
 */
package strategy.game.version.beta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import strategy.common.*;
import strategy.game.*;
import strategy.game.common.*;

/**
 * @author Alex C
 * @version September 13, 2013
 */
public class BetaStrategyGameControlller implements StrategyGameController {
	
	private int numMoves;
	private boolean gameStarted;
	private boolean gameOver;	
	private PlayerColor lastPlayerColor;
	private HashMap<Location, Piece> board;
	
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
		
		Iterator<PieceLocationDescriptor> redIter, blueIter;
		redIter = redPieces.iterator();
		blueIter = bluePieces.iterator();
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				// initialize board
				board.put(new Location2D(j,i), null);
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
		Piece fromPiece, toPiece;
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
		
		if (toPiece == null) {
			checkLocations(from, to);
			board.put(from, null);
			board.put(to, fromPiece);
			numMoves++;
			lastPlayerColor = fromPiece.getOwner();
			return new MoveResult(MoveResultStatus.OK, new PieceLocationDescriptor(fromPiece, to));
		}
		
		if (fromPiece.getOwner() == toPiece.getOwner()) {
			throw new StrategyException("Cannot move to a space with your own piece on it already");	
		}
		
		return null;
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
		PieceLocationDescriptor thisPiece, firstPiece;
		int thisPieceLocation;
		int spaceTotal;		
		
		final Iterator<PieceLocationDescriptor> pieceIter, firstPieceIter;
		pieceIter = playerPieces.iterator();
		firstPieceIter = playerPieces.iterator();
		
		firstPiece = firstPieceIter.next();
		switch(firstPiece.getPiece().getOwner()) 
		{
			case RED:
				spaceTotal = 78;
				break;
			case BLUE:
				spaceTotal = 366;
				break;
			default:
				throw new StrategyException("Unknown Player Color");
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
