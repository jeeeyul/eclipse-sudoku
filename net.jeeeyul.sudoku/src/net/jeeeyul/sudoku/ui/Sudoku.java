package net.jeeeyul.sudoku.ui;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Sudoku extends Canvas {
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());

		new Sudoku(shell, SWT.NORMAL);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private SudokuItem[] items;
	private int boxSize;
	private Rectangle itemArea;
	private int margin = 5;

	private Font itemFont;
	private HashSet<Resource> resources = new HashSet<Resource>();

	public Sudoku(Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);

		createItems();

		hook();
	}

	private void createItems() {
		items = new SudokuItem[81];

		for (int i = 0; i < 81; i++) {
			items[i] = new SudokuItem(this);
			items[i].setValue(9);
			items[i].setFont(getItemFont());
		}
	}

	public Rectangle getItemArea() {
		if(itemArea == null){
			layoutItems();
		}
		return itemArea;
	}

	public Font getItemFont() {
		if (itemFont == null || itemFont.isDisposed()) {
			FontData[] datas = getFont().getFontData();
			FontData itemFontData = datas[0];
			itemFontData.setHeight(40);

			itemFont = new Font(getDisplay(), itemFontData);
			resources.add(itemFont);
		}
		return itemFont;
	}

	private void hook() {
		addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				layoutItems();
			}
		});

		addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				onPaint(event);
			}
		});
	}

	private void layoutItems() {
		Rectangle clientArea = getClientArea();
		Rectangle paddingArea = new Rectangle(clientArea.x + margin,
				clientArea.y + margin, clientArea.width - margin * 2,
				clientArea.height - margin * 2);

		paddingArea.x += margin;
		paddingArea.y += margin;
		paddingArea.width -= margin * 2;
		paddingArea.height -= margin * 2;

		int smaller = Math.min(paddingArea.width, paddingArea.height);
		boxSize = smaller / 9;
		int size = boxSize * 9;
		itemArea = new Rectangle((clientArea.width - size) / 2,
				(clientArea.height - size) / 2, size, size);

		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				SudokuItem item = items[r * 9 + c];
				item.setBounds(new Rectangle(itemArea.x + c * boxSize,
						itemArea.y + r * boxSize, boxSize, boxSize));

				if ((r / 3 + c / 3) % 2 == 1) {
					item.setBackground(getDisplay().getSystemColor(
							SWT.COLOR_WHITE));
				}
			}
		}
	}

	private void onPaint(Event event) {
		event.gc.setAntialias(SWT.ON);
		for (SudokuItem each : items) {
			each.paint(event.gc);
		}
	}

}
