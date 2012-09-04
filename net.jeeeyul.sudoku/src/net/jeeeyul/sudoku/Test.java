package net.jeeeyul.sudoku;

public class Test {
	public static void main(String[] args) {
		new Test().run();
	}

	private synchronized void run() {
		synchronized (Object.class) {
			System.out.println("run");
		}
	}
}
