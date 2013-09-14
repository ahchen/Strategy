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
	
	private final int numMoves;
	private final boolean gameStarted;
	private final boolean gameOver;	
	private HashMap<Location, Piece> board;
	
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
		board = new HashMap<Location, Piece>();
		
		Iterator<PieceLocationDescriptor> redIter, blueIter;
		redIter = redPieces.iterator();
		blueIter = bluePieces.iterator();
		
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
		//TODO
	}

	/* 
	 * @see strategy.game.StrategyGameController#move(strategy.game.common.PieceType, strategy.game.common.Location, strategy.game.common.Location)
	 */
	@Override
	public MoveResult move(PieceType piece, Location from, Location to)
			throws StrategyException {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @see strategy.game.StrategyGameController#getPieceAt(strategy.game.common.Location)
	 */
	@Override
	public Piece getPieceAt(Location location) {
		// TODO Auto-generated method stub
		return null;
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
		
		if (firstPieceIter.hasNext()) 
		{
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
		}
		else 
		{
			throw new StrategyException("Player Pieces Empty");
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
