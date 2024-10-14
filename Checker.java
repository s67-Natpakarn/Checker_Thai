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
int currentPlayer = 2; // 1 for black, 2 for white
boolean gameOver = false;
int winner = 0; // 1 for black, 2 for white, 0 for none
boolean notStart = true;

void setup() {
  size(810, 810);
  noLoop();
}

void draw() {
  background(0);
  
  if (notStart) {
    textSize(55);
    textAlign(CENTER);
    background(0);
    fill(255);
    text("Welcome to Checker by Java : )", width / 2, height / 2 - 200);
    textSize(40);
    text("Click anywhere or Load game to start the game", width / 2, height /2 - 120);
    textSize(30);
    text("Press S to save game", width / 2, height / 2 + 110);
    text("Press L to load game", width / 2, height / 2 + 170);
  }
  
  if (!notStart) {
    for (int row = 0; row < gridSize; row++) {
      for (int col = 0; col < gridSize; col++) {
        if ((row + col) % 2 == 0) {
          fill(255);
        } else {
          fill(100);
        }
        rect(col * squareSize + borderWidth, row * squareSize + borderWidth, squareSize, squareSize);
        noFill();
        stroke(0);
        strokeWeight(gridWidth);
        rect(col * squareSize + borderWidth, row * squareSize + borderWidth, squareSize, squareSize);
        
        // Draw checkers
        if (checkerStatus[row][col] == 1) {
          fill(0);
          ellipse(col * squareSize + borderWidth + squareSize / 2, row * squareSize + borderWidth + squareSize / 2, squareSize * 0.6, squareSize * 0.6);
        } else if (checkerStatus[row][col] == 2) {
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
  // Check for game over
  checkGameOver();
  
  if (gameOver) {
    textSize(50);
    textAlign(CENTER);
    if (winner == 1) {
      background(0);
      fill(255);
      text("Game Over : )", width / 2, height / 2 - 40);
      text("Winner : Black ", width / 2, height / 2 + 20);
    } else if (winner == 2) {
      background(0);
      fill(255);
      text("Game Over : )", width / 2, height / 2 - 40);
      text("Winner : White ", width / 2, height / 2 + 20);
    }
    textSize(20);
    text("Press R to Restart", width / 2, height / 2 + 60);
  }
}
  
boolean isValidMove(int row, int col) {
  if (selectedRow == -1 || selectedCol == -1) return false;

  int moveableRow = row - selectedRow;
  int moveableCol = col - selectedCol;

  if (currentPlayer == 1 && moveableRow == 1 && abs(moveableCol) == 1 && checkerStatus[row][col] == 0) {
    return true;
  }
  
  if (currentPlayer == 2 && moveableRow == -1 && abs(moveableCol) == 1 && checkerStatus[row][col] == 0) {
    return true;
  }

  if (abs(moveableRow) == 2 && abs(moveableCol) == 2) {
    int midRow = (selectedRow + row) / 2;
    int midCol = (selectedCol + col) / 2;

    if (currentPlayer == 1 && moveableRow == 2 && checkerStatus[midRow][midCol] == 2 &&
        checkerStatus[row][col] == 0) {
      return true;
    }
    if (currentPlayer == 2 && moveableRow == -2 && checkerStatus[midRow][midCol] == 1 &&
        checkerStatus[row][col] == 0) {
      return true;
    }
  }
  return false;
}

void mousePressed() {
  if (gameOver) return; // Ignore clicks if the game is over
  notStart = false;

  int col = (mouseX - borderWidth) / squareSize;
  int row = (mouseY - borderWidth) / squareSize;

  if (col >= 0 && col < gridSize && row >= 0 && row < gridSize) {
    if (selectedRow == -1 && selectedCol == -1) {
      if (checkerStatus[row][col] == currentPlayer) {
        selectedRow = row;
        selectedCol = col;
      }
    } else {
      if (isValidMove(row, col)) {
        checkerStatus[row][col] = checkerStatus[selectedRow][selectedCol];
        checkerStatus[selectedRow][selectedCol] = 0;

        if (abs(row - selectedRow) == 2) {
          int midRow = (selectedRow + row) / 2;
          int midCol = (selectedCol + col) / 2;
          checkerStatus[midRow][midCol] = 0; // Remove the captured piece
        }

        currentPlayer = (currentPlayer == 1) ? 2 : 1;
      }
      selectedRow = -1;
      selectedCol = -1;
    }
  }
  checkGameOver(); // Check if the game is over after the move
  redraw();
}

void checkGameOver() {
  int blackCount = 0;
  int whiteCount = 0;

  for (int row = 0; row < gridSize; row++) {
    for (int col = 0; col < gridSize; col++) {
      if (checkerStatus[row][col] == 1) blackCount++;
      if (checkerStatus[row][col] == 2) whiteCount++;
    }
  }

  // Check if any player has no pieces left
  if (blackCount == 0) {
    gameOver = true;
    winner = 2; // White wins
  } else if (whiteCount == 0) {
    gameOver = true;
    winner = 1; // Black wins
  }
}

void keyPressed() {
  if (key == 's') {
    saveGame();
  } else if (key == 'l') {
    loadGame();
    notStart = false;
    redraw();
  } else if (key == 'r') {
    restartGame();
  }
}

void restartGame() {
  checkerStatus = new int[][] {
    {0, 1, 0, 1, 0, 1, 0, 1},
    {1, 0, 1, 0, 1, 0, 1, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 0, 0, 0, 0, 0, 0, 0},
    {0, 2, 0, 2, 0, 2, 0, 2},
    {2, 0, 2, 0, 2, 0, 2, 0}
  };
  currentPlayer = 2;
  gameOver = false;
  winner = 0;
  redraw();
}

// Save game state to a file
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