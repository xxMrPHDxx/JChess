package com.chess.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.chess.engine.board.Board;
import com.chess.engine.move.Move;
import com.chess.gui.Table.MoveLog;

public class GameHistoryPanel extends JPanel {

	private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

	private final DataModel model;
	private final JScrollPane scrollPane;

	public GameHistoryPanel() {
		this.model = new DataModel();
		setLayout(new BorderLayout());

		final JTable table = new JTable(model);
		table.setRowHeight(15);
		add(this.scrollPane = new JScrollPane(table), BorderLayout.CENTER);
		scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
		scrollPane.setColumnHeaderView(table.getTableHeader());
		setVisible(true);
	}

	public void redo(final Board board, final MoveLog moveLog) {
		this.model.clear();
		int currentRow = 0;
		for(final Move move : moveLog.getMoves()) {
			final String moveText = move.toString();
			switch(move.getPiece().getAlliance()) {
				case WHITE:
					model.setValueAt(moveText, currentRow, 0);
					break;
				case BLACK:
					model.setValueAt(moveText, currentRow++, 1);
					break;
			}
		}
		;

		if(moveLog.size() > 0) {
			final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
			final String moveText = lastMove.toString();

			switch(lastMove.getPiece().getAlliance()) {
				case WHITE:
					model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
					break;
				case BLACK:
					model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow-1, 1);
					break;
			}
		}

		final JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
	}

	private static String calculateCheckAndCheckMateHash(final Board board) {
		if(board.getCurrentPlayer().isInCheckMate()) {
			return "#";
		} else if(board.getCurrentPlayer().isInCheck()) {
			return "+";
		}
		return "";
	}

	private static class DataModel extends DefaultTableModel {

		private static final String[] NAMES = { "White", "Black" };

		private final List<Row> values;

		public DataModel() {
			this.values = new ArrayList<Row>();
		}

		public void clear() {
			this.values.clear();
			setRowCount(0);
		}

		@Override
		public int getRowCount() {
			return this.values == null ? 0 : this.values.size();
		}

		@Override
		public int getColumnCount() {
			return NAMES.length;
		}

		@Override
		public Object getValueAt(final int row, final int column) {
			final Row currentRow = this.values.get(row);
			if(currentRow == null)
				return null;
			switch(column) {
				case 0:
					return currentRow.getWhiteMove();
				case 1:
					return currentRow.getBlackMove();
				default:
					return null;
			}
		}

		@Override
		public void setValueAt(final Object value, final int row, final int column) {
			final Row currentRow;
			if(this.values.size() <= row) {
				this.values.add(currentRow = new Row());
			} else {
				currentRow = this.values.get(row);
			}
			switch(column) {
				case 0:
					currentRow.setWhiteMove(value.toString());
					fireTableRowsInserted(row, row);
					break;
				case 1:
					currentRow.setBlackMove(value.toString());
					fireTableCellUpdated(row, column);
					break;
				default:
					return;
			}
		}

		@Override
		public Class<?> getColumnClass(final int column) {
			return Move.class;
		}

		@Override
		public String getColumnName(int column) {
			return NAMES[column];
		}

	}

	private static class Row {

		private String whiteMove, blackMove;

		public Row() {

		}

		public void setWhiteMove(final String whiteMove) {
			this.whiteMove = whiteMove;
		}

		public String getWhiteMove() {
			return this.whiteMove;
		}

		public void setBlackMove(final String blackMove) {
			this.blackMove = blackMove;
		}

		public String getBlackMove() {
			return this.blackMove;
		}

	}

}
