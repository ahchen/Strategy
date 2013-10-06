package strategy.game.version.gamma;

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

public class GammaStrategyTest {
	
	static private StrategyGameFactory gameFactory;
	private StrategyGameController game;
	private Collection<PieceLocationDescriptor> redCollection;
	private Collection<PieceLocationDescriptor> blueCollection;
	
	private static Location[] everySpace = new Location2D[36];  
	// define pieces in certain configuration
	private static PieceType[] playerPieces = 
	{
		PieceType.LIEUTENANT, PieceType.LIEUTENANT, 
		PieceType.SERGEANT, PieceType.SERGEANT,
		PieceType.COLONEL,
		PieceType.CAPTAIN,
		PieceType.FLAG,
		PieceType.MARSHAL,
		PieceType.COLONEL,
		PieceType.CAPTAIN, 
		PieceType.LIEUTENANT,
		PieceType.SERGEANT, 
	};
	
	/*
	 * The board with the initial configuration looks like this:
	 *   |  0  |  1  |  2  |  3  |  4  |  5  |
	 * - +-----+-----+-----+-----+-----+-----+
	 * 5 | CPT | COL | SGT | SGT | LT  | LT  |
	 * - +-----+-----+-----+-----+-----+-----+
	 * 4 | SGT | LT  | CPT | COL | MAR |  F  |
	 * - +-----+-----+-----+-----+-----+-----+
	 * 3 |     |     |CHOKE|CHOKE|     |     |
	 * - +-----+-----+-----+-----+-----+-----+
	 * 2 |     |     |CHOKE|CHOKE|     |     |
	 * - +-----+-----+-----+-----+-----+-----+
	 * 1 |  F  | MAR | COL | CPT | LT  | SGT |
	 * - +-----+-----+-----+-----+-----+-----+
	 * 0 | LT  | LT  | SGT | SGT | COL | CPT |
	 * - +-----+-----+-----+-----+-----+-----+
	 *   |  0  |  1  |  2  |  3  |  4  |  5  |
	 */
	
	private static Piece[] bluePieces = new Piece[12]; 
	private static Piece[] redPieces = new Piece[12];
	
	@BeforeClass
	public static void BetaTestSetup() {
		gameFactory = StrategyGameFactory.getInstance();
		
		int i = 0;
		int j;
		
		// fill in location array with every space on board
		for (j = 0; j < 6; j++) {
			for (int k = 0; k < 6; k++) {
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
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		assertNotNull(game);
	}
	
	@Test(expected=StrategyException.class)
	public void NullCollectionTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(null, null); 
	}
	
	@Test(expected=StrategyException.class)
	public void NotEnoughPiecesTest() throws StrategyException {
		Collection<PieceLocationDescriptor> redOnePiece, blueOnePiece;
		
		// create new collection
		redOnePiece = new ArrayList<PieceLocationDescriptor>();
		blueOnePiece = new ArrayList<PieceLocationDescriptor>();
		
		// add one piece to the collection
		redOnePiece.add(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		blueOnePiece.add(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		game = gameFactory.makeGammaStrategyGame(redOnePiece, blueOnePiece);
	} 
	
	@Test(expected=StrategyException.class)
	public void InvalidPieceCombinationTest() throws StrategyException {
		Collection<PieceLocationDescriptor> invalidRed = redCollection;
		Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		// remove a red and blue Lieutenant
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		// add an extra sergeant where the Lieutenant would have been
		invalidRed.add(new PieceLocationDescriptor(redPieces[2], everySpace[0]));
		invalidBlue.add(new PieceLocationDescriptor(bluePieces[2], everySpace[35]));
		
		game = gameFactory.makeGammaStrategyGame(invalidRed, invalidBlue);
	} 
	
	@Test(expected=StrategyException.class)
	public void InvalidGammaPieceTest() throws StrategyException {
		Collection<PieceLocationDescriptor> invalidRed = redCollection;
		Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		// remove a red and blue Lieutenant
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		// add a bomb and scout (invalid piece for beta)
		invalidRed.add(new PieceLocationDescriptor(new Piece(PieceType.BOMB, PlayerColor.RED), everySpace[0]));
		invalidBlue.add(new PieceLocationDescriptor(new Piece(PieceType.SCOUT, PlayerColor.BLUE), everySpace[35]));
		
		game = gameFactory.makeGammaStrategyGame(invalidRed, invalidBlue);
	}
	
	
	@Test(expected=StrategyException.class)
	public void InvalidPieceLayoutTest() throws StrategyException {
		Collection<PieceLocationDescriptor> invalidRed = redCollection;
		Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		// remove a red and blue Lieutenant
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		// add a red and blue Lieutenant on marshal location
		invalidRed.add(new PieceLocationDescriptor(redPieces[0], everySpace[1]));
		invalidBlue.add(new PieceLocationDescriptor(bluePieces[0], everySpace[34]));
		
		game = gameFactory.makeGammaStrategyGame(invalidRed, invalidBlue);
	} 
	
	@Test(expected=StrategyException.class)
	public void InvalidPieceLayoutTest2() throws StrategyException {
		Collection<PieceLocationDescriptor> invalidRed = redCollection;
		Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		// remove a red and blue Lieutenant
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		// add red and blue Lieutenant on middle spaces
		invalidRed.add(new PieceLocationDescriptor(redPieces[0], everySpace[14]));
		invalidBlue.add(new PieceLocationDescriptor(bluePieces[0], everySpace[20]));
		
		game = gameFactory.makeGammaStrategyGame(invalidRed, invalidBlue);
	}

	
	@Test
	public void ChokePointLocationTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		assertEquals(game.getPieceAt(new Location2D(2,2)).getType(), PieceType.CHOKE_POINT);
		assertEquals(game.getPieceAt(new Location2D(2,3)).getType(), PieceType.CHOKE_POINT);
		assertEquals(game.getPieceAt(new Location2D(3,2)).getType(), PieceType.CHOKE_POINT);
		assertEquals(game.getPieceAt(new Location2D(3,3)).getType(), PieceType.CHOKE_POINT);
	}
	
	@Test(expected=StrategyException.class)
	public void StartGameTwiceTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		game.startGame();
		game.startGame();
	}
	
	@Test(expected=StrategyException.class)
	public void moveBeforeStartGame() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		// move before game start
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
	}
	
