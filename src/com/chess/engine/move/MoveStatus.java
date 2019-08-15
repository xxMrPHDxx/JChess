package com.chess.engine.move;

public enum MoveStatus {
	DONE,
	ILLEGAL,
	LEAVES_PLAYER_IN_CHECK,
	NULL;
	public boolean isDone() {
		return this == DONE;
	}
}
