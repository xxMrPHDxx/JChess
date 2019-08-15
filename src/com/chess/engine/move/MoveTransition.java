package com.chess.engine.move;

import com.chess.engine.board.Board;

public class MoveTransition {
	
	private final Board board;
	private final Move move;
	private final MoveStatus moveStatus;
	
	public MoveTransition(final Board board, final Move move, final MoveStatus moveStatus) {
		this.board = board;
		this.move = move;
		this.moveStatus = moveStatus;
	}
	
	public Board getBoard() {
		return this.board;
	}
	
	public Move getMove() {
		return this.move;
	}
	
	public MoveStatus getMoveStatus() {
		return this.moveStatus;
	}
	
}
