/**
 * 
 */
package strategy.game.version.delta;

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
 * @version October 8, 2013
 */
public class DeltaStrategyTest {

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
	public void CreateValidGameTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		assertNotNull(game);
	}
	
	@Test(expected=StrategyException.class)
	public void NullCollectionTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(null, null); 
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
		
		game = gameFactory.makeDeltaStrategyGame(redOnePiece, blueOnePiece);
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
		
		game = gameFactory.makeDeltaStrategyGame(invalidRed, invalidBlue);
	} 
	
	@Test(expected=StrategyException.class)
	public void InvalidPieceLayoutTest() throws StrategyException {
		final Collection<PieceLocationDescriptor> invalidRed = redCollection;
		final Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		invalidRed.add(new PieceLocationDescriptor(redPieces[0], everySpace[1]));
		invalidBlue.add(new PieceLocationDescriptor(bluePieces[0], everySpace[99]));
		
		game = gameFactory.makeDeltaStrategyGame(invalidRed, invalidBlue);
	} 
	
	@Test
	public void ChokePointLocationTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
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
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		game.startGame();
		game.startGame();
	}
	
	@Test(expected=StrategyException.class)
	public void moveBeforeStartGame() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		// move before game start
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
	}
	
	@Test(expected=StrategyException.class)
	public void moveFlag() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.FLAG, everySpace[29], everySpace[39]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveChokePointTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.CHOKE_POINT, everySpace[43], everySpace[44]);
	} 
	
	@Test(expected=StrategyException.class)
	public void locationOffBoard() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.MARSHAL, new Location2D(10,10), new Location2D(10,11));
	}
	
	@Test(expected=StrategyException.class)
	public void moveInvalidFromLocation() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		game.move(PieceType.MARSHAL, everySpace[39], everySpace[49]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveInvalidToLocation() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		// occupied by another same player piece
		game.move(PieceType.MARSHAL, everySpace[38], everySpace[39]);
	}

	@Test(expected=StrategyException.class)
	public void moveSameSpaceTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.MINER, everySpace[30], everySpace[31]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveTwoSpacesNotScoutTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.MAJOR, everySpace[35], everySpace[55]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveDiagonallyTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.MINER, everySpace[30], everySpace[41]);
	}
	
	@Test(expected=StrategyException.class)
	public void blueFirstMoveTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
	}
	
	@Test(expected=StrategyException.class)
	public void playerMoveTwiceTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveBombTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.BOMB, everySpace[39], everySpace[49]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveToChokePointTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.GENERAL, everySpace[37], everySpace[47]);
	}
	
	@Test
	public void moveScoutTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		final MoveResult res;
		
		res = game.move(PieceType.SCOUT, everySpace[34], everySpace[54]);
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.SCOUT, PlayerColor.RED), everySpace[54]));
		
		assertEquals(game.getPieceAt(everySpace[54]), new Piece(PieceType.SCOUT, PlayerColor.RED));
		assertNull(game.getPieceAt(everySpace[34]));
	}
	
	@Test(expected=StrategyException.class)
	public void moveScoutTest2() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		// cannot move multiple spaces and attack on the same move
		game.move(PieceType.SCOUT, everySpace[34], everySpace[64]);
	}
	
	@Test(expected=StrategyException.class)
	public void moveScoutTestBlockedUp() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
	
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// red scout at 0,0 
		newBoard.put(everySpace[0], redPieces[34]);
		// red colonel at 0,3
		newBoard.put(everySpace[30], redPieces[0]);
		// blue Scout at 0.6
		newBoard.put(everySpace[60], bluePieces[34]);
		
		// use mock DeltaStrategyGameController to set the board
		game = new MockDeltaStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
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
		newBoard.put(everySpace[0], redPieces[34]);
		// red colonel at 0,3
		newBoard.put(everySpace[30], redPieces[0]);
		// blue Scout at 0.6
		newBoard.put(everySpace[60], bluePieces[34]);
		
		// use mock DeltaStrategyGameController to set the board
		game = new MockDeltaStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
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
		newBoard.put(everySpace[0], redPieces[34]);
		// red colonel at 4,0
		newBoard.put(everySpace[4], redPieces[0]);
		// blue Scout at 9,0
		newBoard.put(everySpace[9], bluePieces[34]);
		
		// use mock DeltaStrategyGameController to set the board
		game = new MockDeltaStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
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
		newBoard.put(everySpace[0], redPieces[34]);
		// red colonel at 4,0
		newBoard.put(everySpace[4], redPieces[0]);
		// blue Scout at 9,0
		newBoard.put(everySpace[9], bluePieces[34]);
		
		// use mock DeltaStrategyGameController to set the board
		game = new MockDeltaStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
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
		newBoard.put(everySpace[0], redPieces[34]);
		// red colonel at 4,0
		newBoard.put(everySpace[4], redPieces[0]);
		// blue Scout at 9,0
		newBoard.put(everySpace[9], bluePieces[34]);
		
		// use mock DeltaStrategyGameController to set the board
		game = new MockDeltaStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();

		game.move(PieceType.SCOUT, everySpace[0], everySpace[3]);
		assertNull(game.getPieceAt(everySpace[0]));
		assertEquals(game.getPieceAt(everySpace[3]), redPieces[34]);
	}
	
	@Test
	public void moveScoutTestValidLeft() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// red scout at 0,0 
		newBoard.put(everySpace[0], redPieces[34]);
		// red colonel at 4,0
		newBoard.put(everySpace[4], redPieces[0]);
		// blue Scout at 9,0
		newBoard.put(everySpace[9], bluePieces[34]);
		
		// use mock DeltaStrategyGameController to set the board
		game = new MockDeltaStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();

		game.move(PieceType.COLONEL, everySpace[4], everySpace[3]);
		game.move(PieceType.SCOUT, everySpace[9], everySpace[6]);
		
		assertNull(game.getPieceAt(everySpace[9]));
		assertEquals(game.getPieceAt(everySpace[6]), bluePieces[34]);
	}
	
	
	@Test(expected=StrategyException.class)
	public void moveScoutDiagonallyTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		// cannot move multiple spaces and attack on the same move
		game.move(PieceType.SCOUT, everySpace[34], everySpace[55]);
	}
	
	@Test
	public void minerAttacksBombTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
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
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
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
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
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
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
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
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
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
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
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
	public void redWins() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MINER, everySpace[59], everySpace[49]);
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		game.move(PieceType.MINER, everySpace[49], everySpace[39]);
		
		// flag captured
		res = game.move(PieceType.MINER, everySpace[60], everySpace[70]);
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.MINER, PlayerColor.RED), everySpace[70]));
	}
	
	@Test
	public void blueWins() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MINER, everySpace[59], everySpace[49]);
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		game.move(PieceType.MINER, everySpace[49], everySpace[39]);
		game.move(PieceType.MINER, everySpace[60], everySpace[50]);
		
		// flag captured
		res = game.move(PieceType.MINER, everySpace[39], everySpace[29]);
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.MINER, PlayerColor.BLUE), everySpace[29]));
	}
	
	
	@Test(expected=StrategyException.class)
	public void moveAfterFinish() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MINER, everySpace[59], everySpace[49]);
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		game.move(PieceType.MINER, everySpace[49], everySpace[39]);
		
		// flag captured
		game.move(PieceType.MINER, everySpace[60], everySpace[70]);
		
		// move after game invalid
		game.move(PieceType.MINER, everySpace[39], everySpace[29]);
	}
	
	@Test(expected=StrategyException.class)
	public void startGameAfterFinish() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		game.move(PieceType.MINER, everySpace[30], everySpace[40]);
		game.move(PieceType.MINER, everySpace[69], everySpace[59]);
		game.move(PieceType.MINER, everySpace[40], everySpace[50]);
		game.move(PieceType.MINER, everySpace[59], everySpace[49]);
		game.move(PieceType.MINER, everySpace[50], everySpace[60]);
		game.move(PieceType.MINER, everySpace[49], everySpace[39]);
		
		// flag captured
		game.move(PieceType.MINER, everySpace[60], everySpace[70]);
		
		// start game after a finish
		game.startGame();
		// make sure moves don't work
		game.move(PieceType.MINER, everySpace[39], everySpace[29]);
	}
	
	@Test
	public void battleTie() throws StrategyException {		
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		final MoveResult res;

		game.move(PieceType.SCOUT, everySpace[34], everySpace[54]);
		game.move(PieceType.SCOUT, everySpace[65], everySpace[55]);
		
		res = game.move(PieceType.SCOUT, everySpace[54], everySpace[55]);
		
		// both pieces should be removed
		assertNull(game.getPieceAt(everySpace[55]));
		assertNull(game.getPieceAt(everySpace[54]));
		
		// check moveresult
		assertNull(res.getBattleWinner());
		assertEquals(res.getStatus(), MoveResultStatus.OK);
	}
	
	@Test
	public void defenderWin() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		final MoveResult res;

		game.move(PieceType.SCOUT, everySpace[34], everySpace[54]);
		game.move(PieceType.SCOUT, everySpace[65], everySpace[55]);
		
		res = game.move(PieceType.SCOUT, everySpace[54], everySpace[64]);
		
		// scout loses
		assertNull(game.getPieceAt(everySpace[64]));
		
		// check moveresult
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(bluePieces[35], everySpace[54]));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
	} 
	
	@Test
	public void defenderWin2() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		final MoveResult res;

		game.move(PieceType.SCOUT, everySpace[34], everySpace[54]);
		game.move(PieceType.SCOUT, everySpace[65], everySpace[45]);
		game.move(PieceType.SCOUT, everySpace[54], everySpace[44]);
		
		res = game.move(PieceType.SCOUT, everySpace[45], everySpace[35]);
		
		// scout loses, major moves to scout space.
		assertNull(game.getPieceAt(everySpace[35]));
		
		// check moveresult
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(redPieces[35], everySpace[45]));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
	} 
	
	@Test
	public void redMoveRepetitionTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MAJOR, everySpace[35], everySpace[45]);
		game.move(PieceType.MAJOR, everySpace[64], everySpace[54]);
		game.move(PieceType.MAJOR, everySpace[45], everySpace[35]);
		game.move(PieceType.MAJOR, everySpace[54], everySpace[64]);
		
		res = game.move(PieceType.MAJOR, everySpace[35], everySpace[45]);
		
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
	}

	@Test
	public void blueMoveRepetitionTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.MAJOR, everySpace[35], everySpace[45]);
		game.move(PieceType.MAJOR, everySpace[64], everySpace[54]);
		game.move(PieceType.MAJOR, everySpace[45], everySpace[35]);
		game.move(PieceType.MAJOR, everySpace[54], everySpace[64]);
		game.move(PieceType.SCOUT, everySpace[34], everySpace[44]);
		
		res = game.move(PieceType.MAJOR, everySpace[64], everySpace[54]);
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
	}
	
	@Test
	public void scoutRepetitionTest() throws StrategyException {
		game = gameFactory.makeDeltaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		final MoveResult res;
		
		game.move(PieceType.SCOUT, everySpace[34], everySpace[54]);
		game.move(PieceType.SCOUT, everySpace[65], everySpace[55]);
		game.move(PieceType.SCOUT, everySpace[54], everySpace[34]);
		game.move(PieceType.SCOUT, everySpace[55], everySpace[45]);
		
		
		res = game.move(PieceType.SCOUT, everySpace[34], everySpace[54]);
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
	}
	
	
	@Test
	public void testDrawNoPiecesLeft() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// put 2 scouts right next to each other 
		newBoard.put(everySpace[44], redPieces[34]);
		newBoard.put(everySpace[45], bluePieces[34]);
		
		// use mock DeltaStrategyGameController to set the board with just 1 piece left per side
		game = new MockDeltaStrategyGameController(redCollection, blueCollection, newBoard, 1, 1);
		
		game.startGame();
		
		// have the 2 pieces kill each other
		final MoveResult res = game.move(PieceType.SCOUT, everySpace[44], everySpace[45]);
		
		assertEquals(res.getStatus(), MoveResultStatus.DRAW);
	}
	
	@Test
	public void noRedPiecesLeftTest() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// put 2 scouts right next to each other 
		newBoard.put(everySpace[44], redPieces[34]);
		newBoard.put(everySpace[45], bluePieces[34]);
		// add extra blue Marshal
		newBoard.put(everySpace[99], bluePieces[38]);
		
		// use mock DeltaStrategyGameController to set the board with just 1 red piece and 2 blue pieces
		game = new MockDeltaStrategyGameController(redCollection, blueCollection, newBoard, 1, 2);
		
		game.startGame();
		
		// have the 2 pieces kill each other
		final MoveResult res = game.move(PieceType.SCOUT, everySpace[44], everySpace[45]);
		
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
	}
	
	@Test
	public void noBluePiecesLeftTest() throws StrategyException {
		final Map<Location, Piece> newBoard = new HashMap<Location, Piece>();
		
		for (int i = 0; i < 100; i++) {
			newBoard.put(everySpace[i], null);
		}
		
		// put 2 scouts right next to each other 
		newBoard.put(everySpace[44], redPieces[34]);
		newBoard.put(everySpace[45], bluePieces[34]);
		// add extra blue Marshal
		newBoard.put(everySpace[0], redPieces[38]);
		
		// use mock DeltaStrategyGameController to set the board with just 1 red pieces and 1 blue piece
		game = new MockDeltaStrategyGameController(redCollection, blueCollection, newBoard, 2, 1);
		
		game.startGame();
		
		// have the 2 pieces kill each other
		final MoveResult res = game.move(PieceType.SCOUT, everySpace[44], everySpace[45]);
		
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
	}
	
	
	
}
