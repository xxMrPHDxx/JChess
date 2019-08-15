package com.chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;
import com.chess.engine.move.Move;
import com.chess.engine.move.Move.MoveFactory;
import com.chess.engine.move.Move.PawnEnPassantAttackMove;
import com.chess.engine.move.MoveTransition;
import com.chess.engine.pieces.Piece;

public class Table extends JFrame {

	private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
	private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
	private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

	private static final Color DEFAULT_LIGHT_COLOR = Color.decode("#E1C596");
	private static final Color DEFAULT_DARK_COLOR = Color.decode("#674220");

	private static Board chessBoard = Board.createStandardBoard();
	private static BoardPanel boardPanel = new BoardPanel();
	private static GameHistoryPanel gameHistoryPanel = new GameHistoryPanel();
	private static TakenPiecesPanel takenPiecesPanel = new TakenPiecesPanel();
	private static MoveLog moveLog = new MoveLog();

	private static Color lightColor = DEFAULT_LIGHT_COLOR;
	private static Color darkColor = DEFAULT_DARK_COLOR;

	private static Tile sourceTile;
	private static Tile destinationTile;
	private static Piece movedPiece;

	private static BoardDirection boardDirection = BoardDirection.NORMAL;
	private static boolean highlightLegalMoves = false;

	public Table() {
		super("JChess");
		setSize(OUTER_FRAME_DIMENSION);
		setJMenuBar(createMenuBar());
		add(gameHistoryPanel, BorderLayout.EAST);
		add(takenPiecesPanel, BorderLayout.WEST);
		add(boardPanel);
		boardPanel.drawBoard();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setLocationRelativeTo(null);
	}

	private JMenuBar createMenuBar() {
		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createPreferencesMenu());
		return menuBar;
	}

	private JMenu createFileMenu() {
		final JMenu fileMenu = new JMenu("File");

		final JMenuItem loadPGN = new JMenuItem("Load PGN File");
		loadPGN.addActionListener(event -> {
			System.out.println("Open up that PGN file!");
		});
		fileMenu.add(loadPGN);

		final JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(event -> System.exit(0));
		fileMenu.add(exit);

		return fileMenu;
	}

	private JMenu createPreferencesMenu() {
		final JMenu prefMenu = new JMenu("Preferences");

		final JMenuItem flipBoard = new JMenuItem("Flip Board");
		flipBoard.addActionListener(event -> {
			boardDirection = boardDirection.opposite();
			boardPanel.drawBoard();
		});
		prefMenu.add(flipBoard);

		prefMenu.addSeparator();

		final JCheckBoxMenuItem highlightMoves = new JCheckBoxMenuItem("Highlight Legal Moves", false);
		highlightMoves.addActionListener(event -> {
			highlightLegalMoves = highlightMoves.isSelected();
			boardPanel.drawBoard();
		});
		prefMenu.add(highlightMoves);

		return prefMenu;
	}

	private static final class BoardPanel extends JPanel {

		final List<TilePanel> tiles;

		public BoardPanel() {
			super(new GridLayout(BoardUtils.NUM_TILES_PER_ROW, BoardUtils.NUM_TILES_PER_ROW));
			this.tiles = IntStream.range(0, BoardUtils.NUM_TILES).mapToObj(TilePanel::new).collect(Collectors.toList());
			this.tiles.forEach(this::add);
			setPreferredSize(BOARD_PANEL_DIMENSION);
			validate();
		}

		public void drawBoard() {
			removeAll();
			for(final TilePanel tile : boardDirection.traverse(tiles)) {
				tile.drawTile();
				add(tile);
			}
			validate();
			repaint();
		}

	}

	public static class MoveLog {

		private final List<Move> moves;

		MoveLog() {
			this.moves = new ArrayList<Move>();
		}

		public List<Move> getMoves() {
			return this.moves;
		}

		public void add(final Move move) {
			this.moves.add(move);
		}

		public int size() {
			return this.moves.size();
		}

		public void clear() {
			this.moves.clear();
		}

		public boolean remove(final Move move) {
			return this.moves.remove(move);
		}

		public Move remove(final int index) {
			return this.moves.remove(index);
		}

	}

	private static final class TilePanel extends JPanel implements MouseListener {

		final int id;

		public TilePanel(final int id) {
			super(new GridBagLayout());
			this.id = id;
			setPreferredSize(TILE_PANEL_DIMENSION);
			drawTile();
			addMouseListener(this);
			validate();
		}

		private void drawTile() {
			assignTileColor();
			assignTileIcon();
			highlightLegalMoves();
		}

		private void assignTileColor() {
			setBackground(id % 2 == 0 ? (int) (id / BoardUtils.NUM_TILES_PER_ROW) % 2 == 0 ? lightColor : darkColor : (int) (id / BoardUtils.NUM_TILES_PER_ROW) % 2 != 0 ? lightColor : darkColor);
		}

		private void assignTileIcon() {
			this.removeAll();
			if(chessBoard.getTile(id).isOccupied()) {
				final Piece piece = chessBoard.getTile(id).getPiece();
				add(new JLabel(new ImageIcon(IconRegistry.getIconOf(piece.getAlliance(), piece.getType()))));
			}
		}

		private void highlightLegalMoves() {
			if(highlightLegalMoves) {
				for(final Move move : getPieceLegalMoves()) {
					if(move.getDestination() == this.id) {
						if(move.isAttackMove()) {
							if(id == 19) {
//								System.out.println(move.getClass());
							}
							setBackground(new Color(255, 0, 0, 180));
						} else {
							add(new JLabel(new ImageIcon(IconRegistry.getIconOf("green_dot"))));
						}
					}
				}
			}
		}

		private Collection<Move> getPieceLegalMoves() {
			if(movedPiece != null && movedPiece.getAlliance() == chessBoard.getCurrentPlayer().getAlliance()) {
				return movedPiece.calculateLegalMoves(chessBoard);
			}
			return Collections.emptyList();
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) {
				sourceTile = destinationTile = null;
				movedPiece = null;
			} else if(SwingUtilities.isLeftMouseButton(e)) {
				if(sourceTile == null) {
					sourceTile = chessBoard.getTile(id);
					movedPiece = sourceTile.getPiece();
					if(movedPiece == null) {
						sourceTile = null;
					}
				} else {
					destinationTile = chessBoard.getTile(id);
					final Move move = MoveFactory.createMove(chessBoard, sourceTile.getPosition(), destinationTile.getPosition());
					System.out.println("Legal Moves: "+chessBoard.getCurrentPlayer().getLegalMoves());
					final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
					if(transition.getMoveStatus().isDone()) {
						chessBoard = transition.getBoard();
						moveLog.add(move);
					}
					sourceTile = destinationTile = null;
					movedPiece = null;
				}
			}
			SwingUtilities.invokeLater(() -> {
				gameHistoryPanel.redo(chessBoard, moveLog);
				takenPiecesPanel.redo(moveLog);
				boardPanel.drawBoard();
			});
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

	}

	public static enum BoardDirection {
		NORMAL, FLIPPED;
		public List<TilePanel> traverse(final List<TilePanel> tiles) {
			final List<TilePanel> copy = Arrays.asList(tiles.toArray(new TilePanel[0]));
			if(this == FLIPPED)
				Collections.reverse(copy);
			return copy;
		}

		public BoardDirection opposite() {
			return this == NORMAL ? FLIPPED : NORMAL;
		}
	}

}
