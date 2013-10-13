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
import strategy.game.common.MoveResult;
import strategy.game.common.MoveResultStatus;
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
		PieceType.FIRST_LIEUTENANT,
		PieceType.LIEUTENANT, 
		PieceType.SCOUT, 
		PieceType.FLAG, 
		PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT, 
		PieceType.SERGEANT,
		PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT,
		PieceType.BOMB,
		PieceType.FLAG,
		PieceType.MINER,
		PieceType.SPY,
		PieceType.CAPTAIN,
		PieceType.MAJOR,
		PieceType.LIEUTENANT,
		PieceType.FIRST_LIEUTENANT,
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
	 * 8 |SCOUT| LT  | 1LT |MINER|MINER|MINER|MINER| SGT | SGT | SGT |
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 7 |FLAG |BOMB |SCOUT|SCOUT|SCOUT| SGT |SCOUT|SCOUT|SCOUT|FLAG |
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 6 |BOMB | MAR | GEN | COL | 1LT | LT  | MAJ | CAP | SPY |MINER|  
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 5 |     |     |CHOKE|CHOKE|     |     |CHOKE|CHOKE|     |     |
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 4 |     |     |CHOKE|CHOKE|     |     |CHOKE|CHOKE|     |     |
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 3 |MINER| SPY | CAP | MAJ | LT  | 1LT | COL | GEN | MAR |BOMB | 
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 2 |FLAG |SCOUT|SCOUT|SCOUT| SGT |SCOUT|SCOUT|SCOUT|BOMB |FLAG |
	 * - +-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
	 * 1 | SGT | SGT | SGT |MINER|MINER|MINER|MINER| 1LT | LT  |SCOUT| 
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
	public void CreateValidGameTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		assertNotNull(game);
	}
	
	@Test(expected=StrategyException.class)
	public void NullCollectionTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(null, null, null); 
	}
	
	@Test(expected=StrategyException.class)
	public void NotEnoughPiecesTest() throws StrategyException {
		final Collection<PieceLocationDescriptor> redOnePiece, blueOnePiece;
		
		// create new collection
		redOnePiece = new ArrayList<PieceLocationDescriptor>();
		blueOnePiece = new ArrayList<PieceLocationDescriptor>();
		
		// add one piece to the collection
		redOnePiece.add(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		blueOnePiece.add(new PieceLocationDescriptor(bluePieces[0], everySpace[99]));
		
		game = gameFactory.makeEpsilonStrategyGame(redOnePiece, blueOnePiece, null);
	} 
	
	@Test(expected=StrategyException.class)
	public void InvalidPieceCombinationTest() throws StrategyException {
		final Collection<PieceLocationDescriptor> invalidRed = redCollection;
		final Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		// remove a red and blue piece
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[99]));
		
		// add an extra piece where the removed piece would have been
		invalidRed.add(new PieceLocationDescriptor(redPieces[1], everySpace[0]));
		invalidBlue.add(new PieceLocationDescriptor(bluePieces[1], everySpace[99]));
		
		game = gameFactory.makeEpsilonStrategyGame(invalidRed, invalidBlue, null);
	} 
	
	@Test(expected=StrategyException.class)
	public void InvalidPieceLayoutTest() throws StrategyException {
		final Collection<PieceLocationDescriptor> invalidRed = redCollection;
		final Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[99]));
		
		invalidRed.add(new PieceLocationDescriptor(redPieces[0], everySpace[1]));
		invalidBlue.add(new PieceLocationDescriptor(bluePieces[0], everySpace[98]));
		
		game = gameFactory.makeEpsilonStrategyGame(invalidRed, invalidBlue, null);
	} 
	
	@Test(expected=StrategyException.class)
	public void OneFlagTest() throws StrategyException {
		final Collection<PieceLocationDescriptor> invalidRed = redCollection;
		final Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		// remove second flag
		invalidRed.remove(new PieceLocationDescriptor(redPieces[20], everySpace[20]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[20], everySpace[79]));
		
		// add in a missing scout
		invalidRed.add(new PieceLocationDescriptor(redPieces[21], everySpace[20]));
		invalidBlue.add(new PieceLocationDescriptor(bluePieces[21], everySpace[79]));
		
		game = gameFactory.makeEpsilonStrategyGame(invalidRed, invalidBlue, null);
	} 
	
	@Test
	public void ChokePointLocationTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		assertEquals(game.getPieceAt(everySpace[42]).getType(), PieceType.CHOKE_POINT);
		assertEquals(game.getPieceAt(everySpace[43]).getType(), PieceType.CHOKE_POINT);
		assertEquals(game.getPieceAt(everySpace[46]).getType(), PieceType.CHOKE_POINT);
		assertEquals(game.getPieceAt(everySpace[47]).getType(), PieceType.CHOKE_POINT);
		
		assertEquals(game.getPieceAt(everySpace[52]).getType(), PieceType.CHOKE_POINT);
		assertEquals(game.getPieceAt(everySpace[53]).getType(), PieceType.CHOKE_POINT);
		assertEquals(game.getPieceAt(everySpace[56]).getType(), PieceType.CHOKE_POINT);
		assertEquals(game.getPieceAt(everySpace[57]).getType(), PieceType.CHOKE_POINT);
	}
	
	@Test(expected=StrategyException.class)
	public void StartGameTwiceTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		game.startGame();
		game.startGame();
	}
	
	@Test(expected=StrategyException.class)
	public void moveBeforeStartGame() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		// move before game start
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
	}
	
	@Test(expected=StrategyException.class)
	public void moveFlag() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.FLAG, everySpace[29], everySpace[39]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveChokePointTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.CHOKE_POINT, everySpace[43], everySpace[44]);
	} 
	
	@Test(expected=StrategyException.class)
	public void locationOffBoard() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.MARSHAL, new Location2D(10,10), new Location2D(10,11));
	}
	
	@Test(expected=StrategyException.class)
	public void moveInvalidFromLocation() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		game.move(PieceType.MARSHAL, everySpace[39], everySpace[49]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveInvalidToLocation() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		// occupied by another same player piece
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[39]);
	}

	@Test(expected=StrategyException.class)
	public void moveSameSpaceTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.MINER, everySpace[30], everySpace[31]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveTwoSpacesNotScoutTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.MAJOR, everySpace[35], everySpace[55]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveDiagonallyTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.MINER, everySpace[30], everySpace[41]);
	}
	
	@Test(expected=StrategyException.class)
	public void blueFirstMoveTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
	}
	
	@Test(expected=StrategyException.class)
	public void playerMoveTwiceTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveBombTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.BOMB, everySpace[39], everySpace[49]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveToChokePointTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.GENERAL, everySpace[37], everySpace[47]);
	}

	@Test(expected=StrategyException.class)
	public void FirstLTMove2SpacesTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[35], everySpace[55]);
	}
	
	@Test
	public void FirstLTAttack2SpacesDrawTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.LIEUTENANT, everySpace[34], everySpace[44]);
		
		MoveResult res = game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[44]);
		
		assertNull(game.getPieceAt(everySpace[44]));
		assertNull(game.getPieceAt(everySpace[64]));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertNull(res.getBattleWinner());		
	}
	
	@Test
	public void FirstLTAttack2SpacesWinTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.LIEUTENANT, everySpace[34], everySpace[44]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[54]);
		game.move(PieceType.LIEUTENANT, everySpace[44], everySpace[45]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[54], everySpace[44]);
		game.move(PieceType.LIEUTENANT, everySpace[45], everySpace[55]);
		
		MoveResult res = game.move(PieceType.FIRST_LIEUTENANT, everySpace[44], everySpace[24]);
		
		assertNull(game.getPieceAt(everySpace[44]));
		assertEquals(game.getPieceAt(everySpace[24]), bluePieces[35]);
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(bluePieces[35], everySpace[24]));		
	}
	
	@Test
	public void FirstLTAttack2SpacesLoseTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.LIEUTENANT, everySpace[34], everySpace[44]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[54]);
		game.move(PieceType.LIEUTENANT, everySpace[44], everySpace[45]);
		game.move(PieceType.LIEUTENANT, everySpace[65], everySpace[55]);
		game.move(PieceType.MAJOR, everySpace[33], everySpace[34]);
		
		MoveResult res = game.move(PieceType.FIRST_LIEUTENANT, everySpace[54], everySpace[34]);
		
		assertNull(game.getPieceAt(everySpace[54]));
		assertEquals(game.getPieceAt(everySpace[34]), redPieces[33]);
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(redPieces[33], everySpace[34]));		
	}
	
	@Test(expected=StrategyException.class)
	public void FirstLTMoveDiagonallyTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[35], everySpace[54]);
	}
	
	
	@Test(expected=StrategyException.class)
	public void FirstLTMove3SpacesTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.LIEUTENANT, everySpace[34], everySpace[44]);
		game.move(PieceType.LIEUTENANT, everySpace[65], everySpace[55]);
		game.move(PieceType.LIEUTENANT, everySpace[44], everySpace[54]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[34]);
	}

	@Test(expected=StrategyException.class)
	public void FirstLTPieceBetweenAttackTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		game.move(PieceType.LIEUTENANT, everySpace[34], everySpace[44]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[54]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[35], everySpace[34]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[54], everySpace[34]);
		
	}
	
	@Test
	public void minerAttacksBombTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MARSHAL, everySpace[61], everySpace[51]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MARSHAL, everySpace[51], everySpace[41]);
		
		res = game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.MINER, PlayerColor.RED), everySpace[60]));
		
		assertEquals(game.getPieceAt(everySpace[60]), new Piece(PieceType.MINER, PlayerColor.RED));
		assertNull(game.getPieceAt(everySpace[50]));
	}
	
	@Test
	public void redNotMinerAttacksBombTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.SPY, everySpace[31], everySpace[41]);
		game.move(PieceType.MARSHAL, everySpace[61], everySpace[51]);
		game.move(PieceType.SPY, everySpace[41], everySpace[40]);
		game.move(PieceType.MARSHAL, everySpace[51], everySpace[41]);
		game.move(PieceType.SPY, everySpace[40], everySpace[50]);
		game.move(PieceType.MARSHAL, everySpace[41], everySpace[51]);
		
		res = game.move(PieceType.SPY, everySpace[50], everySpace[60]);
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.BOMB, PlayerColor.BLUE), everySpace[60]));
		
		assertEquals(game.getPieceAt(everySpace[60]), new Piece(PieceType.BOMB, PlayerColor.BLUE));
		assertNull(game.getPieceAt(everySpace[50]));
	}
	
	@Test
	public void blueNotMinerAttacksBombTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.SPY, everySpace[31], everySpace[41]);
		game.move(PieceType.SPY, everySpace[68], everySpace[58]);
		game.move(PieceType.SPY, everySpace[41], everySpace[40]);
		game.move(PieceType.SPY, everySpace[58], everySpace[48]);
		game.move(PieceType.SPY, everySpace[40], everySpace[50]);
		game.move(PieceType.SPY, everySpace[48], everySpace[49]);
		game.move(PieceType.SPY, everySpace[50], everySpace[40]);
		
		res = game.move(PieceType.SPY, everySpace[49], everySpace[39]); 
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.BOMB, PlayerColor.RED), everySpace[39]));
		
		assertEquals(game.getPieceAt(everySpace[39]), new Piece(PieceType.BOMB, PlayerColor.RED));
		assertNull(game.getPieceAt(everySpace[49]));
	}
	
	@Test
	public void spyKillMarshalTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.SPY, everySpace[31], everySpace[41]);
		game.move(PieceType.MARSHAL, everySpace[61], everySpace[51]);
		res = game.move(PieceType.SPY, everySpace[41], everySpace[51]);
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(redPieces[31], everySpace[51]));
		assertEquals(game.getPieceAt(everySpace[51]), redPieces[31]);
		assertNull(game.getPieceAt(everySpace[41]));
	}
	
	@Test
	public void spyKillMarshalTest2() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[48]);
		game.move(PieceType.SPY, everySpace[68], everySpace[58]);
		game.move(PieceType.GENERAL, everySpace[37], everySpace[38]);
		
		res = game.move(PieceType.SPY, everySpace[58], everySpace[48]);
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(bluePieces[31], everySpace[48]));
		assertEquals(game.getPieceAt(everySpace[48]), bluePieces[31]);
		assertNull(game.getPieceAt(everySpace[58]));
	}
	
	@Test
	public void MarshalKillSpyTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.SPY, everySpace[31], everySpace[41]);
		game.move(PieceType.MARSHAL, everySpace[61], everySpace[51]);
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		res = game.move(PieceType.MARSHAL, everySpace[51], everySpace[41]);
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(bluePieces[38], everySpace[41]));
		assertEquals(game.getPieceAt(everySpace[41]), bluePieces[38]);
		assertNull(game.getPieceAt(everySpace[51]));
	}
	
	@Test
	public void redResignTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		res = game.move(null, null, null);
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
	}
	
	@Test
	public void blueResignTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[35], everySpace[45]);
		
		res = game.move(null, null, null);
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
	}
	
}
