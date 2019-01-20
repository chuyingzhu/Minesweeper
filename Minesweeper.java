import java.util.Arrays;
import java.util.HashSet;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
/* The program contains a minesweeper game.
 * 
 * @author Alan Zhu
 * @version February 28, 2018
 */

public class Minesweeper {
    // This method defines the mainline logic for this program.
    // It contains the main game loop. It does not return anything.
    public static void main( String[] args ) {
        int row;
        int col;
        int[] array = new int[2];
        HashSet<Integer> searchedCells = new HashSet<Integer>();
        
        array = getDimensions();
        row = array[0];
        col = array[1];
        
        // Initializes the game board and sets all elements as empty ' '
        char[][] board = new char[row][col];
        for (int r=0; r<row; r++) {
            Arrays.fill(board[r], ' ');
        }
        
        int numBomb = (row+col)/2;
        initializeBoard(board, numBomb);
        
        // debugging ONLY
        displayBoard(board, searchedCells, true);
        
        // Starts the main game loop
        while (true) {
            displayBoard(board, searchedCells, false);
            if (makeUserMove(board, searchedCells)) {
                System.out.println( "YOU LOSE!" );
                break;
            }
            if (searchedCells.size() == (row*col-numBomb)) {
                System.out.println( "YOU WIN!" );
                break;
            }
        }
        
        // End of the game
        displayBoard(board, searchedCells, true);
    }
    
    // This method accepts the game board as a parameter and randomly
    // hides bombs ‘B’ at different locations on the board.
    // It does not return anything.
    private static void initializeBoard(char[][] board, int bomb) {
        Random generator = new Random();
        HashSet<Integer> bombs = new HashSet<Integer>(bomb);
        int position;
        
        // Determines the dimensions of the board
        int row = board.length;
        int col = board[0].length;
        
        // Randomly generates location for bombs (cannot have more than one
        // at same location.
        for (int count=0; count<bomb; count++) {
            position = generator.nextInt(row*col);
            while (!bombs.add(position)) {
                position = generator.nextInt(row*col);
            }
        }
        
        // When the bomb positions are unique, set the according elements to 'B'
        // Includes math trick to deal with indices
        for (int pos : bombs) {
            board[pos/col][pos%col] = 'B';
        }
        
        // Assign numbers to each block that is not a bomb
        for (int r=0; r<row; r++) {
            for (int c=0; c<col; c++) {
                if (board[r][c]=='B')
                    continue;
                else {
                    int count = 0;
                    // Checks if any surrounding cell is 'B'
                    for (int a=r-1; a<r+2; a++) {
                        // Checks if row index is out of range
                        if (a<0 || a>=row)
                            continue;
                        for (int b=c-1; b<c+2; b++) {
                            // Checks if col index is out of range
                            if (b<0 || b>=col)
                                continue;
                            if (board[a][b]=='B') {
                                count++;
                            }
                        }
                    }
                    board[r][c] = Integer.toString(count).toCharArray()[0];
                }
            }
        }
    }
    
    // This method accepts the game board as a parameter and displays
    // it as a neatly formatted grid with column numbers and row numbers.
    // If the boolean showTreasure parameter is true then the locations
    // of the treasures ‘T’ are displayed, otherwise they are hidden.
    // It does not return anything.
    private static void displayBoard(char[][] board, HashSet<Integer> revealCells, boolean showBomb) {
        String line;
        // Determines the dimensions of the board
        int row = board.length;
        int col = board[0].length;
        
        // Creates a new board to be displayed
        char[][] tempBoard = new char[row][col];
        for (int r=0; r<row; r++) {
            Arrays.fill(tempBoard[r], ' ');
        }
        
        for (int i=0; i<row*col; i++) {
            if (revealCells.contains(i)) {
                tempBoard[i/col][i%col] = board[i/col][i%col];
            }
            if (board[i/col][i%col] == 'B') {
                tempBoard[i/col][i%col] = 'B';
            }
        }
        
        // Prints column numbers
        System.out.println();
        for (int c=0; c<col; c++) {
            System.out.print("   "+c);
        }
        System.out.print("\n");
        
        // Prints each row
        for (int r=0; r<row; r++) {
            line = r+": "+String.join(" | ", new String(tempBoard[r]).split(""));
            if (!showBomb) {
                // Hides the bombs 'B'
                line = line.replaceAll("B", " ");
            }
            System.out.println( line );
            System.out.println( "  "+String.join("+", Collections.nCopies(col, "---")) );
        }
    }
    
