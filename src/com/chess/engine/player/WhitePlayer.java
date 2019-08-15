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

public class WhitePlayer extends Player {

	public WhitePlayer(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentLegalMoves) {
		super(board, legalMoves, opponentLegalMoves);
	}
	
	@Override
	public Alliance getAlliance() {
		return Alliance.WHITE;
	}
	
	@Override
	public Player getOpponent() {
		return this.board.getBlackPlayer();
	}
	
	@Override
	public Collection<Piece> getActivePieces() {
		return this.board.getWhiteActivePieces();
	}
	
	@Override
	protected Collection<Move> calculateKingCastles(final Collection<Move> legalMoves, final Collection<Move> opponentLegalMoves) {
		final Collection<Move> kingCastles = new ArrayList<Move>();
		if(this.king.isFirstMove() && !this.isInCheck()) {
			if(!this.board.getTile(61).isOccupied() && !this.board.getTile(62).isOccupied()) {
				final Tile rookTile = this.board.getTile(63);
				if(rookTile.isOccupied() && rookTile.getPiece().isFirstMove()) {
					if(Player.calculateAttacksOnTile(61, opponentLegalMoves).isEmpty() &&
						Player.calculateAttacksOnTile(62, opponentLegalMoves).isEmpty() &&
						rookTile.getPiece().getType().isRook()) {
						kingCastles.add(new KingSideCastleMove(board, king, 62, (Rook) rookTile.getPiece(), rookTile.getPosition(), 61));
					}
				}
			}
			if(!this.board.getTile(59).isOccupied() && !this.board.getTile(58).isOccupied() && !this.board.getTile(57).isOccupied()) {
				final Tile rookTile = this.board.getTile(56);
				if(rookTile.isOccupied() && rookTile.getPiece().isFirstMove()) {
					if(Player.calculateAttacksOnTile(58, opponentLegalMoves).isEmpty() &&
						Player.calculateAttacksOnTile(59, opponentLegalMoves).isEmpty() &&
						rookTile.getPiece().getType().isRook()) {
						kingCastles.add(new QueenSideCastleMove(board, king, 58, (Rook) rookTile.getPiece(), rookTile.getPosition(), 59));
					}
				}
			}
		}
		return Collections.unmodifiableCollection(kingCastles);
	}
	
}
