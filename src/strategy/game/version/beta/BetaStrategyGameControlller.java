/**
 * 
 */
package strategy.game.version.beta;

import java.util.HashMap;

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
	private HashMap<Integer, Piece> board;
	
	public BetaStrategyGameControlller() {
		numMoves = 0;
		gameStarted = false;
		gameOver = false;
		board = new HashMap<Integer, Piece>();
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

}
