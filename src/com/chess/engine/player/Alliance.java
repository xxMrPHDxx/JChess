package com.chess.engine.player;

public enum Alliance {
	WHITE{
		@Override
		public int getDirection() {
			return -1;
		}
	},
	BLACK{
		@Override
		public int getDirection() {
			return 1;
		}
	};
	public boolean isBlack() {
		return this == BLACK;
	}
	
	public boolean isWhite() {
		return this == WHITE;
	}
	
	public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
		return this == WHITE ? whitePlayer : blackPlayer;
	}
	
	public int getOppositeDirection() {
		return getDirection() * -1;
	}
	
	public abstract int getDirection();
}
