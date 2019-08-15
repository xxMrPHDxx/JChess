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

public class Bishop extends Piece {

	private static final int[] CANDIDATE_OFFSETS = { -9, -7, 7, 9 };
	
	public Bishop(final Alliance ally, final int position, final boolean isFirstMove) {
		super(Piece.Type.BISHOP, ally, position, isFirstMove);
	}
	
	public Bishop(final Alliance ally, final int position) {
		this(ally, position, true);
	}
	
	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final Collection<Move> legalMoves = new ArrayList<Move>();
		for(final int offset : CANDIDATE_OFFSETS) {
			int destination = position;
			while(BoardUtils.isValidTile(destination)) {
				if(hasExclusion(destination, offset)) break;
				destination += offset;
				
				if(BoardUtils.isValidTile(destination)) {
					final Tile tile = board.getTile(destination);
					if(tile.isOccupied()) {
						final Piece piece = tile.getPiece();
						if(piece.ally != ally) {
							legalMoves.add(new MajorAttackMove(board, this, destination, piece));
						}
						break;
					}else {
						legalMoves.add(new MajorMove(board, this, destination));
					}
				}
			}
		}
		return Collections.unmodifiableCollection(legalMoves);
	}
	
	@Override
	public boolean isFirstColumnExclusion(final int position, final int offset) {
		return BoardUtils.FIRST_COLUMN[position] && (offset == -9 || offset == 7);
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
		return BoardUtils.EIGHTH_COLUMN[position] && (offset == -7 || offset == 9);
	}
	
	@Override
	public Bishop makeMove(final int destination) {
		return new Bishop(ally, destination, false);
	}
	
}
