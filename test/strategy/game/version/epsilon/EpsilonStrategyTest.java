/**
 * 
 */
package strategy.game.version.epsilon;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[58]);
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
	public void LtAttack1STLTTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		MoveResult res;
		
		game.startGame();
		game.move(PieceType.LIEUTENANT, everySpace[34], everySpace[44]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[54]);

		res = game.move(PieceType.LIEUTENANT, everySpace[44], everySpace[54]);
		
		assertNull(game.getPieceAt(everySpace[44]));
		assertNull(game.getPieceAt(everySpace[54]));
		
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
	public void redResignFirstMoveTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		res = game.move(null, null, null);
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
	}
	
	@Test
	public void redResignTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[48]);
		game.move(PieceType.MARSHAL, everySpace[61], everySpace[51]);
		
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
	
	@Test(expected=StrategyException.class)
	public void nullPieceTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		game.move(null, everySpace[35], everySpace[45]);

	}
	
	@Test(expected=StrategyException.class)
	public void nullPieceAndFromTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();

		game.move(null, null, everySpace[45]);

	}
	
	@Test(expected=StrategyException.class)
	public void nullToTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[35], null);

	}
	
	@Test
	public void redCap1BlueFlagTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MINER, everySpace[59], everySpace[49]);
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		game.move(PieceType.MINER, everySpace[49], everySpace[39]);
		
		// 1 flag captured
		res = game.move(PieceType.MINER, everySpace[60], everySpace[70]);
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(redPieces[30], everySpace[70]));
		assertNull(game.getPieceAt(everySpace[60]));
		assertEquals(game.getPieceAt(everySpace[70]), redPieces[30]);		
	}
	
	@Test
	public void blueCap1RedFlagTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MINER, everySpace[59], everySpace[49]);
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		game.move(PieceType.MINER, everySpace[49], everySpace[39]);
		game.move(PieceType.MINER, everySpace[60], everySpace[50]);
		
		// 1 flag captured
		res = game.move(PieceType.MINER, everySpace[39], everySpace[29]);
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(bluePieces[30], everySpace[29]));
		assertNull(game.getPieceAt(everySpace[39]));
		assertEquals(game.getPieceAt(everySpace[29]), bluePieces[30]);
	}
	
	@Test
	public void redCap2BlueFlagTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MINER, everySpace[59], everySpace[49]);
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		game.move(PieceType.MINER, everySpace[49], everySpace[39]);
		// 1 flag captured
		game.move(PieceType.MINER, everySpace[60], everySpace[70]);
		// blue captures 1 red flag but game should not be over
		game.move(PieceType.MINER, everySpace[39], everySpace[29]);
		
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[48]);
		game.move(PieceType.MINER, everySpace[29], everySpace[19]);
		game.move(PieceType.MARSHAL, everySpace[48], everySpace[58]);
		game.move(PieceType.MINER, everySpace[19], everySpace[9]);
		game.move(PieceType.MARSHAL, everySpace[58], everySpace[59]);
		game.move(PieceType.MINER, everySpace[9], everySpace[19]);
		game.move(PieceType.MARSHAL, everySpace[59], everySpace[69]);
		game.move(PieceType.MINER, everySpace[19], everySpace[29]);
		
		// second flag captured
		res = game.move(PieceType.MARSHAL, everySpace[69], everySpace[79]);

		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(redPieces[38], everySpace[79]));
		assertNull(game.getPieceAt(everySpace[69]));
		assertEquals(game.getPieceAt(everySpace[79]), redPieces[38]);			
	}
	
	@Test
	public void blueCap2RedFlagTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MINER, everySpace[59], everySpace[49]);
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		game.move(PieceType.MINER, everySpace[49], everySpace[39]);
		game.move(PieceType.MINER, everySpace[60], everySpace[50]);
		
		// 1 flag captured
		game.move(PieceType.MINER, everySpace[39], everySpace[29]);
		
		game.move(PieceType.MINER, everySpace[50], everySpace[40]);
		game.move(PieceType.MARSHAL, everySpace[61], everySpace[51]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MARSHAL, everySpace[51], everySpace[41]);
		// red caps 1 blue flag but this shouldn't end the game
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		
		game.move(PieceType.MARSHAL, everySpace[41], everySpace[40]);
		game.move(PieceType.MINER, everySpace[60], everySpace[70]);
		game.move(PieceType.MARSHAL, everySpace[40], everySpace[30]);
		game.move(PieceType.MINER, everySpace[70], everySpace[80]);
		
		// 2nd flag captured
		res = game.move(PieceType.MARSHAL, everySpace[30], everySpace[20]);
		
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(bluePieces[38], everySpace[20]));
		assertNull(game.getPieceAt(everySpace[30]));
		assertEquals(game.getPieceAt(everySpace[20]), bluePieces[38]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveAfterFinish() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MINER, everySpace[59], everySpace[49]);
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		game.move(PieceType.MINER, everySpace[49], everySpace[39]);
		// 1 flag captured
		game.move(PieceType.MINER, everySpace[60], everySpace[70]);
		// blue captures 1 red flag but game should not be over
		game.move(PieceType.MINER, everySpace[39], everySpace[29]);
		
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[48]);
		game.move(PieceType.MINER, everySpace[29], everySpace[19]);
		game.move(PieceType.MARSHAL, everySpace[48], everySpace[58]);
		game.move(PieceType.MINER, everySpace[19], everySpace[9]);
		game.move(PieceType.MARSHAL, everySpace[58], everySpace[59]);
		game.move(PieceType.MINER, everySpace[9], everySpace[19]);
		game.move(PieceType.MARSHAL, everySpace[59], everySpace[69]);
		game.move(PieceType.MINER, everySpace[19], everySpace[29]);
		
		// second flag captured, game over
		game.move(PieceType.MARSHAL, everySpace[69], everySpace[79]);

		game.move(PieceType.MINER, everySpace[29], everySpace[39]);
	}
	
	@Test(expected=StrategyException.class)
	public void startGameAfterFinish() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MINER, everySpace[59], everySpace[49]);
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		game.move(PieceType.MINER, everySpace[49], everySpace[39]);
		// 1 flag captured
		game.move(PieceType.MINER, everySpace[60], everySpace[70]);
		// blue captures 1 red flag but game should not be over
		game.move(PieceType.MINER, everySpace[39], everySpace[29]);
		
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[48]);
		game.move(PieceType.MINER, everySpace[29], everySpace[19]);
		game.move(PieceType.MARSHAL, everySpace[48], everySpace[58]);
		game.move(PieceType.MINER, everySpace[19], everySpace[9]);
		game.move(PieceType.MARSHAL, everySpace[58], everySpace[59]);
		game.move(PieceType.MINER, everySpace[9], everySpace[19]);
		game.move(PieceType.MARSHAL, everySpace[59], everySpace[69]);
		game.move(PieceType.MINER, everySpace[19], everySpace[29]);
		
		// second flag captured, game over
		game.move(PieceType.MARSHAL, everySpace[69], everySpace[79]);

		game.startGame();
	}
	
	@Test
	public void RedRepRuleTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		MoveResult res;

		game.move(PieceType.FIRST_LIEUTENANT, everySpace[35], everySpace[45]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[54]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[45], everySpace[35]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[54], everySpace[64]);
		
		res = game.move(PieceType.FIRST_LIEUTENANT, everySpace[35], everySpace[45]);
		
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
	}
	
	@Test
	public void BlueRepRuleTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		MoveResult res;

		game.move(PieceType.FIRST_LIEUTENANT, everySpace[35], everySpace[45]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[54]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[45], everySpace[35]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[54], everySpace[64]);
		game.move(PieceType.LIEUTENANT, everySpace[34], everySpace[44]);
		
		res = game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[54]);
		
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
	}
	
	@Test
	public void battleDrawTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		MoveResult res;

		game.move(PieceType.FIRST_LIEUTENANT, everySpace[35], everySpace[45]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[54]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[45], everySpace[55]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[54], everySpace[55]);
		
		game.move(PieceType.SCOUT, everySpace[25], everySpace[55]);
		game.move(PieceType.SCOUT, everySpace[74], everySpace[54]);
		
		res = game.move(PieceType.SCOUT, everySpace[55], everySpace[54]);
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertNull(game.getPieceAt(everySpace[54]));
		assertNull(game.getPieceAt(everySpace[55]));
	}
	
	@Test
	public void battleDefenderWinsTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		
		game.startGame();
		
		MoveResult res;

		game.move(PieceType.FIRST_LIEUTENANT, everySpace[35], everySpace[45]);
		game.move(PieceType.LIEUTENANT, everySpace[65], everySpace[55]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[45], everySpace[55]);
		game.move(PieceType.FIRST_LIEUTENANT, everySpace[64], everySpace[54]);
		
		game.move(PieceType.SCOUT, everySpace[25], everySpace[55]);
		game.move(PieceType.SERGEANT, everySpace[75], everySpace[65]);
		
		res = game.move(PieceType.SCOUT, everySpace[55], everySpace[65]);
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(bluePieces[24], everySpace[55]));
		assertEquals(game.getPieceAt(everySpace[55]), bluePieces[24]);
		assertNull(game.getPieceAt(everySpace[65]));
	}
	
	@Test
	public void NoBlueMovablePiecesLeft() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		game.startGame();
		
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[48]);
		game.move(PieceType.SPY, everySpace[68], everySpace[58]);
		game.move(PieceType.MARSHAL, everySpace[48], everySpace[58]);
		game.move(PieceType.MINER, everySpace[69], everySpace[68]);
		game.move(PieceType.MARSHAL, everySpace[58], everySpace[68]);
		game.move(PieceType.SCOUT, everySpace[78], everySpace[68]);
		game.move(PieceType.MARSHAL, everySpace[78], everySpace[88]);
		game.move(PieceType.SERGEANT, everySpace[89], everySpace[88]);
		game.move(PieceType.MARSHAL, everySpace[89], everySpace[99]);
		game.move(PieceType.MAJOR, everySpace[98], everySpace[99]);
		game.move(PieceType.MARSHAL, everySpace[98], everySpace[97]);
		game.move(PieceType.SERGEANT, everySpace[87], everySpace[97]);
		game.move(PieceType.MARSHAL, everySpace[87], everySpace[77]);
		game.move(PieceType.CAPTAIN, everySpace[67], everySpace[77]);
		game.move(PieceType.MARSHAL, everySpace[67], everySpace[66]);
		game.move(PieceType.SCOUT, everySpace[76], everySpace[66]);
		game.move(PieceType.MARSHAL, everySpace[76], everySpace[86]);
		game.move(PieceType.CAPTAIN, everySpace[96], everySpace[86]);
		game.move(PieceType.MARSHAL, everySpace[96], everySpace[95]);
		game.move(PieceType.CAPTAIN, everySpace[94], everySpace[95]);
		game.move(PieceType.MARSHAL, everySpace[94], everySpace[84]);
		game.move(PieceType.MINER, everySpace[85], everySpace[84]);
		game.move(PieceType.MARSHAL, everySpace[85], everySpace[75]);
		game.move(PieceType.LIEUTENANT, everySpace[65], everySpace[75]);
		game.move(PieceType.MARSHAL, everySpace[65], everySpace[64]);
		game.move(PieceType.SCOUT, everySpace[74], everySpace[64]);
		game.move(PieceType.MARSHAL, everySpace[74], everySpace[84]);
		game.move(PieceType.MINER, everySpace[83], everySpace[84]);
		game.move(PieceType.MARSHAL, everySpace[83], everySpace[73]);
		game.move(PieceType.COLONEL, everySpace[63], everySpace[73]);
		game.move(PieceType.MARSHAL, everySpace[63], everySpace[62]);
		game.move(PieceType.SCOUT, everySpace[72], everySpace[62]);
		game.move(PieceType.MARSHAL, everySpace[72], everySpace[82]);
		
		game.move(PieceType.MARSHAL, everySpace[61], everySpace[62]); // blue
		game.move(PieceType.MARSHAL, everySpace[82], everySpace[81]); // red
		game.move(PieceType.MARSHAL, everySpace[62], everySpace[72]); // blue
		game.move(PieceType.MARSHAL, everySpace[81], everySpace[80]); // red 
		game.move(PieceType.MARSHAL, everySpace[72], everySpace[82]); // blue 
		game.move(PieceType.MARSHAL, everySpace[80], everySpace[81]); // red
		
		final MoveResult res = game.move(PieceType.MARSHAL, everySpace[82], everySpace[81]); // blue
		
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
	}
	
	@Test
	public void NoBlueMovablePiecesLeft2() throws StrategyException {
		// remove blue bomb
		blueCollection.remove(new PieceLocationDescriptor(bluePieces[28], everySpace[71]));
		// replace it with the missing scout that was replaced from additional flag
		blueCollection.add(new PieceLocationDescriptor(bluePieces[21], everySpace[71]));
		
		// we should have to get rid of 1 more movable piece before the game ends this time
		
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		game.startGame();
		
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[48]);
		game.move(PieceType.SPY, everySpace[68], everySpace[58]);
		game.move(PieceType.MARSHAL, everySpace[48], everySpace[58]);
		game.move(PieceType.MINER, everySpace[69], everySpace[68]);
		game.move(PieceType.MARSHAL, everySpace[58], everySpace[68]);
		game.move(PieceType.SCOUT, everySpace[78], everySpace[68]);
		game.move(PieceType.MARSHAL, everySpace[78], everySpace[88]);
		game.move(PieceType.SERGEANT, everySpace[89], everySpace[88]);
		game.move(PieceType.MARSHAL, everySpace[89], everySpace[99]);
		game.move(PieceType.MAJOR, everySpace[98], everySpace[99]);
		game.move(PieceType.MARSHAL, everySpace[98], everySpace[97]);
		game.move(PieceType.SERGEANT, everySpace[87], everySpace[97]);
		game.move(PieceType.MARSHAL, everySpace[87], everySpace[77]);
		game.move(PieceType.CAPTAIN, everySpace[67], everySpace[77]);
		game.move(PieceType.MARSHAL, everySpace[67], everySpace[66]);
		game.move(PieceType.SCOUT, everySpace[76], everySpace[66]);
		game.move(PieceType.MARSHAL, everySpace[76], everySpace[86]);
		game.move(PieceType.CAPTAIN, everySpace[96], everySpace[86]);
		game.move(PieceType.MARSHAL, everySpace[96], everySpace[95]);
		game.move(PieceType.CAPTAIN, everySpace[94], everySpace[95]);
		game.move(PieceType.MARSHAL, everySpace[94], everySpace[84]);
		game.move(PieceType.MINER, everySpace[85], everySpace[84]);
		game.move(PieceType.MARSHAL, everySpace[85], everySpace[75]);
		game.move(PieceType.LIEUTENANT, everySpace[65], everySpace[75]);
		game.move(PieceType.MARSHAL, everySpace[65], everySpace[64]);
		game.move(PieceType.SCOUT, everySpace[74], everySpace[64]);
		game.move(PieceType.MARSHAL, everySpace[74], everySpace[84]);
		game.move(PieceType.MINER, everySpace[83], everySpace[84]);
		game.move(PieceType.MARSHAL, everySpace[83], everySpace[73]);
		game.move(PieceType.COLONEL, everySpace[63], everySpace[73]);
		game.move(PieceType.MARSHAL, everySpace[63], everySpace[62]);
		game.move(PieceType.SCOUT, everySpace[72], everySpace[62]);
		game.move(PieceType.MARSHAL, everySpace[72], everySpace[82]);
		game.move(PieceType.LIEUTENANT, everySpace[81], everySpace[82]);
		game.move(PieceType.MARSHAL, everySpace[81], everySpace[80]);
		
		game.move(PieceType.SCOUT, everySpace[71], everySpace[81]);
		game.move(PieceType.MARSHAL, everySpace[80], everySpace[81]); // red
		game.move(PieceType.MARSHAL, everySpace[61], everySpace[71]); // blue
		
		final MoveResult res = game.move(PieceType.MARSHAL, everySpace[81], everySpace[71]); // red
		
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
	}
	
	@Test
	public void NoRedMovablePiecesLeft() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		game.startGame();
		
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[48]);
		
		game.move(PieceType.MARSHAL, everySpace[61], everySpace[51]);
		game.move(PieceType.SPY, everySpace[31], everySpace[41]);
		game.move(PieceType.MARSHAL, everySpace[51], everySpace[41]);
		game.move(PieceType.MINER, everySpace[30], everySpace[31]);
		game.move(PieceType.MARSHAL, everySpace[41], everySpace[31]);
		game.move(PieceType.SCOUT, everySpace[21], everySpace[31]);
		game.move(PieceType.MARSHAL, everySpace[21], everySpace[11]);
		game.move(PieceType.SERGEANT, everySpace[10], everySpace[11]);
		game.move(PieceType.MARSHAL, everySpace[10], everySpace[0]);
		game.move(PieceType.MAJOR, everySpace[1], everySpace[0]);
		game.move(PieceType.MARSHAL, everySpace[1], everySpace[2]);
		game.move(PieceType.SERGEANT, everySpace[12], everySpace[2]);
		game.move(PieceType.MARSHAL, everySpace[12], everySpace[22]);
		game.move(PieceType.CAPTAIN, everySpace[32], everySpace[22]);
		game.move(PieceType.MARSHAL, everySpace[32], everySpace[33]);
		game.move(PieceType.SCOUT, everySpace[23], everySpace[33]);
		game.move(PieceType.MARSHAL, everySpace[23], everySpace[13]);
		game.move(PieceType.CAPTAIN, everySpace[3], everySpace[13]);
		game.move(PieceType.MARSHAL, everySpace[3], everySpace[4]);
		game.move(PieceType.CAPTAIN, everySpace[5], everySpace[4]);
		game.move(PieceType.MARSHAL, everySpace[5], everySpace[15]);
		game.move(PieceType.MINER, everySpace[14], everySpace[15]);
		game.move(PieceType.MARSHAL, everySpace[14], everySpace[24]);
		game.move(PieceType.LIEUTENANT, everySpace[34], everySpace[24]);
		game.move(PieceType.MARSHAL, everySpace[34], everySpace[35]);
		game.move(PieceType.SCOUT, everySpace[25], everySpace[35]);
		game.move(PieceType.MARSHAL, everySpace[25], everySpace[15]);
		game.move(PieceType.MINER, everySpace[16], everySpace[15]);
		game.move(PieceType.MARSHAL, everySpace[16], everySpace[26]);
		game.move(PieceType.COLONEL, everySpace[36], everySpace[26]);
		game.move(PieceType.MARSHAL, everySpace[36], everySpace[37]);
		game.move(PieceType.SCOUT, everySpace[27], everySpace[37]);
		game.move(PieceType.MARSHAL, everySpace[27], everySpace[17]);
		
		game.move(PieceType.MARSHAL, everySpace[48], everySpace[38]); // red
		
		game.move(PieceType.MARSHAL, everySpace[17], everySpace[18]); // blue
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[37]); 
		game.move(PieceType.MARSHAL, everySpace[18], everySpace[19]); //blue
		game.move(PieceType.MARSHAL, everySpace[37], everySpace[27]);  
		game.move(PieceType.MARSHAL, everySpace[19], everySpace[18]); // blue
		game.move(PieceType.MARSHAL, everySpace[27], everySpace[17]);  
		
		final MoveResult res = game.move(PieceType.MARSHAL, everySpace[18], everySpace[17]); // blue
		
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
	}
	
	@Test
	public void GameDrawTest() throws StrategyException {
		game = gameFactory.makeEpsilonStrategyGame(redCollection, blueCollection, null);
		game.startGame();
		
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[48]);
		game.move(PieceType.SPY, everySpace[68], everySpace[58]);
		game.move(PieceType.MARSHAL, everySpace[48], everySpace[58]);
		game.move(PieceType.MINER, everySpace[69], everySpace[68]);
		game.move(PieceType.MARSHAL, everySpace[58], everySpace[68]);
		game.move(PieceType.SCOUT, everySpace[78], everySpace[68]);
		game.move(PieceType.MARSHAL, everySpace[78], everySpace[88]);
		game.move(PieceType.SERGEANT, everySpace[89], everySpace[88]);
		game.move(PieceType.MARSHAL, everySpace[89], everySpace[99]);
		game.move(PieceType.MAJOR, everySpace[98], everySpace[99]);
		game.move(PieceType.MARSHAL, everySpace[98], everySpace[97]);
		game.move(PieceType.SERGEANT, everySpace[87], everySpace[97]);
		game.move(PieceType.MARSHAL, everySpace[87], everySpace[77]);
		game.move(PieceType.CAPTAIN, everySpace[67], everySpace[77]);
		game.move(PieceType.MARSHAL, everySpace[67], everySpace[66]);
		game.move(PieceType.SCOUT, everySpace[76], everySpace[66]);
		game.move(PieceType.MARSHAL, everySpace[76], everySpace[86]);
		game.move(PieceType.CAPTAIN, everySpace[96], everySpace[86]);
		game.move(PieceType.MARSHAL, everySpace[96], everySpace[95]);
		game.move(PieceType.CAPTAIN, everySpace[94], everySpace[95]);
		game.move(PieceType.MARSHAL, everySpace[94], everySpace[84]);
		game.move(PieceType.MINER, everySpace[85], everySpace[84]);
		game.move(PieceType.MARSHAL, everySpace[85], everySpace[75]);
		game.move(PieceType.LIEUTENANT, everySpace[65], everySpace[75]);
		game.move(PieceType.MARSHAL, everySpace[65], everySpace[64]);
		game.move(PieceType.SCOUT, everySpace[74], everySpace[64]);
		game.move(PieceType.MARSHAL, everySpace[74], everySpace[84]);
		game.move(PieceType.MINER, everySpace[83], everySpace[84]);
		game.move(PieceType.MARSHAL, everySpace[83], everySpace[73]);
		game.move(PieceType.COLONEL, everySpace[63], everySpace[73]);
		game.move(PieceType.MARSHAL, everySpace[63], everySpace[62]);
		game.move(PieceType.SCOUT, everySpace[72], everySpace[62]);
		game.move(PieceType.MARSHAL, everySpace[72], everySpace[82]);
		game.move(PieceType.LIEUTENANT, everySpace[81], everySpace[82]);
		game.move(PieceType.MARSHAL, everySpace[81], everySpace[80]); // all but blue MARSHAL
		
		game.move(PieceType.MARSHAL, everySpace[61], everySpace[51]);
		game.move(PieceType.SPY, everySpace[31], everySpace[41]);
		game.move(PieceType.MARSHAL, everySpace[51], everySpace[41]);
		game.move(PieceType.MINER, everySpace[30], everySpace[31]);
		game.move(PieceType.MARSHAL, everySpace[41], everySpace[31]);
		game.move(PieceType.SCOUT, everySpace[21], everySpace[31]);
		game.move(PieceType.MARSHAL, everySpace[21], everySpace[11]);
		game.move(PieceType.SERGEANT, everySpace[10], everySpace[11]);
		game.move(PieceType.MARSHAL, everySpace[10], everySpace[0]);
		game.move(PieceType.MAJOR, everySpace[1], everySpace[0]);
		game.move(PieceType.MARSHAL, everySpace[1], everySpace[2]);
		game.move(PieceType.SERGEANT, everySpace[12], everySpace[2]);
		game.move(PieceType.MARSHAL, everySpace[12], everySpace[22]);
		game.move(PieceType.CAPTAIN, everySpace[32], everySpace[22]);
		game.move(PieceType.MARSHAL, everySpace[32], everySpace[33]);
		game.move(PieceType.SCOUT, everySpace[23], everySpace[33]);
		game.move(PieceType.MARSHAL, everySpace[23], everySpace[13]);
		game.move(PieceType.CAPTAIN, everySpace[3], everySpace[13]);
		game.move(PieceType.MARSHAL, everySpace[3], everySpace[4]);
		game.move(PieceType.CAPTAIN, everySpace[5], everySpace[4]);
		game.move(PieceType.MARSHAL, everySpace[5], everySpace[15]);
		game.move(PieceType.MINER, everySpace[14], everySpace[15]);
		game.move(PieceType.MARSHAL, everySpace[14], everySpace[24]);
		game.move(PieceType.LIEUTENANT, everySpace[34], everySpace[24]);
		game.move(PieceType.MARSHAL, everySpace[34], everySpace[35]);
		game.move(PieceType.SCOUT, everySpace[25], everySpace[35]);
		game.move(PieceType.MARSHAL, everySpace[25], everySpace[15]);
		game.move(PieceType.MINER, everySpace[16], everySpace[15]);
		game.move(PieceType.MARSHAL, everySpace[16], everySpace[26]);
		game.move(PieceType.COLONEL, everySpace[36], everySpace[26]);
		game.move(PieceType.MARSHAL, everySpace[36], everySpace[37]);
		game.move(PieceType.SCOUT, everySpace[27], everySpace[37]);
		game.move(PieceType.MARSHAL, everySpace[27], everySpace[17]);
		game.move(PieceType.LIEUTENANT, everySpace[18], everySpace[17]);
		game.move(PieceType.MARSHAL, everySpace[18], everySpace[19]); // All but red MARSHALL
		
		// MARSHALS go to kill each other
		game.move(PieceType.MARSHAL, everySpace[80], everySpace[81]); // red
		game.move(PieceType.MARSHAL, everySpace[19], everySpace[18]); // blue
		game.move(PieceType.MARSHAL, everySpace[81], everySpace[82]); // red
		game.move(PieceType.MARSHAL, everySpace[18], everySpace[17]); // blue
		game.move(PieceType.MARSHAL, everySpace[82], everySpace[83]); // red
		game.move(PieceType.MARSHAL, everySpace[17], everySpace[16]); // blue
		game.move(PieceType.MARSHAL, everySpace[83], everySpace[84]); // red
		game.move(PieceType.MARSHAL, everySpace[16], everySpace[15]); // blue
		game.move(PieceType.MARSHAL, everySpace[84], everySpace[85]); // red
		game.move(PieceType.MARSHAL, everySpace[15], everySpace[25]); // blue
		
		game.move(PieceType.MARSHAL, everySpace[85], everySpace[75]); // red
		game.move(PieceType.MARSHAL, everySpace[25], everySpace[35]); // blue
		game.move(PieceType.MARSHAL, everySpace[75], everySpace[65]); // red
		game.move(PieceType.MARSHAL, everySpace[35], everySpace[45]); // blue
		game.move(PieceType.MARSHAL, everySpace[65], everySpace[55]); // red
		
		final MoveResult res = game.move(PieceType.MARSHAL, everySpace[45], everySpace[55]);
		
		assertEquals(res.getStatus(), MoveResultStatus.DRAW);
	}
	
	
	/* ~~~~~~~~~~~~~~~~~~~~~~~~ USING MOCK ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	@Test(expected=StrategyException.class)
	public void moveScoutTestBlockedUp() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
	
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// red scout at 0,0 
		newBoard.put(everySpace[0], redPieces[21]);
		// red colonel at 0,3
		newBoard.put(everySpace[30], redPieces[0]);
		// blue Scout at 0.6
		newBoard.put(everySpace[60], bluePieces[21]);
		
		// use mock MockEpsilonStrategyGameController to set the board
		game = new MockEpsilonStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();
		
		// cannot move multiple spaces if there is a piece in the way
		game.move(PieceType.SCOUT, everySpace[0], everySpace[50]);
	}	

	@Test(expected=StrategyException.class)
	public void moveScoutTestBlockedDown() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// red scout at 0,0 
		newBoard.put(everySpace[0], redPieces[21]);
		// red colonel at 0,3
		newBoard.put(everySpace[30], redPieces[0]);
		// blue Scout at 0.6
		newBoard.put(everySpace[60], bluePieces[21]);
		
		// use mock MockEpsilonStrategyGameController to set the board
		game = new MockEpsilonStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();
		
		game.move(PieceType.COLONEL, everySpace[30], everySpace[20]);
		// cannot move multiple spaces if there is a piece in the way
		game.move(PieceType.SCOUT, everySpace[60], everySpace[10]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveScoutTestBlockedRight() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// red scout at 0,0 
		newBoard.put(everySpace[0], redPieces[21]);
		// red colonel at 4,0
		newBoard.put(everySpace[4], redPieces[0]);
		// blue Scout at 9,0
		newBoard.put(everySpace[9], bluePieces[21]);
		
		// use mock MockEpsilonStrategyGameController to set the board
		game = new MockEpsilonStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();

		// cannot move multiple spaces if there is a piece in the way
		game.move(PieceType.SCOUT, everySpace[0], everySpace[8]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveScoutTestBlockedLeft() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// red scout at 0,0 
		newBoard.put(everySpace[0], redPieces[21]);
		// red colonel at 4,0
		newBoard.put(everySpace[4], redPieces[0]);
		// blue Scout at 9,0
		newBoard.put(everySpace[9], bluePieces[21]);
		
		// use mock MockEpsilonStrategyGameController to set the board
		game = new MockEpsilonStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();

		game.move(PieceType.COLONEL, everySpace[4], everySpace[5]);
		// cannot move multiple spaces if there is a piece in the way
		game.move(PieceType.SCOUT, everySpace[9], everySpace[1]);
	}
	
	@Test
	public void moveScoutTestValidRight() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// red scout at 0,0 
		newBoard.put(everySpace[0], redPieces[21]);
		// red colonel at 4,0
		newBoard.put(everySpace[4], redPieces[0]);
		// blue Scout at 9,0
		newBoard.put(everySpace[9], bluePieces[21]);
		
		// use mock MockEpsilonStrategyGameController to set the board
		game = new MockEpsilonStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();

		game.move(PieceType.SCOUT, everySpace[0], everySpace[3]);
		assertNull(game.getPieceAt(everySpace[0]));
		assertEquals(game.getPieceAt(everySpace[3]), redPieces[21]);
	}
	
	@Test
	public void moveScoutTestValidLeft() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// red scout at 0,0 
		newBoard.put(everySpace[0], redPieces[21]);
		// red colonel at 4,0
		newBoard.put(everySpace[4], redPieces[0]);
		// blue Scout at 9,0
		newBoard.put(everySpace[9], bluePieces[21]);
		
		// use mock MockEpsilonStrategyGameController to set the board
		game = new MockEpsilonStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();

		game.move(PieceType.COLONEL, everySpace[4], everySpace[3]);
		game.move(PieceType.SCOUT, everySpace[9], everySpace[6]);
		
		assertNull(game.getPieceAt(everySpace[9]));
		assertEquals(game.getPieceAt(everySpace[6]), bluePieces[21]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveScoutDiagonally() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// red scout at 0,0 
		newBoard.put(everySpace[0], redPieces[21]);
		// red colonel at 4,0
		newBoard.put(everySpace[4], redPieces[0]);
		// blue Scout at 9,0
		newBoard.put(everySpace[9], bluePieces[21]);
		
		// use mock MockEpsilonStrategyGameController to set the board
		game = new MockEpsilonStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();

		game.move(PieceType.SCOUT, everySpace[0], everySpace[11]);
	}
	
	@Test(expected=StrategyException.class)
	public void ScoutMoveAndAttackTest() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// red scout at 0,0 
		newBoard.put(everySpace[0], redPieces[21]);
		// red colonel at 4,0
		newBoard.put(everySpace[4], redPieces[0]);
		// blue Scout at 9,0
		newBoard.put(everySpace[9], bluePieces[21]);
		
		// use mock MockEpsilonStrategyGameController to set the board
		game = new MockEpsilonStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();

		game.move(PieceType.SCOUT, everySpace[0], everySpace[3]);
		game.move(PieceType.SCOUT, everySpace[9], everySpace[4]);
		
	}
	
	
	
}
