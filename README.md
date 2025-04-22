# 🔷 Java Chess Game

This is a full-featured chess game built in **Java** using **Swing** for the GUI, featuring a **smart AI opponent** that captures pieces and plays strategically.

## 👨‍💻 Features
- Full 8x8 Chessboard UI using `JPanel` and `GridLayout`
- All major piece movement rules implemented:
  - Pawn, Rook, Knight, Bishop, Queen, and King
- Check and Checkmate detection
- Turn-based logic with player switching
- AI opponent (Black) that:
  - Captures available opponent pieces first
  - Falls back to random legal moves when no capture is available
- Highlighting of selected pieces
- Unicode-based visual representation of chess pieces
- Responsive design using `JFrame` with fullscreen support

## 🚀 Technologies Used
- Java
- Java Swing (`javax.swing.*`)
- AWT for GUI components and event handling
- Object-Oriented Programming (OOP) principles
- Basic AI using random + capture-priority logic

## 🧠 How the AI Works
The `AIPlayer` class:
- Scans all possible legal moves for the computer-controlled pieces
- Prioritizes moves that result in capturing opponent pieces
- Chooses random valid moves when no captures are available
- Plays after every player move with a slight delay for realism

## 📷 Screenshot (Optional)
_Include a screenshot of the game running in fullscreen mode_

## 💻 How to Run
```bash
javac ChessGame.java
java ChessGame
