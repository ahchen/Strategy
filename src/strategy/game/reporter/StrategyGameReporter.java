/**
 * 
 */
package strategy.game.reporter;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import strategy.common.StrategyException;
import strategy.game.common.Location;
import strategy.game.common.MoveResult;
import strategy.game.common.MoveResultStatus;
import strategy.game.common.PieceLocationDescriptor;
import strategy.game.common.PieceType;
import strategy.game.common.StrategyGameObserver;

/**
 * @author Alex C
 * @version October 16, 2013
 */
public class StrategyGameReporter implements StrategyGameObserver {
	
	private final Writer writer;
	
	/**
	 * Public Constructor for Strategy Game Reporter
	 * @param writer A writer to be passed in so that console output can be written to this and later tested
	 */
	public StrategyGameReporter(Writer writer) {
		this.writer = writer;
	}

	/* 
	 * @see strategy.game.common.StrategyGameObserver#gameStart(java.util.Collection, java.util.Collection)
	 */
	@Override
	public void gameStart(Collection<PieceLocationDescriptor> redConfiguration,
			Collection<PieceLocationDescriptor> blueConfiguration) {
		
		System.out.println("Game Start Called.");
		System.out.println("Red's Initial piece configuration is:");
		
		for (PieceLocationDescriptor pieceLocDes : redConfiguration) {
			System.out.println(pieceLocDes.getPiece() + " at " + pieceLocDes.getLocation());
		}
		
		System.out.println("Blue's Initial piece configuration is:");
		
		for (PieceLocationDescriptor pieceLocDes : blueConfiguration) {
			System.out.println(pieceLocDes.getPiece() + " at " + pieceLocDes.getLocation());
		}
		
		
		// add the lines to the Writer as well so we can write tests for the output.
		try {
			writer.write("Game Start Called.\n");
			writer.append("Red's Initial piece configuration is:\n");
			
			for (PieceLocationDescriptor pieceLocDes : redConfiguration) {
				writer.append(pieceLocDes.getPiece() + " at " + pieceLocDes.getLocation() + "\n");
			}
			
			writer.append("Blue's Initial piece configuration is:");
			
			for (PieceLocationDescriptor pieceLocDes : blueConfiguration) {
				writer.append(pieceLocDes.getPiece() + " at " + pieceLocDes.getLocation() + "\n");
			}
			
		} catch (IOException e) {
			//  Writing to the Writer is only used for testing purposes.
			// if an IO exception occurs while doing this, just print the stack trace.
			e.printStackTrace();
		}

	}

	/* 
	 * @see strategy.game.common.StrategyGameObserver#moveHappened(strategy.game.common.PieceType, strategy.game.common.Location, strategy.game.common.Location, strategy.game.common.MoveResult, strategy.common.StrategyException)
	 */
	@Override
	public void moveHappened(PieceType piece, Location from, Location to,
			MoveResult result, StrategyException fault) {

		String message = ""; 
		
		if (piece == null && from == null && to == null) {
			message += "Player resigned. Result: " + result.getStatus() + "\n";
		}
		else if (fault == null) {
			message += piece.getPrintableName() + " moving from:" + from + " to:" + to + "\n";
			
			if (result.getStatus() != MoveResultStatus.OK) {
				message += "\tGame Over. Result: " + result.getStatus() + "\n";
			}
			else if (result.getBattleWinner() != null) {
				message += "\tMove/Battle successful. Result: " + result.getStatus() + " with piece " + result.getBattleWinner().getPiece() + " now at location " + result.getBattleWinner().getLocation() + "\n";
			}
			else {
				message += "\tBattle Draw. Both pieces removed.\n";
			}
		}
		else {
			message += "Exception thrown with message: " + fault.getMessage() + "\n";
		}
		
		System.out.print(message);
		
		try {
			writer.append(message);
		}
		catch (IOException e) {
			//  Writing to the Writer is only used for testing purposes.
			// if an IO exception occurs while doing this, just print the stack trace.
			e.printStackTrace();
		}

	}

}