	@Test(expected=StrategyException.class)
	public void moveFlag() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.FLAG, new Location2D(0,1), new Location2D(0,2));
	}
	
	@Test(expected=StrategyException.class)
	public void locationOffBoard() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.MARSHAL, new Location2D(6,6), new Location2D(10,1));
	}
	
	@Test(expected=StrategyException.class)
	public void moveInvalidFromLocation() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		// Marshal located at 1,1
		game.move(PieceType.MARSHAL, new Location2D(3,1), new Location2D(3,2));
	}
	
	@Test(expected=StrategyException.class)
	public void moveInvalidToLocation() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		// occupied by another same player piece
		game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(0,1));
	}
	
	@Test(expected=StrategyException.class)
	public void moveTwoSpacesTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.LIEUTENANT, new Location2D(4,1), new Location2D(4,3));
	}
	
	@Test(expected=StrategyException.class)
	public void moveSameSpaceTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.COLONEL, new Location2D(2,1), new Location2D(2,1));
	}
	
	@Test(expected=StrategyException.class)
	public void moveDiagonallyTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.CAPTAIN, new Location2D(3,1), new Location2D(4,2));
	}
	
	@Test(expected=StrategyException.class)
	public void blueFirstMoveTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
	}
	
	@Test(expected=StrategyException.class)
	public void playerMoveTwiceTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.LIEUTENANT, new Location2D(4,1), new Location2D(4,2));
		game.move(PieceType.LIEUTENANT, new Location2D(4,2), new Location2D(4,3));
	} 
	
	@Test(expected=StrategyException.class)
	public void moveChokePointTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.CHOKE_POINT, new Location2D(2,2), new Location2D(1,2));
	} 
	
	@Test
	public void validMove() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult mResult= game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(1,2));
		MoveResult expectedResult= new MoveResult(MoveResultStatus.OK, 
				new PieceLocationDescriptor(new Piece(PieceType.MARSHAL, PlayerColor.RED), new Location2D(1,2)));
		
		// no piece at old location
		assertNull(game.getPieceAt(new Location2D(1,1)));
		// piece at new location
		assertEquals(game.getPieceAt(new Location2D(1,2)), new Piece(PieceType.MARSHAL, PlayerColor.RED));
		//verify MoveResult
		assertEquals(mResult.getBattleWinner(), expectedResult.getBattleWinner());
		assertEquals(mResult.getStatus(), expectedResult.getStatus());
	}
	
	@Test
	public void redWins() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(5,3));
		game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(0,2));
		
		// flag captured
		res = game.move(PieceType.SERGEANT, new Location2D(5,3), new Location2D(5,4));
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.SERGEANT, PlayerColor.RED), new Location2D(5,4)));
	}
	
	@Test
	public void blueWins() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(5,3));
		game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(0,2));
		game.move(PieceType.SERGEANT, new Location2D(5,3), new Location2D(5,2));
		
		// flag captured
		res = game.move(PieceType.SERGEANT, new Location2D(0,2), new Location2D(0,1));
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.SERGEANT, PlayerColor.BLUE), new Location2D(0,1)));
	}
	
	@Test(expected=StrategyException.class)
	public void moveAfterFinish() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(5,3));
		game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(0,2));
		// red captures flag in this move, game over
		game.move(PieceType.SERGEANT, new Location2D(5,3), new Location2D(5,4));
		
		// move after gameinvalid
		game.move(PieceType.SERGEANT, new Location2D(0,2), new Location2D(0,3));
	}
	
	@Test(expected=StrategyException.class)
	public void startGameAfterFinish() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(5,3));
		game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(0,2));
		// red captures flag in this move, game over
		game.move(PieceType.SERGEANT, new Location2D(5,3), new Location2D(5,4));
	
		// start game after a finish
		game.startGame();
		// make sure moves don't work
		assertNotNull(game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2)));
	}
	
	@Test(expected=StrategyException.class)
	public void moveToChokePointTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		game.move(PieceType.COLONEL, new Location2D(2,1), new Location2D(2,2));
	}
	
	@Test
	public void battleTie() throws StrategyException {
		// switch the marshal and lieutenant pieces for red configuration to make a tie battle easier
		redCollection.remove(new PieceLocationDescriptor(redPieces[7], everySpace[7]));
		redCollection.remove(new PieceLocationDescriptor(redPieces[10], everySpace[10]));
		
		redCollection.add(new PieceLocationDescriptor(redPieces[7], everySpace[10]));
		redCollection.add(new PieceLocationDescriptor(redPieces[10], everySpace[7]));
		
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		MoveResult res;
		
		game.move(PieceType.MARSHAL, new Location2D(4,1), new Location2D(4,2));
		game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,3));
		res = game.move(PieceType.MARSHAL, new Location2D(4,2), new Location2D(4,3));
		
		// both pieces should be removed
		assertNull(game.getPieceAt(new Location2D(4,2)));
		assertNull(game.getPieceAt(new Location2D(4,3)));
		
		// check moveresult
		assertNull(res.getBattleWinner());
		assertEquals(res.getStatus(), MoveResultStatus.OK);
	}
	
	@Test
	public void attackerWin() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		MoveResult res;
		
		game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(1,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.MARSHAL, new Location2D(1,2), new Location2D(1,3));
		game.move(PieceType.CAPTAIN, new Location2D(0,5), new Location2D(0,4));
		
		res = game.move(PieceType.MARSHAL, new Location2D(1,3), new Location2D(0,3));
		
		assertNull(game.getPieceAt(new Location2D(1,3)));
		assertEquals(game.getPieceAt(new Location2D(0,3)), new Piece(PieceType.MARSHAL, PlayerColor.RED));
		
		// check moveresult
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.MARSHAL, PlayerColor.RED), new Location2D(0,3)));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
	}
	
	@Test
	public void defenderWin() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		MoveResult res;
		
		game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(1,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.MARSHAL, new Location2D(1,2), new Location2D(1,3));
		
		res = game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(1,3));
		
		assertNull(game.getPieceAt(new Location2D(1,3)));
		
		assertEquals(game.getPieceAt(new Location2D(0,3)), new Piece(PieceType.MARSHAL, PlayerColor.RED));
		
		// check moveresult
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.MARSHAL, PlayerColor.RED), new Location2D(0,3)));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
	} 
	
	@Test
	public void redMoveRepetitionTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.LIEUTENANT, new Location2D(1,4), new Location2D(1,3));
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(5,1));
		game.move(PieceType.LIEUTENANT, new Location2D(1,3), new Location2D(1,2));
		res = game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
	}

	@Test
	public void blueMoveRepetitionTest() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		MoveResult res;
		
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.LIEUTENANT, new Location2D(1,4), new Location2D(1,3));
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(5,3));
		game.move(PieceType.LIEUTENANT, new Location2D(1,3), new Location2D(1,4));
		game.move(PieceType.SERGEANT, new Location2D(5,3), new Location2D(5,2));
		res = game.move(PieceType.LIEUTENANT, new Location2D(1,4), new Location2D(1,3));
		
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
	}

	@Test
	public void repetitonRuleOnStrike() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;

		game.move(PieceType.LIEUTENANT, new Location2D(4,1), new Location2D(4,2));
		game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,3));
		game.move(PieceType.LIEUTENANT, new Location2D(4,2), new Location2D(4,1));
		game.move(PieceType.MARSHAL, new Location2D(4,3), new Location2D(4,2));
		res = game.move(PieceType.LIEUTENANT, new Location2D(4,1), new Location2D(4,2));
		
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
		
	}

	@Test
	public void repetitonRuleWithStrikeMovingPiece() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;

		game.move(PieceType.LIEUTENANT, new Location2D(4,1), new Location2D(4,2));
		game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,3));
		game.move(PieceType.LIEUTENANT, new Location2D(4,2), new Location2D(4,3));
		// marshal piece forced to 4,2 (not moved there)
		game.move(PieceType.MARSHAL, new Location2D(4,2), new Location2D(4,3));
		game.move(PieceType.COLONEL, new Location2D(4,0), new Location2D(4,1));
		// moving back to 4,2 should be valid since it was forced there
		res = game.move(PieceType.MARSHAL, new Location2D(4,3), new Location2D(4,2));
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
	}

	@Test
	public void repetitonRuleWithStrikeMovingPiece2() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(1,2));
		game.move(PieceType.LIEUTENANT, new Location2D(1,4), new Location2D(1,3));
		// marshal moves to 1,3 and overtakes lieutenant position
		game.move(PieceType.MARSHAL, new Location2D(1,2), new Location2D(1,3));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.MARSHAL, new Location2D(1,3), new Location2D(1,2));
		game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(0,2));
		// should violate rule
		res = game.move(PieceType.MARSHAL, new Location2D(1,2), new Location2D(1,3));
		
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
	}
	
	@Test
	public void noBluePiecesLeft() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(1,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.MARSHAL, new Location2D(1,2), new Location2D(1,3));
		game.move(PieceType.CAPTAIN, new Location2D(0,5), new Location2D(0,4));
		
		game.move(PieceType.MARSHAL, new Location2D(1,3), new Location2D(0,3));
		game.move(PieceType.CAPTAIN, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.MARSHAL, new Location2D(0,4), new Location2D(1,4));
		game.move(PieceType.COLONEL, new Location2D(1,5), new Location2D(1,4));
		game.move(PieceType.MARSHAL, new Location2D(1,5), new Location2D(2,5));
		game.move(PieceType.CAPTAIN, new Location2D(2,4), new Location2D(2,5));
		game.move(PieceType.MARSHAL, new Location2D(2,4), new Location2D(3,4));
		game.move(PieceType.SERGEANT, new Location2D(3,5), new Location2D(3,4));
		game.move(PieceType.MARSHAL, new Location2D(3,5), new Location2D(4,5));
		game.move(PieceType.LIEUTENANT, new Location2D(5,5), new Location2D(4,5));
		game.move(PieceType.MARSHAL, new Location2D(5,5), new Location2D(4,5));
		
		res = game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,5));
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);		
	}
	
	@Test
	public void noRedPiecesLeft() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,3));
		game.move(PieceType.CAPTAIN, new Location2D(5,0), new Location2D(5,1));
		game.move(PieceType.MARSHAL, new Location2D(4,3), new Location2D(4,2));
		
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(4,2));
		game.move(PieceType.MARSHAL, new Location2D(5,2), new Location2D(5,1));
		game.move(PieceType.LIEUTENANT, new Location2D(4,1), new Location2D(5,1));
		game.move(PieceType.MARSHAL, new Location2D(4,1), new Location2D(4,0));
		game.move(PieceType.SERGEANT, new Location2D(3,0), new Location2D(4,0));
		game.move(PieceType.MARSHAL, new Location2D(3,0), new Location2D(3,1));
		game.move(PieceType.COLONEL, new Location2D(2,1), new Location2D(3,1));
		game.move(PieceType.MARSHAL, new Location2D(2,1), new Location2D(2,0));
		game.move(PieceType.LIEUTENANT, new Location2D(1,0), new Location2D(2,0));
		game.move(PieceType.MARSHAL, new Location2D(1,0), new Location2D(0,0));
		game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(1,0));
		
		res = game.move(PieceType.MARSHAL, new Location2D(0,0), new Location2D(1,0));
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);		
	}
	
	@Test
	public void noPiecesLeft() throws StrategyException {
		game = gameFactory.makeGammaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(1,2));
		game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,3));
		game.move(PieceType.MARSHAL, new Location2D(1,2), new Location2D(1,3));
		game.move(PieceType.MARSHAL, new Location2D(4,3), new Location2D(4,2));
		
		game.move(PieceType.MARSHAL, new Location2D(1,3), new Location2D(1,4));
		game.move(PieceType.MARSHAL, new Location2D(4,2), new Location2D(4,1));
		game.move(PieceType.MARSHAL, new Location2D(1,4), new Location2D(0,4));
		game.move(PieceType.MARSHAL, new Location2D(4,1), new Location2D(5,1));
		game.move(PieceType.MARSHAL, new Location2D(0,4), new Location2D(0,5));
		game.move(PieceType.MARSHAL, new Location2D(5,1), new Location2D(5,0));
		game.move(PieceType.MARSHAL, new Location2D(0,5), new Location2D(1,5));
		game.move(PieceType.MARSHAL, new Location2D(5,0), new Location2D(4,0));

		game.move(PieceType.MARSHAL, new Location2D(1,5), new Location2D(1,4));
		game.move(PieceType.MARSHAL, new Location2D(4,0), new Location2D(4,1));
		game.move(PieceType.MARSHAL, new Location2D(1,4), new Location2D(2,4));
		game.move(PieceType.MARSHAL, new Location2D(4,1), new Location2D(3,1));
		game.move(PieceType.MARSHAL, new Location2D(2,4), new Location2D(2,5));
		game.move(PieceType.MARSHAL, new Location2D(3,1), new Location2D(3,0));
		game.move(PieceType.MARSHAL, new Location2D(2,5), new Location2D(3,5));
		game.move(PieceType.MARSHAL, new Location2D(3,0), new Location2D(2,0));
		
		game.move(PieceType.MARSHAL, new Location2D(3,5), new Location2D(3,4));
		game.move(PieceType.MARSHAL, new Location2D(2,0), new Location2D(2,1));
		game.move(PieceType.MARSHAL, new Location2D(3,4), new Location2D(4,4));
		game.move(PieceType.MARSHAL, new Location2D(2,1), new Location2D(1,1));
		game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,5));
		game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(1,0));
		game.move(PieceType.MARSHAL, new Location2D(4,5), new Location2D(5,5));
		game.move(PieceType.MARSHAL, new Location2D(1,0), new Location2D(0,0));
		
		//at this point, all pieces except marshal and flags destroyed
		game.move(PieceType.MARSHAL, new Location2D(5,5), new Location2D(4,5));
		game.move(PieceType.MARSHAL, new Location2D(0,0), new Location2D(1,0));
		game.move(PieceType.MARSHAL, new Location2D(4,5), new Location2D(4,4));
		game.move(PieceType.MARSHAL, new Location2D(1,0), new Location2D(2,0));
		game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,3));
		game.move(PieceType.MARSHAL, new Location2D(2,0), new Location2D(3,0));
		game.move(PieceType.MARSHAL, new Location2D(4,3), new Location2D(4,2));
		game.move(PieceType.MARSHAL, new Location2D(3,0), new Location2D(4,0));
		game.move(PieceType.MARSHAL, new Location2D(4,2), new Location2D(4,1));
		
		res = game.move(PieceType.MARSHAL, new Location2D(4,0), new Location2D(4,1));
		assertEquals(res.getStatus(), MoveResultStatus.DRAW);
	}
	
}
