/*
 * Name: Alan Zhu
 *
 * Date: May 29, 2018
 *
 * Desc: This program contains the MainActivity for the
 *       Minesweeper game. In this game, 10 bombs are randomly
 *       hidden in the grid. The player has to flag all the
 *       bombs to win. If the player hits any bombs, they lose.
 */

package com.example.alan.minesweeper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.CheckBox;
import android.view.View.OnClickListener;
import android.graphics.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

// Main structure of the app.
public class MainActivity extends Activity {
    private boolean flag = false;
    private HashSet<Integer> flaggedCells = new HashSet<>();
    private HashSet<Integer> bombCells;

    // Character array for internal game board information
    private char[][] board = new char[10][10];
    private int numBomb = 10;
    private int bombCount = 10;

    private ImageButton[][] cells = new ImageButton[10][10];
    // 2D array of all the ImageButton ID so that it matches the ImageButton array
    private int[][] cellID = {{R.id.cell00,R.id.cell01,R.id.cell02,R.id.cell03,R.id.cell04,R.id.cell05,
            R.id.cell06,R.id.cell07,R.id.cell08,R.id.cell09}, {R.id.cell10,R.id.cell11,R.id.cell12,
            R.id.cell13,R.id.cell14,R.id.cell15,R.id.cell16,R.id.cell17,R.id.cell18,R.id.cell19},
            {R.id.cell20,R.id.cell21,R.id.cell22,R.id.cell23,R.id.cell24,R.id.cell25,R.id.cell26,
                    R.id.cell27,R.id.cell28,R.id.cell29}, {R.id.cell30,R.id.cell31,R.id.cell32,
            R.id.cell33,R.id.cell34,R.id.cell35,R.id.cell36,R.id.cell37,R.id.cell38,R.id.cell39},
            {R.id.cell40,R.id.cell41,R.id.cell42,R.id.cell43,R.id.cell44,R.id.cell45,R.id.cell46,
                    R.id.cell47,R.id.cell48,R.id.cell49}, {R.id.cell50,R.id.cell51,R.id.cell52,
            R.id.cell53,R.id.cell54,R.id.cell55,R.id.cell56,R.id.cell57,R.id.cell58,R.id.cell59},
            {R.id.cell60,R.id.cell61,R.id.cell62,R.id.cell63,R.id.cell64,R.id.cell65,R.id.cell66,
                    R.id.cell67,R.id.cell68,R.id.cell69}, {R.id.cell70,R.id.cell71,R.id.cell72,
            R.id.cell73,R.id.cell74,R.id.cell75,R.id.cell76,R.id.cell77,R.id.cell78,R.id.cell79},
            {R.id.cell80,R.id.cell81,R.id.cell82,R.id.cell83,R.id.cell84,R.id.cell85,R.id.cell86,
                    R.id.cell87,R.id.cell88,R.id.cell89}, {R.id.cell90,R.id.cell91,R.id.cell92,
            R.id.cell93,R.id.cell94,R.id.cell95,R.id.cell96,R.id.cell97,R.id.cell98,R.id.cell99}};

    // 1D array for images of numbers 0-8 (A cell can have a maximum of 8
    // surrounding bombs)
    private int[] imageID = {R.drawable.zero, R.drawable.one, R.drawable.two, R.drawable.three,
            R.drawable.four, R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight};

    private Button resetButton;
    private TextView bombCountTextView;
    private TextView endGameTextView;
    private CheckBox flagCheckBox;

    private SharedPreferences savedPrefs;

