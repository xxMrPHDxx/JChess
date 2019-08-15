package com.chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.chess.engine.move.Move;
import com.chess.engine.pieces.Piece;
import com.chess.gui.Table.MoveLog;

public class TakenPiecesPanel extends JPanel {

	private static final Color PANEL_COLOR = Color.decode("0x0FDFE6");
	private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
	private static final Dimension MAIN_PANEL_DIMENSION = new Dimension(40, 80); 
	
	private final JPanel northPanel;
	private final JPanel southPanel;
	
	public TakenPiecesPanel() {
		super(new BorderLayout());
		setBackground(PANEL_COLOR);
		setBorder(PANEL_BORDER);
		add(this.northPanel = new JPanel(new GridLayout(8,2)), BorderLayout.NORTH);
		add(this.southPanel = new JPanel(new GridLayout(8,2)), BorderLayout.SOUTH);
		this.northPanel.setBackground(PANEL_COLOR);
		this.southPanel.setBackground(PANEL_COLOR);
		setPreferredSize(MAIN_PANEL_DIMENSION);
	}
	
	public void redo(final MoveLog moveLog) {
		southPanel.removeAll();
		northPanel.removeAll();
		
		final List<Piece> whiteTakenPieces = new ArrayList<Piece>();
		final List<Piece> blackTakenPieces = new ArrayList<Piece>();
		
		moveLog.getMoves()
			.parallelStream()
			.filter(Move::isAttackMove)
			.forEach(move -> {
				switch(move.getAttackedPiece().getAlliance()) {
					case WHITE: whiteTakenPieces.add(move.getAttackedPiece()); break;
					case BLACK: blackTakenPieces.add(move.getAttackedPiece()); break;
				}
			});

		Collections.sort(whiteTakenPieces, Piece::compare);
		Collections.sort(blackTakenPieces, Piece::compare);
		
		for(final Piece piece : whiteTakenPieces) {
			northPanel.add(new JLabel(new ImageIcon(IconRegistry.getIconOf(piece.getAlliance(), piece.getType()))));
		}
		for(final Piece piece : blackTakenPieces) {
			southPanel.add(new JLabel(new ImageIcon(IconRegistry.getIconOf(piece.getAlliance(), piece.getType()))));
		}
		
		validate();
	}
	
}