    // This method accepts the game board as a parameter and asks the
    // user to enter a row and column to search for bombs. It ensures that
    // only valid integer values are accepted. If the user input is invalid
    // or the cell has already been searched, it prompts the user to enter
    // new values. If the user hits a bomb during their move, this
    // method will return true, otherwise it will return false and replace
    // ' ' with 'X'. If the user’s guess is only 1 cell
    // away from a hidden treasure (in any direction), change the cell to '!'.
    private static boolean makeUserMove(char[][] board, HashSet<Integer> searchedCells) {
        // Determines the dimensions of the board
        int rowLength = board.length;
        int colLength = board[0].length;
        
        Scanner input = new Scanner(System.in);
        String userInput;
        int row = 0;
        int col = 0;
        boolean validInput = false;
        
        // This do..while loop prompts the user for row and col until
        // they enter a valid integer.
        do {
            try {
                // Asks for row number
                System.out.printf( "\nWhat row would you like to search (0-%d): ", rowLength-1 );
                userInput = input.nextLine();
                row = Integer.parseInt(userInput);
                if (row<0 || row>=rowLength) {
                    System.out.println( "\nSorry, invalid location. Please try again!" );
                    continue;
                }
                
                // Asks for column number
                System.out.printf( "What col would you like to search (0-%d): ", colLength-1 );
                userInput = input.nextLine();
                col = Integer.parseInt(userInput);
                if (col<0 || col>=colLength) {
                    System.out.println( "\nSorry, invalid location. Please try again!" );
                    continue;
                }
            }
            // This catch block executes when the user enters a non-integer value
            catch ( NumberFormatException numberFormatException ) {
                System.out.println( "\nIntegers only for row and column values. Please try again!" );
                continue;
            }
            
            // Checks if the cell has already been searched
            // If not, the user input is valid and the loop terminates
            if ( searchedCells.contains(row*rowLength+col) ) {
                System.out.println( "\nYou already tried there, please pick again." );
            }
            else {
                validInput = true;
                searchedCells.add(row*rowLength+col);
            }
        } while (!validInput);
        
        // Checks if the cell is 'B'
        if (board[row][col]=='B') {
            return true;
        }
        else{
            return false;
        }
    }
    
    /**
     * This method prompts the user to enter the number of rows
     * and columns that the grid should have. It returns an
     * integer array, the first index being the length of the 
     * grid and the second index being the width.
     * 
     * @return int[] - The dimensions of the grid
     * 
     * @see Scanner
     */
    private static int[] getDimensions() {
        Scanner input = new Scanner(System.in);
        String userInput;
        int row = 0;
        int col = 0;
        boolean validInput = false;
        // This do..while loop prompts the user for row and col until they enter a valid integer
        do {
            try {
                // Asks for row number
                System.out.print( "\nHow many rows would you like (5-15): " );
                userInput = input.nextLine();
                row = Integer.parseInt(userInput);
                if ( (row<5) || (row>15) ) {
                    System.out.println( "\nSorry, invalid row length. Please try again!" );
                    continue;
                }
                
                // Asks for column number
                System.out.print( "How many columns would you like (5-15): " );
                userInput = input.nextLine();
                col = Integer.parseInt(userInput);
                if ( (col>=5) && (col<=15) ) {
                    validInput = true;
                }
                else {
                    System.out.println( "\nSorry, invalid column length. Please try again!" );
                }
            }
            // This catch block executes when the user enters a non-integer value
            catch ( NumberFormatException numberFormatException ) {
                System.out.println( "\nIntegers only for row and column numbers. Please try again!" );
            }
        } while (!validInput);
        
        input.close();
        int[] dimensions = {row, col};
        return dimensions;
    }
}