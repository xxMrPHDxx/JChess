package com.chess.engine.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.chess.engine.pieces.Piece;

public abstract class Tile {

	private static final Map<Integer,EmptyTile> EMPTY_TILES_CACHE = createAllEmptyTiles();
	
	private static Map<Integer,EmptyTile> createAllEmptyTiles() {
		final Map<Integer,EmptyTile> emptyTiles = new HashMap<Integer,EmptyTile>();
		for(int i=0;i<BoardUtils.NUM_TILES;i++) {
			emptyTiles.put(i, new EmptyTile(i));
		}
		return Collections.unmodifiableMap(emptyTiles);
	}
	
	protected final int position;
	
	private Tile(int position) {
		this.position = position;
	}
	
	public int getPosition() {
		return this.position;
	}
	
	public static Tile createTile(final int position, final Piece piece) {
		return piece != null ? new OccupiedTile(position, piece) : EMPTY_TILES_CACHE.get(position);
	}
	
	public abstract boolean isOccupied();
	public abstract Piece getPiece();
	
	public static final class EmptyTile extends Tile {
		
		private EmptyTile(final int position) {
			super(position);
		}
		
		@Override
		public String toString() {
			return "-";
		}
		
		@Override
		public boolean isOccupied() {
			return false;
		}
		
		@Override
		public Piece getPiece() {
			return null;
		}
		
	}
	
	public static final class OccupiedTile extends Tile {
		
		private final Piece piece;
		
		private OccupiedTile(final int position, final Piece piece) {
			super(position);
			this.piece = piece;
		}
		
		@Override
		public String toString() {
			return this.piece.toString();
		}
		
		@Override
		public boolean isOccupied() {
			return true;
		}
		
		@Override
		public Piece getPiece() {
			return this.piece;
		}
		
	}
	
}
