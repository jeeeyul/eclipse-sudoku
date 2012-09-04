package net.jeeeyul.sudoku.model.util

import net.jeeeyul.sudoku.model.sudoku.Board
import net.jeeeyul.sudoku.model.sudoku.SudokuFactory

class GameGenerator {
	extension SudokuFactory = SudokuFactory::eINSTANCE
	
	def Board generate(){
		var board = createBoard()

		for(r : 0..8){
			for(c : 0..8){
				board.cells += createCell 
			}
		}
		
		return board
	}
}