package com.chess.engine.pieces;

import java.util.Collection;
import java.util.Objects;

import com.chess.engine.board.Board;
import com.chess.engine.move.Move;
import com.chess.engine.player.Alliance;

public abstract class Piece {

	public static enum Type {
		ROOK(500),
		KNIGHT(300),
		BISHOP(300),
		QUEEN(900),
		KING(10000),
		PAWN(100);
		
		private final int value;
		
		private Type(final int value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return this == KNIGHT ? "N" : this.name().substring(0,1);
		}
		
		public boolean isRook() {
			return this == ROOK;
		}
		
		public boolean isKing() {
			return this == KING;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	protected final Type type;
	protected final Alliance ally;
	protected final int position;
	protected final boolean isFirstMove;
	
	protected Piece(final Type type, final Alliance ally, final int position, final boolean isFirstMove) {
		this.type = type;
		this.ally = ally;
		this.position = position;
		this.isFirstMove = isFirstMove;
	}
	
	@Override
	public boolean equals(final Object other) {
		if(other == null) return false;
		if(this == other) return true;
		if(!(other instanceof Piece)) return false;
		final Piece piece = (Piece) other;
		return type == piece.type &&
				ally == piece.ally &&
				position == piece.position &&
				isFirstMove == piece.isFirstMove;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, ally, position, isFirstMove);
	}
	
	@Override
	public String toString() {
		return this.ally == Alliance.BLACK ? this.type.toString().toLowerCase() : this.type.toString();
	}
	
	public Type getType() {
		return this.type;
	}
	
	public Alliance getAlliance() {
		return this.ally;
	}
	
	public int getPosition() {
		return this.position;
	}
	
	public boolean isFirstMove() {
		return this.isFirstMove;
	}
	
	public boolean hasExclusion(final int position, final int offset) {
		return isFirstColumnExclusion(position, offset) ||
				isSecondColumnExclusion(position, offset) ||
				isSeventhColumnExclusion(position, offset) ||
				isEighthColumnExclusion(position, offset);
	}
	
	public abstract Collection<Move> calculateLegalMoves(final Board board);
	public abstract boolean isFirstColumnExclusion(final int position, final int offset);
	public abstract boolean isSecondColumnExclusion(final int position, final int offset);
	public abstract boolean isSeventhColumnExclusion(final int position, final int offset);
	public abstract boolean isEighthColumnExclusion(final int position, final int offset);
	public abstract Piece makeMove(final int destination);
	
	public static int compare(final Piece p1, final Piece p2) {
		return Integer.compare(p1.getType().getValue(),p2.getType().getValue());
	}
	
}
