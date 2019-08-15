package com.chess.engine.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.chess.engine.move.Move;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;
import com.chess.engine.player.Alliance;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

public class Board {

	private final List<Tile> tiles;
	private final Collection<Piece> whitePieces;
	private final Collection<Piece> blackPieces;
	
	private final WhitePlayer whitePlayer;
	private final BlackPlayer blackPlayer;
	private final Player currentPlayer;
	
	private final Pawn enPassantPawn;
	
	private Board(final Builder builder) {
		this.tiles = IntStream.range(0,BoardUtils.NUM_TILES)
				.mapToObj(i -> Tile.createTile(i, builder.pieces.get(i)))
				.collect(Collectors.toList());
		this.whitePieces = calculateActivePieces(Alliance.WHITE);
		this.blackPieces = calculateActivePieces(Alliance.BLACK);
		
		final Collection<Move> whiteLegalMoves = calculateLegalMoves(whitePieces);
		final Collection<Move> blackLegalMoves = calculateLegalMoves(blackPieces);

		this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
		this.blackPlayer = new BlackPlayer(this, blackLegalMoves, whiteLegalMoves);
		this.currentPlayer = builder.moveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
		
		this.enPassantPawn = builder.enPassantPawn;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for(int i=0;i<BoardUtils.NUM_TILES;i++) {
			sb.append(String.format("%3s", this.tiles.get(i)));
			if(i % BoardUtils.NUM_TILES_PER_ROW == 7 && i < BoardUtils.NUM_TILES-1) sb.append("\n");
		}
		return sb.toString();
	}
	
	public Tile getTile(final int position) {
		return tiles.get(position);
	}
	
	public Collection<Piece> getWhiteActivePieces() {
		return this.whitePieces;
	}
	
	public Collection<Piece> getBlackActivePieces() {
		return this.blackPieces;
	}
	
	public Collection<Piece> getAllActivePieces() {
		final Collection<Piece> activePieces = new ArrayList<Piece>();
		this.whitePieces.forEach(activePieces::add);
		this.blackPieces.forEach(activePieces::add);
		return Collections.unmodifiableCollection(activePieces);
	}
	
	public WhitePlayer getWhitePlayer() {
		return this.whitePlayer;
	}
	
	public BlackPlayer getBlackPlayer() {
		return this.blackPlayer;
	}
	
	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}
	
	public Pawn getEnPassantPawn() {
		return this.enPassantPawn;
	}
	
	public Collection<Move> getAllLegalMoves() {
		final Collection<Move> legalMoves = new ArrayList<Move>();
		this.whitePlayer.getLegalMoves().forEach(legalMoves::add);
		this.blackPlayer.getLegalMoves().forEach(legalMoves::add);
		return Collections.unmodifiableCollection(legalMoves);
	}
	
	private Collection<Piece> calculateActivePieces(final Alliance ally) {
		return Collections.unmodifiableCollection(
			this.tiles
				.stream()
				.filter(Tile::isOccupied)
				.map(Tile::getPiece)
				.filter(piece -> piece.getAlliance().equals(ally))
				.collect(Collectors.toList())
		);
	}
	
	private Collection<Move> calculateLegalMoves(final Collection<Piece> activePieces) {
		return Collections.unmodifiableCollection(
			activePieces
				.stream()
				.map(piece -> piece.calculateLegalMoves(this))
				.reduce(new ArrayList<Move>(),(a,b) -> { a.addAll(b); return a; })
		);
	}
	
	public static Board createStandardBoard() {
		final Builder builder = new Builder(Alliance.WHITE);
		
		builder
			.setPiece(new Rook(Alliance.BLACK, 0))
			.setPiece(new Knight(Alliance.BLACK, 1))
			.setPiece(new Bishop(Alliance.BLACK, 2))
			.setPiece(new Queen(Alliance.BLACK, 3))
			.setPiece(new King(Alliance.BLACK, 4))
			.setPiece(new Bishop(Alliance.BLACK, 5))
			.setPiece(new Knight(Alliance.BLACK, 6))
			.setPiece(new Rook(Alliance.BLACK, 7));
		
		builder
			.setPiece(new Rook(Alliance.WHITE, 63))
			.setPiece(new Knight(Alliance.WHITE, 62))
			.setPiece(new Bishop(Alliance.WHITE, 61))
			.setPiece(new Queen(Alliance.WHITE, 60))
			.setPiece(new King(Alliance.WHITE, 59))
			.setPiece(new Bishop(Alliance.WHITE, 58))
			.setPiece(new Knight(Alliance.WHITE, 57))
			.setPiece(new Rook(Alliance.WHITE, 56));
		
		IntStream.range(0, BoardUtils.NUM_TILES_PER_ROW).forEach(i -> {
			for(final Alliance ally : Alliance.values()) {
				builder.setPiece(new Pawn(ally, i + (ally == Alliance.BLACK ? 8 : 48)));
			}
		});
		
		return builder.build();
	}
	
	public static final class Builder {
		
		final Map<Integer,Piece> pieces;
		final Alliance moveMaker;
		Pawn enPassantPawn;
		
		public Builder(final Alliance nextMoveMaker) {
			this.moveMaker = nextMoveMaker;
			this.pieces = new HashMap<Integer,Piece>();
		}
		
		public Builder setPiece(final Piece piece) {
			if(piece != null) this.pieces.put(piece.getPosition(), piece);
			return this;
		}
		
		public Builder setEnPassantPawn(final Pawn enPassantPawn) {
			this.enPassantPawn = enPassantPawn;
			return this;
		}
		
		public Board build() {
			return new Board(this);
		}
		
	}
	
}
