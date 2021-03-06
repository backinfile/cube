package com.backinfile.cube.support;

import com.badlogic.gdx.graphics.Pixmap;

public class DrawUtils {
	public static void drawX(Pixmap pixmap, int width, int height, float persent) {
		int startX = Math.round(width * persent);
		int startY = Math.round(height * persent);
		pixmap.drawLine(startX, startY, width - startX, height - startY);
		pixmap.drawLine(width - startX, startY, startX, height - startY);
	}

	public static void drawLine(Pixmap pixmap, int startX, int startY, int endX, int endY, int width) {
		int halfWidth = Math.round(width / 2);
		if (startX == endX) {
			pixmap.fillRectangle(startX - halfWidth, startY, endX + halfWidth, endY);
		} else {
			pixmap.fillRectangle(startX, startY - halfWidth, endX, endY + halfWidth);
		}
	}

	public static void drawEye(Pixmap pixmap, int x, int y, int width) {
		pixmap.fillRectangle(x - width * 5 / 8, y - width / 12, width * 5 / 4, width / 6);
		pixmap.fillRectangle(x - width / 2, y + width / 2, width, width);
	}

	public static void drawArc(Pixmap pixmap, int x, int y, int radius, int from, int end) {
		double fromRadian = from * Math.PI / 180;
		double endRadian = end * Math.PI / 180;
		for (double radian = fromRadian; radian < endRadian; radian += 0.02f) {
			pixmap.drawPixel(x + (int) Math.floor((radius * Math.cos(radian))),
					y + (int) Math.floor(radius * Math.sin(radian)));
			pixmap.drawPixel(x + (int) Math.floor(((radius - 1) * Math.cos(radian))),
					y + (int) Math.floor((radius - 1) * Math.sin(radian)));
		}
	}

	public static void drawBorder(Pixmap pixmap, int lineWidth) {
		int width = pixmap.getWidth();
		int height = pixmap.getHeight();
		pixmap.fillRectangle(0, 0, lineWidth, height);
		pixmap.fillRectangle(lineWidth, 0, width, lineWidth);
		pixmap.fillRectangle(lineWidth, height - lineWidth, width, lineWidth);
		pixmap.fillRectangle(width - lineWidth, lineWidth, lineWidth, height - lineWidth * 2);
	}

	public static void drawBorder(Pixmap pixmap, int edgeEmptyWidth, int lineWidth) {
		int width = pixmap.getWidth();
		int height = pixmap.getHeight();
		pixmap.fillRectangle(edgeEmptyWidth, edgeEmptyWidth + lineWidth, width - edgeEmptyWidth * 2, lineWidth);
		pixmap.fillRectangle(edgeEmptyWidth, height - edgeEmptyWidth, lineWidth,
				height - edgeEmptyWidth * 2 - lineWidth);

		pixmap.fillRectangle(edgeEmptyWidth + lineWidth, height - edgeEmptyWidth,
				width - edgeEmptyWidth * 2 - lineWidth * 2, lineWidth);
		pixmap.fillRectangle(width - lineWidth - edgeEmptyWidth, lineWidth - edgeEmptyWidth, lineWidth,
				height - lineWidth - edgeEmptyWidth * 2);
	}

}
