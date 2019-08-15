package com.chess.engine.move;

import java.util.Objects;

import com.chess.engine.board.Board;
import com.chess.engine.board.Board.Builder;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class Move {

	public static final Move NULL_MOVE = new NullMove();

	protected final Board board;
	protected final Piece piece;
	protected final int destination;
	protected final boolean isFirstMove;

	private Move(final Board board, final Piece piece, final int destination) {
		this.board = board;
		this.piece = piece;
		this.destination = destination;
		this.isFirstMove = piece.isFirstMove();
	}

	private Move(final Board board, final int destination) {
		this.board = board;
		this.piece = null;
		this.destination = destination;
		this.isFirstMove = false;
	}

	@Override
	public boolean equals(final Object other) {
		if(piece == null)
			return false;
		if(this == other)
			return true;
		if(!(other instanceof Move))
			return false;
		final Move move = (Move) other;
		return piece.equals(move.piece) && destination == move.destination && isFirstMove == move.isFirstMove;
	}

	@Override
	public int hashCode() {
		return Objects.hash(piece, destination);
	}

	@Override
	public String toString() {
		return piece.getType().toString() + BoardUtils.encodePosition(destination);
	}

	public Board getBoard() {
		return this.board;
	}

	public Piece getPiece() {
		return this.piece;
	}

	public int getPosition() {
		return this.piece.getPosition();
	}

	public int getDestination() {
		return this.destination;
	}

	public boolean isAttackMove() {
		return false;
	}

	public boolean isCastlingMove() {
		return false;
	}

	public Piece getAttackedPiece() {
		return null;
	}

	public Board execute() {
		final Builder builder = new Builder(this.board.getCurrentPlayer().getOpponent().getAlliance());
		for(final Piece piece : this.board.getAllActivePieces()) {
			if(!this.piece.equals(piece)) {
				builder.setPiece(piece);
			}
		}
		return builder.setPiece(piece.makeMove(this.destination)).build();
	}

	public static final class MajorMove extends Move {

		public MajorMove(final Board board, final Piece piece, final int destination) {
			super(board, piece, destination);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof MajorMove && super.equals(other);
		}

	}

	public static class AttackMove extends Move {

		protected final Piece attackedPiece;

		public AttackMove(final Board board, final Piece piece, final int destination, final Piece attackedPiece) {
			super(board, piece, destination);
			this.attackedPiece = attackedPiece;
		}

		@Override
		public boolean equals(final Object other) {
			if(this == other)
				return true;
			if(!(other instanceof AttackMove))
				return false;
			final AttackMove move = (AttackMove) other;
			return super.equals(move) && attackedPiece.equals(move.attackedPiece);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), attackedPiece);
		}

		@Override
		public String toString() {
			return String.format("AttackMove[Piece: %s, Destination: %s, AttackedPiece: %s]", piece, destination,
					attackedPiece);
		}

		@Override
		public Board execute() {
			final Builder builder = new Builder(this.board.getCurrentPlayer().getOpponent().getAlliance());
			for(final Piece piece : this.board.getAllActivePieces()) {
				if(this.piece != piece && this.attackedPiece != piece) {
					builder.setPiece(piece);
				}
			}
			return builder.setPiece(piece.makeMove(destination)).build();
		}

		@Override
		public boolean isAttackMove() {
			return true;
		}

		@Override
		public Piece getAttackedPiece() {
			return this.attackedPiece;
		}

	}

	public static class MajorAttackMove extends AttackMove {

		public MajorAttackMove(final Board board, final Piece piece, final int destination, final Piece attackedPiece) {
			super(board, piece, destination, attackedPiece);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof MajorAttackMove && super.equals(other);
		}

		@Override
		public String toString() {
			return piece.getType() + BoardUtils.encodePosition(destination);
		}

	}

	public static final class PawnMove extends Move {

		public PawnMove(final Board board, final Piece piece, final int destination) {
			super(board, piece, destination);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnMove && super.equals(other);
		}

		@Override
		public String toString() {
			return BoardUtils.encodePosition(destination);
		}

	}

	public static class PawnAttackMove extends MajorAttackMove {

		public PawnAttackMove(final Board board, final Piece piece, final int destination, final Piece attackedPiece) {
			super(board, piece, destination, attackedPiece);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnAttackMove && super.equals(other);
		}

		@Override
		public String toString() {
			return BoardUtils.encodePosition(piece.getPosition()).substring(0, 1) + "x"
					+ BoardUtils.encodePosition(destination);
		}

	}

	public static final class PawnEnPassantAttackMove extends PawnAttackMove {

		public PawnEnPassantAttackMove(final Board board, final Piece piece, final int destination,
				final Piece attackedPiece) {
			super(board, piece, destination, attackedPiece);
		}
		
		@Override
		public String toString() {
			System.out.println("EnPassant toString called!");
			return super.toString();
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
		}

		@Override
		public Board execute() {
//			final Builder builder = new Builder(board.getCurrentPlayer().getOpponent().getAlliance());
//			for(final Piece piece : board.getAllActivePieces()) {
//				if(!this.piece.equals(piece) && !this.attackedPiece.equals(piece)) {
//					builder.setPiece(piece);
//				}
//			}
//			return builder.setPiece(piece.makeMove(destination)).build();
			System.out.println("Executing en passant move!");
			return super.execute();
		}

	}

	public static final class PawnJump extends Move {

		public PawnJump(final Board board, final Piece piece, final int destination) {
			super(board, piece, destination);
		}

		@Override
		public String toString() {
			return BoardUtils.encodePosition(destination);
		}

		@Override
		public Board execute() {
			final Builder builder = new Builder(this.board.getCurrentPlayer().getOpponent().getAlliance());
			for(final Piece piece : this.board.getAllActivePieces()) {
				if(!this.piece.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			final Pawn pawn = (Pawn) this.piece.makeMove(destination);
			return builder.setPiece(pawn).setEnPassantPawn(pawn).build();
		}

	}

	private static abstract class CastleMove extends Move {

		protected final Rook rook;
		protected final int rookPosition;
		protected final int rookDestination;

		public CastleMove(final Board board, final Piece piece, final int destination, final Rook rook,
				final int rookPosition, final int rookDestination) {
			super(board, piece, destination);
			this.rook = rook;
			this.rookPosition = rookPosition;
			this.rookDestination = rookDestination;
		}

		@Override
		public boolean equals(final Object other) {
			if(this == other)
				return true;
			if(!(other instanceof CastleMove))
				return false;
			final CastleMove move = (CastleMove) other;
			return super.equals(move) && rook.equals(move.rook) && rookDestination == move.rookDestination;
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), rook, rookPosition, rookDestination);
		}

		@Override
		public boolean isCastlingMove() {
			return true;
		}

		public Rook getCastleRook() {
			return this.rook;
		}

		@Override
		public Board execute() {
			final Builder builder = new Builder(this.board.getCurrentPlayer().getOpponent().getAlliance());

			board.getAllActivePieces().forEach(builder::setPiece);

			return builder.setPiece(this.piece.makeMove(destination)).setPiece(this.rook.makeMove(rookDestination))
					.build();
		}

	}

	public static final class KingSideCastleMove extends CastleMove {

		public KingSideCastleMove(final Board board, final Piece piece, final int destination, final Rook rook,
				final int rookPosition, final int rookDestination) {
			super(board, piece, destination, rook, rookPosition, rookDestination);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof KingSideCastleMove && super.equals(other);
		}

		@Override
		public String toString() {
			return "O-O";
		}

	}

	public static final class QueenSideCastleMove extends CastleMove {

		public QueenSideCastleMove(final Board board, final Piece piece, final int destination, final Rook rook,
				final int rookPosition, final int rookDestination) {
			super(board, piece, destination, rook, rookPosition, rookDestination);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof QueenSideCastleMove && super.equals(other);
		}

		@Override
		public String toString() {
			return "O-O-O";
		}

	}

	public static final class NullMove extends Move {

		private NullMove() {
			super(null, -1);
		}
		
		@Override
		public String toString() {
			return "NullMove";
		}

		@Override
		public Board execute() {
			throw new RuntimeException("Failed to execute a NULL move!");
		}

	}

	public static class MoveFactory {

		private MoveFactory() {
			throw new RuntimeException("Non instantiable!");
		}

		public static Move createMove(final Board board, final int position, final int destination) {
			for(final Move move : board.getAllLegalMoves()) {
				if(move.getPosition() == position && move.getDestination() == destination) {
					return move;
				}
			}
			return NULL_MOVE;
		}

	}

}
