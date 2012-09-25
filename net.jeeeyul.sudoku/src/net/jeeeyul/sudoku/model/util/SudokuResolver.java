package net.jeeeyul.sudoku.model.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * An instance of this class will generate or solve sudoku puzzle.
 * 
 * @author jeeeyul@gmail.com
 * 
 * @see #getNextSolution()
 * 
 */
public class SudokuResolver {
	/**
	 * Thrown when there is no answer for given condition.
	 * 
	 * @author Jeeeyul
	 * 
	 */
	public class NoAnswerException extends IllegalStateException {
		private static final long serialVersionUID = -1444620548292314974L;

		public NoAnswerException() {
			super("There is no answer.");
		}
	}

	private class SearchSequence {
		private int[] sequence;
		private int currentIndex;

		public SearchSequence(int[] sequence) {
			this.sequence = sequence;
			currentIndex = 0;
		}

		public boolean canNext() {
			return currentIndex < sequence.length - 1;
		}

		public void next() {
			currentIndex++;
			data[dataIndex] = sequence[currentIndex];
		}
	}

	private static final List<Integer> ALL_NUMBERS = Arrays.asList(1, 2, 3, 4,
			5, 6, 7, 8, 9);

	private static final int[] EMPTY_SEQUENCE = new int[0];

	private boolean isResolved = false;
	private Random random;
	private int[] data;
	private boolean[] fixed;
	private Stack<SearchSequence> stack;
	private int dataIndex;
	private long randomSeed;
	private long stepCount;

	/**
	 * Create a {@link SudokuResolver} without any condition. {@link #resolve()}
	 * will generate a new game.
	 */
	public SudokuResolver() {
		this(System.currentTimeMillis());
	}

	/**
	 * Create a {@link SudokuResolver} with condition.
	 * 
	 * @param puzzle
	 *            A condition. it is 81 length int array. Each value must be 0
	 *            to 9. Zero value will be solved by {@link #resolve()}.
	 */
	public SudokuResolver(int[] puzzle) {
		this(System.currentTimeMillis(), puzzle);
	}

	/**
	 * Create a {@link SudokuResolver} without any condition. {@link #resolve()}
	 * will generate a new game.
	 * 
	 * @param seed
	 *            Random seed. Same random seed cause always same result.
	 */
	public SudokuResolver(long seed) {
		data = new int[81];
		fixed = new boolean[81];
		dataIndex = -1;
		stack = new Stack<SudokuResolver.SearchSequence>();
		randomSeed = seed;
		random = new Random(seed);
		stepCount = 0;
	}

	/**
	 * Create a {@link SudokuResolver} with condition and random seed.
	 * 
	 * @param seed
	 *            Random seed. Same random seed cause always same result.
	 * @param puzzle
	 *            A condition. it is 81 length int array. Each value must be 0
	 *            to 9. Zero value will be solved by {@link #resolve()}.
	 */
	public SudokuResolver(long seed, int[] puzzle) {
		this(seed);

		if (puzzle.length != 81) {
			throw new IllegalArgumentException(
					"Puzzle must be 81 length array.");
		}

		for (int i = 0; i < puzzle.length; i++) {
			if (puzzle[i] != 0) {
				data[i] = puzzle[i];
				fixed[i] = true;
			}
		}
	}

	private SearchSequence currentSequence() {
		if (stack.isEmpty()) {
			return null;
		}
		return stack.peek();
	}

	private int getAt(int row, int col) {
		return data[row * 9 + col];
	}

	private int[] getAvailableNumberFor(int index) {
		if (!validateContext()) {
			return EMPTY_SEQUENCE;
		}

		if (fixed[index]) {
			return new int[] { data[index] };
		}

		List<Integer> result = new ArrayList<Integer>(ALL_NUMBERS);

		int row = index / 9;
		int col = index % 9;

		// Horizontal, Vertical Conflict
		for (int x = 0; x < 9; x++) {
			result.remove((Integer) getAt(x, col));
			result.remove((Integer) getAt(row, x));
		}

		int bRow = row / 3;
		int bCol = col / 3;

		for (int rd = 0; rd < 3; rd++) {
			for (int cd = 0; cd < 3; cd++) {
				result.remove((Integer) getAt(bRow * 3 + rd, bCol * 3 + cd));
			}
		}

		Collections.shuffle(result, random);

		int size = result.size();
		int[] array = new int[size];

		for (int i = 0; i < result.size(); i++) {
			array[i] = result.get(i);
		}

		return array;
	}

