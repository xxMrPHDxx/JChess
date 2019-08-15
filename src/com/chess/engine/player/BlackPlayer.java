package com.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.move.Move;
import com.chess.engine.move.Move.KingSideCastleMove;
import com.chess.engine.move.Move.QueenSideCastleMove;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public class BlackPlayer extends Player {

	public BlackPlayer(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentLegalMoves) {
		super(board, legalMoves, opponentLegalMoves);
	}
	
	@Override
	public Alliance getAlliance() {
		return Alliance.BLACK;
	}
	
	@Override
	public Player getOpponent() {
		return this.board.getWhitePlayer();
	}
	
	@Override
	public Collection<Piece> getActivePieces() {
		return this.board.getBlackActivePieces();
	}
	
	@Override
	protected Collection<Move> calculateKingCastles(final Collection<Move> legalMoves, final Collection<Move> opponentLegalMoves) {
		final Collection<Move> kingCastles = new ArrayList<Move>();
		if(this.king.isFirstMove() && !this.isInCheck()) {
			if(!this.board.getTile(5).isOccupied() && !this.board.getTile(6).isOccupied()) {
				final Tile rookTile = this.board.getTile(7);
				if(rookTile.isOccupied() && rookTile.getPiece().isFirstMove()) {
					if(Player.calculateAttacksOnTile(5, opponentLegalMoves).isEmpty() &&
						Player.calculateAttacksOnTile(6, opponentLegalMoves).isEmpty() &&
						rookTile.getPiece().getType().isRook()) {
						kingCastles.add(new KingSideCastleMove(board, king, 6, (Rook) rookTile.getPiece(), rookTile.getPosition(), 5));
					}
				}
			}
			if(!this.board.getTile(3).isOccupied() && !this.board.getTile(2).isOccupied() && !this.board.getTile(1).isOccupied()) {
				final Tile rookTile = this.board.getTile(0);
				if(rookTile.isOccupied() && rookTile.getPiece().isFirstMove()) {
					if(Player.calculateAttacksOnTile(2, opponentLegalMoves).isEmpty() &&
						Player.calculateAttacksOnTile(3, opponentLegalMoves).isEmpty() &&
						rookTile.getPiece().getType().isRook()) {
						kingCastles.add(new QueenSideCastleMove(board, king, 2, (Rook) rookTile.getPiece(), rookTile.getPosition(), 3));
					}
				}
			}
		}
		return Collections.unmodifiableCollection(kingCastles);
	}
	
}
