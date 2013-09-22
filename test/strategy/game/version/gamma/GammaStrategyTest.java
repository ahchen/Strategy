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
}
