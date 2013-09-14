package strategy.game.version.beta;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;






import strategy.common.*;
import strategy.game.*;
import strategy.game.common.*;


/**
 * 
 * @author Alex C
 *
 */
public class BetaStrategyTest {

	static private StrategyGameFactory gameFactory;
	private StrategyGameController game;
	private Collection<PieceLocationDescriptor> redCollection;
	private Collection<PieceLocationDescriptor> blueCollection;
	
	private static Location[] everySpace = new Location2D[36];  
	private static PieceType[] playerPieces = 
	{
			PieceType.FLAG,
			PieceType.MARSHAL,
			PieceType.COLONEL, PieceType.COLONEL,
			PieceType.CAPTAIN, PieceType.CAPTAIN, 
			PieceType.LIEUTENANT, PieceType.LIEUTENANT, PieceType.LIEUTENANT, 
			PieceType.SERGEANT, PieceType.SERGEANT, PieceType.SERGEANT,
	};
	
	private static Piece[] bluePieces = new Piece[12]; 
	private static Piece[] redPieces = new Piece[12];
	
	@BeforeClass
	public static void BetaTestSetup() {
		gameFactory = StrategyGameFactory.getInstance();
		
		int i = 0;
		int j;
		
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
			redPieces[i] = new Piece(playerPieces[i], PlayerColor.RED);
			bluePieces[i] = new Piece(playerPieces[i], PlayerColor.BLUE);	
			
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
		
		redOnePiece = new ArrayList<PieceLocationDescriptor>();
		blueOnePiece = new ArrayList<PieceLocationDescriptor>();
		
		redOnePiece.add(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		blueOnePiece.add(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		game = gameFactory.makeBetaStrategyGame(redOnePiece, blueOnePiece);
	}
	
	@Test(expected=StrategyException.class)
	public void InvalidPieceCombinationTest() throws StrategyException {
		Collection<PieceLocationDescriptor> invalidRed = redCollection;
		Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		// remove red and blue flags
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		// add an extra marshall where the flags would have been
		invalidRed.add(new PieceLocationDescriptor(redPieces[1], everySpace[0]));
		invalidBlue.add(new PieceLocationDescriptor(bluePieces[1], everySpace[35]));
		
		game = gameFactory.makeBetaStrategyGame(invalidRed, invalidBlue);
	}
	
	
	@Test(expected=StrategyException.class)
	public void InvalidPieceLayoutTest() throws StrategyException {
		Collection<PieceLocationDescriptor> invalidRed = redCollection;
		Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		// remove red and blue flags
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		// add red and blue flags on marshal location
		invalidRed.add(new PieceLocationDescriptor(redPieces[0], everySpace[1]));
		invalidBlue.add(new PieceLocationDescriptor(bluePieces[0], everySpace[34]));
		
		game = gameFactory.makeBetaStrategyGame(invalidRed, invalidBlue);
	} 
	
	@Test(expected=StrategyException.class)
	public void InvalidPieceLayoutTest2() throws StrategyException {
		Collection<PieceLocationDescriptor> invalidRed = redCollection;
		Collection<PieceLocationDescriptor> invalidBlue = blueCollection;
		
		// remove red and blue flags
		invalidRed.remove(new PieceLocationDescriptor(redPieces[0], everySpace[0]));
		invalidBlue.remove(new PieceLocationDescriptor(bluePieces[0], everySpace[35]));
		
		// add red and blue flags on middle spaces
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
	public void getPieceAtTest() throws StrategyException {
		game = gameFactory.makeBetaStrategyGame(redCollection, blueCollection);
		
		// Red Flag should be at location 0,0
		assertEquals(game.getPieceAt(new Location2D(0,0)), new Piece(playerPieces[0], PlayerColor.RED));
		// No piece at location 2,2
		assertNull(game.getPieceAt(new Location2D(2,2)));
	}
	
	

}
