package strategy.game.version.beta;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;









import strategy.common.*;
import strategy.game.*;
import strategy.game.common.*;


/**
 * File to contain the tests for BetaStrategyGameController
 * @author Alex C
 *
 */
public class BetaStrategyTest {

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
		
		int j = 35;
		for (int i=0; i < 12; i++) {
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
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		assertNotNull(game);
	}
	
	@Test(expected=StrategyException.class)
	public void NullCollectionTest() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(null, null);
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
		
		game = gameFactory.makeBetaStrategyGame(redOnePiece, blueOnePiece);
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
		
		game = gameFactory.makeBetaStrategyGame(invalidRed, invalidBlue);
	}
	
	@Test(expected=StrategyException.class)
	public void InvalidBetaPieceTest() throws StrategyException {
		Collection<PieceLocationDescriptor> invalidRed = redCollection;
		Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		// remove a red and blue Lieutenant
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		// add a bomb and scout (invalid piece for beta)
		invalidRed.add(new PieceLocationDescriptor(new Piece(PieceType.BOMB, PlayerColor.RED), everySpace[0]));
		invalidBlue.add(new PieceLocationDescriptor(new Piece(PieceType.SCOUT, PlayerColor.BLUE), everySpace[35]));
		
		game = gameFactory.makeBetaStrategyGame(invalidRed, invalidBlue);
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
		
		game = gameFactory.makeBetaStrategyGame(invalidRed, invalidBlue);
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
		
		game = gameFactory.makeBetaStrategyGame(invalidRed, invalidBlue);
	}
	
	@Test(expected=StrategyException.class)
	public void StartGameBeforeFinishing() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.startGame();
	}
	
	@Test
	public void testGetPieceAt() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		// Red Flag should be at location 0,1
		assertEquals(game.getPieceAt(new Location2D(0,1)), new Piece(playerPieces[6], PlayerColor.RED));
		
		// No piece at location 2,2
		assertNull(game.getPieceAt(new Location2D(2,2)));
	}
	
	@Test(expected=StrategyException.class)
	public void moveBeforeStartGame() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		// move before game start
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
	}
	
	@Test(expected=StrategyException.class)
	public void moveFlag() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.FLAG, new Location2D(0,1), new Location2D(0,2));
	}
	
	@Test(expected=StrategyException.class)
	public void locationOffBoard() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.MARSHAL, new Location2D(6,6), new Location2D(10,1));
	}
	
	@Test(expected=StrategyException.class)
	public void moveInvalidFromLocation() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		// Marshal located at 1,1
		game.move(PieceType.MARSHAL, new Location2D(3,1), new Location2D(3,2));
	}
	
	@Test(expected=StrategyException.class)
	public void moveInvalidToLocation() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		// occupied by another same player piece
		game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(0,1));
	}
	
	@Test(expected=StrategyException.class)
	public void moveTwoSpacesTest() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.LIEUTENANT, new Location2D(4,1), new Location2D(4,3));
	}
	
	@Test(expected=StrategyException.class)
	public void moveSameSpaceTest() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.COLONEL, new Location2D(2,1), new Location2D(2,1));
	}
	
	@Test(expected=StrategyException.class)
	public void moveDiagonallyTest() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.CAPTAIN, new Location2D(3,1), new Location2D(4,2));
	}
	
	@Test
	public void validMove() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult mResult= game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(1,2));
		MoveResult expectedResult= new MoveResult(MoveResultStatus.OK, 
				new PieceLocationDescriptor(new Piece(PieceType.MARSHAL, PlayerColor.RED), new Location2D(1,2)));
				
		assertEquals(mResult.getBattleWinner(), expectedResult.getBattleWinner());
		assertEquals(mResult.getStatus(), expectedResult.getStatus());
	}
	
	@Test(expected=StrategyException.class)
	public void blueFirstMoveTest() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
	}
	
	@Test(expected=StrategyException.class)
	public void playerMoveTwiceTest() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		game.move(PieceType.LIEUTENANT, new Location2D(4,1), new Location2D(4,2));
		game.move(PieceType.LIEUTENANT, new Location2D(4,2), new Location2D(4,3));
	}
	
	@Test
	public void redWins() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		res = game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.SERGEANT, PlayerColor.RED), new Location2D(5,2)));
		
		res = game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.SERGEANT, PlayerColor.BLUE), new Location2D(0,3)));
		
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(5,3));
		game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(0,2));
		
		res = game.move(PieceType.SERGEANT, new Location2D(5,3), new Location2D(5,4));
		assertEquals(res.getStatus(), MoveResultStatus.RED_WINS);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.SERGEANT, PlayerColor.RED), new Location2D(5,4)));
	}
	
	@Test
	public void blueWins() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(5,3));
		game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(0,2));
		game.move(PieceType.SERGEANT, new Location2D(5,3), new Location2D(5,2));
		
		res = game.move(PieceType.SERGEANT, new Location2D(0,2), new Location2D(0,1));
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.SERGEANT, PlayerColor.BLUE), new Location2D(0,1)));
	}
	
	@Test(expected=StrategyException.class)
	public void moveAfterFinish() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();

		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(5,3));
		game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(0,2));
		game.move(PieceType.SERGEANT, new Location2D(5,3), new Location2D(5,4));
		
		game.move(PieceType.SERGEANT, new Location2D(0,2), new Location2D(0,3));
		
	}
	
	@Test
	public void drawAfterSixMoves() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(4,2));
		game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(1,3));
		
		game.move(PieceType.SERGEANT, new Location2D(4,2), new Location2D(3,2));
		game.move(PieceType.SERGEANT, new Location2D(1,3), new Location2D(2,3));
		
		game.move(PieceType.SERGEANT, new Location2D(3,2), new Location2D(2,2));
		game.move(PieceType.SERGEANT, new Location2D(2,3), new Location2D(3,3));

		game.move(PieceType.SERGEANT, new Location2D(2,2), new Location2D(1,2));
		game.move(PieceType.SERGEANT, new Location2D(3,3), new Location2D(4,3));
		
		game.move(PieceType.SERGEANT, new Location2D(1,2), new Location2D(0,2));
		
		res = game.move(PieceType.SERGEANT, new Location2D(4,3), new Location2D(5,3));
		assertEquals(res.getStatus(), MoveResultStatus.DRAW);
		assertNull(res.getBattleWinner());		
	}
	
	@Test
	public void winOnSixthMove() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.COLONEL, new Location2D(3,4), new Location2D(3,3));
		
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(4,2));
		game.move(PieceType.COLONEL, new Location2D(3,3), new Location2D(2,3));
		
		game.move(PieceType.SERGEANT, new Location2D(4,2), new Location2D(3,2));
		game.move(PieceType.COLONEL, new Location2D(2,3), new Location2D(1,3));
		
		game.move(PieceType.SERGEANT, new Location2D(3,2), new Location2D(2,2));
		game.move(PieceType.COLONEL, new Location2D(1,3), new Location2D(0,3));

		game.move(PieceType.SERGEANT, new Location2D(2,2), new Location2D(1,2));
		game.move(PieceType.COLONEL, new Location2D(0,3), new Location2D(0,2));
		
		game.move(PieceType.SERGEANT, new Location2D(1,2), new Location2D(2,2));
		
		res = game.move(PieceType.COLONEL, new Location2D(0,2), new Location2D(0,1));
		assertEquals(res.getStatus(), MoveResultStatus.BLUE_WINS);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.COLONEL, PlayerColor.BLUE), new Location2D(0,1)));		
	}
	
	@Test
	public void battleTie() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.COLONEL, new Location2D(2,1), new Location2D(2,2));
		game.move(PieceType.COLONEL, new Location2D(3,4), new Location2D(3,3));
		
		game.move(PieceType.COLONEL, new Location2D(2,2), new Location2D(2,3));
		res = game.move(PieceType.COLONEL, new Location2D(3,3), new Location2D(2,3));
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertNull(res.getBattleWinner());		
	}
	
	@Test
	public void marshalBattle() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.MARSHAL, new Location2D(1,1), new Location2D(1,2));
		game.move(PieceType.LIEUTENANT, new Location2D(1,4), new Location2D(1,3));
		
		res = game.move(PieceType.MARSHAL, new Location2D(1,2), new Location2D(1,3));
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.MARSHAL, PlayerColor.RED), new Location2D(1,3)));		
	}
	
	@Test
	public void colonelLose() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.COLONEL, new Location2D(2,1), new Location2D(2,2));
		game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,3));
		
		game.move(PieceType.COLONEL, new Location2D(2,2), new Location2D(2,3));
		game.move(PieceType.MARSHAL, new Location2D(4,3), new Location2D(3,3));
		
		res = game.move(PieceType.COLONEL, new Location2D(2,3), new Location2D(3,3));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.MARSHAL, PlayerColor.BLUE), new Location2D(2,3)));		
	}
	
	@Test
	public void colonelWin() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.COLONEL, new Location2D(2,1), new Location2D(2,2));
		game.move(PieceType.CAPTAIN, new Location2D(2,4), new Location2D(2,3));
		
		res = game.move(PieceType.COLONEL, new Location2D(2,2), new Location2D(2,3));
		
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.COLONEL, PlayerColor.RED), new Location2D(2,3)));		
	}
	
	@Test
	public void captainLose() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.CAPTAIN, new Location2D(3,1), new Location2D(3,2));
		game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,3));
		
		game.move(PieceType.CAPTAIN, new Location2D(3,2), new Location2D(3,3));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));

		res = game.move(PieceType.CAPTAIN, new Location2D(3,3), new Location2D(4,3));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.MARSHAL, PlayerColor.BLUE), new Location2D(3,3)));		
	}
	
	@Test
	public void captainWin() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.CAPTAIN, new Location2D(3,1), new Location2D(3,2));
		game.move(PieceType.LIEUTENANT, new Location2D(1,4), new Location2D(1,3));
		
		game.move(PieceType.CAPTAIN, new Location2D(3,2), new Location2D(3,3));
		game.move(PieceType.LIEUTENANT, new Location2D(1,3), new Location2D(2,3));
		
		res = game.move(PieceType.CAPTAIN, new Location2D(3,3), new Location2D(2,3));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.CAPTAIN, PlayerColor.RED), new Location2D(2,3)));		
	}
	
	@Test
	public void lieutenantLose() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.LIEUTENANT, new Location2D(4,1), new Location2D(4,2));
		game.move(PieceType.MARSHAL, new Location2D(4,4), new Location2D(4,3));

		res = game.move(PieceType.LIEUTENANT, new Location2D(4,2), new Location2D(4,3));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.MARSHAL, PlayerColor.BLUE), new Location2D(4,3)));		
	}
	
	@Test
	public void lieutenantWin() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.LIEUTENANT, new Location2D(4,1), new Location2D(4,2));
		game.move(PieceType.SERGEANT, new Location2D(0,4), new Location2D(0,3));
		
		game.move(PieceType.LIEUTENANT, new Location2D(4,2), new Location2D(3,2));
		game.move(PieceType.SERGEANT, new Location2D(0,3), new Location2D(1,3));
		
		game.move(PieceType.LIEUTENANT, new Location2D(3,2), new Location2D(2,2));
		game.move(PieceType.SERGEANT, new Location2D(1,3), new Location2D(2,3));
		
		res = game.move(PieceType.LIEUTENANT, new Location2D(2,2), new Location2D(2,3));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.LIEUTENANT, PlayerColor.RED), new Location2D(2,3)));		
	}
	
	@Test 
	public void sergeantBattle() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		game.startGame();
		
		MoveResult res;
		
		game.move(PieceType.SERGEANT, new Location2D(5,1), new Location2D(5,2));
		game.move(PieceType.COLONEL, new Location2D(3,4), new Location2D(3,3));
		
		game.move(PieceType.SERGEANT, new Location2D(5,2), new Location2D(4,2));
		game.move(PieceType.COLONEL, new Location2D(3,3), new Location2D(4,3));

		res = game.move(PieceType.SERGEANT, new Location2D(4,2), new Location2D(4,3));
		assertEquals(res.getStatus(), MoveResultStatus.OK);
		assertEquals(res.getBattleWinner(), new PieceLocationDescriptor(new Piece(PieceType.COLONEL, PlayerColor.BLUE), new Location2D(4,2)));
	}

}
