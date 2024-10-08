int gridSize = 8;
int squareSize = 100;
int borderWidth = 5;
float gridWidth = 3.5;
int[][] checkerStatus = {
  {0, 1, 0, 1, 0, 1, 0, 1},
  {1, 0, 1, 0, 1, 0, 1, 0},
  {0, 0, 0, 0, 0, 0, 0, 0},
  {0, 0, 0, 0, 0, 0, 0, 0},
  {0, 0, 0, 0, 0, 0, 0, 0},
  {0, 0, 0, 0, 0, 0, 0, 0},
  {0, 2, 0, 2, 0, 2, 0, 2},
  {2, 0, 2, 0, 2, 0, 2, 0}
};

int selectedRow = -1;
int selectedCol = -1;
int currentPlayer = 1; // 1 for black, 2 for white

void setup() {
  size(810, 810);
  noLoop();
}

void draw() {
  background(0);

  for (int row = 0; row < gridSize; row++) {
    for (int col = 0; col < gridSize; col++) {
      if ((row + col) % 2 == 0) {
        fill(255);
      } else {
        fill(100);
      }

      // Draw the square with a border
      rect(col * squareSize + borderWidth, row * squareSize + borderWidth, squareSize, squareSize);

      // Draw the border around each square
      noFill();
      stroke(0);
      strokeWeight(gridWidth);
      rect(col * squareSize + borderWidth, row * squareSize + borderWidth, squareSize, squareSize);

      // Draw the black checker pieces
      if (checkerStatus[row][col] == 1) {
        fill(0);
        ellipse(col * squareSize + borderWidth + squareSize / 2, row * squareSize + borderWidth + squareSize / 2, squareSize * 0.6, squareSize * 0.6);
      } 
      // Draw the white checker pieces
      else if (checkerStatus[row][col] == 2) {
        fill(255);
        ellipse(col * squareSize + borderWidth + squareSize / 2, row * squareSize + borderWidth + squareSize / 2, squareSize * 0.6, squareSize * 0.6);
      }
      
      // Highlight valid moves
      if (isValidMove(row, col)) {
        fill(0, 255, 0);
        rect(col * squareSize + borderWidth, row * squareSize + borderWidth, squareSize, squareSize);
      }
    }
  }
}

boolean isValidMove(int row, int col) {
  if (selectedRow == -1 || selectedCol == -1) return false;
  
  int moveableRow = row - selectedRow;
  int moveableCol = col - selectedCol;

  // Simple move (one step diagonally)
  if (abs(moveableRow) == 1 && abs(moveableCol) == 1 && checkerStatus[row][col] == 0) {
    return true;
  }

  // Capture move (two steps diagonally)
  if (abs(moveableRow) == 2 && abs(moveableCol) == 2) {
    int midRow = (selectedRow + row) / 2;
    int midCol = (selectedCol + col) / 2;
    
    // Ensure that there is an opponent's piece to capture and no piece blocking the way
    if (checkerStatus[midRow][midCol] != 0 && checkerStatus[midRow][midCol] != checkerStatus[selectedRow][selectedCol] &&
        checkerStatus[row][col] == 0) {
      return true;
    }
  }

  return false; // Invalid move
}

void mousePressed() {
  int col = (mouseX - borderWidth) / squareSize;
  int row = (mouseY - borderWidth) / squareSize;

  if (col >= 0 && col < gridSize && row >= 0 && row < gridSize) {
    if (selectedRow == -1 && selectedCol == -1) {
      if (checkerStatus[row][col] == currentPlayer) { // Only allow current player to select their pieces
        selectedRow = row;
        selectedCol = col;
      }
    } else {
      if (isValidMove(row, col)) {
        checkerStatus[row][col] = checkerStatus[selectedRow][selectedCol];
        checkerStatus[selectedRow][selectedCol] = 0;

        // Handle capturing opponent's piece
        if (abs(row - selectedRow) == 2) {
          int midRow = (selectedRow + row) / 2;
          int midCol = (selectedCol + col) / 2;
          checkerStatus[midRow][midCol] = 0; // Remove the captured piece
        }

        // Switch turns after a successful move
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
      }
      selectedRow = -1;
      selectedCol = -1;
    }
  }
  redraw();
}

void saveGame() {
  String[] saveData = new String[gridSize + 1];
  for (int row = 0; row < gridSize; row++) {
    saveData[row] = join(intArrayToStringArray(checkerStatus[row]), ",");
  }
  saveData[gridSize] = str(currentPlayer); // Save the current player
  saveStrings("checkers_save.txt", saveData);
  println("Game saved.");
}


void loadGame() {
  String[] loadedData = loadStrings("checkers_save.txt");
  if (loadedData != null) {
    for (int row = 0; row < gridSize; row++) {
      String[] rowData = split(loadedData[row], ",");
      for (int col = 0; col < gridSize; col++) {
        checkerStatus[row][col] = int(rowData[col]);
      }
    }
    currentPlayer = int(loadedData[gridSize]);
    println("Game loaded.");
  } else {
    println("No saved game found.");
  }
}

// Convert int array to String array for saving
String[] intArrayToStringArray(int[] arr) {
  String[] result = new String[arr.length];
  for (int i = 0; i < arr.length; i++) {
    result[i] = str(arr[i]);
  }
  return result;
}

void keyPressed() {
  if (key == 's') {
    saveGame();
  } else if (key == 'l') {
    loadGame();
    redraw();
  }
}