package com.chess.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Alliance;

import static com.chess.engine.player.Alliance.*;
import static com.chess.engine.pieces.Piece.Type.*;

public class IconRegistry {
	
	private static final Map<Alliance,Map<Piece.Type,BufferedImage>> CHESS_PIECE_ICONS;
	private static final Map<String,BufferedImage> MISC_ICONS;
	
	private static BufferedImage loadImage(final String path) throws IOException {
		return ImageIO.read(File.class.getResourceAsStream(path));
	}
	
	static {
		try {
			final BufferedImage figures = loadImage("/figures.png");
			final Map<Alliance,Map<Piece.Type,BufferedImage>> outerMap = new HashMap<Alliance, Map<Piece.Type, BufferedImage>>();
			int y = 0;
			for(final Alliance ally : new Alliance[]{ BLACK, WHITE }) {
				final Map<Piece.Type,BufferedImage> map = new HashMap<Piece.Type,BufferedImage>();
				int x = 0;
				for(final Piece.Type type : new Piece.Type[] { ROOK, KNIGHT, BISHOP, QUEEN, KING, PAWN }) {
					final BufferedImage image = figures.getSubimage(x++*56, y*56, 56, 56);
					map.put(type,image);
				}
				y++;
				outerMap.put(ally,map);
			}
			CHESS_PIECE_ICONS = Collections.unmodifiableMap(outerMap);
			
			final Map<String,BufferedImage> icons = new HashMap<String,BufferedImage>();
			icons.put("green_dot",loadImage("/green_dot.png"));
			MISC_ICONS = Collections.unmodifiableMap(icons);
		}catch(final IOException e) {
			throw new RuntimeException("Failed loading chess pieces figures", e);
		}
	}
	
	public static BufferedImage getIconOf(final Alliance ally, final Piece.Type type) {
		return CHESS_PIECE_ICONS.get(ally).get(type);
	}
	
	public static BufferedImage getIconOf(final String name) {
		return MISC_ICONS.get(name);
	}
	
}
