package net.jeeeyul.sudoku.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Item;

public class SudokuItem extends Item {

	private Sudoku parent;
	private Rectangle bounds = new Rectangle(0, 0, 0, 0);
	private Color foreground;
	private Color background;
	private int value;
	private Font font;

	public SudokuItem(Sudoku parent) {
		super(parent, SWT.NORMAL);
		this.parent = parent;
	}

	public Color getBackground() {
		if (this.background == null) {
			return parent.getBackground();
		}
		return background;
	}

	public Font getFont() {
		if (font == null) {
			return parent.getFont();
		}
		return font;
	}

	public Color getForeground() {
		if (this.foreground == null) {
			return parent.getForeground();
		}
		return foreground;
	}

	public int getValue() {
		return value;
	}

	public void paint(GC gc) {
		gc.setBackground(getBackground());
		gc.fillRectangle(bounds.x, bounds.y, bounds.width, bounds.height);

		gc.setForeground(getForeground());
		gc.drawRectangle(bounds);

		Transform transform = new Transform(gc.getDevice());
		transform.translate(bounds.x + bounds.width / 2, bounds.y
				+ bounds.height / 2);

		transform.scale(bounds.width / 100f, bounds.height / 100f);
		gc.setTransform(transform);

		if (1 <= value && value <= 9) {
			gc.setFont(getFont());
			Point textBounds = gc.stringExtent(Integer.toString(value));
			gc.drawText(Integer.toString(value), (100 - textBounds.x) / 2 - 50,
					(100 - textBounds.y) / 2 - 50);
		}

		gc.setTransform(null);
		transform.dispose();
	}

	public void redraw() {
		if (bounds != null) {
			parent.redraw(bounds.x, bounds.y, bounds.width, bounds.height,
					false);
		}
	}

	public void setBackground(Color background) {
		this.background = background;
		redraw();
	}

	public void setBounds(Rectangle rectangle) {
		if (bounds != null) {
			parent.redraw(bounds.x, bounds.y, bounds.width, bounds.height,
					false);
		}

		this.bounds = rectangle;
		parent.redraw(bounds.x, bounds.y, bounds.width, bounds.height, false);
	}

	public void setFont(Font font) {
		this.font = font;
		redraw();
	}

	public void setForeground(Color foreground) {
		this.foreground = foreground;
		redraw();
	}

	public void setValue(int value) {
		this.value = value;
		redraw();
	}

}
