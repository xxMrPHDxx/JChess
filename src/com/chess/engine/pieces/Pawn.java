package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.move.Move;
import com.chess.engine.move.Move.PawnAttackMove;
import com.chess.engine.move.Move.PawnEnPassantAttackMove;
import com.chess.engine.move.Move.PawnJump;
import com.chess.engine.move.Move.PawnMove;
import com.chess.engine.player.Alliance;

public class Pawn extends Piece {

	private static final int[] CANDIDATE_OFFSETS = { 7, 8, 9, 16 };
	
	public Pawn(final Alliance ally, final int position, final boolean isFirstMove) {
		super(Piece.Type.PAWN, ally, position, isFirstMove);
	}
	
	public Pawn(final Alliance ally, final int position) {
		this(ally, position, true);
	}
	
	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final Collection<Move> legalMoves = new ArrayList<Move>();
		for(final int offset : CANDIDATE_OFFSETS) {
			final int destination = position + offset * ally.getDirection();
			
			if(!BoardUtils.isValidTile(destination)) continue;
			
			if(offset == 8 && !board.getTile(destination).isOccupied()) {
				// TODO: Deal with promotions!
				legalMoves.add(new PawnMove(board, this, destination));
			}else if(offset == 16 && isFirstMove() && 
					((BoardUtils.SECOND_ROW[position] && ally.isBlack()) || 
					 (BoardUtils.SEVENTH_ROW[position] && ally.isWhite()))) {
				final int behindDestination = position + ally.getDirection() * 8;
				if(!board.getTile(behindDestination).isOccupied() && !board.getTile(destination).isOccupied()) {
					legalMoves.add(new PawnJump(board, this, destination));
				}
			}else if(offset == 7 &&
					!((BoardUtils.EIGHTH_COLUMN[position] && ally.isWhite()) || 
					 (BoardUtils.FIRST_COLUMN[position] && ally.isBlack()))) {
				if(board.getTile(destination).isOccupied()) {
					final Piece piece = board.getTile(destination).getPiece();
					if(piece.ally != ally) {
						// TODO: Attack into pawn promotion!
						legalMoves.add(new PawnAttackMove(board, this, destination, piece));
					}
				}else if(board.getEnPassantPawn() != null) {
					final Pawn pawn = board.getEnPassantPawn();
					if(pawn.getPosition() == (position + ally.getOppositeDirection())) {
						if(ally != pawn.ally) {
							legalMoves.add(new PawnEnPassantAttackMove(board, this, destination, pawn));
						}
					}
				}
			}else if(offset == 9 &&
					!((BoardUtils.FIRST_COLUMN[position] && ally.isWhite()) ||
					 (BoardUtils.EIGHTH_COLUMN[position] && ally.isBlack()))) {
				if(board.getTile(destination).isOccupied()) {
					final Piece piece = board.getTile(destination).getPiece();
					if(piece.ally != ally) {
						// TODO: Attack into pawn promotion!
						legalMoves.add(new PawnAttackMove(board, this, destination, piece));
					}
				}else if(board.getEnPassantPawn() != null) {
					final Pawn pawn = board.getEnPassantPawn();
					if(pawn.position == (position - ally.getOppositeDirection())) {
						if(ally != pawn.ally) {
							legalMoves.add(new PawnEnPassantAttackMove(board, this, destination, pawn));
						}
					}
				}
			}
		}
		return Collections.unmodifiableCollection(legalMoves);
	}
	
	@Override
	public boolean isFirstColumnExclusion(final int position, final int offset) {
		return false;
	}
	
	@Override
	public boolean isSecondColumnExclusion(final int position, final int offset) {
		return false;
	}
	
	@Override
	public boolean isSeventhColumnExclusion(final int position, final int offset) {
		return false;
	}
	
	@Override
	public boolean isEighthColumnExclusion(final int position, final int offset) {
		return false;
	}
	
	@Override
	public Pawn makeMove(final int destination) {
		return new Pawn(ally, destination, false);
	}
	
}
