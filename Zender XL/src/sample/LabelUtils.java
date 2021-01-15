package sample;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LabelUtils {

	public static BufferedImage makeLabel(String label) {
		String text = label;
		text = text.replaceAll("_", " ");
		/*
		 * Because font metrics is based on a graphics context, we need to create a
		 * small, temporary image so we can ascertain the width and height of the final
		 * image
		 */
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setPaint ( Color.BLACK );
		g2d.fillRect ( 0, 0, img.getWidth(), img.getHeight() );
		Font font = new Font("Arial", Font.PLAIN,180);
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		int width = fm.stringWidth(text);
		int height = fm.getHeight();
		g2d.dispose();

		img = new BufferedImage(width+ 5, height+ 5, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setFont(font);
		fm = g2d.getFontMetrics();
		g2d.setColor(Color.black);
		g2d.drawString(text, 0, fm.getAscent());
		g2d.dispose();

		return img;
	}
	
	public static BufferedImage addlabelFront(BufferedImage front, BufferedImage label) {

		int widthLabel = label.getWidth() / 2;
		int heightLabel = label.getHeight();
		int widthFront = front.getWidth() / 2;
		int h = 550;
		for (int i = 0; i < widthLabel; i++) {
			for (int j = 0; j < heightLabel; j++) {
				front.setRGB(widthFront - i, j + h, label.getRGB(widthLabel - i, j));
				front.setRGB(widthFront + i, j + h, label.getRGB(widthLabel + i, j));
			}
		}
		return front;
	}

	public static BufferedImage addlabelBack(BufferedImage back, BufferedImage label) {
		int widthLabel = label.getWidth() / 2;
		int heightLabel = label.getHeight();
		int widthFront = back.getWidth() / 2;
		int h = 120;
		for (int i = 0; i < widthLabel; i++) {
			for (int j = 0; j < heightLabel; j++) {
				back.setRGB(widthFront - i, j + h, label.getRGB(widthLabel - i, j));
				back.setRGB(widthFront + i, j + h, label.getRGB(widthLabel + i, j));
			}
		}
		return back;
	}

	public static BufferedImage addlabelHemCenterBot(BufferedImage buffResult, BufferedImage buffLabel) {
		int widthLabel = buffLabel.getWidth() / 2;
		int heightLabel = buffLabel.getHeight();
		int widthHemFront = buffResult.getWidth() / 2;
		int heightHemFront = buffResult.getHeight();
		//int h = 426;

		for (int i = 0; i < widthLabel; i++) {
			for (int j = 0; j < heightLabel; j++) {
				buffResult.setRGB(widthHemFront - i, heightHemFront - j - 1, buffLabel.getRGB(widthLabel - i, heightLabel - j - 1));
				buffResult.setRGB(widthHemFront + i, heightHemFront - j - 1, buffLabel.getRGB(widthLabel + i, heightLabel - j - 1));
			}
		}
		return buffResult;
	}
	public static BufferedImage addlabelHandCenterBot(BufferedImage buffResult, BufferedImage buffLabel) {
		int widthLabel = buffLabel.getWidth() / 2;
		int heightLabel = buffLabel.getHeight();
		int widthHemFront = buffResult.getWidth() / 2;
		int heightHemFront = buffResult.getHeight();
		//int h = 426;
		for (int i = 0; i < widthLabel; i++) {
			for (int j = 0; j < heightLabel; j++) {
				buffResult.setRGB(widthHemFront - i, heightHemFront - j - 1, buffLabel.getRGB(widthLabel - i, heightLabel - j - 1));
				buffResult.setRGB(widthHemFront + i, heightHemFront - j - 1, buffLabel.getRGB(widthLabel + i, heightLabel - j - 1));
			}
		}
		return buffResult;
	}

	public static BufferedImage addlabelTopLeft(BufferedImage newDesign, BufferedImage label) {
		try {
			int widthLabel = label.getWidth();
			int heightLabel = label.getHeight();

			//int h = 426;
			for (int i = 3; i < widthLabel; i++) {
				for (int j = 3; j < heightLabel; j++) {
					newDesign.setRGB( i + 400, j, label.getRGB(i, j));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newDesign;
	}
}
