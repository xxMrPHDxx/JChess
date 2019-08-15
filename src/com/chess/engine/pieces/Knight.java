package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.move.Move;
import com.chess.engine.move.Move.MajorAttackMove;
import com.chess.engine.move.Move.MajorMove;
import com.chess.engine.player.Alliance;

public class Knight extends Piece {

	private static final int[] CANDIDATE_OFFSETS = { -17, -15, -10, -6, 6, 10, 15, 17 };
	
	public Knight(final Alliance ally, final int position, final boolean isFirstMove) {
		super(Piece.Type.KNIGHT, ally, position, isFirstMove);
	}
	
	public Knight(final Alliance ally, final int position) {
		this(ally, position, true);
	}
	
	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<Move>();
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
		return BoardUtils.FIRST_COLUMN[position] && (offset == -17 || offset == -10 || offset == 6 || offset == 15);
	}
	
	@Override
	public boolean isSecondColumnExclusion(final int position, final int offset) {
		return BoardUtils.SECOND_COLUMN[position] && (offset == -10 || offset == 6);
	}
	
	@Override
	public boolean isSeventhColumnExclusion(final int position, final int offset) {
		return BoardUtils.SEVENTH_COLUMN[position] && (offset == -6 || offset == 10);
	}
	
	@Override
	public boolean isEighthColumnExclusion(final int position, final int offset) {
		return BoardUtils.EIGHTH_COLUMN[position] && (offset == -15 || offset == -6 || offset == 10 || offset == 17);
	}
	
	@Override
	public Knight makeMove(final int destination) {
		return new Knight(ally, destination, false);
	}
	
}