    // This is automatically called when the app is launched.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText( MainActivity.this,
                "Welcome to Minesweeper! Flag all 10 mines to win.", Toast.LENGTH_LONG).show();

        // Initialize the character array
        bombCells = initializeBoard();

        View.OnClickListener buttonEventListener = new ButtonListener();
        // Give every ImageButton its corresponding ID and the event listener
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                cells[r][c] = findViewById(cellID[r][c]);
                cells[r][c].setOnClickListener(buttonEventListener);
            }
        }

        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(buttonEventListener);
        bombCountTextView = findViewById(R.id.bombCountTextView);
        endGameTextView = findViewById(R.id.endGameTextView);
        flagCheckBox = findViewById(R.id.flagCheckBox);
        flagCheckBox.setOnClickListener(new CheckBoxListener());

        savedPrefs = getSharedPreferences( "MinesweeperPrefs", MODE_PRIVATE );
    }

    // This is called when the app is closed.
    @Override
    public void onPause() {
        // Save the instance variables
        Editor prefsEditor = savedPrefs.edit();
        prefsEditor.putBoolean( "flag", flag );
        prefsEditor.commit();

        // Calling the parent onPause() must be done LAST
        super.onPause();
    }

    // This is called when the app is re-opened.
    @Override
    public void onResume() {
        super.onResume();

        // Load the instance variables back (or default values)
        flag = savedPrefs.getBoolean("flag", false);
        flagCheckBox.setChecked(flag);
    }

    // This is the inner class that handles Button and ImageButton events.
    class ButtonListener implements View.OnClickListener {
        int index;
        @Override
        public void onClick( View v ) {
            // If the player clicks reset
            if (v.getId() == R.id.resetButton) {
                // Enable all ImageButtons and make them blank (new board)
                for (int row = 0; row < 10; row++) {
                    for (int col = 0; col < 10; col++) {
                        cells[row][col].setEnabled(true);
                        cells[row][col].setImageResource(R.drawable.unknown);
                    }
                }
                flag = false;
                flagCheckBox.setChecked(false);
                flagCheckBox.setEnabled(true);
                flaggedCells.clear();
                // Generate now bomb locations
                bombCells.clear();
                bombCells = initializeBoard();
                bombCount = 10;
                bombCountTextView.setText("10");
                bombCountTextView.setTextColor(Color.rgb(0, 0, 0));
                endGameTextView.setVisibility(View.INVISIBLE);
            }
            // If the player clicks one of the cells
            else {
                // Finds which cell is clicked by checking every ImageButton ID (by row)
                for (int r = 0; r < 10; r++) {
                    index = Arrays.binarySearch(cellID[r], v.getId());
                    // Non-negative index indicates a successful search
                    if (index >= 0) {
                        // If the player is flagging, mark the cell if it is unmarked,
                        // or un-mark a marked cell.
                        if (flag) {
                            // r*10+index gives the index of the cell as if it belongs to a 1D array,
                            // which can be conveniently stored into the HashSet.
                            if (flaggedCells.add(r*10+index)) {
                                // If it successfully appends to HashSet, the cell is not flagged.
                                // Mark it.
                                cells[r][index].setImageResource(R.drawable.flag);
                                bombCount--;
                            }
                            else {
                                // The cell is already flagged. Un-mark the cell and remove
                                // the index from HashSet.
                                cells[r][index].setImageResource(R.drawable.unknown);
                                flaggedCells.remove(r*10+index);
                                bombCount++;
                            }
                            // Updates the bomb counter label
                            bombCountTextView.setText(Integer.toString(bombCount));
                            // If the counter becomes negative, the player has flagged the wrong cell.
                            // Warn them with red font.
                            if (bombCount < 0) {
                                bombCountTextView.setTextColor(Color.rgb(255, 0, 0));
                            }
                            // If the player flagged 10 cells, check if all the bombs are flagged (win)
                            else if (bombCount == 0) {
                                // The player has won, disable all buttons except reset button
                                if (flaggedCells.equals(bombCells)) {
                                    endGameTextView.setText("YOU WIN!");
                                    endGameTextView.setVisibility(View.VISIBLE);
                                    for (int row = 0; row < 10; row++) {
                                        for (int col = 0; col < 10; col++) {
                                            cells[row][col].setEnabled(false);
                                        }
                                    }
                                    flagCheckBox.setEnabled(false);
                                }
                                // If no win, the player has flagged the wrong cell. Warn them with red font.
                                else {
                                    bombCountTextView.setTextColor(Color.rgb(255, 0, 0));
                                }
                            }
                            else {
                                bombCountTextView.setTextColor(Color.rgb(0, 0, 0));
                            }
                        }
                        // The player is in search mode
                        else {
                            // If the player tries to search a flagged cell, stop them with a message
                            if (flaggedCells.contains(r*10+index)) {
                                Toast.makeText( MainActivity.this, "This cell is flagged", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // The player hits a bomb, reveal all cells and disable all buttons except
                                // the reset button
                                if (board[r][index] == 'B') {
                                    displayBoard();
                                    for (int row = 0; row < 10; row++) {
                                        for (int col = 0; col < 10; col++) {
                                            cells[row][col].setEnabled(false);
                                        }
                                    }
                                    flagCheckBox.setEnabled(false);
                                    endGameTextView.setText("YOU LOSE!");
                                    endGameTextView.setVisibility(View.VISIBLE);
                                }
                                // If the player does not hit a bomb, reveal the number and disable the button
                                // so that it is impossible to flag a searched cell
                                else {
                                    // Get the number from the char array, convert it to an
                                    // integer, then use that integer as index to get the image ID
                                    cells[r][index].setImageResource(imageID[Character.getNumericValue(board[r][index])]);
                                    cells[r][index].setEnabled(false);
                                }
                            }
                        }
                        // We already found the ImageButton that is clicked,
                        // no need to look for it in other rows
                        break;
                    }
                }
            }
        }
    }

    // The is the inner class that handles CheckBox events.
    class CheckBoxListener implements OnClickListener {
        @Override
        public void onClick( View v ) {
            flag = flagCheckBox.isChecked();
        }
    }

    // This method randomly hides bombs ‘B’ at different locations on the board.
    // It also assigns a number to each cell that is not a bomb. It takes
    // no parameters and returns a HashSet of bomb indices.
    private HashSet<Integer> initializeBoard() {
        for (int r = 0; r < 10; r++) {
            Arrays.fill(board[r], ' ');
        }

        Random generator = new Random();
        HashSet<Integer> bombs = new HashSet<>(numBomb);
        int position;

        // Determines the dimensions of the board
        int row = board.length;
        int col = board[0].length;

        // Randomly generates location for bombs (cannot have more than one
        // at same location, and the HashSet ensures that)
        for (int count=0; count<numBomb; count++) {
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
        // Searches adjacent cells for bombs
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
        return bombs;
    }

    // This method reveals every cell, whether it is a bomb or not.
    // It takes no parameters and returns nothing.
    private void displayBoard() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (board[r][c] == 'B') {
                    cells[r][c].setImageResource(R.drawable.bomb);
                }
                else {
                    cells[r][c].setImageResource(imageID[Character.getNumericValue(board[r][c])]);
                }
            }
        }
    }
}