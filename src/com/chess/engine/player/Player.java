package com.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.chess.engine.board.Board;
import com.chess.engine.move.Move;
import com.chess.engine.move.MoveStatus;
import com.chess.engine.move.MoveTransition;
import com.chess.engine.move.Move.PawnEnPassantAttackMove;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;

public abstract class Player {
	
	protected final Board board;
	protected final King king;
	protected final Collection<Move> legalMoves;
	private final boolean isInCheck;
	
	protected Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentLegalMoves) {
		this.board = board;
		this.king = establishKing();
		this.legalMoves = calculateLegalMoves(legalMoves, opponentLegalMoves);
		this.isInCheck = !Player.calculateAttacksOnTile(this.king.getPosition(), opponentLegalMoves).isEmpty();
	}
	
	protected static Collection<Move> calculateAttacksOnTile(final int position, final Collection<Move> opponentLegalMoves) {
		return Collections.unmodifiableCollection(
			opponentLegalMoves
				.stream()
				.filter(move -> move.getDestination() == position)
				.collect(Collectors.toList())
		);
	}
	
	public Collection<Move> getLegalMoves() {
		return this.legalMoves;
	}
	
	public King getKing() {
		return this.king;
	}
	
	public boolean isMoveLegal(final Move move) {
		return this.legalMoves.contains(move);
	}
	
	public boolean isInCheck() {
		return this.isInCheck;
	}

	public boolean isInCheckMate() {
		return this.isInCheck && !hasEscapeMoves();
	}
	
	public boolean isInStaleMate() {
		return !this.isInCheck && !hasEscapeMoves();
	}

	// TODO: Implements below methods	
	public boolean isCastled() {
		return false;
	}
	
	public MoveTransition makeMove(final Move move) {
		if(move instanceof PawnEnPassantAttackMove) {
			System.out.println(move);
		}
		
		if(!isMoveLegal(move)) {
			return new MoveTransition(board, move, MoveStatus.ILLEGAL);
		}
		
		final Board board = move.execute();
		final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(
			board.getCurrentPlayer().getOpponent().getKing().getPosition(), 
			board.getCurrentPlayer().getLegalMoves()
		);
		if(!kingAttacks.isEmpty()) {
			return new MoveTransition(board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
		}
		
		return new MoveTransition(board, move, MoveStatus.DONE);
	}
	
	protected boolean hasEscapeMoves() {
		for(final Move move : this.legalMoves) {
			final MoveTransition transition = makeMove(move);
			if(transition.getMoveStatus().isDone()) {
				return true;
			}
		}
		return false;
	}
	
	private Collection<Move> calculateLegalMoves(final Collection<Move> legalMoves, final Collection<Move> opponentLegalMoves) {
		final Collection<Move> allLegalMoves = new ArrayList<Move>();
		legalMoves.forEach(allLegalMoves::add);
		calculateKingCastles(legalMoves, opponentLegalMoves).forEach(allLegalMoves::add);
		return Collections.unmodifiableCollection(allLegalMoves);
	}
	
	private King establishKing() {
		for(final Piece piece : getActivePieces()) {
			if(piece.getType().isKing()) {
				return (King) piece;
			}
		}
		throw new RuntimeException(String.format("Illegal state: No king found for %s player!", getAlliance()));
	}
	
	public abstract Alliance getAlliance();
	public abstract Player getOpponent();
	public abstract Collection<Piece> getActivePieces();
	protected abstract Collection<Move> calculateKingCastles(final Collection<Move> legalMoves, final Collection<Move> opponentLegalMoves);
	
}
