package com.chess.engine.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class BoardUtils {

	public static final int NUM_TILES = 64;
	public static final int NUM_TILES_PER_ROW = 8;

	public static final boolean[] FIRST_COLUMN = initColumn(0);
	public static final boolean[] SECOND_COLUMN = initColumn(1);
	public static final boolean[] SEVENTH_COLUMN = initColumn(6);
	public static final boolean[] EIGHTH_COLUMN = initColumn(7);

	public static final boolean[] SECOND_ROW = initRow(1);
	public static final boolean[] SEVENTH_ROW = initRow(6);

	private static final char[] LETTERS = "abcdefgh".toCharArray();
	public static final List<String> ALGEBRAIC_NOTATION;
	public static final Map<String, Integer> POSITION_TO_NOTATION;

	private static boolean[] initColumn(final int col) {
		final boolean[] cols = new boolean[NUM_TILES];
		for(int i = col; i < NUM_TILES; i += 8) {
			cols[i] = true;
		}
		return cols;
	}

	private static boolean[] initRow(final int row) {
		final boolean[] rows = new boolean[NUM_TILES];
		for(int i = 0; i < NUM_TILES_PER_ROW; i++) {
			rows[row * NUM_TILES_PER_ROW + i] = true;
		}
		return rows;
	}

	private BoardUtils() {
		throw new RuntimeException("You cannot instantiate me!");
	}

	public static boolean isValidTile(final int position) {
		return position >= 0 && position < NUM_TILES;
	}

	public static String encodePosition(final int position) {
		return ALGEBRAIC_NOTATION.get(position);
	}

	public static int decodePosition(final String position) {
		return POSITION_TO_NOTATION.get(position);
	}

	private static String toAlgebraic(final int position) {
		final int row = position / NUM_TILES_PER_ROW;
		final int col = position % NUM_TILES_PER_ROW;
		return String.format("%s%s", LETTERS[col], NUM_TILES_PER_ROW - row);
	}

	static {
		final List<String> algebraicNotation = new ArrayList<String>();
		final Map<String, Integer> positionAlgebraicMap = new HashMap<String, Integer>();
		IntStream.range(0, NUM_TILES).forEach(position -> {
			final String algebraic = toAlgebraic(position);
			positionAlgebraicMap.put(algebraic, position);
			algebraicNotation.add(algebraic);
		});
		ALGEBRAIC_NOTATION = Collections.unmodifiableList(algebraicNotation);
		POSITION_TO_NOTATION = Collections.unmodifiableMap(positionAlgebraicMap);
	}

}
