/**
 * 
 */
package strategy.game.version.epsilon;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import strategy.common.PlayerColor;
import strategy.common.StrategyException;
import strategy.game.StrategyGameController;
import strategy.game.StrategyGameFactory;
import strategy.game.common.Location;
import strategy.game.common.Location2D;
import strategy.game.common.Piece;
import strategy.game.common.PieceLocationDescriptor;
import strategy.game.common.PieceType;

/**
 * @author Alex C
 *
 */
public class EpsilonStrategyTest {

	static private StrategyGameFactory gameFactory;
	private StrategyGameController game;
	private Collection<PieceLocationDescriptor> redCollection;
	private Collection<PieceLocationDescriptor> blueCollection;
	
	private static final Location[] everySpace = new Location2D[100];  
	// define pieces in certain configuration
	private static final PieceType[] playerPieces = 
	{
		PieceType.COLONEL,
		PieceType.MAJOR, PieceType.MAJOR, 
		PieceType.CAPTAIN, PieceType.CAPTAIN, PieceType.CAPTAIN, 
		PieceType.BOMB, PieceType.BOMB, PieceType.BOMB, PieceType.BOMB, 
		PieceType.SERGEANT, PieceType.SERGEANT, PieceType.SERGEANT, 
		PieceType.MINER, PieceType.MINER, PieceType.MINER, PieceType.MINER, 
		PieceType.LIEUTENANT, PieceType.LIEUTENANT, PieceType.LIEUTENANT, 
		PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT, 
		PieceType.SERGEANT,
		PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT,
		PieceType.BOMB,
		PieceType.FLAG,
		PieceType.MINER,
		PieceType.SPY,
		PieceType.LIEUTENANT,
		PieceType.CAPTAIN,
		PieceType.SCOUT,
		PieceType.MAJOR,
		PieceType.COLONEL,
		PieceType.GENERAL,
		PieceType.MARSHAL,
		PieceType.BOMB
	};
	
	/*
	 * The board with the initial configuration looks like this:
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 9 |BOMB |BOMB |BOMB |BOMB | CAP | CAP | CAP | MAJ | MAJ | COL | 
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 8 | LT  | LT  | LT  |MINER|MINER|MINER|MINER| SGT | SGT | SGT |
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 7 |FLAG |BOMB |SCOUT|SCOUT|SCOUT| SGT |SCOUT|SCOUT|SCOUT|SCOUT|
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 6 |BOMB | MAR | GEN | COL | MAJ |SCOUT| CAP | LT  | SPY |MINER|  
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 5 |     |     |CHOKE|CHOKE|     |     |CHOKE|CHOKE|     |     |
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 4 |     |     |CHOKE|CHOKE|     |     |CHOKE|CHOKE|     |     |
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 3 |MINER| SPY | LT  | CAP |SCOUT| MAJ | COL | GEN | MAR |BOMB | 
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 2 |SCOUT|SCOUT|SCOUT|SCOUT| SGT |SCOUT|SCOUT|SCOUT|BOMB |FLAG |
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 1 | SGT | SGT | SGT |MINER|MINER|MINER|MINER| LT  | LT  | LT  | 
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 0 | COL | MAJ | MAJ | CAP | CAP | CAP |BOMB |BOMB |BOMB |BOMB |
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 *   |  0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |  9  |  
	 */
	
	private static final Piece[] bluePieces = new Piece[40]; 
	private static final Piece[] redPieces = new Piece[40];
	
	@BeforeClass
	public static void BetaTestSetup() {
		gameFactory = StrategyGameFactory.getInstance();
		
		int i = 0;
		int j;
		
		// fill in location array with every space on board
		for (j = 0; j < 10; j++) {
			for (int k = 0; k < 10; k++) {
				everySpace[i] = new Location2D(k,j);
				i++;
			}
		}
	}
	
	@Before
	public void setup() {
		redCollection = new ArrayList<PieceLocationDescriptor>();
		blueCollection = new ArrayList<PieceLocationDescriptor>();
		
		int j = everySpace.length - 1;
		for (int i=0; i < playerPieces.length; i++) {
			// fill array with pieces
			redPieces[i] = new Piece(playerPieces[i], PlayerColor.RED);
			bluePieces[i] = new Piece(playerPieces[i], PlayerColor.BLUE);

			// add pieces to collection
			redCollection.add(new PieceLocationDescriptor(redPieces[i], everySpace[i]));
			blueCollection.add(new PieceLocationDescriptor(bluePieces[i], everySpace[j]));
			
			j--;
		}
	}
	
	@Test
	public void CreateInValidGameTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
	}

}