	/**
	 * resolve given puzzle, What if there is no given condition It will
	 * generate a new complete puzzle.
	 * 
	 * If there was no answer, It will throw a {@link NoAnswerException}.
	 * 
	 * @return an int array contains completed sudoku puzzle.
	 * 
	 * @see #setFixedValue(int, int, int)
	 * @see #Search(int[])
	 * @see #Search(long, int[])
	 * 
	 * @see NoAnswerException
	 */
	public synchronized int[] getNextSolution() {
		if (!isResolved) {
			return resolve();
		} else {
			step();
			return resolve();
		}
	}

	public long getStepCount() {
		return stepCount;
	}

	private boolean isFilled() {
		for (int i = 80; i >= 0; i--) {
			if (data[i] == 0) {
				return false;
			}
		}
		return true;
	}

	private void pop() {
		stack.pop();
		if (!fixed[dataIndex]) {
			data[dataIndex] = 0;
		}
		dataIndex--;
		if (stack.isEmpty())
			throw new NoAnswerException();
	}

	private void push(int[] sequence) {
		if (sequence == null || sequence.length == 0) {
			throw new IllegalArgumentException();
		}

		stack.push(new SearchSequence(sequence));
		dataIndex++;
		data[dataIndex] = sequence[0];
	}

	public synchronized void reset() {
		isResolved = false;
		while (!stack.isEmpty()) {
			stack.pop();
			if (!fixed[dataIndex]) {
				data[dataIndex] = 0;
			}
			dataIndex--;
		}
		random = new Random(randomSeed);
		stepCount = 0;
	}

	private synchronized int[] resolve() {
		while (!isFilled()) {
			step();
		}
		isResolved = true;
		return Arrays.copyOf(data, data.length);
	}

	private void setFixedValue(int index, int val) {
		if (index < 0 || 80 < index || val < 1 || 9 < val) {
			throw new IllegalArgumentException();
		}
		data[index] = val;
		fixed[index] = true;
	}

	/**
	 * Sets a condition.
	 * 
	 * @param row
	 *            Row index in sudoku, it must be 0 to 8.
	 * @param col
	 *            Column index in sudoku, it must be 0 to 8.
	 * @param val
	 *            Value to set. It must be 1 to 9.
	 */
	public synchronized void setFixedValue(int row, int col, int val) {
		if (isResolved) {
			throw new IllegalStateException(
					"Can't modify condition after resolving.");
		}
		if (row < 0 || 8 < row || col < 0 || 8 < col || val < 1 || 9 < val) {
			throw new IllegalArgumentException();
		}
		int index = row * 9 + col;
		setFixedValue(index, val);
	}

	private void step() {
		stepCount++;

		boolean isFilled = isFilled();

		if (currentSequence() == null) {
			int[] nextSequence = getAvailableNumberFor(dataIndex + 1);
			if (nextSequence.length == 0) {
				throw new NoAnswerException();
			}
			push(nextSequence);
			return;
		}

		if (isFilled) {
			if (currentSequence().canNext()) {
				currentSequence().next();
			} else {
				while (!currentSequence().canNext()) {
					pop();
				}
				currentSequence().next();
			}
		}

		else {
			int[] nextSequence = getAvailableNumberFor(dataIndex + 1);
			boolean hasNextBranch = nextSequence.length > 0;

			if (!hasNextBranch && currentSequence().canNext()) {
				currentSequence().next();
			}

			else if (hasNextBranch) {
				push(nextSequence);
			}

			else {
				while (!currentSequence().canNext()) {
					pop();
				}
				currentSequence().next();
			}
		}
	}

	private boolean validateContext() {
		// horizontal conflict
		for (int r = 0; r < 9; r++) {
			int flag = 0x0;

			for (int c = 0; c < 9; c++) {
				int val = getAt(r, c);
				if (val != 0) {
					int mask = 0x1 << (val - 1);
					if ((flag & mask) != 0) {
						return false;
					} else {
						flag |= mask;
					}
				}
			}
		}

		// vertical conflict
		for (int c = 0; c < 9; c++) {
			int flag = 0x0;
			for (int r = 0; r < 9; r++) {
				int val = getAt(r, c);
				if (val != 0) {
					int mask = 0x1 << (val - 1);
					if ((flag & mask) != 0) {
						return false;
					} else {
						flag |= mask;
					}
				}
			}
		}

		// box conflict
		for (int br = 0; br < 3; br++) {
			for (int bc = 0; bc < 3; bc++) {
				int flag = 0x00;
				for (int r = 0; r < 3; r++) {
					for (int c = 0; c < 3; c++) {
						int index = (br * 3 + r) * 9 + bc * 3 + c;
						if (data[index] != 0) {
							int mask = 0x1 << (data[index] - 1);
							if ((flag & mask) != 0) {
								return false;
							} else {
								flag |= mask;
							}
						}
					}
				}
			}
		}

		return true;
	}
}
