package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.move.Move;
import com.chess.engine.move.Move.MajorAttackMove;
import com.chess.engine.move.Move.MajorMove;
import com.chess.engine.player.Alliance;

public class King extends Piece {

	private static final int[] CANDIDATE_OFFSETS = { -9, -8, -7, -1, 1, 7, 8, 9 };
	
	public King(final Alliance ally, final int position, final boolean isFirstMove) {
		super(Piece.Type.KING, ally, position, isFirstMove);
	}
	
	public King(final Alliance ally, final int position) {
		this(ally, position, true);
	}
	
	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final Collection<Move> legalMoves = new ArrayList<Move>();
		for(final int offset : CANDIDATE_OFFSETS) {
			final int destination = position + offset;
			if(BoardUtils.isValidTile(destination)) {
				if(hasExclusion(position, offset)) continue;
				
				final Tile tile = board.getTile(destination);
				if(tile.isOccupied()) {
					final Piece piece = tile.getPiece();
					if(piece.ally != ally) {
						legalMoves.add(new MajorAttackMove(board, this, destination, piece));
					}
				}else {
					legalMoves.add(new MajorMove(board, this, destination));
				}
			}
		}
		return Collections.unmodifiableCollection(legalMoves);
	}
	
	@Override
	public boolean isFirstColumnExclusion(final int position, final int offset) {
		return BoardUtils.FIRST_COLUMN[position] && (offset == -9 || offset == -1 || offset == 7);
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
		return BoardUtils.EIGHTH_COLUMN[position] && (offset == -7 || offset ==1 || offset == 9);
	}
	
	@Override
	public King makeMove(final int destination) {
		return new King(ally, destination, false);
	}
	
}
