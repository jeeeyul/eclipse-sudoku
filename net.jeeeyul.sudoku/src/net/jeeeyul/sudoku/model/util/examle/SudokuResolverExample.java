package net.jeeeyul.sudoku.model.util.examle;

import net.jeeeyul.sudoku.model.util.SudokuResolver;

public class SudokuResolverExample {
	public static void main(String[] args) {
		SudokuResolver resolver = new SudokuResolver();

		// Sets condition
		resolver.setFixedValue(0, 0, 9);
		resolver.setFixedValue(8, 8, 9);
		resolver.setFixedValue(8, 0, 1);

		// find 10 solution.
		for (int phase = 1; phase <= 10; phase++) {
			int[] nextSolution = resolver.getNextSolution();

			System.out.println("Phase " + phase);
			System.out.println("Search Steps: " + resolver.getStepCount());
			for (int i = 0; i < nextSolution.length; i++) {
				if (i != 0 && i % 9 == 0) {
					System.out.println();
				}
				System.out.printf("%2d", nextSolution[i]);
			}

			System.out.println("\r\n");
		}

	}
}
