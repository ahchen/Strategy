/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package strategy.game;

import java.util.Collection;
import java.util.Iterator;

import strategy.common.*;
import strategy.game.common.*;
import strategy.game.version.alpha.AlphaStrategyGameController;
import strategy.game.version.beta.BetaStrategyGameControlller;

/**
 * <p>
 * Factory to produce various versions of the Strategy game. This is implemented
 * as a singleton.
 * </p><p>
 * NOTE: If an error occurs creating any game, that is not specified in the particular
 * factory method's documentation, the factory method should throw a 
 * StrategyRuntimeException.
 * </p>
 * 
 * @author gpollice
 * @version Sep 10, 2013
 */
public class StrategyGameFactory
{
	private final static StrategyGameFactory instance = new StrategyGameFactory();
	private int numCaptain;
	
	/**
	 * Default private constructor to ensure this is a singleton.
	 */
	private StrategyGameFactory()
	{
		// Intentionally left empty.
	}

	/**
	 * @return the instance
	 */
	public static StrategyGameFactory getInstance()
	{
		return instance;
	}
	
	/**
	 * Create an Alpha Strategy game.
	 * @return the created Alpha Strategy game
	 */
	public StrategyGameController makeAlphaStrategyGame()
	{
		return new AlphaStrategyGameController();
	}
	
	/**
	 * Create a new Beta Strategy game given the 
	 * @param redConfiguration the initial starting configuration for the RED pieces
	 * @param blueConfiguration the initial starting configuration for the BLUE pieces
	 * @return the Beta Strategy game instance with the initial configuration of pieces
	 * @throws StrategyException if either configuration is incorrect
	 */
	public StrategyGameController makeBetaStrategyGame(
			Collection<PieceLocationDescriptor> redConfiguration,
			Collection<PieceLocationDescriptor> blueConfiguration)
		throws StrategyException
	{
		
		if (redConfiguration == null || blueConfiguration == null) {
			throw new StrategyException("Not Given Both Configurations");
		}
		
		if (redConfiguration.size() != 12 || blueConfiguration.size() != 12) {
			throw new StrategyException("Invalid Number of Pieces");
		}
		
		int numFlags = 2;
		int numMarshal = 2;
		int numColonel = 4;
		int numCaptain = 4;
		int numLieutenant = 6;
		int numSergeant = 6;
		
		int redSpaceTotal = 78;
		int blueSpaceTotal = 366;
		
		
		final Iterator<PieceLocationDescriptor> redIter, blueIter;
		redIter = redConfiguration.iterator();
		blueIter = blueConfiguration.iterator();
		
		PieceLocationDescriptor thisRedPiece, thisBluePiece;
		int thisRedLocation, thisBlueLocation;
		
		
		// we know that both collections have exactly 12 
		// so it is sufficient to only check 1 of the iterators 
		while (redIter.hasNext()) {
			thisRedPiece = redIter.next();
			thisBluePiece = blueIter.next();
			
			thisRedLocation = thisRedPiece.getLocation().getCoordinate(Coordinate.X_COORDINATE) + (thisRedPiece.getLocation().getCoordinate(Coordinate.Y_COORDINATE) * 6) + 1;
			thisBlueLocation = thisBluePiece.getLocation().getCoordinate(Coordinate.X_COORDINATE) + (thisBluePiece.getLocation().getCoordinate(Coordinate.Y_COORDINATE) * 6) + 1;

			redSpaceTotal -= thisRedLocation;
			blueSpaceTotal -= thisBlueLocation;
			
			switch(thisRedPiece.getPiece().getType()) {
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
					throw new StrategyException("Invalid Combination of Pieces");	
			}
			
			switch(thisBluePiece.getPiece().getType()) {
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
					throw new StrategyException("Invalid Combination of Pieces");	
			}
		}

		if (numFlags != 0 || numMarshal != 0 || numColonel != 0 || numCaptain != 0 
				|| numLieutenant != 0 || numSergeant != 0) {
			throw new StrategyException("Invalid Combination of Pieces"); 
		}	
		
		if (redSpaceTotal != 0 || blueSpaceTotal != 0)
		{
			throw new StrategyException("Invalid Placement of Pieces");
		}
		
		return new BetaStrategyGameControlller();
	}
}